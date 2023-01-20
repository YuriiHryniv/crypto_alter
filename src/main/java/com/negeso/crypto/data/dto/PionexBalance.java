package com.negeso.crypto.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PionexBalance {
    @JsonProperty("coin")
    private String coin;
    @JsonProperty("free")
    private String free;
    @JsonProperty("frozen")
    private String frozen;
}
