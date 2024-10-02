package com.xstocks.uc.pojo.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Kevin
 * @date 2024/3/11 15:48
 * @apiNote
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BQueryTraderParam extends PageQueryParam {

    private Integer status;

    private String address;

}
