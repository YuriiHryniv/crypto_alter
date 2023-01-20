package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketDataWrapper {
    @JsonProperty("data")
    private SymbolsData data;
    @JsonProperty("result")
    private Boolean result;
    @JsonProperty("timestamp")
    private Long timestamp;
}
