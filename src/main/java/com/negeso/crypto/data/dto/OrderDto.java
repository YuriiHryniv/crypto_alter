package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @JsonProperty("currencySymbol")
    private String currencySymbol;
    @JsonProperty("operationType")
    private String operationType;
    @JsonProperty("orderType")
    private String orderType;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("periodicity")
    private String periodicity;
    @JsonProperty("threshold")
    private String threshold;
    @JsonProperty("tradable")
    private String tradable;
    @JsonProperty("typeOfPayment")
    private String typeOfPayment;
    @JsonProperty("currentPrice")
    private String currentPrice;
}
