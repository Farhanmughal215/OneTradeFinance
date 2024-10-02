package com.xstocks.uc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.config.security.UserDetail;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import com.xstocks.uc.pojo.param.*;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.OperatorVO;
import com.xstocks.uc.pojo.vo.RegisterValidateResultVO;
import com.xstocks.uc.pojo.vo.UserVO;
import com.xstocks.uc.service.EmailService;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.service.remote.TelesignService;
import com.xstocks.uc.utils.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.xstocks.uc.pojo.constants.CommonConstant.EMAIL_PATTERN;
import static com.xstocks.uc.pojo.constants.CommonConstant.PHONE_PATTERN;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TelesignService telesignService;

    @Autowired
    private EmailService emailService;

    @RateLimit
    @PostMapping(value = "/a/u/l")
    public BaseResp<UserVO> login(@RequestBody LoginParam loginParam) {
        if (StringUtils.isBlank(loginParam.getPhone()) || StringUtils.isBlank(loginParam.getPsw())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone and password required");
        }

        UserVO userVO = userService.login(loginParam);
        userService.setUserAssets4c(userVO);
        return BaseResp.success(userVO);
    }

    @RateLimit
    @PostMapping(value = "/c/u/getUser")
    public BaseResp<UserVO> getUserById(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                UserPO currentLoginUser) {
        UserVO userVO = userService.getUserById(currentLoginUser.getId());
        return BaseResp.success(userVO);
    }


    @RateLimit
    @PostMapping(value = "/a/u/c")
    public BaseResp<UserVO> register(@RequestBody CreateUserByCParam createUserByCParam) {
        UserVO userVO = userService.createUserByC(createUserByCParam);
        userService.setUserAssets4c(userVO);
        return BaseResp.success(userVO);
    }

    @RateLimit
    @PostMapping(value = "/a/u/scaned")
    public BaseResp<UserVO> scanedUser(@RequestBody ScanedUserParam scanedUserParam) {
        if (StringUtils.isBlank(scanedUserParam.getNet()) || StringUtils.isBlank(scanedUserParam.getAddress())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "net and address required");
        }

        return BaseResp.success(userService.scanedUser(scanedUserParam));
    }

    @RateLimit
    @PostMapping(value = "/c/u/updatePwd")
    public BaseResp<Boolean> updatePwd(@RequestBody UpdatePwdParam updatePwdParam,
                                       @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                               UserPO currentLoginUser) {
        if (StringUtils.isBlank(updatePwdParam.getOldPwd())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "old password required");
        }
        if (StringUtils.isBlank(updatePwdParam.getNewPwd())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "new password required");
        }

        userService.updatePwd(currentLoginUser.getId(), updatePwdParam);

        return BaseResp.success(Boolean.TRUE);
    }

    @RateLimit
    @PostMapping(value = "/a/u/resetPwd")
    public BaseResp<Boolean> resetPwd(@RequestBody ResetPwdParam resetPwdParam) {
        if (StringUtils.isBlank(resetPwdParam.getPhone()))
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone required");

        if (StringUtils.isBlank(resetPwdParam.getPsw()))
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "password required");

        if (StringUtils.isBlank(resetPwdParam.getVerifyCode()))
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "verify code required");

        userService.resetPwd(resetPwdParam);

        return BaseResp.success(Boolean.TRUE);
    }

/*
    @RateLimit
    @PostMapping(value = "a/u/fastLogin")
    public BaseResp<UserVO> fastLogin(@RequestBody LoginCommonParam param) {
        if (StringUtils.isEmpty(param.getAccount())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "account required");
        }
        if (StringUtils.isEmpty(param.getVerifyCode())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "verify code required");
        }

        if (PHONE_PATTERN.matcher(param.getAccount().trim()).matches()
                || EMAIL_PATTERN.matcher(param.getAccount().trim()).matches()) {
            //校验手机号验证码
            return BaseResp.success(userService.fastLogin(param));
        }
        throw new BizException(ErrorCode.UNSUPPORTED_METHOD, "account must be phone or email");
    }

    @RateLimit
    @PostMapping(value = "a/u/sendCode")
    public BaseResp<Boolean> sendCode(@RequestBody LoginCommonParam param) {
        if (StringUtils.isEmpty(param.getAccount())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "account required");
        }

        Matcher phoneMatcher = PHONE_PATTERN.matcher(param.getAccount().trim());
        if (phoneMatcher.matches()) {
            return BaseResp.success(telesignService.sendSmsVerify(param.getAccount().trim()));
        }
        Matcher emailMatcher = EMAIL_PATTERN.matcher(param.getAccount().trim());
        if (emailMatcher.matches()) {
            return BaseResp.success(emailService.sendEmailVerify(param.getAccount().trim()));
        }
        throw new BizException(ErrorCode.ILLEGAL_REQUEST, "account must be phone or email");
    }
 */

    @RateLimit
    @PostMapping(value = "a/u/v")
    public BaseResp<RegisterValidateResultVO> validate(@RequestBody CreateUserByCParam createUserByCParam, HttpServletRequest request) {

        if (StringUtils.isBlank(createUserByCParam.getPhone())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone or email required");
        }
        UserPO user =
                userService.getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, createUserByCParam.getPhone())
                        .last("LIMIT 1"));

        if (Objects.nonNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "phone or email duplicate");
        }

        RegisterValidateResultVO result = new RegisterValidateResultVO();
        Matcher phoneMatcher = PHONE_PATTERN.matcher(createUserByCParam.getPhone().trim());
        if (phoneMatcher.matches()) {
            result.setValid(telesignService.sendSmsVerify(createUserByCParam.getPhone().trim()));
            result.setValidateType(0);
            return BaseResp.success(result);
        }
        Matcher emailMatcher = EMAIL_PATTERN.matcher(createUserByCParam.getPhone().trim());
        if (emailMatcher.matches()) {
            result.setValid(emailService.sendEmailVerify(createUserByCParam.getPhone().trim()));
            result.setValidateType(1);
            return BaseResp.success(result);
        }
        throw new BizException(ErrorCode.ILLEGAL_REQUEST, "input illegal,not a phone or email");
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/b/u/c")
    public BaseResp<UserVO> createOperatorByAdmin(@RequestBody CreateOrgUserParam createOrgUserParam,
                                                  @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                          UserPO currentLoginUser) {
        UserVO userVO = userService.createUserByB(createOrgUserParam, currentLoginUser);
        userService.setUserAssets4c(userVO);
        return BaseResp.success(userVO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT') || hasRole('ROLE_USER') || hasRole('ROLE_OPERATOR')")
    @PostMapping(value = {"/c/u/r", "/b/u/r"})
    public BaseResp<UserVO> read() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        UserPO user = userDetail.getUserPOEntity();
        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(user);
        userVO.setRole(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).findAny().orElse(
                RoleTypeEnum.ROLE_USER.getCode()));
        userService.setUserAssets4c(userVO);
        return BaseResp.success(userVO);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM')")
    @PostMapping(value = "/s/u/r")
    public BaseResp<UserVO> read(@RequestBody UserQueryParam userQueryParam) {
        UserPO user;
        if (Objects.nonNull(userQueryParam.getUserId())) {
            user = userService.getById(userQueryParam.getUserId());
        } else if (StringUtils.isNotBlank(userQueryParam.getUserBizId())) {
            user = userService.getOne(
                    Wrappers.<UserPO>lambdaQuery().eq(UserPO::getBizId, userQueryParam.getUserBizId()).last("LIMIT 1"));
        } else if (StringUtils.isNotBlank(userQueryParam.getPhone())) {
            user = userService.getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getPhone, userQueryParam.getPhone())
                    .last("LIMIT 1"));
        } else {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "[userId userBizId phone] one of param required");
        }
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(user);
        userVO.setRole(userService.getRole(user).getCode());
        userService.setUserAssets4b(userVO);
        return BaseResp.success(userVO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')||hasRole('ROLE_OPERATOR')")
    @PostMapping(value = "/b/u/l")
    public BaseResp<IPage<UserPO>> listUser(@RequestBody UserQueryParam userQueryParam,
                                            @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                    UserPO currentLoginUser) {
        return BaseResp.success(userService.queryPageUser(userQueryParam, currentLoginUser));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')||hasRole('ROLE_OPERATOR')")
    @PostMapping(value = "/b/ou/l")
    public BaseResp<List<OperatorVO>> listOperator(@RequestBody UserQueryParam userQueryParam,
                                                   @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                           UserPO currentLoginUser) {
        if (Objects.isNull(userQueryParam.getRole())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "role required");
        }
        return BaseResp.success(userService.getOperatorList(userQueryParam.getRole(), currentLoginUser));
    }

    @RateLimit(rate = 20)
    @GetMapping("/s/u/getBalance/{id}")
    public BaseResp<BigDecimal> getBalance(@PathVariable("id") Long id) {
        return BaseResp.success(userService.getBalance(id));
    }

    @RateLimit(rate = 20)
    @PostMapping("/s/u/getUser")
    public BaseResp<UserPO> getUser(@RequestBody ServerQueryParam param) {
        if (null == param || param.getAddress() == null) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "address required");
        }
        return BaseResp.success(userService.getByTxAddress(param.getAddress()));
    }

    @RateLimit(rate = 20)
    @PostMapping(value = "/s/u/getById/{userId}")
    public BaseResp<UserPO> getTraderInfo(@PathVariable("userId") Long userId) {
        return BaseResp.success(userService.getById(userId));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "b/u/u")
    public BaseResp<Boolean> update(@RequestBody UpdateUserParam updateUserParam,
                                    @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                            UserPO currentLoginUser) {
        userService.updateUser(updateUserParam, currentLoginUser);
        return BaseResp.success(Boolean.TRUE);
    }


    /**
     * 更新交易员基本信息
     *
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/user/updateUser")
    public BaseResp<Boolean> updateUser(@RequestBody UpdateUserParam param,
                                        @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                UserPO currentLoginUser) {
        userService.updateUserInfo(param, currentLoginUser.getId());
        return BaseResp.success();
    }

    /**
     * 根据Authorization获取当前用户的TRC地址
     * @param currentLoginUser
     * @return
     */
    @RateLimit
    @PostMapping(value = "/c/u/getTrcAddress")
    public BaseResp<String> getTrcAddress(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                        UserPO currentLoginUser) {
        return BaseResp.success(userService.getTrcAddress(currentLoginUser.getId()));
    }

    @RateLimit
    @PostMapping(value = "/c/u/updateTrcAddress")
    public BaseResp<Boolean> updateTrcAddress(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER) UserPO currentLoginUser,
                                             @Valid @RequestBody UpdateTrcAddressParam param) {
        userService.updateTrcAddress(param, currentLoginUser.getId());
        return BaseResp.success();
    }
}
