package com.xstocks.uc.pojo.dto.polygo.ws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xstocks.uc.pojo.enums.PolygoWebSocketStatusEnum;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusDTO extends BaseDTO{
    private PolygoWebSocketStatusEnum status;
    private String message;
}
