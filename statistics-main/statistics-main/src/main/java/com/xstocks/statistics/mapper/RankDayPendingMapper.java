package com.xstocks.statistics.mapper;

import com.xstocks.statistics.pojo.po.RankDayPending;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.statistics.pojo.vo.ExportPendingRankVo;
import com.xstocks.statistics.pojo.vo.RankItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 日榜-待放榜 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-05-22
 */
public interface RankDayPendingMapper extends BaseMapper<RankDayPending> {

    RankItemVo queryCurrentUserRank(@Param("uid") Long uid, @Param("type") String type, @Param("date") String date);

    List<ExportPendingRankVo> queryExportRankList(@Param("type") String type, @Param("date") String date, @Param("top") Integer top);

    void rankTradeCount(Date date);

    void rankRealize(Date date);

    void rankProfitRate(Date date);

}
