package com.xstocks.uc.pojo.enums;

import com.xstocks.uc.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum VIPEnum {
    VIP1("vip1", "VIP1", new BigDecimal("0.000000000000000000"), new BigDecimal("1000.000000000000000000")),
    VIP2("vip2", "VIP2", new BigDecimal("1000.000000000000000000"), new BigDecimal("3000.000000000000000000")),
    VIP3("vip3", "VIP3", new BigDecimal("3000.000000000000000000"), new BigDecimal("10000.000000000000000000")),
    VIP4("vip4", "VIP4", new BigDecimal("10000.000000000000000000"), new BigDecimal("50000.000000000000000000")),
    VIP5("vip5", "VIP5", new BigDecimal("50000.000000000000000000"), new BigDecimal("100000.000000000000000000")),
    VIP6("vip6", "VIP6", new BigDecimal("100000.000000000000000000"), new BigDecimal("250000.000000000000000000")),
    VIP7("vip7", "VIP7", new BigDecimal("250000.000000000000000000"), new BigDecimal(Integer.MAX_VALUE));

    private final String name;
    private final String value;
    private final BigDecimal depositThresholdLow;
    private final BigDecimal depositThresholdHigh;

    public static VIPEnum getByName(String name) {
        for (VIPEnum value : VIPEnum.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new BizException(ErrorCode.ILLEGAL_REQUEST);
    }

    public static VIPEnum getByDepositTotalAmount(BigDecimal amount) {
        for (VIPEnum value : VIPEnum.values()) {
            if (amount.compareTo(value.getDepositThresholdLow()) >= 0 && amount.compareTo(value.getDepositThresholdHigh()) < 0) {
                return value;
            }
        }
        return VIP1;
    }
}
