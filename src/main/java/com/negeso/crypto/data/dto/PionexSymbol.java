package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PionexSymbol {
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("type")
    private String type;
    @JsonProperty("baseCurrency")
    private String baseCurrency;
    @JsonProperty("quoteCurrency")
    private String quoteCurrency;
    @JsonProperty("basePrecision")
    private Integer basePrecision;
    @JsonProperty("quotePrecision")
    private Integer quotePrecision;
    @JsonProperty("amountPrecision")
    private Integer amountPrecision;
    @JsonProperty("minAmount")
    private String minAmount;
    @JsonProperty("minTradeSize")
    private String minTradeSize;
    @JsonProperty("maxTradeSize")
    private String maxTradeSize;
    @JsonProperty("minTradeDumping")
    private String minTradeDumping;
    @JsonProperty("maxTradeDumping")
    private String maxTradeDumping;
    @JsonProperty("enable")
    private Boolean enable;
    @JsonProperty("buyCeiling")
    private String buyCeiling;
    @JsonProperty("sellFloor")
    private String sellFloor;
}
