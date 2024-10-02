package com.xstocks.statistics.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.statistics.common.Constants;
import com.xstocks.statistics.common.UserStatisticsEnum;
import com.xstocks.statistics.mapper.UserStatisticsMapper;
import com.xstocks.statistics.pojo.po.UserStatistics;
import com.xstocks.statistics.pojo.vo.BaseResp;
import com.xstocks.statistics.pojo.vo.DailyDataVo;
import com.xstocks.statistics.pojo.vo.RemainUserVo;
import com.xstocks.statistics.service.UserStatisticsService;
import com.xstocks.statistics.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户数据统计 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
@Slf4j
@Service
public class UserStatisticsServiceImpl extends ServiceImpl<UserStatisticsMapper, UserStatistics> implements UserStatisticsService {

    @Value("${app.uc.user_remain_url}")
    private String userRemainUrl;

    @Value("${app.uc.user_reg_count_url}")
    private String userRegCountUrl;

    @Value("${app.uc.data_fee_url}")
    private String dataFeeUrl;

    @Value("${app.uc.token}")
    private String ucToken;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserStatisticsMapper userStatisticsMapper;


    //0点调用
    @Override
    public void remainStatistics() {
        JSONObject object = new JSONObject();
        object.put("addressList", this.getBaseMapper().getAddress());
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        reqHeaders.add("Authorization", ucToken);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(object, reqHeaders);
        BaseResp<List<LinkedHashMap>> resp = restTemplate.postForEntity(userRemainUrl, httpEntity, BaseResp.class).getBody();
        if (resp == null || resp.getCode() != 0) {
            log.error("查询存留用户失败!");
            return;
        }

        List<LinkedHashMap> data = resp.getData();
        if (data == null || data.isEmpty()) {
            log.error("无存留用户!");
            return;
        }
        List<RemainUserVo> allRemainUser = new ArrayList<>();
        for (LinkedHashMap map : data) {
            RemainUserVo remainUserVo = new RemainUserVo();
            remainUserVo.setAddress((String) map.get("address"));
            remainUserVo.setNewAddress((Boolean) map.get("newAddress"));
            allRemainUser.add(remainUserVo);
        }
        List<String> remainUserList = allRemainUser.stream().filter(r -> r.getNewAddress() != null && !r.getNewAddress()).collect(Collectors.toList())
                .stream().map(RemainUserVo::getAddress).collect(Collectors.toList());
        if (remainUserList.isEmpty()) {
            log.info("当前无存留用户!");
            return;
        }
        for (Map.Entry<String, List<String>> map : Constants.REMAIN_ORDER_SYMBOL_TYPE_MAP.entrySet()) {
            this.getBaseMapper().insertRemainStatistics(map.getKey(), map.getKey().contains("address"), map.getValue(), remainUserList);
            this.getBaseMapper().insertPlatformRemainStatistics(map.getKey(), map.getKey().contains("address"), map.getValue(), remainUserList);
        }
    }

    @Override
    public List<UserStatistics> getCurrentStatistics(String symbol, String type) {
        Date min = DateUtils.formatDateToMin(new Date());
        Date max = DateUtils.formatDateToMax(new Date());
        return this.getBaseMapper().getCurrentStatistics(symbol, type, min, max);
    }

    //每日交易地址数据统计

    @Override
    @Transactional
    public void fix(String type, List<String> orderType) {
        if (UserStatisticsEnum.TRADE_SUM.getType().equalsIgnoreCase(type)) {
            this.getBaseMapper().insertTradeSumStatistics();
            this.getBaseMapper().insertPlatformTradeSumStatistics();
        }else {
            //先删除在创建
            if (type.contains("cap")) {
                this.getBaseMapper().insertStatisticsCap(type, orderType);
                this.getBaseMapper().insertPlatformStatisticsCap(type, orderType);
                return;
            }
            this.getBaseMapper().insertStatistics(type, orderType);
            this.getBaseMapper().insertPlatformStatistics(type, orderType);
        }
    }

    @Override
    public void insertTodayTradeStatistics(String type, String transactionType, List<String> orderType) {
        if (type.contains("cap")) {
            this.getBaseMapper().insertTodayTradeStatisticsCap(type, transactionType, orderType);
            this.getBaseMapper().insertPlatformTodayTradeStatisticsCap(type, transactionType, orderType);
            return;
        }
        this.getBaseMapper().insertTodayTradeStatistics(type, transactionType, orderType);
        this.getBaseMapper().insertPlatformTodayTradeStatistics(type, transactionType, orderType);
    }

    @Override
    public void insertStatisticsAmount(String type, List<String> orderType) {
        this.getBaseMapper().insertStatisticsAmount(type, orderType);
        this.getBaseMapper().insertPlatformStatisticsAmount(type, orderType);
    }

    @Override
    public void insertTodayTradeCount(String type) {
        this.getBaseMapper().insertTodayTradeCount(type);
    }

    @Override
    public void insertTodayLongAndCloseCount(String type) {
        this.getBaseMapper().insertTodayLongAndCloseCount(type);
        this.getBaseMapper().insertPlatformTodayLongAndCloseCount(type);
    }

    @Override
    public void insertTodayAvgLongTime() {
        this.getBaseMapper().insertTodayAvgLongTime();
        this.getBaseMapper().insertPlatformTodayAvgLongTime();
    }


    @Override
    public void insertMaxLoss() {
        this.getBaseMapper().insertMaxLoss();
        this.getBaseMapper().insertPlatformMaxLoss();
    }

    @Override
    public void insertMaxProfit() {
        this.getBaseMapper().insertMaxProfit();
        this.getBaseMapper().insertPlatformMaxProfit();
    }

    @Override
    public void insertTodayProfitRate() {
        //UserStatisticsEnum.MAX_PROFIT_RATE.getType()
        this.getBaseMapper().insertTodayMaxProfitRate();
        this.getBaseMapper().insertPlatformTodayMaxProfitRate();
        //UserStatisticsEnum.MAX_LOSS_RATE.getType()
        this.getBaseMapper().insertTodayMaxLossRate();
        this.getBaseMapper().insertPlatformTodayMaxLossRate();
    }

    @Override
    @Transactional
    public void userRegCount() {
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        reqHeaders.add("Authorization", ucToken);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(null, reqHeaders);
        BaseResp<LinkedHashMap<String, Integer>> resp = restTemplate.postForEntity(userRegCountUrl, httpEntity, BaseResp.class).getBody();
        if (resp == null || resp.getCode() != 0) {
            log.error("查询用户注册数量失败!,{}", resp);
            return;
        }
        LinkedHashMap<String, Integer> data = resp.getData();
        if (data == null || data.isEmpty()) {
            log.error("无用户注册数量!");
            return;
        }
        removeUserReg();
        UserStatistics us = new UserStatistics();
        us.setStatisticsDate(LocalDate.now());

        us.setType(UserStatisticsEnum.TODAY_REG.getType());
        us.setSymbol("PLATFORM");
        us.setAmount(data.getOrDefault("todayCount", 0).toString());

        this.getBaseMapper().insert(us);

        UserStatistics usTotal = new UserStatistics();
        usTotal.setStatisticsDate(LocalDate.now());
        usTotal.setType(UserStatisticsEnum.TOTAL_REG.getType());
        usTotal.setSymbol("PLATFORM");
        usTotal.setAmount(data.getOrDefault("totalCount", 0).toString());
        this.getBaseMapper().insert(usTotal);
    }

    private void removeUserReg() {
        this.getBaseMapper().removeUserReg();
    }



    public List<DailyDataVo> getDailyData(){

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 获取70天前的日期
        LocalDate pastDate = currentDate.minusDays(70);

        // 定义日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 将日期格式化为字符串
        String currentDateString = currentDate.format(formatter);
        String pastDateString = pastDate.format(formatter);

        JSONObject object = new JSONObject();
        object.put("startDate", pastDateString);
        object.put("endDate", currentDateString);
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
//        reqHeaders.add("Authorization", ucToken);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(object, reqHeaders);
        BaseResp<List<LinkedHashMap>> resp = restTemplate.postForEntity(dataFeeUrl, httpEntity, BaseResp.class).getBody();
        if (resp == null || resp.getCode() != 0) {
            log.error("查询70天内统计数据失败!");
        }
        List<LinkedHashMap> data = resp.getData();
        if (data == null || data.isEmpty()) {
            log.error("无存留用户!");
        }

        Map<String,JSONObject> feeMap = new HashMap<>();
        for (LinkedHashMap map : data) {
            JSONObject obj = new JSONObject();
            obj.put("dayFee",String.valueOf(map.get("dayFee")));
            obj.put("allFee",String.valueOf(map.get("allFee")));
            feeMap.put((String) map.get("statDate"),obj);
        }

        // 输出结果
        System.out.println("当前日期: " + currentDateString);
        System.out.println("70天前的日期: " + pastDateString);
        List<DailyDataVo> list =  userStatisticsMapper.getDailyStatistics();
        for(DailyDataVo vo : list){
            String statisticsDate = vo.getStatisticsDate();
            String feeSum = vo.getFeeSum() == null ? "0" : vo.getFeeSum();
            String dailyFee = vo.getDailyFee() == null ? "0" :vo.getDailyFee();
            if(feeMap.containsKey(statisticsDate)){
                JSONObject obj = feeMap.get(statisticsDate);
                feeSum = obj.getString("allFee");
                dailyFee = obj.getString("dayFee");
                vo.setFeeSum(feeSum);
                vo.setDailyFee(dailyFee);
            }else{
                vo.setFeeSum(feeSum);
                vo.setDailyFee(dailyFee);
            }
            vo.setInterestSum("0");
            vo.setDailyInterest("0");
        }
        return list;

    }

}
