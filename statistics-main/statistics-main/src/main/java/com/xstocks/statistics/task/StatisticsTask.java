package com.xstocks.statistics.task;

import com.xstocks.statistics.common.Constants;
import com.xstocks.statistics.common.UserStatisticsEnum;
import com.xstocks.statistics.service.RankService;
import com.xstocks.statistics.service.UserStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.task-config", value = "enable", havingValue = "true")
public class StatisticsTask {

    @Autowired
    private UserStatisticsService statisticsService;

    @Autowired
    private RankService rankService;


    @Scheduled(cron = "0 0/4 * * * ?")
    public void rankStatisticsToday() {
        log.info("rankStatisticsToday task start");
        Date date = new Date();
        rankService.rankTradeCount(date);
        rankService.rankRealize(date);
        rankService.rankProfitRate(date);
        log.info("rankStatisticsToday task end");
    }


    @Scheduled(cron = "0 0 0/1 * * ?")
    public void rankStatisticsAll() {
        log.info("rankStatisticsAll task start");
        rankService.rankTradeCountAll();
        rankService.rankRealizeAll();
        rankService.rankProfitRateAll();
        log.info("rankStatisticsAll task end");
    }

    @Scheduled(cron = "1 1 0 1/1 * ?")
    public void statisticsOncePerDay() {
        log.info("statistics remain item");
        statisticsService.remainStatistics();
    }


    @Scheduled(cron = "45 59 23 * * ? ")
    public void statisticsTodayFix() {
        statisticsToday();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void statisticsTodayNormal() {
        statisticsToday();
    }

    public void statisticsToday() {
        log.info("statisticsToday task start");

        //用户注册数
        statisticsService.userRegCount();


        for (Map.Entry<String, List<String>> map : Constants.ORDER_SYMBOL_TYPE_MAP.entrySet()) {
            statisticsService.fix(map.getKey(), map.getValue());
        }
        statisticsService.insertMaxProfit();
        statisticsService.insertMaxLoss();

        log.info("statistics profit_loss_sum");
        for (Map.Entry<String, List<String>> map : Constants.AMOUNT_ORDER_SYMBOL_TYPE_MAP.entrySet()) {
            statisticsService.insertStatisticsAmount(map.getKey(), map.getValue());
        }


        log.info("statistics today item");
        for (Map.Entry<String, List<String>> map : Constants.TODAY_ORDER_SYMBOL_TYPE_MAP.entrySet()) {
            String transactionType = null;
            if (map.getKey().contains("bid")) {
                transactionType = "bid";
            } else if (map.getKey().contains("ask")) {
                transactionType = "ask";
            }
            statisticsService.insertTodayTradeStatistics(map.getKey(), transactionType, map.getValue());
        }

        //每日最大实现收益率地址及额度
        statisticsService.insertTodayProfitRate();

        //统计每日日内交易平均持仓时间
        statisticsService.insertTodayAvgLongTime();

        //每日日内交易笔数
        statisticsService.insertTodayLongAndCloseCount(UserStatisticsEnum.TRADE_COUNT.getType());

        log.info("statisticsToday task end");
    }


}
