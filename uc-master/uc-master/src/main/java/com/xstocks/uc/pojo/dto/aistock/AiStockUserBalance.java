package com.xstocks.uc.pojo.dto.aistock;

import lombok.Data;

@Data
public class AiStockUserBalance {
    private Double realizedPL = 0.00;
    private Double unrealizedPL = 0.00;
    private Double margin = 0.00;
}
