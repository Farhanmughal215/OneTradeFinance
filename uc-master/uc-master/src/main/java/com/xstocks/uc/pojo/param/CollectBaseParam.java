package com.xstocks.uc.pojo.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName CollectBaseParam
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/14 16:28
 **/

@Data
@Accessors(chain = true)
public class CollectBaseParam {
    private Long userId;
    private Long tickerId;
    private Integer isDel;
}
