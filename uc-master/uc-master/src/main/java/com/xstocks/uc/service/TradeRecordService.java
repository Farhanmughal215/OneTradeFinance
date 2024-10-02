package com.xstocks.uc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.TradeRecord;
import com.xstocks.uc.pojo.param.RecordQueryParam;
import com.xstocks.uc.pojo.vo.TradeRecordVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 资金划转表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-03-20
 */
public interface TradeRecordService extends IService<TradeRecord> {

    List<TradeRecordVo> queryRecordPage(RecordQueryParam recordQueryParam);
}
