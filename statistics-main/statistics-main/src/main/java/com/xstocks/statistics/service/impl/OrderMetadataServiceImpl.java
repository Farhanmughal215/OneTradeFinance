package com.xstocks.statistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.statistics.common.OrderEnum;
import com.xstocks.statistics.mapper.OrderMetadataMapper;
import com.xstocks.statistics.pojo.dto.UserPositions;
import com.xstocks.statistics.pojo.po.OrderMetadata;
import com.xstocks.statistics.service.OrderMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户原始订单数据 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
@Slf4j
@Service
public class OrderMetadataServiceImpl extends ServiceImpl<OrderMetadataMapper, OrderMetadata> implements OrderMetadataService {


    /*
    long:建仓,close:平仓,break:爆仓,long_market_cap:建仓市值,
    close_market_cap:平仓市值,break_market_cap:爆仓市值,
     */
    public OrderMetadata preCreateOrderMetadata(UserPositions positions) {
        OrderMetadata metadata = new OrderMetadata();
        metadata.setOrderId(positions.getOrderId());
        metadata.setUid(positions.getUid());
        metadata.setDoneTs(positions.getDoneTs());
        metadata.setReverseOrderId(null != positions.getReverseOrderId() ? positions.getReverseOrderId().toString() : "0");
        metadata.setSymbol(getSymbolDesc(positions.getCurrency(), positions.getCurrencyCode()));
        metadata.setAddress(positions.getAddress());
        metadata.setMarginAmount(positions.getMarginAmount());
        metadata.setTradeType(positions.getTradeType());
        metadata.setTransactionType(positions.getTransactionType());
        metadata.setCreateTime(LocalDateTime.now());
        return metadata;
    }

    private List<OrderMetadata> determineType(UserPositions positions) {
        List<OrderMetadata> list = new ArrayList<>(2);
        if (UserPositions.OPENING.equals(positions.getPositionStatus())) {
            OrderMetadata metadata = preCreateOrderMetadata(positions);
            metadata.setType(OrderEnum.LONG.getType());
            metadata.setAmount("1");
            list.add(metadata);
            //建仓市值
            OrderMetadata marketCap = preCreateOrderMetadata(positions);
            marketCap.setType(OrderEnum.LONG_MARKET_CAP.getType());
            BigDecimal volume = positions.getSourcePrice().multiply(new BigDecimal(positions.getSourceSize()));
            marketCap.setAmount(volume.toString());
            list.add(marketCap);
        }
        if (UserPositions.CLOSING.equals(positions.getPositionStatus())) {
            //计算收益额
            BigDecimal priceDiff;
            if (positions.getTransactionType().equalsIgnoreCase(UserPositions.ASK)) {
                //建仓价 - 市价 sourcePrice - donePrice
                priceDiff = positions.getSourcePrice().subtract(positions.getDonePrice());
            } else {
                //市价 - 建仓价
                priceDiff = positions.getDonePrice().subtract(positions.getSourcePrice());
            }
            log.info("订单号:{},transactionType:{} ,tradeType:{},平/爆仓差价:{}", positions.getOrderId(), positions.getTransactionType(), positions.getTradeType(), priceDiff);

            //实现盈亏
            BigDecimal pl = priceDiff.multiply(new BigDecimal(positions.getSourceSize()));

            if (positions.getBreakOrder() != null && positions.getBreakOrder()) {
                //爆仓
                OrderMetadata metadata = preCreateOrderMetadata(positions);
                metadata.setType(OrderEnum.BREAK.getType());
                metadata.setAmount(pl.toString());
                list.add(metadata);

                //爆仓市值
                OrderMetadata marketCap = preCreateOrderMetadata(positions);
                marketCap.setType(OrderEnum.BREAK_MARKET_CAP.getType());
                BigDecimal volume = positions.getDonePrice().multiply(new BigDecimal(positions.getSourceSize()));
                marketCap.setAmount(volume.toString());
                list.add(marketCap);
            } else {
                //平仓
                OrderMetadata metadata = preCreateOrderMetadata(positions);
                metadata.setType(OrderEnum.CLOSE.getType());
                positions.getSourcePrice().subtract(positions.getDonePrice()).multiply(new BigDecimal(positions.getSourceSize())).toString();
                metadata.setAmount(pl.toString());//计算实现盈亏
                list.add(metadata);

                //平仓市值
                OrderMetadata marketCap = preCreateOrderMetadata(positions);
                marketCap.setType(OrderEnum.CLOSE_MARKET_CAP.getType());
                BigDecimal volume = positions.getDonePrice().multiply(new BigDecimal(positions.getSourceSize()));
                marketCap.setAmount(volume.toString());
                list.add(marketCap);
            }
        }
        return list;
    }

    @Override
    @Transactional
    public void saveData(UserPositions positions) {
        //转换成订单
        if (positions == null)
            return;
        if (UserPositions.CLOSING.equals(positions.getPositionStatus())) {
            //平仓传入反向订单
            positions.setTransactionType(positions.getTransactionType().equalsIgnoreCase(UserPositions.BID) ? UserPositions.ASK : UserPositions.BID);//设置方向交易
            positions.setTradeType(positions.getTradeType().equalsIgnoreCase(UserPositions.LONG) ? UserPositions.SHORT : UserPositions.LONG);//设置方向交易
        }

        List<OrderMetadata> list = determineType(positions);
        if (CollectionUtils.isEmpty(list))
            return;
        this.saveBatch(list);
    }

    public static synchronized String getSymbolDesc(String currency, String currencyCode) {
        if (SYMBOL_MAP.containsKey(currency))
            return SYMBOL_MAP.get(currency);

        if (StringUtils.isEmpty(currencyCode))
            return null;
        String code = currencyCode.split("-")[0];
        SYMBOL_MAP.put(currency, code);
        return code;
    }

    public static final Map<String, String> SYMBOL_MAP = new HashMap<>();
}
