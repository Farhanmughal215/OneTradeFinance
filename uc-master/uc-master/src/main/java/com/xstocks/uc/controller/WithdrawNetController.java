package com.xstocks.uc.controller;

import com.xstocks.uc.pojo.WithdrawNet;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.WithdrawNetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 提现网络表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-01-15
 */
@RestController
@RequestMapping("/c/net")
public class WithdrawNetController {

    @Autowired
    private WithdrawNetService withdrawNetService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "withdraw/list")
    public BaseResp<List<WithdrawNet>> audit(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                     UserPO currentLoginUser) {
        return BaseResp.success(withdrawNetService.list());
    }
}
