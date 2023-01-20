package com.negeso.crypto.config;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.provider.SettingsProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@AllArgsConstructor
public class BinanceRestConfig {
    private SettingsProvider settingsProvider;
    private UserDetailsService userDetailsService;


    public BinanceApiRestClient binanceApiRestClient(String apiKey, String apiSecret) {
        return BinanceApiClientFactory.newInstance(apiKey, apiSecret).newRestClient();
    }
}
