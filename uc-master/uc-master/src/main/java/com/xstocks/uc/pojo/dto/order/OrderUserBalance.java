package com.xstocks.uc.pojo.dto.order;

import lombok.Data;

/**
 * @ClassName UserBalance
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/15 16:23
 **/

@Data
public class OrderUserBalance {
    private String balanceAvailable = "0.00";

    private String balanceProfit = "0.00";

    private String closeOutLine = "0.00";

    private String freezeMargin = "0.00";

}
