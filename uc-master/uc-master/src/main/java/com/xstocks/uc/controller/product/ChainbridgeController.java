package com.xstocks.uc.controller.product;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class ChainbridgeController extends BaseController {

    private final static String PRODUCT_TAG = "chainbridge";

    @RateLimit
    @PostMapping("/a/chainbridge/**")
    public BaseResp foward2a(HttpServletRequest request, @RequestBody(required = false) ObjectNode jsonObject) {
        return getBaseResp(getUrlPath(request), jsonObject, null, getProductService().getAapi());
    }


    @RateLimit
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/c/chainbridge/**")
    public BaseResp foward2c(HttpServletRequest request, @RequestBody(required = false) ObjectNode jsonObject,
                             @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                             UserPO currentLoginUser) {

        return getBaseResp(getUrlPath(request), jsonObject, currentLoginUser, getProductService().getCapi());
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_SYSTEM') || hasRole('ROLE_ROOT')")
    @PostMapping("/b/chainbridge/**")
    public BaseResp foward2b(HttpServletRequest request, @RequestBody(required = false) ObjectNode jsonObject,
                             @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                             UserPO currentLoginUser) {
        return getBaseResp(getUrlPath(request), jsonObject, currentLoginUser, getProductService().getBapi());
    }

    @Override
    protected String getProduct() {
        return PRODUCT_TAG;
    }

}
