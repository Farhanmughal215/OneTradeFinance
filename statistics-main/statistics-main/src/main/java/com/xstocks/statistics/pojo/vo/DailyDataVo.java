package com.xstocks.statistics.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class DailyDataVo {

    private String statisticsDate;
    private String newReg;
    private String regSum;
    private String tradeSum;
    private String longSum;
    private String closeSum;
    private String breakSum;
    private String profitLossSum;
    private String feeSum;
    private String dailyFee;
    private String interestSum;
    private String dailyInterest;

    // 自定义的 setter 方法，覆盖 Lombok 自动生成的 setter 方法
    public void setNewReg(String newReg) {
        this.newReg = formatValue(newReg);
    }

    public void setRegSum(String regSum) {
        this.regSum = formatValue(regSum);
    }

    public void setTradeSum(String tradeSum) {
        this.tradeSum = formatValue(tradeSum);
    }

    public void setLongSum(String longSum) {
        this.longSum = formatValue(longSum);
    }

    public void setCloseSum(String closeSum) {
        this.closeSum = formatValue(closeSum);
    }

    public void setBreakSum(String breakSum) {
        this.breakSum = formatValue(breakSum);
    }

    public void setProfitLossSum(String profitLossSum) {
        this.profitLossSum = formatValue(profitLossSum);
    }

    public void setFeeSum(String feeSum) {
        this.feeSum = formatValue(feeSum);
    }

    public void setDailyFee(String dailyFee) {
        this.dailyFee = formatValue(dailyFee);
    }

    public void setInterestSum(String interestSum) {
        this.interestSum = formatValue(interestSum);
    }

    public void setDailyInterest(String dailyInterest) {
        this.dailyInterest = formatValue(dailyInterest);
    }


    // Helper method to format the value and remove trailing zeros
    private String formatValue(String value) {
        if (value == null || value.isEmpty()) {
            return "0";
        }
        try {
            BigDecimal decimalValue = new BigDecimal(value);
            decimalValue = decimalValue.stripTrailingZeros();
            return decimalValue.compareTo(BigDecimal.ZERO) == 0 ? "0" : decimalValue.toPlainString();
        } catch (NumberFormatException e) {
            // 记录日志或处理异常
            System.err.println("Invalid number format: " + value);
            return "0"; // 或者根据需求抛出异常或返回其他默认值
        }
    }

}