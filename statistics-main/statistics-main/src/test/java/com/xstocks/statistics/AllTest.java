package com.xstocks.statistics;

import com.xstocks.statistics.common.UrlConstants;
import com.xstocks.statistics.pojo.dto.DexEntity;
import com.xstocks.statistics.pojo.vo.BaseResp;
import com.xstocks.statistics.service.RankService;
import com.xstocks.statistics.service.UserStatisticsService;
import com.xstocks.statistics.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootTest
@ActiveProfiles(value = "debug")
public class AllTest {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserStatisticsService statisticsService;

    @Autowired
    private RankService rankService;

    @Test
    public void allTaskTest() {

        Date date = new Date();
        rankService.rankTradeCount(date);
        rankService.rankRealize(date);
        rankService.rankProfitRate(date);

        rankService.rankTradeCountAll();
        rankService.rankRealizeAll();
        rankService.rankProfitRateAll();
    }

    @Test
    public void taskTest() {
        statisticsService.remainStatistics();
//        statisticsService.remainStatistics();
        /*
        String d = "2024-05-26";
        Map<String, List<String>> m = Constants.ORDER_SYMBOL_TYPE_MAP;
        Date date = DateUtils.parseDateStrictly(d, "yyyy-MM-dd");

        rankService.rankTradeCount(date);
        rankService.rankRealize(date);
        rankService.rankProfitRate(date);

         */
/*


        rankService.rankTradeCountAll();
        rankService.rankRealizeAll();
        rankService.rankProfitRateAll();

*/


//        statisticsService.insertTodayAvgLongTime(UserStatisticsEnum.AVG_OPENING_TIME.getType(), date);

//        statisticsService.insertTodayLongAndCloseCount("trade_count", date);

        /*
        for (Map.Entry<String, List<String>> map : Constants.TODAY_ORDER_SYMBOL_TYPE_MAP.entrySet()) {

            String type = null;
            if (map.getKey().contains("bid")) {
                type = "long";
            } else if (map.getKey().contains("ask")) {
                type = "short";
            }
            statisticsService.insertTodayTradeStatistics(map.getKey(), type, map.getValue(), date);
        }
         */


//        statisticsService.remainStatistics();
        /*
        String d = "2024-05-27";
        Map<String, List<String>> m = Constants.ORDER_SYMBOL_TYPE_MAP;
        Date date = DateUtils.parseDateStrictly(d,"yyyy-MM-dd");
        for (Map.Entry<String, List<String>> map : m.entrySet()) {
            statisticsService.fix(map.getKey(), map.getValue(), map.getKey().contains("cap"),date);
        }
         */
    }


    @Test
    public void stockTest() {
        BaseResp resp = restTemplate.postForEntity(
                UrlConstants.ALL_STOCKS_URL, null, BaseResp.class).getBody();
        List<DexEntity> list = JsonUtil.getJSONArray(resp.getData().toString(), DexEntity.class);
//        List<DexEntity> list = new ArrayList<>();
//        list.add(new DexEntity("1705835431542542", 41532.353653, -24062.0, "opening"));
//        outStockService.platFormOutStock(list).forEach(System.out::println);
    }

}
