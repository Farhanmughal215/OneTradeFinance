package com.xstocks.uc.pojo.enums;

import com.xstocks.uc.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author firtuss
 */

@Getter
@AllArgsConstructor
public enum TxTypeEnum {
    /**
     * 充值
     */
    DEPOSIT(0, "deposit"),
    /**
     * 系统放贷(授信)
     */
    LOAN(1, "loan"),
    /**
     * 自动还钱
     */
    REPAYMENT(2, "repayment"),
    /**
     * 提现
     */
    WITHDRAW(3, "withdraw"),
    /**
     * 授权
     */
    AUTHORIZE(4, "authorize"),
    /**
     * 划转转出
     */
    TRANSFER_OUT(5, "transfer out"),
    /**
     * 划转转入
     */
    TRANSFER_IN(6, "transfer in"),
    /**
     * 系统强制还贷(解除授信)
     */
    CANCEL_LOAN(7,"cancel loan"),
    /**
     * 上传截图充值
     */
    DEPOSIT_BY_UPLOAD_EVIDENCE(8,"deposit by upload evidence");




    private final int code;
    private final String desc;

    public static TxTypeEnum getByCode(int code) {
        for (TxTypeEnum value : TxTypeEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new BizException(ErrorCode.ILLEGAL_REQUEST);
    }
}
