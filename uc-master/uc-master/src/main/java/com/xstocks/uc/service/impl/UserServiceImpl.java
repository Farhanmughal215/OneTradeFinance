package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.common.Constants;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.config.security.JwtManager;
import com.xstocks.uc.config.security.UserDetail;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.RoleMapper;
import com.xstocks.uc.mapper.RoleUserMapper;
import com.xstocks.uc.mapper.UserMapper;
import com.xstocks.uc.pojo.dto.aistock.AiStockUserBalance;
import com.xstocks.uc.pojo.dto.order.OrderUserBalance;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import com.xstocks.uc.pojo.enums.UserStatusEnum;
import com.xstocks.uc.pojo.param.*;
import com.xstocks.uc.pojo.po.OrgPO;
import com.xstocks.uc.pojo.po.RolePO;
import com.xstocks.uc.pojo.po.RoleUserPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.OperatorVO;
import com.xstocks.uc.pojo.vo.UserVO;
import com.xstocks.uc.service.OrgService;
import com.xstocks.uc.service.SysConfService;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.service.remote.AiStockService;
import com.xstocks.uc.service.remote.OrderService;
import com.xstocks.uc.utils.IdHelper;
import com.xstocks.uc.utils.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author dongjunfu
 * @description 针对表【user(user)】的数据库操作Service实现
 * @createDate 2023-10-28 14:38:31
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO>
        implements UserService, UserDetailsService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Autowired
    private SysConfService sysConfService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AiStockService aiStockService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        UserPO user = getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, phone).last("LIMIT 1"));
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.USER_NOT_FOUND_ERROR, "user not found, login first");
        }

        List<SimpleGrantedAuthority> roleCodeList = new ArrayList<>();

        RoleUserPO roleUser =
                roleUserMapper.selectOne(Wrappers.<RoleUserPO>lambdaQuery().eq(RoleUserPO::getUserId, user.getId()),
                        false);
        if (Objects.nonNull(roleUser)) {
            RolePO role = roleMapper.selectById(roleUser.getRoleId());
            if (Objects.nonNull(role)) {
                roleCodeList.add(new SimpleGrantedAuthority(role.getRoleCode()));
            }
        }
        if (CollectionUtils.isEmpty(roleCodeList)) {
            roleCodeList.add(new SimpleGrantedAuthority(RoleTypeEnum.ROLE_USER.getCode()));
        }

        return new UserDetail(user, roleCodeList);
    }

    @Override
    public String generateTokenByUserPO(UserPO user) {
        boolean isAdminOrOperator = false;

        RoleUserPO roleUser =
                roleUserMapper.selectOne(Wrappers.<RoleUserPO>lambdaQuery().eq(RoleUserPO::getUserId, user.getId()),
                        false);
        if (Objects.nonNull(roleUser)) {
            RolePO role = roleMapper.selectById(roleUser.getRoleId());
            if (Objects.nonNull(role) && (RoleTypeEnum.ROLE_ADMIN.getCode().equals(role.getRoleCode()) ||
                    RoleTypeEnum.ROLE_OPERATOR.getCode().equals(role.getRoleCode()))) {
                isAdminOrOperator = true;
            }
        }

        return jwtManager.generate(user.getPhone(), isAdminOrOperator ?
                Optional.of(appConfig.getJwtConfig().getAdminExpireSeconds()) : Optional.empty());
    }

    @Override
    public UserVO createUserByC(CreateUserByCParam createUserByCParam) {
        if (StringUtils.isBlank(createUserByCParam.getPhone()) || StringUtils.isBlank(createUserByCParam.getPsw())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone and password required");
        }
        UserPO user =
                getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, createUserByCParam.getPhone())
                        .last("LIMIT 1"));

        if (Objects.nonNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone or email duplicate");
        }
        if (StringUtils.isBlank(createUserByCParam.getValidateCode())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "validate code required");
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(Constants.VERIFY_CODE_PREFIX + createUserByCParam.getPhone().trim()))) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "validate code expired");
        }
        if (!redisTemplate.opsForValue().get(Constants.VERIFY_CODE_PREFIX + createUserByCParam.getPhone().trim()).toString().equalsIgnoreCase(createUserByCParam.getValidateCode())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "validate code wrong");
        }


        user = new UserPO();
        user.setPhone(createUserByCParam.getPhone());
        user.setPsw(createUserByCParam.getPsw());

        if (StringUtils.isNotBlank(createUserByCParam.getInviteBy())) {
            UserPO inviteUser = getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getBizId,
                    createUserByCParam.getInviteBy().trim()).last(
                    "LIMIT 1"));
            if (Objects.nonNull(inviteUser)) {
                user.setInviteBy(inviteUser.getId());
                user.setOrgCode(inviteUser.getOrgCode());
            }
        } else {
            Long defaultInviteUserId = NumberUtils.createLong(sysConfService.getBizConfig("defaultInviteUserId"));
            UserPO defaultInviteUser = getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getId,
                    defaultInviteUserId).last(
                    "LIMIT 1"));
            user.setInviteBy(defaultInviteUserId);
            user.setOrgCode(defaultInviteUser.getOrgCode());
        }
        user.setBizId(IdHelper.generateId());
        save(user);

        RoleUserPO roleUser = new RoleUserPO();
        roleUser.setUserId(user.getId());
        roleUser.setRoleId(RoleTypeEnum.ROLE_USER.getRoleId());
        roleUser.setCreateTime(new Date());
        roleUser.setUpdateTime(new Date());
        roleUserMapper.insert(roleUser);

        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(getById(user.getId()));
        userVO.setToken(generateTokenByUserPO(user));
        userVO.setRole(RoleTypeEnum.ROLE_USER.getCode());

        user.setUpdateBy(user.getId());
        updateById(user);

        rabbitTemplate.convertAndSend(appConfig.getRabbitmqConfig().getUser().getExchange(), null, user);

        return userVO;
    }

    @Override
    public UserVO createUserByB(CreateOrgUserParam createOrgUserParam, UserPO currentLoginUser) {

        if (StringUtils.isBlank(createOrgUserParam.getPhone().trim()) ||
                StringUtils.isBlank(createOrgUserParam.getPsw().trim())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone and password required");
        }
        UserPO user =
                getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, createOrgUserParam.getPhone())
                        .last("LIMIT 1"));

        if (Objects.nonNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone duplicate");
        }
        if (StringUtils.isBlank(createOrgUserParam.getUserName())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "name required");
        }
        if (Objects.isNull(createOrgUserParam.getRoleTypeEnum())) {
            //默认员工账户
            createOrgUserParam.setRoleTypeEnum(RoleTypeEnum.ROLE_OPERATOR);
        }
        if (StringUtils.isNotBlank(createOrgUserParam.getOrgCode().trim())) {
            OrgPO specifiedOrg = orgService.getOne(
                    Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, createOrgUserParam.getOrgCode().trim())
                            .last("LIMIT 1"));
            if (Objects.isNull(specifiedOrg)) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding org");
            }
            OrgPO currentAdminOrg = orgService.getOne(
                    Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, currentLoginUser.getOrgCode()).last("LIMIT 1"));
            //判断当前管理员是否对该级组织有管理权限
            if (!specifiedOrg.getOrgCodePath().startsWith(currentAdminOrg.getOrgCodePath())) {
                throw new BizException(ErrorCode.UNAUTHORIZED,
                        "no auth to manage " + createOrgUserParam.getOrgCode().trim());
            }
        } else {
            createOrgUserParam.setOrgCode(currentLoginUser.getOrgCode());
        }
        createOrgUserParam.setCreateBy(currentLoginUser.getId());

        user = new UserPO();
        user.setPhone(createOrgUserParam.getPhone());
        user.setPsw(createOrgUserParam.getPsw());
        user.setInviteBy(createOrgUserParam.getCreateBy());
        user.setBizId(IdHelper.generateId());
        user.setUpdateBy(createOrgUserParam.getCreateBy());
        user.setOrgCode(createOrgUserParam.getOrgCode());
        user.setUserName(createOrgUserParam.getUserName());
        user.setUserStatus(UserStatusEnum.TRADABLE.getCode());
        save(user);

        RoleUserPO roleUser = new RoleUserPO();
        roleUser.setUserId(user.getId());
        roleUser.setRoleId(createOrgUserParam.getRoleTypeEnum().getRoleId());
        roleUserMapper.insert(roleUser);

        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(getById(user.getId()));
        userVO.setToken(generateTokenByUserPO(user));
        userVO.setRole(createOrgUserParam.getRoleTypeEnum().getCode());

        rabbitTemplate.convertAndSend(appConfig.getRabbitmqConfig().getUser().getExchange(), null, user);

        return userVO;
    }

    @Override
    public UserVO login(LoginParam loginParam) {
        UserPO user =
                getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, loginParam.getPhone()).last("LIMIT 1"));

        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone password missmatch");
        }

        if (!user.getPsw().equals(loginParam.getPsw())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone password missmatch");
        }

        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(user);
        userVO.setRole(getRole(user).getCode());
        userVO.setToken(generateTokenByUserPO(user));
        return userVO;
    }

    @Override
    public RoleTypeEnum getRole(UserPO user) {
        RoleUserPO roleUser =
                roleUserMapper.selectOne(Wrappers.<RoleUserPO>lambdaQuery().eq(RoleUserPO::getUserId, user.getId()),
                        false);
        if (Objects.nonNull(roleUser)) {
            RolePO role = roleMapper.selectById(roleUser.getRoleId());
            if (Objects.nonNull(role)) {
                return RoleTypeEnum.getByCode(role.getRoleCode());
            }
        }
        return RoleTypeEnum.ROLE_USER;
    }

    @Override
    public void updateUser(UpdateUserParam updateUserParam, UserPO currentLoginUser) {
        if (Objects.isNull(updateUserParam.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userId required");
        }
        UserPO updateUser = getById(updateUserParam.getUserId());
        if (Objects.isNull(updateUser)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }

        if (StringUtils.isNotBlank(updateUserParam.getOrgCode())) {
            OrgPO targetOrg = orgService.getOne(Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode,
                    updateUserParam.getOrgCode().trim()).last("LIMIT 1"));
            if (Objects.isNull(targetOrg)) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding org");
            }
            OrgPO updateUserOrg = orgService.getOne(Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode,
                    updateUser.getOrgCode()).last("LIMIT 1"));
            OrgPO currentAdminOrg = orgService.getOne(
                    Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, currentLoginUser.getOrgCode()).last("LIMIT 1"));
            //对用户当前组织有管理权限
            if (!updateUserOrg.getOrgCodePath().startsWith(currentAdminOrg.getOrgCodePath())) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "no auth to manage " + updateUserOrg.getCode());
            }
            //对目标组织有管理权限
            if (!targetOrg.getOrgCodePath().startsWith(currentAdminOrg.getOrgCodePath())) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "no auth to manage " + targetOrg.getCode());
            }
            updateUser.setOrgCode(updateUserParam.getOrgCode().trim());
        }

        if (Objects.nonNull(updateUserParam.getUserStatus())) {
            updateUser.setUserStatus(UserStatusEnum.getByCode(updateUserParam.getUserStatus()).getCode());
        }
        if (StringUtils.isNotBlank(updateUserParam.getUserName())) {
            updateUser.setUserName(updateUserParam.getUserName());
        }
        if (StringUtils.isNotBlank(updateUserParam.getAddress())) {
            updateUser.setAddress(updateUserParam.getAddress());
        }
        if (StringUtils.isNotBlank(updateUserParam.getBankNo())) {
            updateUser.setBankNo(updateUserParam.getBankNo());
        }
        if (StringUtils.isNotBlank(updateUserParam.getInviteBy())) {
            UserPO inviteUser = getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getBizId,
                    updateUserParam.getInviteBy().trim()).last(
                    "LIMIT 1"));
            if (Objects.nonNull(inviteUser)) {
                updateUser.setInviteBy(inviteUser.getId());
            } else {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding inviteUser");
            }
        }
        updateById(updateUser);
        if (Objects.nonNull(updateUserParam.getRoleTypeEnum())) {
            RoleUserPO roleUserPO =
                    roleUserMapper.selectOne(Wrappers.<RoleUserPO>lambdaQuery().eq(RoleUserPO::getUserId,
                            updateUserParam.getUserId()).last("LIMIT 1"));
            if (Objects.nonNull(roleUserPO)) {
                roleUserPO.setRoleId(updateUserParam.getRoleTypeEnum().getRoleId());
                roleUserMapper.updateById(roleUserPO);
            }
        }
    }

    @Override
    public void setUserAssets4c(UserVO userVO) {
        try {
            OrderUserBalance userBalance = orderService.getOrderBalance(userVO);
            AiStockUserBalance aiStockUserBalance = aiStockService.getOrderBalance(userVO);
            userVO.setUserBalance(userBalance);
            userVO.setAiStockUserBalance(aiStockUserBalance);
            userVO.setTotalAssets(userVO.getAvailableAssets()
                    .add(new BigDecimal(userBalance.getCloseOutLine()))
                    .add(new BigDecimal(userBalance.getBalanceProfit()))
                    .add(BigDecimal.valueOf(aiStockUserBalance.getRealizedPL()))
                    .add(BigDecimal.valueOf(aiStockUserBalance.getUnrealizedPL())));
            userVO.setNetAssets(userVO.getAvailableAssets()
                    .add(new BigDecimal(userBalance.getCloseOutLine()))
                    .subtract(new BigDecimal(userBalance.getFreezeMargin()))
                    .add(BigDecimal.valueOf(aiStockUserBalance.getRealizedPL())
                            .subtract(BigDecimal.valueOf(aiStockUserBalance.getMargin()))));
        }catch (Exception e){
            log.error("setUserAssets4c error",e);
        }
    }

    @Override
    public void setUserAssets4b(UserVO userVO) {
        try {
            OrderUserBalance userBalance = orderService.getOrderBalance(userVO);
            userVO.setUserBalance(userBalance);
            userVO.setTotalAssets(userVO.getAvailableAssets()
                    .add(new BigDecimal(userBalance.getCloseOutLine()))
                    .add(new BigDecimal(userBalance.getBalanceProfit())));
            userVO.setNetAssets(userVO.getAvailableAssets()
                    .add(new BigDecimal(userBalance.getCloseOutLine()))
                    .subtract(new BigDecimal(userBalance.getFreezeMargin())));
        }catch (Exception e){
            log.error("setUserAssets4b error",e);
        }
    }

    @Override
    public IPage<UserPO> queryPageUser(UserQueryParam userQueryParam, UserPO currentLoginUser) {

        Page<UserPO> page = new Page<>(userQueryParam.getPageNo(), userQueryParam.getPageSize());
        page.addOrder(OrderItem.desc("update_time"), OrderItem.desc("create_time"));
        LambdaQueryWrapper<UserPO> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(userQueryParam.getUserId())) {
            lambdaQueryWrapper.eq(UserPO::getId, userQueryParam.getUserId());
        }
        if (StringUtils.isNotBlank(userQueryParam.getUserBizId())) {
            lambdaQueryWrapper.eq(UserPO::getBizId, userQueryParam.getUserBizId());
        }
        if (StringUtils.isNotBlank(userQueryParam.getPhone())) {
            lambdaQueryWrapper.eq(UserPO::getPhone, userQueryParam.getPhone());
        }
        if (StringUtils.isNotBlank(userQueryParam.getOrgCode())) {
            OrgPO queryOrg = orgService.getOne(Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode,
                    userQueryParam.getOrgCode().trim()).last("LIMIT 1"));
            if (Objects.isNull(queryOrg)) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding org");
            }
            OrgPO currentAdminOrg = orgService.getOne(
                    Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, currentLoginUser.getOrgCode()).last("LIMIT 1"));
            //对查询组织有管理权限
            if (!queryOrg.getOrgCodePath().startsWith(currentAdminOrg.getOrgCodePath())) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "no auth to manage " + queryOrg.getCode());
            }
            lambdaQueryWrapper.eq(UserPO::getOrgCode, userQueryParam.getOrgCode().trim());
        } else {
            lambdaQueryWrapper.eq(UserPO::getOrgCode, currentLoginUser.getOrgCode());
        }
        if (Objects.nonNull(userQueryParam.getInviteBy())) {
            lambdaQueryWrapper.eq(UserPO::getInviteBy, userQueryParam.getInviteBy());
        }
        return page(page, lambdaQueryWrapper);
    }

    @Override
    public List<OperatorVO> getOperatorList(RoleTypeEnum roleTypeEnum, UserPO currentLoginUser) {
        OrgPO currentAdminOrg = orgService.getOne(
                Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, currentLoginUser.getOrgCode()).last("LIMIT 1"));
        return userMapper.selectOperatorOfOrg(currentAdminOrg.getOrgCodePath(), roleTypeEnum.getCode());
    }

    @Override
    public Boolean isManageable(UserPO currentAdmin, Long userId) {

        if (Objects.nonNull(userId) && Objects.nonNull(currentAdmin)) {
            OrgPO currentAdminOrg = orgService.getOne(
                    Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, currentAdmin.getOrgCode()).last("LIMIT 1"));
            UserPO user = getById(userId);
            if (Objects.nonNull(user) && Objects.nonNull(currentAdminOrg)) {
                OrgPO userOrg = orgService.getOne(
                        Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, user.getOrgCode()).last("LIMIT 1"));
                if (Objects.nonNull(userOrg)) {
                    return userOrg.getOrgCodePath().startsWith(currentAdminOrg.getOrgCodePath());
                }
            }
        }
        return false;
    }


    @Override
    public UserPO getByTxAddress(String txAddress) {
        return getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getTxAddress, txAddress).last("LIMIT 1"));
    }

    @Override
    public Long getUserIdByTxAddress(String txAddress) {
        UserPO userPO = getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getTxAddress, txAddress).last("LIMIT 1"));
        return Objects.isNull(userPO) ? null : userPO.getId();
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) {
        UserPO user = getById(userId);
        if (null == user)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");

        UserPO userPO = new UserPO();
        userPO.setId(userId);
        userPO.setAvailableAssets(user.getAvailableAssets().add(amount));
        updateById(userPO);
    }

    @Override
    public UserVO fastLogin(LoginCommonParam param) {
        checkVerifyCode(param);
        UserPO user =
                getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, param.getAccount()).last("LIMIT 1"));
        if (Objects.isNull(user)) {
            //创建账号
            user = createUser(param);
        }
        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(getById(user.getId()));
        userVO.setToken(generateTokenByUserPO(user));
        userVO.setRole(RoleTypeEnum.ROLE_USER.getCode());
        return userVO;
    }

    @Override
    public BigDecimal getBalance(Long id) {
        UserPO user = getById(id);
        if (null == user) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        return user.getAvailableAssets();
    }

    @Override
    public void reduceBalance(Long userId, BigDecimal preReduceAmount) {
        UserPO user = getById(userId);
        if (null == user) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        UserPO newUser = new UserPO();
        newUser.setId(userId);
        newUser.setAvailableAssets(user.getAvailableAssets().subtract(preReduceAmount));
        updateById(newUser);
    }

    @Override
    public UserVO scanedUser(ScanedUserParam scanedUserParam) {
        UserPO user = getByTxAddress(scanedUserParam.getAddress());
        if (null == user) {
            user = new UserPO();
            user.setTxAddress(scanedUserParam.getAddress());
            user.setType(1);
            user.setPhone(scanedUserParam.getAddress());
            user.setAvailableAssets(BigDecimal.ZERO);
            user.setCreateTime(new Date());
            save(user);
            RoleUserPO roleUser = new RoleUserPO();
            roleUser.setUserId(user.getId());
            roleUser.setRoleId(RoleTypeEnum.ROLE_USER.getRoleId());
            roleUser.setCreateTime(new Date());
            roleUser.setUpdateTime(new Date());
            roleUserMapper.insert(roleUser);
        }

        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(user);
        userVO.setToken(generateTokenByUserPO(user));
        userVO.setRole(RoleTypeEnum.ROLE_USER.getCode());

        rabbitTemplate.convertAndSend(appConfig.getRabbitmqConfig().getUser().getExchange(), null, user);
        return userVO;
    }

    @Override
    public void resetPwd(ResetPwdParam resetPwdParam) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(Constants.VERIFY_CODE_PREFIX + resetPwdParam.getPhone().trim()))) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "validate code expired");
        }
        if (!redisTemplate.opsForValue().get(Constants.VERIFY_CODE_PREFIX + resetPwdParam.getPhone().trim()).toString().equalsIgnoreCase(resetPwdParam.getVerifyCode())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "validate code wrong");
        }

        UserPO userPO = getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, resetPwdParam.getPhone()).last("LIMIT 1"));
        if (Objects.isNull(userPO)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        UserPO user = new UserPO();
        user.setId(userPO.getId());
        user.setPsw(resetPwdParam.getPsw());
        updateById(user);
    }

    @Override
    public void updatePwd(Long id, UpdatePwdParam updatePwdParam) {
        UserPO user = getById(id);
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        if (!user.getPsw().equals(updatePwdParam.getOldPwd()))
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "old password wrong");

        UserPO userPO = new UserPO();
        userPO.setId(id);
        userPO.setPsw(updatePwdParam.getNewPwd());
        updateById(userPO);
    }

    @Override
    public UserVO getUserById(Long id) {
        UserPO userPO = this.getById(id);
        if (Objects.isNull(userPO)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(userPO);
        userVO.setRole(getRole(userPO).getCode());
        return userVO;
    }

    @Override
    public void updateUserInfo(UpdateUserParam param, Long id) {
        UserPO user = getById(id);
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.USER_NOT_EXIST_ERROR, "no corresponding user");
        }

        if (param.getNickName() != null && param.getNickName().trim().length() > 64) {
            throw new BizException(ErrorCode.ILLEGITMATE_NICKNAME_ERROR);
        }

        if (param.getSynopsis() != null && param.getSynopsis().trim().length() > 150)
            throw new BizException(ErrorCode.SYNOPSIS_TOO_LONG_ERROR);

        UserPO userPO = new UserPO();
        userPO.setId(id);
        userPO.setNickName(param.getNickName() != null ? param.getNickName().trim() : null);
        userPO.setSynopsis(param.getSynopsis() != null ? param.getSynopsis().trim() : null);
        userPO.setLogo(param.getLogo());
        updateById(userPO);
    }

    @Override
    public String getTrcAddress(Long id) {
        log.info("【获取TRC地址】id={}", id);
        UserPO user = getById(id);
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.USER_NOT_EXIST_ERROR, "no corresponding user");
        }
        log.info("用户{}的TRC地址={}", id, user.getTrcAddress());
        return user.getTrcAddress();
    }

    @Override
    public void updateTrcAddress(UpdateTrcAddressParam param, Long id) {
        log.info("【更新用户的TRC地址】id={}", id);
        UserPO user = getById(id);
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.USER_NOT_EXIST_ERROR, "no corresponding user");
        }
        UserPO userPO = new UserPO();
        userPO.setId(id);
        userPO.setTrcAddress(param.getTrcAddress());
        updateById(userPO);
        log.info("-->更新TRC地址成功!");
    }

    private UserPO createUser(LoginCommonParam param) {
        UserPO user = new UserPO();
        user.setPhone(param.getAccount());
        user.setCreateTime(new Date());
//        user.setPsw(createUserByCParam.getPsw());

        Long defaultInviteUserId = NumberUtils.createLong(sysConfService.getBizConfig("defaultInviteUserId"));
        UserPO defaultInviteUser = getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getId,
                defaultInviteUserId).last(
                "LIMIT 1"));
        user.setInviteBy(defaultInviteUserId);
        user.setOrgCode(defaultInviteUser.getOrgCode());
        user.setBizId(IdHelper.generateId());
        save(user);

        RoleUserPO roleUser = new RoleUserPO();
        roleUser.setUserId(user.getId());
        roleUser.setRoleId(RoleTypeEnum.ROLE_USER.getRoleId());
        roleUser.setCreateTime(new Date());
        roleUser.setUpdateTime(new Date());
        roleUserMapper.insert(roleUser);

        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(getById(user.getId()));
        userVO.setToken(generateTokenByUserPO(user));
        userVO.setRole(RoleTypeEnum.ROLE_USER.getCode());

        rabbitTemplate.convertAndSend(appConfig.getRabbitmqConfig().getUser().getExchange(), null, user);

        return user;
    }

    private void checkVerifyCode(LoginCommonParam param) {
        Object code = redisTemplate.opsForValue().get(Constants.VERIFY_CODE_PREFIX + param.getAccount());
        if (Objects.isNull(code) || !code.toString().equals(param.getVerifyCode())) {
            throw new BizException(ErrorCode.ILLEGAL_STATE, "verify code not match");
        }
        redisTemplate.delete(Constants.VERIFY_CODE_PREFIX + param.getAccount());
    }
}




