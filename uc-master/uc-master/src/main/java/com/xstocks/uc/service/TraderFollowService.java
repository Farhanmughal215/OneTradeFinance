package com.xstocks.uc.service;

import com.xstocks.uc.pojo.TraderFollow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.param.FollowTraderParam;
import com.xstocks.uc.pojo.param.TraderFollowParam;
import com.xstocks.uc.pojo.vo.FollowUserVo;
import com.xstocks.uc.pojo.vo.TraderDataVo;

import java.util.List;

/**
 * <p>
 * 交易员带单用户表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
public interface TraderFollowService extends IService<TraderFollow> {

    void follow(FollowTraderParam followTraderParam, Long id);

    List<FollowUserVo> getFollowList(Integer userId);

    List<TraderDataVo>  getMyTraders(Long id);

    List<TraderDataVo> follows(TraderFollowParam param, Long userId);

    void unFollow(TraderFollowParam unFollowTraderParam, Long userId);

    void cancelOrder(Long id);

    TraderFollow getByUid(Integer traderId,Integer userId);
}
