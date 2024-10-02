package com.xstocks.statistics.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xstocks.statistics.common.UserStatisticsEnum;
import com.xstocks.statistics.pojo.po.UserStatistics;
import com.xstocks.statistics.pojo.vo.BaseResp;
import com.xstocks.statistics.pojo.vo.DailyDataVo;
import com.xstocks.statistics.pojo.vo.UserStatisticsVo;
import com.xstocks.statistics.service.RankService;
import com.xstocks.statistics.service.UserStatisticsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * 用户数据统计 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
@RestController
@RequestMapping("/b/statistics")
public class UserStatisticsController {

    @Autowired
    private UserStatisticsService statisticsService;

    @Autowired
    private RankService rankService;

    private final static String[] SORT_ARRAY = {"today_reg","total_count","trade_sum", "long", "close", "break", "long_market_cap", "close_market_cap",
            "break_market_cap","profit_loss_sum", "max_profit", "max_profit_address", "max_loss", "max_loss_address", "max_profit_rate",
            "max_profit_rate_address","max_loss_rate", "max_loss_rate_address", "remain_trade", "remain_long", "remain_close", "remain_break",
            "remain_trade_address","remain_long_address", "remain_close_address", "remain_break_address", "trade_count", "avg_opening_time",
            "bid_long_market_cap","bid_close_market_cap", "bid_count", "ask_long_market_cap", "ask_close_market_cap", "ask_trade_count"};

    @PostMapping(value = "/data")
    public BaseResp<List<UserStatisticsVo>> data() {
        //查询统计数据
        List<String> types = Arrays.asList("today_reg","total_count","trade_sum","long","close","break","profit_loss_sum");
        List<UserStatistics> list = statisticsService.list(Wrappers.<UserStatistics>lambdaQuery().between(UserStatistics::getStatisticsDate,
                LocalDate.now(), LocalDate.now()).in(UserStatistics::getType,types));
        if (list == null || list.isEmpty())
            return BaseResp.success();

        Map<String, List<UserStatistics>> g = list.stream().collect(Collectors.groupingBy(UserStatistics::getType));

        List<UserStatisticsVo> result = new ArrayList<>(g.size());
        for (Map.Entry<String, List<UserStatistics>> entry : g.entrySet()) {
            result.add(new UserStatisticsVo(entry.getKey(), UserStatisticsEnum.getDesc(entry.getKey()), entry.getValue()));
        }


        /*
        result.add(processAddress(list, "max_profit","max_profit_address"));
        result.add(processAddress(list, "max_loss","max_loss_address"));
        result.add(processAddress(list, "max_profit_rate","max_profit_rate_address"));
        result.add(processAddress(list,"max_loss_rate", "max_loss_rate_address"));

        */
        Map<String, Integer> typeToIndexMap = IntStream.range(0, SORT_ARRAY.length)
                .boxed()
                .collect(Collectors.toMap(i -> SORT_ARRAY[i], Function.identity()));

        result.sort(Comparator.comparingInt(user -> typeToIndexMap.getOrDefault(user.getType(), Integer.MAX_VALUE)));
        return BaseResp.success(result);
    }

    private UserStatisticsVo processAddress(List<UserStatistics> uss, String originType, String addressType) {
        if (uss.isEmpty())
            return null;
        return new UserStatisticsVo(addressType, UserStatisticsEnum.getDesc(addressType),
                uss.stream().filter(r -> r.getType().equalsIgnoreCase(originType)).collect(Collectors.toList()).stream().map(r -> {
                    UserStatistics us = new UserStatistics();
                    BeanUtils.copyProperties(r, us);
                    us.setAmount(r.getAddress());
                    us.setType(addressType);
                    return us;
                }).collect(Collectors.toList()));
    }

    @PostMapping(value = "/task")
    public BaseResp<String> task() {
        statisticsService.remainStatistics();
        rankService.rankTradeCountAll();
        rankService.rankRealizeAll();
        rankService.rankProfitRateAll();
        return BaseResp.success();
    }



    @PostMapping(value = "/getExportData")
    public BaseResp<List<DailyDataVo>> getDailyData() {
        List<DailyDataVo> list = statisticsService.getDailyData();
        return BaseResp.success(list);
    }




}
