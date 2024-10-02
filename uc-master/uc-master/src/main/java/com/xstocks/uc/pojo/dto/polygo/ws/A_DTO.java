package com.xstocks.uc.pojo.dto.polygo.ws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class A_DTO extends BaseDTO {
    private String sym;

    private Long v;

    private Long av;

    private Double op;

    private Double vw;

    private Double o;

    private Double c;

    private Double h;

    private Double l;

    private Double a;

    private Integer z;

    private Long s;

    private Long e;
}
