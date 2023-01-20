package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PionexTrade {
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("tradeId")
    private String tradeId;
    @JsonProperty("price")
    private String price;
    @JsonProperty("size")
    private String size;
    @JsonProperty("side")
    private String side;
    @JsonProperty("timestamp")
    private Long timestamp;
}
