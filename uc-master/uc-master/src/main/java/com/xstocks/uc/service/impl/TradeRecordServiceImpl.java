package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.TradeRecordMapper;
import com.xstocks.uc.pojo.TradeRecord;
import com.xstocks.uc.pojo.param.RecordQueryParam;
import com.xstocks.uc.pojo.vo.TradeRecordVo;
import com.xstocks.uc.service.TradeRecordService;
import com.xstocks.uc.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 资金划转表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-03-20
 */
@Service
public class TradeRecordServiceImpl extends ServiceImpl<TradeRecordMapper, TradeRecord> implements TradeRecordService {

    @Override
    public List<TradeRecordVo> queryRecordPage(RecordQueryParam recordQueryParam) {

        if (recordQueryParam.getDay() != null) {
            recordQueryParam.setDay(DateUtils.formatDateToMin(recordQueryParam.getDay()));
        }
        List<TradeRecord> result = getBaseMapper().queryRecord(recordQueryParam);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        Map<String, List<TradeRecord>> map = result.stream().collect(Collectors.groupingBy(TradeRecord::getDate));
        List<TradeRecordVo> list = new ArrayList<>();
        for (Map.Entry<String, List<TradeRecord>> entry : map.entrySet()) {
            list.add(new TradeRecordVo(entry.getKey(), entry.getValue()));
        }
        return list.stream().sorted((Comparator.comparing(TradeRecordVo::getDate).reversed())).collect(Collectors.toList());
    }
}
