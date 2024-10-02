package com.xstocks.statistics.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DexEntity {

    private String orderId;

    private Double marginAmount;

    private Double pl;

    private String positionStatus;
}
