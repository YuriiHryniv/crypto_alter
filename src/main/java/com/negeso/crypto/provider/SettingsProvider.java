package com.negeso.crypto.provider;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SettingsProvider {
    private String apiKey;
    private String apiSecret;
}
