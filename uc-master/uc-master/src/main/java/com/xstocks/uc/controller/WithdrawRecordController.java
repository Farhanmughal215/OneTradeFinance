package com.xstocks.uc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.WithdrawRecordApplyParam;
import com.xstocks.uc.pojo.param.WithdrawRecordAuditParam;
import com.xstocks.uc.pojo.param.WithdrawRecordQueryParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.po.WithdrawRecordPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.WithdrawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * <p>
 * 提现记录表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-01-08
 */
@RestController
public class WithdrawRecordController {

    @Autowired
    private WithdrawRecordService withdrawRecordService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/withdraw/l")
    public BaseResp<IPage<WithdrawRecordPO>> withdrawRecordList(@RequestBody WithdrawRecordQueryParam recordQueryParam,
                                                                @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                                        UserPO currentLoginUser) {
        recordQueryParam.setUserId(currentLoginUser.getId());
        return BaseResp.success(withdrawRecordService.queryPageUserWithDraw(recordQueryParam));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/withdraw/apply")
    public BaseResp<Boolean> apply(@Validated @RequestBody WithdrawRecordApplyParam applyParam,
                                   @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                           UserPO currentLoginUser) {
        if (applyParam.getAmount() == null)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "amount required");

        if (applyParam.getHandleFee() == null)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "handle fee required");

        if (applyParam.getAddress() == null)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "address required");

        if (applyParam.getNetwork() == null)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "network required");

        if (applyParam.getCurrency() == null)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "currency required");

        if (applyParam.getAmount().compareTo(BigDecimal.valueOf(2)) < 0)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "amount must greater than 2");


        applyParam.setUserId(currentLoginUser.getId());
        withdrawRecordService.apply(applyParam);
        return BaseResp.success(true);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')||hasRole('ROLE_OPERATOR')")
    @PostMapping(value = "/b/withdraw/audit")

    public BaseResp<Boolean> audit(@Validated @RequestBody WithdrawRecordAuditParam auditParam,
                                   @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                   UserPO currentLoginUser) {
        auditParam.setUserId(currentLoginUser.getId());
        withdrawRecordService.audit(auditParam);
        return BaseResp.success(true);
    }
}
