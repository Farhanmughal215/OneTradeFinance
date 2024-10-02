package com.xstocks.uc.pojo.param.polygo.ws;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SubscribeParam {
    private String action;

    private String params;
}
