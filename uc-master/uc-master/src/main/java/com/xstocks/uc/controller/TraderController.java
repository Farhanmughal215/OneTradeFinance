package com.xstocks.uc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.pojo.Trader;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.param.*;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.*;
import com.xstocks.uc.service.TraderFollowService;
import com.xstocks.uc.service.TraderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Jiao交易员表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
@RestController
public class TraderController {


    @Autowired
    private TraderService traderService;

    @Autowired
    private TraderFollowService traderFollowService;


    /**
     * 用户申请成为交易员
     *
     * @param applyTraderParam
     * @param currentLoginUser
     * @return
     */
    @RateLimit(rate = 1)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/apply")
    public BaseResp<Boolean> apply(@Validated @RequestBody ApplyTraderParam applyTraderParam,
                                   @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                           UserPO currentLoginUser) {
        traderService.apply(applyTraderParam, currentLoginUser.getId());
        return BaseResp.success();
    }

    /**
     * b端交易员列表
     *
     * @param queryTraderParam
     * @return
     */
    @PostMapping(value = "/b/trader/traderList")
    public BaseResp<IPage<TraderStatisticsVo>> bTraderList(@Validated @RequestBody BQueryTraderParam queryTraderParam) {
        return BaseResp.success(traderService.bTraderList(queryTraderParam));
    }


    /**
     * 查询交易员详情
     * @param detailParam
     * @return
     */
    @PostMapping(value = "/b/trader/queryTraderDetail")
    public BaseResp<TraderStatisticsVo> queryTraderDetail(@RequestBody BTraderDetailParam detailParam) {
        return BaseResp.success(traderService.queryTraderDetail(detailParam));
    }


    /**
     * 交易员待审核列表
     *
     * @param queryTraderParam
     * @return
     */
    @PostMapping(value = "/b/trader/waitList")
    public BaseResp<IPage<BTraderListVo>> waitList(@Validated @RequestBody BQueryTraderParam queryTraderParam) {
        return BaseResp.success(traderService.waitList(queryTraderParam));
    }


    /**
     * b端审核交易员
     *
     * @param auditTraderParam
     * @return
     */
    @PostMapping(value = "/b/trader/audit")
    public BaseResp<Boolean> audit(@Validated @RequestBody AuditTraderParam auditTraderParam) {
        traderService.audit(auditTraderParam);
        return BaseResp.success();
    }

    /**
     * 封禁交易员
     *
     * @param auditTraderParam
     * @return
     */
    @PostMapping(value = "/b/trader/freeze")
    public BaseResp<Boolean> freeze(@Validated @RequestBody AuditTraderParam auditTraderParam) {
        traderService.freeze(auditTraderParam);
        return BaseResp.success();
    }

    /**
     * 查询交易员的数据
     *
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/info")
    public BaseResp<TraderUserVo> info(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                               UserPO currentLoginUser
    ) {
        return BaseResp.success(traderService.getTraderUser(currentLoginUser.getId()));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/getTraderData")
    public BaseResp<TraderUserVo> getTraderData(@RequestBody TraderFollowParam queryParam,
                                                @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                        UserPO currentLoginUser) {
        return BaseResp.success(traderService.getTraderData(queryParam.getTraderUid(), currentLoginUser.getId()));
    }

    /**
     * 查询用户跟单的交易员
     *
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/myTraders")
    public BaseResp<List<TraderDataVo>> myTraderList(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                             UserPO currentLoginUser) {
        return BaseResp.success(traderFollowService.getMyTraders(currentLoginUser.getId()));
    }


    /**
     * 查询关注交易员的用户列表
     *
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/follows")
    public BaseResp<List<TraderDataVo>> follows(@RequestBody TraderFollowParam followParam, @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
            UserPO currentLoginUser) {
        return BaseResp.success(traderFollowService.follows(followParam, currentLoginUser.getId()));
    }

    /**
     * 跟单交易员
     *
     * @param followTraderParam
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/follow")
    public BaseResp<Boolean> follow(@Validated @RequestBody FollowTraderParam followTraderParam,
                                    @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                            UserPO currentLoginUser) {
        traderFollowService.follow(followTraderParam, currentLoginUser.getId());
        return BaseResp.success();
    }

    /**
     * 解除跟单
     *
     * @param unFollowTraderParam
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/unFollow")
    public BaseResp<Boolean> unFollow(@Validated @RequestBody TraderFollowParam unFollowTraderParam,
                                      @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                              UserPO currentLoginUser) {
        traderFollowService.unFollow(unFollowTraderParam, currentLoginUser.getId());
        return BaseResp.success();
    }

    /**
     * 取消带单
     *
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/cancelOrder")
    public BaseResp<Boolean> cancelOrder(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                 UserPO currentLoginUser) {
        traderFollowService.cancelOrder(currentLoginUser.getId());
        return BaseResp.success();
    }


    /**
     * 查询交易员跟单用户列表
     *
     * @param userId
     * @return
     */
    @PostMapping(value = "/s/trader/followList/{userId}")
    public BaseResp<List<FollowUserVo>> getFollowList(@PathVariable("userId") Integer userId) {
        return BaseResp.success(traderFollowService.getFollowList(userId));
    }

    @PostMapping(value = "/s/trader/getTraderInfo/{userId}")
    public BaseResp<TraderInfoDataVo> getTraderInfo(@PathVariable("userId") Long userId) {
        return BaseResp.success(traderService.getTraderInfo(userId));
    }

    @PostMapping(value = "/s/trader/checkUserIdTrader/{userId}")
    public BaseResp<Boolean> checkUserIdTrader(@PathVariable("userId") Integer userId) {
        return BaseResp.success(traderService.checkUserIdTrader(userId));
    }


    @PostMapping(value = "/c/trader/checkUserIdTrader")
    public BaseResp<Boolean> checkUserIdTrader(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                       UserPO currentLoginUser) {
        return BaseResp.success(traderService.checkUserIdTrader(currentLoginUser.getId().intValue()));
    }

    /**
     * 交易员筛选列表
     *
     * @param queryParam
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/list")
    public BaseResp<Object> list(@RequestBody TraderQueryParam queryParam,
                                 @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                         UserPO currentLoginUser) {
        return BaseResp.success(traderService.traderList(queryParam, currentLoginUser.getId()));
    }

    /**
     * 获取交易员资金地址
     *
     * @param queryParam
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/getAddress")
    public BaseResp<String> getAddress(@RequestBody TraderFollowParam queryParam,
                                       @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                               UserPO currentLoginUser) {
        Trader trader = traderService.getByUserId(queryParam.getTraderUid());
        return BaseResp.success(null == trader ? null : trader.getAddress());
    }

    /**
     * 查询关注交易员的限额数据
     *
     * @param queryParam
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/followSet")
    public BaseResp<FollowSetVo> followSet(@RequestBody TraderFollowParam queryParam,
                                           @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                   UserPO currentLoginUser) {
        return BaseResp.success(traderService.getFollowSet(queryParam.getTraderUid(), currentLoginUser.getId()));
    }

    /**
     * 更新交易员基本信息
     *
     * @param updateTraderParam
     * @param currentLoginUser
     * @return
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/trader/update")
    public BaseResp<Boolean> updateTrader(@RequestBody UpdateTraderParam updateTraderParam,
                                          @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                  UserPO currentLoginUser) {
        traderService.updateTrader(updateTraderParam, currentLoginUser.getId());
        return BaseResp.success();
    }
}
