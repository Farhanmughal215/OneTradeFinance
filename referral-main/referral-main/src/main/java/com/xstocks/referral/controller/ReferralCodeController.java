package com.xstocks.referral.controller;

import com.alibaba.fastjson.JSONObject;
import com.xstocks.referral.exception.BizException;
import com.xstocks.referral.pojo.ReferralRelation;
import com.xstocks.referral.pojo.param.CommonParam;
import com.xstocks.referral.pojo.param.ReferralCodeParam;
import com.xstocks.referral.pojo.vo.BaseResp;
import com.xstocks.referral.pojo.vo.ReferralCodeVo;
import com.xstocks.referral.pojo.vo.UserReferralVo;
import com.xstocks.referral.service.ReferralCodeService;
import com.xstocks.referral.service.ReferralRelationService;
import com.xstocks.referral.service.ReferralUserService;
import com.xstocks.referral.service.ReferralWithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 邀请码表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
@Slf4j
@RestController
@RequestMapping("/c/referral")
public class ReferralCodeController {

    @Autowired
    private ReferralCodeService referralCodeService;

    @Autowired
    private ReferralRelationService referralRelationService;

    @Autowired
    private ReferralUserService referralUserService;

    @Autowired
    private ReferralWithdrawService referralWithdrawService;

    @PostMapping(value = "/myData")
    public BaseResp<UserReferralVo> myData(@RequestBody CommonParam param) {
        return BaseResp.success(referralUserService.myData(param));
    }

    @PostMapping(value = "/callback")
    public BaseResp<String> callback(@RequestBody JSONObject object) {
        log.info("退款回调:{}", object);
        //提现记录写入hash
        referralWithdrawService.callback(object);
        return BaseResp.success();
    }

    @PostMapping(value = "/codeList")
    public BaseResp<List<ReferralCodeVo>> codeList(@RequestBody CommonParam param) {
        return BaseResp.success(referralCodeService.codeList(param));
    }

    @PostMapping(value = "/userList")
    public BaseResp<List<ReferralRelation>> userList(@RequestBody CommonParam param) {
        return BaseResp.success(referralRelationService.userList(param));
    }

    @PostMapping(value = "/create")
    public BaseResp<String> create(@RequestBody ReferralCodeParam param) {
        try {
            log.info("create referral code param:{}", param);
            referralCodeService.create(param);
            return BaseResp.success();
        } catch (BizException e) {
            log.error(e.getMessage(), e);
            return BaseResp.error(e.getErrCode());
        }
    }

    @PostMapping(value = "/check")
    public BaseResp<Boolean> check(@RequestBody ReferralCodeParam param) {
        try {
            return BaseResp.success(referralCodeService.check(param));
        } catch (BizException e) {
            log.error(e.getMessage(), e);
            return BaseResp.error(e.getErrCode());
        }
    }

    @PostMapping(value = "/bind")
    public BaseResp<String> bind(@RequestBody ReferralCodeParam param) {
        try {
            referralRelationService.bind(param);
            return BaseResp.success();
        } catch (BizException e) {
            log.error(e.getMessage(), e);
            return BaseResp.error(e.getErrCode());
        }
    }


    @PostMapping(value = "/withdraw")
    public BaseResp<String> withdraw(@RequestBody CommonParam param) {
        try {
            referralUserService.withdraw(param);
            return BaseResp.success();
        } catch (BizException e) {
            log.error(e.getMessage(), e);
            return BaseResp.error(e.getErrCode());
        }
    }

/*
    @PostMapping(value = "/unBind")
    public BaseResp<String> unBind(@RequestBody ReferralCodeParam param) {
        try {
            referralRelationService.unBind(param);
            return BaseResp.success();
        } catch (BizException e) {
            log.error(e.getMessage(), e);
            return BaseResp.error(e.getErrCode());
        }
    }
    */
}
