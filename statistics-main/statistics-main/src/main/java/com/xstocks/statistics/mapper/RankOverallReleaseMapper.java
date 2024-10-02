package com.xstocks.statistics.mapper;

import com.xstocks.statistics.pojo.po.RankDayRelease;
import com.xstocks.statistics.pojo.po.RankOverallRelease;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.statistics.pojo.vo.RankItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 总榜-待放榜 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-05-22
 */
public interface RankOverallReleaseMapper extends BaseMapper<RankOverallRelease> {

    RankItemVo queryCurrentUserRank(@Param("uid") Long uid, @Param("type") String type);

    List<RankItemVo> queryRankList(@Param("type") String type, @Param("top") Integer top);

    int deleteByParam(@Param("type") String type);

    int batchInsert(List<RankOverallRelease> list);

    int insertRemainData(@Param("type") String type, @Param("startRankNo") Integer startRankNo);
}
