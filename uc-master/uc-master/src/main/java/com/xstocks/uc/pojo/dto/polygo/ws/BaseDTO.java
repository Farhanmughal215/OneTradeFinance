package com.xstocks.uc.pojo.dto.polygo.ws;

import com.xstocks.uc.pojo.enums.PolygoWebSocketEvEnum;
import lombok.Data;

@Data
public class BaseDTO {
    private PolygoWebSocketEvEnum ev;
}
