package com.xstocks.statistics.service;

import com.xstocks.statistics.pojo.param.ExportRankParam;
import com.xstocks.statistics.pojo.param.ImportRankParam;
import com.xstocks.statistics.pojo.param.RankParam;
import com.xstocks.statistics.pojo.vo.RankVo;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface RankService {

    RankVo queryRank(RankParam param);

    ResponseEntity export(ExportRankParam param);

    boolean importReleaseRank(ImportRankParam param);

    void rankTradeCount(Date date);

    void rankRealize(Date date);

    void rankProfitRate(Date date);

    void rankTradeCountAll();

    void rankRealizeAll();

    void rankProfitRateAll();
}
