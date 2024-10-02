package com.xstocks.uc.mapper;

import com.xstocks.uc.pojo.TraderFollow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.uc.pojo.vo.FollowUserVo;
import com.xstocks.uc.pojo.vo.TraderDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 交易员带单用户表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
public interface TraderFollowMapper extends BaseMapper<TraderFollow> {

    List<FollowUserVo> getFollowList(Integer userId);

    List<TraderDataVo> getMyTraders(Long id);

    List<TraderDataVo> follows(Long traderUid);

    TraderFollow getByUid(@Param("traderId") Integer traderId, @Param("userId") Integer userId);
}
