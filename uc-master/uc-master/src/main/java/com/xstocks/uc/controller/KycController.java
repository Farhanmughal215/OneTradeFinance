package com.xstocks.uc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.ApprovalKycParam;
import com.xstocks.uc.pojo.param.CreateKycParam;
import com.xstocks.uc.pojo.param.UserKycQueryParam;
import com.xstocks.uc.pojo.param.UserQueryParam;
import com.xstocks.uc.pojo.po.UserKycPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.UserKycVo;
import com.xstocks.uc.service.UserKycService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kevin
 * @date 2024/1/4 15:23
 * @apiNote kyc
 */
@Slf4j
@RestController
public class KycController {

    @Autowired
    private UserKycService userKycService;


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/kyc/create")
    public BaseResp<Boolean> createKyc(@RequestBody CreateKycParam kycParam,
                                       @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                       UserPO currentLoginUser) {
        kycParam.setUserId(currentLoginUser.getId());
        if (StringUtils.isBlank(kycParam.getUserName())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "user name required");
        }
        if (StringUtils.isBlank(kycParam.getAddress())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "address required");
        }
        if (null == kycParam.getCardImages() || kycParam.getCardImages().size() == 0) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "image required");
        }
        userKycService.userKyc(kycParam);
        return BaseResp.success(Boolean.TRUE);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/getKyc")
    public BaseResp<UserKycVo> getKyc(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                      UserPO currentLoginUser) {
        return BaseResp.success(userKycService.getKyc(currentLoginUser.getId()));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')||hasRole('ROLE_OPERATOR')")
    @PostMapping(value = "/b/kyc/l")
    public BaseResp<IPage<UserKycPO>> listUser(@RequestBody UserKycQueryParam kycQueryParam,
                                            @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                            UserPO currentLoginUser) {
        kycQueryParam.setUserId(currentLoginUser.getId());
        return BaseResp.success(userKycService.queryPageUserKyc(kycQueryParam));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/b/kyc/{id}")
    public BaseResp<UserKycPO> kyc(@PathVariable Long id) {
        return BaseResp.success(userKycService.getById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/b/kyc/approval")
    public BaseResp<Boolean> approvalKyc(@RequestBody ApprovalKycParam approvalKycParam,
                                         @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                         UserPO currentLoginUser) {
        if (null == approvalKycParam.getState()) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "state required");
        }
        if (null == approvalKycParam.getId()) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "kyc record id required");
        }

        approvalKycParam.setUpdateBy(currentLoginUser.getId());
        userKycService.approvalKyc(approvalKycParam);
        return BaseResp.success(true);
    }
}
