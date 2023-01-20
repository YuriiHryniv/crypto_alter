package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PionexTrades {
    @JsonProperty("trades")
    private List<PionexTrade> trades;
}
