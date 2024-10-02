package com.xstocks.uc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.uc.pojo.TradeRecord;
import com.xstocks.uc.pojo.param.RecordQueryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 资金划转表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-03-20
 */
public interface TradeRecordMapper extends BaseMapper<TradeRecord> {

    List<TradeRecord> queryRecord(@Param("param") RecordQueryParam recordQueryParam);
}
