package com.xstocks.statistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.statistics.pojo.po.UserStatistics;
import com.xstocks.statistics.pojo.vo.DailyDataVo;

import java.util.List;

/**
 * <p>
 * 用户数据统计 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
public interface UserStatisticsService extends IService<UserStatistics> {

    public List<UserStatistics> getCurrentStatistics(String symbol, String type);

    void fix(String type, List<String> orderType);

    void insertTodayTradeStatistics(String type, String transactionType, List<String> orderType);

    void insertStatisticsAmount(String type, List<String> orderType);


    void remainStatistics();

    void insertTodayTradeCount(String type);

    void insertTodayLongAndCloseCount(String type);

    void insertTodayAvgLongTime();

    void insertMaxLoss();

    void insertMaxProfit();

    void insertTodayProfitRate();

    void userRegCount();

    List<DailyDataVo> getDailyData();
}
