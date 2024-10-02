package com.xstocks.uc.controller;

import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.SysConfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName ConfigController
 * @Description TODO
 * @Author firtuss
 * @Date 2023/9/7 16:58
 **/
@Slf4j
@RestController
public class ConfigController {

    @Autowired
    private SysConfService sysConfService;

    @RateLimit
    @PostMapping(value = "/a/c/l")
    public BaseResp<Map<String, String>> listAllConfig() {
        return BaseResp.success(sysConfService.getAllBizConfig());
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/b/c/u")
    public BaseResp<Boolean> updateConfig(@RequestBody Map<String, String> config,@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
    UserPO currentLoginUser) {
        sysConfService.updateBizConfig(config,currentLoginUser);
        return BaseResp.success(true);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/b/c/d")
    public BaseResp<Boolean> deleteConfig(@RequestBody Set<String> configKeys,@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
    UserPO currentLoginUser) {
        sysConfService.delBizConfig(configKeys);
        return BaseResp.success(true);
    }
}
