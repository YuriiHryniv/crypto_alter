package com.negeso.crypto.data.job;

import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.negeso.crypto.data.dto.TopGainerDto;
import com.negeso.crypto.data.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface BinanceFetcherJob {
    void call();

    void clear();

    List<TopGainerDto> getGainers();

    List<AssetBalance> getBalance(String key, String secret);

    BigDecimal getTotalCapitalization(List<AssetBalance> list);

    boolean exists(String codeName);

    void placeBuyMarketOrder(String symbol, String quantity, User user);

    void sellBuyMarketOrder(String symbol, String quantity, User user);

    String getPrice(String symbol);
}
