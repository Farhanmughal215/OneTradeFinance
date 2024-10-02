package com.xstocks.uc.pojo.dto.polygo.ws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Q_DTO extends BaseDTO {

    private String sym;

    private Double bp;

    private Double ap;
}
