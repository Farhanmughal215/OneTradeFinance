package com.xstocks.uc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import com.xstocks.uc.pojo.param.*;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.OperatorVO;
import com.xstocks.uc.pojo.vo.UserVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dongjunfu
 * @description 针对表【user(user)】的数据库操作Service
 * @createDate 2023-10-28 14:38:31
 */
public interface UserService extends IService<UserPO> {
    String generateTokenByUserPO(UserPO user);

    UserVO createUserByC(CreateUserByCParam createUserByCParam);

    UserVO createUserByB(CreateOrgUserParam createOrgUserParam, UserPO currentLoginUser);

    UserVO login(LoginParam loginParam);

    RoleTypeEnum getRole(UserPO user);

    void updateUser(UpdateUserParam updateUserParam, UserPO currentLoginUser);

    void setUserAssets4c(UserVO userVO);

    void setUserAssets4b(UserVO userVO);

    IPage<UserPO> queryPageUser(UserQueryParam userQueryParam, UserPO currentLoginUser);

    List<OperatorVO> getOperatorList(RoleTypeEnum roleTypeEnum,UserPO currentLoginUser);

    Boolean isManageable(UserPO currentAdmin,Long userId);

    UserPO getByTxAddress(String txAddress);

    Long getUserIdByTxAddress(String txAddress);

    void recharge(Long userId, BigDecimal amount);

    UserVO fastLogin(LoginCommonParam param);

    BigDecimal getBalance(Long id);

    void reduceBalance(Long userId, BigDecimal preReduceAmount);

    UserVO scanedUser(ScanedUserParam scanedUserParam);

    void resetPwd(ResetPwdParam resetPwdParam);

    void updatePwd(Long id, UpdatePwdParam updatePwdParam);

    UserVO getUserById(Long id);

    void updateUserInfo(UpdateUserParam param, Long id);

    String getTrcAddress(Long id);

    void updateTrcAddress(UpdateTrcAddressParam param, Long id);
}
