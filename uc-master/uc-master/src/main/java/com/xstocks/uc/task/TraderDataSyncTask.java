package com.xstocks.uc.task;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.pojo.TraderData;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.TraderDataService;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TraderDataSyncTask {

    @Autowired
    private TraderDataService traderDataService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.analysis.rpc.sync-trader-data-url}")
    private String syncTraderDataUrl;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void syncTraderData() {
        List<TraderData> dataList = traderDataService.list();
        if (CollectionUtils.isEmpty(dataList))
            return;

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("traderUid", dataList.stream().map(TraderData::getUserId).map(String::valueOf).collect(Collectors.joining(",")));
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
        BaseResp<List<LinkedHashMap>> resp = restTemplate.postForEntity(syncTraderDataUrl, httpEntity, BaseResp.class).getBody();
//        log.info("同步交易员数据参数:{}  结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
        if (resp == null || resp.getCode() != 0) {
            log.error("同步交易员数据失败:{}", JsonUtil.toJSONString(resp));
            return;
        }
        /*
        {
          "code": 0,
          "msg": "Success",
          "data": [
            {
              "followScale": "170.0000",
              "followPl": "12.0000",
              "uid": "11",
              "followFpl": "0.0000",
              "traderCount": 0,
              "pl": "0.0000",
              "currentFollowCount": 0,
              "shareAmount": "0.0000"
            },
            {
              "followScale": "0.0000",
              "followPl": "0.0000",
              "uid": "12",
              "followFpl": "0.0000",
              "traderCount": 0,
              "pl": "0.0000",
              "currentFollowCount": 0,
              "shareAmount": "0.0000"
            }
          ],
          "timestamp": 1711936332665
        }

        {
          "followScale": 170.000000, // 跟单规模
          "followPl": 12.000000, // 跟单盈亏
          "uid": "11", //交易员id
          "followFpl": 0.000000, //跟单员浮动盈亏
          "trader_count": 0, //交易次数
          "pl": 0.000000, //交易员盈亏
          "current_follow_count": 0, //跟单人数
          "shareAmount": 0.0000 //分润金额
        }
         */
        List<TraderData> tdList = new ArrayList<>();
        for (LinkedHashMap data : resp.getData()) {
            if (!data.containsKey("uid"))
                continue;
            Integer uid = Integer.parseInt(data.get("uid").toString());
            Optional<TraderData> q = dataList.stream().filter(d -> d.getUserId().equals(uid)).findFirst();
            if (!q.isPresent())
                continue;
            TraderData traderData = q.get();
            TraderData td = new TraderData();
            td.setTraderId(traderData.getTraderId());
            td.setFollowCount(Integer.parseInt(data.getOrDefault("currentFollowCount",0).toString()));
            td.setTradeCount(Integer.parseInt(data.getOrDefault("traderCount",0).toString()));
            td.setPlAmount(BigDecimal.valueOf(Double.parseDouble(data.getOrDefault("pl",0).toString())));
            td.setTotalAmount(BigDecimal.valueOf(Double.parseDouble(data.getOrDefault("followScale",0).toString())));
            td.setFollowTotalAmount(BigDecimal.valueOf(Double.parseDouble(data.getOrDefault("followPl",0).toString())));
            td.setFollowPl(BigDecimal.valueOf(Double.parseDouble(data.getOrDefault("followFpl",0).toString())));
            td.setTotalProfit(BigDecimal.valueOf(Double.parseDouble(data.getOrDefault("shareAmount",0).toString())));
            tdList.add(td);
        }
        if (CollectionUtils.isEmpty(tdList))
            return;
        traderDataService.updateBatchById(tdList);
    }
}
