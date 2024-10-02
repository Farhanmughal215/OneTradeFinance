package com.xstocks.statistics.mapper;

import com.xstocks.statistics.pojo.po.RankDayRelease;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.statistics.pojo.vo.RankItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 日榜-已放榜 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-05-22
 */
public interface RankDayReleaseMapper extends BaseMapper<RankDayRelease> {

    RankItemVo queryCurrentUserRank(@Param("uid") Long uid, @Param("type") String type, @Param("date") String date);

    List<RankItemVo> queryRankList(@Param("type") String type, @Param("date") String date, @Param("top") Integer top);

    int deleteByParam(@Param("type") String type, @Param("date") String date);

    int batchInsert(List<RankDayRelease> list);

    int insertRemainData(@Param("type") String type, @Param("date") String date, @Param("startRankNo") Integer startRankNo);

}
