package com.xstocks.statistics.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xstocks.statistics.exception.BizException;
import com.xstocks.statistics.mapper.RankDayPendingMapper;
import com.xstocks.statistics.mapper.RankDayReleaseMapper;
import com.xstocks.statistics.mapper.RankOverallPendingMapper;
import com.xstocks.statistics.mapper.RankOverallReleaseMapper;
import com.xstocks.statistics.pojo.enums.ErrorCode;
import com.xstocks.statistics.pojo.enums.RankSubTypeEnum;
import com.xstocks.statistics.pojo.param.ExportRankParam;
import com.xstocks.statistics.pojo.param.ImportRankParam;
import com.xstocks.statistics.pojo.param.RankParam;
import com.xstocks.statistics.pojo.po.RankDayRelease;
import com.xstocks.statistics.pojo.po.RankOverallRelease;
import com.xstocks.statistics.pojo.vo.BaseResp;
import com.xstocks.statistics.pojo.vo.ExportPendingRankVo;
import com.xstocks.statistics.pojo.vo.RankItemVo;
import com.xstocks.statistics.pojo.vo.RankVo;
import com.xstocks.statistics.service.RankService;
import com.xstocks.statistics.utils.DateUtils;
import com.xstocks.statistics.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class RankServiceImpl implements RankService {

    @Value("${app.uc.sys_conf_url}")
    private String sysConfUrl;

    @Value("${app.uc.token}")
    private String ucToken;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RankDayPendingMapper rankDayPendingMapper;

    @Autowired
    private RankDayReleaseMapper rankDayReleaseMapper;

    @Autowired
    private RankOverallPendingMapper rankOverallPendingMapper;

    @Autowired
    private RankOverallReleaseMapper rankOverallReleaseMapper;

    /**
     * 查询参数，榜单状态：待放榜
     */
    private static final String RANK_STATUS_PENDING = "pending";

    /**
     * 查询参数，榜单状态：待放榜
     */
    private static final String RANK_STATUS_RELEASE = "release";

    /**
     * 查询参数，榜单类型：日榜
     */
    private static final String RANK_TYPE_DAY = "day";

    /**
     * 查询参数，榜单类型：总榜
     */
    private static final String RANK_TYPE_OVERALL = "overall";

    /**
     * 查询参数，榜单子类型：交易笔数
     */
    private static final String RANK_SUB_TYPE_TRADE_COUNT = "trade_count";

    /**
     * 查询参数，榜单子类型：实现盈亏
     */
    private static final String RANK_SUB_TYPE_REALIZE = "realize";

    /**
     * 查询参数，榜单子类型：收益率
     */
    private static final String RANK_SUB_TYPE_PROFIT_RATE = "profit_rate";

    /**
     * 已放榜查询前100条
     */
    private static final int TOP = 100;


    @Override
    public void rankTradeCount(Date date) {
        rankDayPendingMapper.rankTradeCount(date);
    }

    @Override
    public void rankRealize(Date date) {
        rankDayPendingMapper.rankRealize(date);
    }

    @Override
    public void rankProfitRate(Date date) {
        rankDayPendingMapper.rankProfitRate(date);
    }

    @Override
    public void rankTradeCountAll() {
        rankOverallPendingMapper.rankTradeCountAll();
    }

    @Override
    public void rankRealizeAll() {
        rankOverallPendingMapper.rankRealizeAll();
    }

    @Override
    public void rankProfitRateAll() {
        rankOverallPendingMapper.rankProfitRateAll();
    }


    @Override
    public RankVo queryRank(RankParam request) {
        log.info("==>榜单查询，param={}", JSONObject.toJSONString(request));
        RankVo vo = new RankVo();
        Set<String> configKeys = new HashSet<>();
        configKeys.add("activityStartDate");
        configKeys.add("activityEndDate");
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        reqHeaders.add("Authorization", ucToken);
        HttpEntity<Set<String>> httpEntity = new HttpEntity<>(configKeys, reqHeaders);
        BaseResp<LinkedHashMap<String, String>> resp = restTemplate.postForEntity(sysConfUrl, httpEntity, BaseResp.class).getBody();
        log.info("获取系统配置：{}", resp);
        if (resp == null || resp.getCode() != 0) {
            log.error("查询活动开始时间失败!,{}", resp);
            throw new BizException(ErrorCode.ILLEGAL_REQUEST);
        }
        log.info("查询活动时间返回:{}", resp);
        LinkedHashMap<String, String> data = resp.getData();

        String currDate = DateUtils.format(DateUtils.YYYY_MM_DD, new Date());
        log.info("当前日期：{}", currDate);
        log.info("活动开始日期：{}", data.get("activityStartDate"));
        // 判断是否活动开始日期
        boolean isStartDay = data != null && currDate.equals(data.get("activityStartDate"));

        String rankStatus;
        if (RANK_TYPE_DAY.equals(request.getRankType())) {
            // 活动当天，仅展示待放榜数据
            rankStatus = isStartDay || new Date().getHours() < 2 ? RANK_STATUS_PENDING : RANK_STATUS_RELEASE;
        } else if (RANK_TYPE_OVERALL.equals(request.getRankType())) {
            try {
                // 活动结束48小时后，查询已放榜数据
                Date activityEndDate = DateUtils.formatDateToMax(com.alibaba.excel.util.DateUtils.parseDate(data.get("activityEndDate")));
                rankStatus = new Date().getTime() > org.apache.commons.lang3.time.DateUtils.addHours(activityEndDate, 48).getTime() ? RANK_STATUS_RELEASE : RANK_STATUS_PENDING;
            } catch (ParseException e) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST);
            }
        } else {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST);
        }

        if (RANK_STATUS_PENDING.equals(rankStatus)) { // 待放榜
            // 查询昨日数据，数据来源：日榜t_rank_day_pending  总榜t_rank_overall_pending
            // 只用返回当前用户所在排名即可，如果表中没有则不用处理currentUserRank
            if (RANK_TYPE_DAY.equals(request.getRankType())) {
                log.info("待放榜 -> 日榜");

                // 用户在待放榜的排名区间定义：1~49,  50~99,  100~199,  200~299, 依此类推,  1000 ~ 1999,  10000~19999  依此类推

                String yesterday = isStartDay ? currDate : DateUtils.format(DateUtils.YYYY_MM_DD, org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -1));
                RankItemVo currentUserRank = rankDayPendingMapper.queryCurrentUserRank(request.getUid(), request.getRankSubType(), yesterday);
                if (currentUserRank != null) { // 有可能为空
                    log.info("rankNo={}", currentUserRank.getRankNo());
                    currentUserRank.setRankNo(getRankRange(Integer.valueOf(currentUserRank.getRankNo())));
                }

                vo.setCurrentUserRank(currentUserRank);
                vo.setRankList(new ArrayList<>());

            } else if (RANK_TYPE_OVERALL.equals(request.getRankType())) {
                log.info("待放榜 -> 总榜");

                // 用户在待放榜的排名区间定义：1~49,  50~99,  100~199,  200~299, 依此类推,  1000 ~ 1999,  10000~19999  依此类推

                RankItemVo currentUserRank = rankOverallPendingMapper.queryCurrentUserRank(request.getUid(), request.getRankSubType());
                if (currentUserRank != null) { // 有可能为空
                    log.info("rankNo={}", currentUserRank.getRankNo());
                    currentUserRank.setRankNo(getRankRange(Integer.valueOf(currentUserRank.getRankNo())));
                }

                vo.setCurrentUserRank(currentUserRank);
                vo.setRankList(new ArrayList<>());

            } else {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST);
            }
        } else { // 已放榜
            // 数据来源：日榜t_rank_day_release  总榜t_rank_overall_release
            if (RANK_TYPE_DAY.equals(request.getRankType())) {
                log.info("已放榜 -> 日榜");
                String yesterday = isStartDay ? currDate : DateUtils.format(DateUtils.YYYY_MM_DD, org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -1));
                RankItemVo currentUserRank = rankDayReleaseMapper.queryCurrentUserRank(request.getUid(), request.getRankSubType(), yesterday); // 有可能为空
                List<RankItemVo> rankList = rankDayReleaseMapper.queryRankList(request.getRankSubType(), yesterday, TOP);

                vo.setCurrentUserRank(currentUserRank);
                vo.setRankList(rankList);


            } else if (RANK_TYPE_OVERALL.equals(request.getRankType())) {
                log.info("已放榜 -> 总榜");

                RankItemVo rank = rankOverallReleaseMapper.queryCurrentUserRank(request.getUid(), request.getRankSubType()); // 有可能为空
                List<RankItemVo> rankList = rankOverallReleaseMapper.queryRankList(request.getRankSubType(), TOP);

                vo.setCurrentUserRank(rank);
                vo.setRankList(rankList);
            } else {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST);
            }

        }

        vo.setRankStatus(rankStatus);
        return vo;
    }

    @Override
    public ResponseEntity export(ExportRankParam request) {
        log.info("【导出待放榜】request={}", JSONObject.toJSONString(request));
        List<ExportPendingRankVo> list;
        String fileName;
        if (RANK_TYPE_DAY.equals(request.getRankType())) {
            log.info("导出待放榜 -> 日榜");
            list = rankDayPendingMapper.queryExportRankList(request.getRankSubType(), request.getDate(), request.getTop());
            fileName = "日榜(" + request.getDate() + ")-待放榜-日" + RankSubTypeEnum.getMsg(request.getRankSubType());
        } else if (RANK_TYPE_OVERALL.equals(request.getRankType())) {
            log.info("待放榜 -> 总榜");
            list = rankOverallPendingMapper.queryExportRankList(request.getRankSubType(), request.getTop());
            fileName = "总榜-待放榜-总" + RankSubTypeEnum.getMsg(request.getRankSubType());
        } else {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST);
        }
        log.info("list.ize={}", list.size());
        if (!CollectionUtils.isEmpty(list)) {
            try {
                //数据写入到字节流
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                boolean flag = ExcelUtils.writeExcel(bos, ExportPendingRankVo.class, list);
                log.info("导出flag={}", flag);
                //下载文件
                fileName = fileName + ".xls";
                log.info("开始下载导出的Excel文件");
                return ExcelUtils.downloadExcel(fileName, bos);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("榜单导出异常:{}", e.getMessage());
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importReleaseRank(ImportRankParam request) {
        log.info("【导入已放榜】date={}", request.getDate());
        if (request.getFile().isEmpty()) {
            throw new BizException(ErrorCode.ILLEGAL_FILE);
        }
        try {
            InputStream inputStream = request.getFile().getInputStream();
            // 解析 Excel 文件
            List<ExportPendingRankVo> list = ExcelUtils.readExcel(inputStream, ExportPendingRankVo.class);
            if (CollectionUtils.isEmpty(list)) {
                return false;
            }
            log.info("import list.size={}", list.size());
            for (ExportPendingRankVo vo : list) {
                log.info(JSONObject.toJSONString(vo));
            }
            Date now = new Date();
            if (RANK_TYPE_DAY.equals(request.getRankType())) {
                log.info("导入已放榜 -> 日榜");
                // 先删除旧数据
                rankDayReleaseMapper.deleteByParam(request.getRankSubType(), request.getDate());
                // 再插入运营确认的排名数据
                List<RankDayRelease> addList = new ArrayList<>();
                for (ExportPendingRankVo item : list) {
                    RankDayRelease entity = new RankDayRelease();
                    entity.setType(request.getRankSubType());
                    entity.setStatisticsDate(LocalDate.parse(request.getDate()));
                    entity.setUid(item.getUid().toString());
                    entity.setAddress(item.getAddress());
                    entity.setAmount(item.getAmount());
                    entity.setCreateTime(LocalDateTime.now());
                    addList.add(entity);
                }
                rankDayReleaseMapper.batchInsert(addList);
                // 再插入对应待放榜的剩余数据到已放榜中，这一步是为了保证能在已放榜中直接查到当前用户所在的名次
                rankDayReleaseMapper.insertRemainData(request.getRankSubType(), request.getDate(), request.getStartRankNo());
            } else if (RANK_TYPE_OVERALL.equals(request.getRankType())) {
                log.info("导入已放榜 -> 总榜");
                // 先删除旧数据
                rankOverallReleaseMapper.deleteByParam(request.getRankSubType());
                // 再插入运营确认的排名数据
                List<RankOverallRelease> addList = new ArrayList<>();
                for (ExportPendingRankVo item : list) {
                    RankOverallRelease entity = new RankOverallRelease();
                    entity.setType(request.getRankSubType());
                    entity.setUid(item.getUid().toString());
                    entity.setAddress(item.getAddress());
                    entity.setAmount(item.getAmount());
                    entity.setCreateTime(LocalDateTime.now());
                    addList.add(entity);
                }
                rankOverallReleaseMapper.batchInsert(addList);
                // 再插入对应待放榜的剩余数据到已放榜中，这一步是为了保证能在已放榜中直接查到当前用户所在的名次
                rankOverallReleaseMapper.insertRemainData(request.getRankSubType(), request.getStartRankNo());
            } else {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST);
            }
            log.info("导入完毕");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException(ErrorCode.IMPORT_FAIL);
        }
    }

    public String getRankRange(int rankNo) {
        if (rankNo < 1) {
            throw new RuntimeException("rankNo error : " + rankNo);
        }

        int[] ranges = {49, 99, 199, 299, 399, 499, 599, 699, 799, 899, 999, 1999, 2999, 3999, 4999, 5999, 6999, 7999, 8999, 9999, 19999, 29999, 39999, 49999, 59999, 69999, 79999, 89999, 99999, 199999, 299999, 399999, 499999, 599999, 699999, 799999, 899999, 999999};
        int start = 1;
        int end = ranges[0];

        for (int i = 1; i <= ranges.length; i++) {
            if (rankNo <= end) {
                return start + "~" + end;
            }
            start = end + 1;
            if (i < ranges.length) {
                end = ranges[i];
            } else {
                end = Integer.MAX_VALUE;
            }
        }

        throw new RuntimeException("rankNo error : " + rankNo);
    }

//    public static void main(String[] args) {
//        int rankNo = 1004;
//        String rankRange = new RankServiceImpl().getRankRange(rankNo);
//        System.out.println("Rank Range: " + rankRange);
//    }
}
