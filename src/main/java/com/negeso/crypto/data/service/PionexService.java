package com.negeso.crypto.data.service;

import com.negeso.crypto.data.dto.BalanceWrapper;
import com.negeso.crypto.data.dto.OrderDto;
import com.negeso.crypto.data.dto.PionexBalance;
import com.negeso.crypto.data.entity.PionexCurrency;

import java.math.BigDecimal;
import java.util.List;

public interface PionexService {
    BalanceWrapper getAccountBalance(String apiKey, String apiSecret);
    void updateCurrencies();
    void identifyTopGainers();
    void trade();
    BigDecimal calculateEstimatedCapitalization(List<PionexBalance> balance);
    List<PionexCurrency> getCurrenciesByFilter(String filter);
    List<PionexCurrency> getAllCurrencies();
    void updateTradedCurrency(PionexCurrency currency);
    boolean postAnOrder(String apiKey, String apiSecret, OrderDto orderDto, BigDecimal currentCurrencyPrice);
    boolean currencyExists(String currencyName);
    PionexCurrency findCurrencyById(String id);
}
