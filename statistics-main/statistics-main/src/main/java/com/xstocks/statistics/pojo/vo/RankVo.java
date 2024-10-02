package com.xstocks.statistics.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class RankVo {

    /**
     * 当前用户的排名信息
     */
    private RankItemVo currentUserRank;

    /**
     * 榜单用户列表，只返回前100名
     */
    private List<RankItemVo> rankList;

    /**
     * 查询榜单状态：
     *  pending：待放榜
     *  release：已放榜
     */
    private String rankStatus;

}
