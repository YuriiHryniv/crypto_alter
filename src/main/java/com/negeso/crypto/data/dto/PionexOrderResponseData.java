package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PionexOrderResponseData {
    @JsonProperty("orderId")
    private Long orderId;
    @JsonProperty("data")
    private String clientOrderId;
}
