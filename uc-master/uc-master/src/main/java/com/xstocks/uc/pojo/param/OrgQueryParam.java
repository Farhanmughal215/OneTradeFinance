package com.xstocks.uc.pojo.param;

import com.xstocks.uc.pojo.param.PageQueryParam;
import lombok.Data;

/**
 * @ClassName OrgQueryParam
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/21 19:34
 **/

@Data
public class OrgQueryParam extends PageQueryParam {
    private Integer level;

    private String code;
}
