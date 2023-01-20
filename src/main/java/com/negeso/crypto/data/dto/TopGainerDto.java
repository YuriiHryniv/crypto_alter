package com.negeso.crypto.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopGainerDto {
    private String currencyName;
    private String priceDifferencePercentage;
    private String priceDifferenceAbsolute;
}
