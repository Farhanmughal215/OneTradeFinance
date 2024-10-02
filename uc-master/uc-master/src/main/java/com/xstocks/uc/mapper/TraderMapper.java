package com.xstocks.uc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xstocks.uc.pojo.Trader;
import com.xstocks.uc.pojo.param.BQueryTraderParam;
import com.xstocks.uc.pojo.param.BTraderDetailParam;
import com.xstocks.uc.pojo.po.TraderWrapperPO;
import com.xstocks.uc.pojo.vo.BTraderListVo;
import com.xstocks.uc.pojo.vo.FollowSetVo;
import com.xstocks.uc.pojo.vo.TraderInfoDataVo;
import com.xstocks.uc.pojo.vo.TraderStatisticsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Jiao交易员表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
public interface TraderMapper extends BaseMapper<Trader> {

    Integer getNextAddressRandom();

    int checkUserIdTrader(Integer userId);

    Trader getTraderByAddress(String address);

    List<Integer> getFullList();

    List<Integer> getAvailableList();

    List<TraderWrapperPO> getTraderInfo(@Param("list") List<Integer> list,@Param("userId") Long userId);

    FollowSetVo getFollowSet(@Param("traderUid") Long traderUid,@Param("currentUid") Long currentUid);

    Integer getFollowStatus(@Param("traderUid") Long traderUid,@Param("currentUid") Long currentUid);

    TraderInfoDataVo getTraderDataInfo(Long userId);

    IPage<BTraderListVo> waitList(Page<Trader> page, @Param("param") BQueryTraderParam queryTraderParam);

    IPage<TraderStatisticsVo> bTraderList(Page<Trader> page, @Param("param")BQueryTraderParam queryTraderParam);

    TraderStatisticsVo queryTraderDetail(BTraderDetailParam detailParam);
}
