package com.xstocks.statistics.controller;

import com.xstocks.statistics.pojo.param.ExportRankParam;
import com.xstocks.statistics.pojo.param.ImportRankParam;
import com.xstocks.statistics.pojo.param.RankParam;
import com.xstocks.statistics.pojo.vo.RankVo;
import com.xstocks.statistics.pojo.vo.BaseResp;
import com.xstocks.statistics.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 活动榜单 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
@RestController
public class RankController {

    @Autowired
    private RankService rankService;


    /**
     * 榜单
     *   此接口支持查询：待放榜和已放榜
     *   榜单类型包括：日榜、总榜
     *   榜单子类型包括：交易笔数、实现盈亏、收益率
     * @param param
     * @return
     */
    @PostMapping("/c/u/rank")
    public BaseResp<RankVo> queryRank(@RequestBody RankParam param) {
        return BaseResp.success(rankService.queryRank(param));
    }

    /**
     * 导出指定日期的待放榜数据到excel
     * 支持导出日榜的待放榜、总榜的待放榜
     * @param param
     * @return
     */
    @GetMapping("/c/u/rank/pending/export")
    public ResponseEntity export(ExportRankParam param) {
        return rankService.export(param);
    }

    /**
     * 导入已放榜
     * @param param
     */
    @PostMapping("/c/u/rank/release/import")
    public BaseResp<Boolean> importReleaseRank(ImportRankParam param) {
        return BaseResp.success(rankService.importReleaseRank(param));
    }

}
