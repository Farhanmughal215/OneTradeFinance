package com.xstocks.uc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.Trader;
import com.xstocks.uc.pojo.param.*;
import com.xstocks.uc.pojo.vo.*;

/**
 * <p>
 * Jiao交易员表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
public interface TraderService extends IService<Trader> {

    void apply(ApplyTraderParam applyTraderParam, Long id);

    void audit(AuditTraderParam auditTraderParam);

    Object traderList(TraderQueryParam queryParam, Long id);

    IPage<BTraderListVo> waitList(BQueryTraderParam queryTraderParam);

    void updateTrader(UpdateTraderParam updateTraderParam, Long id);

    Trader getTraderByAddress(String address);

    Boolean checkUserIdTrader(Integer userId);

    Trader getByUserId(Long userId);

    void addBalance(Integer id, String amount);

    TraderUserVo getTraderUser(Long id);

    FollowSetVo getFollowSet(Long traderUid, Long id);

    TraderUserVo getTraderData(Long traderUid, Long currentUid);

    TraderInfoDataVo getTraderInfo(Long userId);

    IPage<TraderStatisticsVo> bTraderList(BQueryTraderParam queryTraderParam);

    void freeze(AuditTraderParam auditTraderParam);

    TraderStatisticsVo queryTraderDetail(BTraderDetailParam detailParam);
}
