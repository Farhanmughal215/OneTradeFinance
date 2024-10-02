package com.xstocks.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.statistics.pojo.po.UserStatistics;
import com.xstocks.statistics.pojo.vo.DailyDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户数据统计 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
@Mapper
public interface UserStatisticsMapper extends BaseMapper<UserStatistics> {

    int insertMaxLoss();

    int insertMaxProfit();

    public int insertStatistics(@Param("type") String type, @Param("orderType") List<String> orderType);

    public int insertTodayTradeStatistics(@Param("type") String type,
                                          @Param("transactionType") String transactionType, @Param("orderType") List<String> orderType);


    public int insertTodayTradeStatisticsCap(@Param("type") String type,
                                             @Param("transactionType") String transactionType, @Param("orderType") List<String> orderType);

    int insertTodayTradeCount(@Param("type") String type);

    int insertTodayLongAndCloseCount(@Param("type") String type);

    int insertTodayAvgLongTime();

    public int insertRemainStatistics(@Param("type") String type,
                                      @Param("isDistinct") boolean isDistinct,
                                      @Param("orderType") List<String> orderType,
                                      @Param("remainList") List<String> remainList);

    void insertPlatformRemainStatistics(@Param("type") String type,
                                        @Param("isDistinct") boolean isDistinct,
                                        @Param("orderType") List<String> orderType,
                                        @Param("remainList") List<String> remainList);

    public int insertStatisticsAmount(@Param("type") String type, @Param("orderType") List<String> orderType);

    public int insertStatisticsCap(@Param("type") String type, @Param("orderType") List<String> orderType);

    List<UserStatistics> getCurrentStatistics(@Param("symbol") String symbol, @Param("type") String type,
                                              @Param("min") Date min, @Param("max") Date max);

    @Select("SELECT address FROM t_order_metadata GROUP BY address")
    List<String> getAddress();

    void insertTodayMaxProfitRate();

    void insertTodayMaxLossRate();

    void insertTradeSumStatistics();

    void insertPlatformTradeSumStatistics();

    void insertPlatformStatisticsCap(@Param("type") String type, @Param("orderType") List<String> orderType);

    void insertPlatformStatistics(@Param("type") String type, @Param("orderType") List<String> orderType);

    void insertPlatformMaxLoss();

    void insertPlatformMaxProfit();

    void insertPlatformStatisticsAmount(@Param("type") String type, @Param("orderType") List<String> orderType);

    void insertPlatformTodayTradeStatisticsCap(@Param("type") String type,
                                               @Param("transactionType") String transactionType, @Param("orderType") List<String> orderType);

    void insertPlatformTodayTradeStatistics(@Param("type") String type,
                                            @Param("transactionType") String transactionType, @Param("orderType") List<String> orderType);

    void insertPlatformTodayMaxProfitRate();

    void insertPlatformTodayMaxLossRate();

    void insertPlatformTodayAvgLongTime();

    void insertPlatformTodayLongAndCloseCount(String type);

    List<DailyDataVo> getDailyStatistics();


    void removeUserReg();
}
