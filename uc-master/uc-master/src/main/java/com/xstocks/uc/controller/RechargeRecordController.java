package com.xstocks.uc.controller;

import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.param.RechargeParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 充值记录表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-01-09
 */
@RestController
public class RechargeRecordController {

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/s/recharge/c",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResp<String> recharge(@RequestBody RechargeParam rechargeParam,
                                     @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                             UserPO currentLoginUser) {
        return BaseResp.success(rechargeRecordService.recharge(rechargeParam));
    }
}
