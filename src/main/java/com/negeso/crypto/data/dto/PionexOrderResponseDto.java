package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PionexOrderResponseDto {
    @JsonProperty("data")
    private PionexOrderResponseData data;
    @JsonProperty("result")
    private Boolean result;
    @JsonProperty("timestamp")
    private Long timestamp;
}
