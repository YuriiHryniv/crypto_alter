package com.negeso.crypto.data.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.negeso.crypto.data.dto.BalanceWrapper;
import com.negeso.crypto.data.dto.MarketDataWrapper;
import com.negeso.crypto.data.dto.MarketTradesWrapper;
import com.negeso.crypto.data.dto.OrderDto;
import com.negeso.crypto.data.dto.PionexBalance;
import com.negeso.crypto.data.dto.PionexOrderResponseDto;
import com.negeso.crypto.data.dto.PionexSymbol;
import com.negeso.crypto.data.entity.PionexCurrency;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.repository.PionexCurrencyRepository;
import com.negeso.crypto.data.repository.UserRepository;
import com.negeso.crypto.data.service.ExcelExportService;
import com.negeso.crypto.data.service.PionexService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
@Service
@Log4j2

public class PionexServiceImpl implements PionexService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PionexCurrencyRepository pionexCurrencyRepository;
    private final ExcelExportService excelExportService;
    private static final String BASE_URL = "https://api.pionex.com";
    private static final String ORDER_URL = "/api/v1/trade/order";
    private static final String BALANCE_URL = "/api/v1/account/balances";
    private static final String MARKET_DATA_URL = "/api/v1/common/symbols";
    private static final String MARKET_TRADES_URL = "/api/v1/market/trades";
    private static final BigDecimal DEFAULT_BUYING_THRESHOLD_PERCENTAGE = new BigDecimal(0.03)
            .round(new MathContext(3, RoundingMode.HALF_UP));
    private static final BigDecimal DEFAULT_BUYING_THRESHOLD_ABSOLUTE = new BigDecimal(0.05)
            .round(new MathContext(3, RoundingMode.HALF_UP));
    private static final BigDecimal DEFAULT_SELLING_THRESHOLD_PERCENTAGE = new BigDecimal(0.03)
            .round(new MathContext(3, RoundingMode.HALF_UP));
    private static final BigDecimal DEFAULT_SELLING_THRESHOLD_ABSOLUTE = new BigDecimal(0.05)
            .round(new MathContext(3, RoundingMode.HALF_UP));


    @Autowired
    public PionexServiceImpl(RestTemplate restTemplate,
                             UserRepository userRepository,
                             PionexCurrencyRepository pionexCurrencyRepository,
                             ExcelExportService excelExportService) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.pionexCurrencyRepository = pionexCurrencyRepository;
        this.excelExportService = excelExportService;
    }

    @Override
    public BalanceWrapper getAccountBalance(String apiKey, String apiSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("PIONEX-KEY", apiKey);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = HttpMethod.GET.name()
                .concat(BALANCE_URL)
                .concat("?timestamp=")
                .concat(timestamp);
        signature = encodeSignature(apiSecret, signature);
        headers.set("PIONEX-SIGNATURE", signature);
        String url = BASE_URL.concat(BALANCE_URL).concat("?timestamp=").concat(timestamp);
        HttpEntity entity = new HttpEntity(null, headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, BalanceWrapper.class).getBody();
    }

    @Override
  //  @Scheduled(fixedDelay = 120000, initialDelay = 100)
    public void updateCurrencies() {
        long startingTime = System.currentTimeMillis();
        long currentTime;
        int requestWeight = 0;
        String url = BASE_URL.concat(MARKET_DATA_URL);
        List<PionexSymbol> currencies = restTemplate.getForObject(url, MarketDataWrapper.class)
                .getData()
                .getSymbols()
                .stream()
                .filter(x -> x.getEnable().equals(true) && x.getType().equals("SPOT")
                        && x.getQuoteCurrency().equals("USDT"))
                .toList();
        requestWeight += 5;
        List<PionexCurrency> dbCurrencies = pionexCurrencyRepository.findAll();
        Map<String, PionexCurrency> dbCurrenciesNames = dbCurrencies
                .stream()
                .collect(Collectors.toMap(PionexCurrency::getCurrencyName, Function.identity()));
        for (PionexSymbol symbol : currencies) {
            if (dbCurrenciesNames.containsKey(symbol.getBaseCurrency())) {
                PionexCurrency currencyFromDb = dbCurrenciesNames.get(symbol.getBaseCurrency());
                updatePrices(currencyFromDb, symbol);
            } else {
                saveNewCurrency(symbol);
            }
            requestWeight += 1;
            currentTime = System.currentTimeMillis();
            if (requestWeight == 9) {
                try {
                    Thread.sleep(1000 - (currentTime - startingTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (currentTime - startingTime > 1000) {
                do {
                    startingTime += 1000;
                } while (currentTime - startingTime > 1000);
                requestWeight = 0;
            }
        }
    }

    @Override
    //@Scheduled(fixedDelay = 120000, initialDelay = 180000)
    public void identifyTopGainers() {
        List<PionexCurrency> currencies = pionexCurrencyRepository.findAll()
                .stream().filter(this::isGainer).toList();
        pionexCurrencyRepository.clearTopGainers();
        currencies.forEach(x -> pionexCurrencyRepository.addTopGainers(x.getCurrencyName()));
    }

    @Override
    //@Scheduled(fixedDelay = 120000, initialDelay = 720000)
    public void trade() {
        List<User> tradeableUsers = userRepository.findAllByAutoTradingTrue();
        log.info("Users who are willing to trade: " + tradeableUsers.stream().map(User::getEmail).toList());
        List<PionexCurrency> tradeableCurrencies = pionexCurrencyRepository.findAllByAutoTradingTrue();
        log.info("Currencies available for trading: " + tradeableCurrencies.stream()
                .map(PionexCurrency::getCurrencyName).toList());
        for (User user : tradeableUsers) {
            List<PionexBalance> balance = getAccountBalance(user.getPionexApiKey(), user.getPionexApiSecret())
                    .getData().getBalances();
            BigDecimal availableUsdt = null;
            for (PionexBalance currency : balance) {
                if (currency.getCoin().equals("USDT")) {
                    availableUsdt = new BigDecimal(currency.getFree());
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (availableUsdt != null
                    && (availableUsdt.compareTo(user.getGamblingLimit()) > 0)
                    && (user.getCurrentNumberOfGambles() < user.getMaxConcurrentGambles())) {
                buy(user, tradeableCurrencies, availableUsdt);
            }
            sell(user, tradeableCurrencies, balance);
        }
    }

    @Override
    public BigDecimal calculateEstimatedCapitalization(List<PionexBalance> balance) {
        BigDecimal result = BigDecimal.ZERO;
        for (PionexBalance currency : balance) {
            if (!currency.getCoin().equals("USDT")) {
                Optional<PionexCurrency> dbCurrency = pionexCurrencyRepository.findById(currency.getCoin());
                if (dbCurrency.isPresent()) {
                    BigDecimal amount = new BigDecimal(currency.getFree()).add(new BigDecimal(currency.getFrozen()));
                    result = result.add(amount.multiply(dbCurrency.get().getCurrentPrice()));
                }
            } else {
                result = result.add(new BigDecimal(currency.getFree()).add(new BigDecimal(currency.getFrozen())));
            }
        }
        return result;
    }

    @Override
    public List<PionexCurrency> getCurrenciesByFilter(String filter) {
        return pionexCurrencyRepository.findCurrenciesByFilter(filter);
    }

    @Override
    public List<PionexCurrency> getAllCurrencies() {
        return pionexCurrencyRepository.findAll();
    }

    @Override
    public void updateTradedCurrency(PionexCurrency currency) {
        pionexCurrencyRepository.updateTradedCurrency(currency.getAutoTrading(),
                currency.getBuyingThresholdPercentage()
                        .divide(BigDecimal.valueOf(100)),
                currency.getBuyingThresholdAbsolute(),
                currency.getSellingThresholdPercentage()
                        .divide(BigDecimal.valueOf(100)),
                currency.getSellingThresholdAbsolute(),
                currency.getCurrencyName());
    }

    @Override
    public boolean postAnOrder(String apiKey, String apiSecret, OrderDto orderDto,
                            BigDecimal currentCurrencyPrice) {
        orderDto.setSymbol(orderDto.getSymbol().concat("_USDT"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("PIONEX-KEY", apiKey);
        ObjectWriter objectWriter = new ObjectMapper().writer();
        String json = null;
        try {
            json = objectWriter.writeValueAsString(orderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = HttpMethod.POST.name()
                .concat(ORDER_URL)
                .concat("?timestamp=")
                .concat(timestamp)
                .concat(json);
        signature = encodeSignature(apiSecret, signature);
        headers.set("PIONEX-SIGNATURE", signature);
        String url = BASE_URL.concat(ORDER_URL).concat("?timestamp=").concat(timestamp);
        HttpEntity<OrderDto> entity = new HttpEntity(orderDto, headers);
        String success = "SUCCESS";
        PionexOrderResponseDto response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, PionexOrderResponseDto.class).getBody();
        } catch (Exception e) {
            log.error("Order request failed", e);
            success = "FAILURE";
        }
        if (response == null
                || response != null && response.getResult() == null
                || !response.getResult().equals(true)) {
            success = "FAILURE";
        }
        log.info("Response: " + response + " was received for order: " + orderDto);
        BigDecimal totalOrderPrice = switch (orderDto.getOperationType()) {
            case "BUY" -> currentCurrencyPrice.multiply(new BigDecimal(orderDto.getAmount()));
            case "SELL" -> currentCurrencyPrice.multiply(new BigDecimal(orderDto.getSize()));
            default -> throw new RuntimeException("Invalid order operation type");
        };
        excelExportService.writeCsvOrderToFile(orderDto, currentCurrencyPrice, totalOrderPrice, success);
        return success.equals("SUCCESS");
    }

    @Override
    public boolean currencyExists(String currencyName) {
        return pionexCurrencyRepository.existsByCurrencyName(currencyName);
    }

    @Override
    public PionexCurrency findCurrencyById(String id) {
        return pionexCurrencyRepository.findById(id).get();
    }

    private void buy(User user, List<PionexCurrency> tradeableCurrencies, BigDecimal availableUsdt) {
        List<PionexCurrency> availableCurrencies = tradeableCurrencies.stream()
                .filter(x ->
                        (x.getMinOrderAmount().multiply(x.getCurrentPrice()).compareTo(user.getGamblingLimit()) < 0)
                                && (x.getCurrentPrice().compareTo(x.getTwoMinPrice()) > 0)
                                && (x.getCurrentPrice().compareTo(x.getFourMinPrice()) > 0)
                                && (x.getCurrentPrice().subtract(x.getTwoMinPrice()).compareTo(x.getBuyingThresholdAbsolute()) > 0)
                                && (x.getCurrentPrice().divide(x.getTwoMinPrice(), 1000, RoundingMode.HALF_UP)
                                .subtract(BigDecimal.ONE).compareTo(x.getBuyingThresholdPercentage()) > 0)
                                && (isGainer(x)))
                .toList();
        log.info("User " + user.getEmail() + " can buy: " + availableCurrencies.stream()
                .map(PionexCurrency::getCurrencyName).toList());
        int requestWeight = 0;
        long startingTime = System.currentTimeMillis();
        long currentTime;

        for (PionexCurrency currency : availableCurrencies) {
            if (user.getCurrentNumberOfGambles() >= user.getMaxConcurrentGambles()) {
                return;
            }
            BigDecimal orderAmount = getBuyingOrderAmount(user, currency);
            BigDecimal orderPrice = orderAmount.multiply(currency.getCurrentPrice(), new MathContext(currency.getPrecision(), RoundingMode.FLOOR));
            OrderDto order = new OrderDto(currency.getCurrencyName(),
                    "BUY",
                    "MARKET",
                    null,
                    orderAmount.toPlainString());
            if (availableUsdt.compareTo(orderPrice) > 0) {
                boolean success = postAnOrder(user.getPionexApiKey(), user.getPionexApiSecret(), order, currency.getCurrentPrice());
                if (success) {
                    availableUsdt = availableUsdt.subtract(orderPrice);
                    user.setCurrentNumberOfGambles(user.getCurrentNumberOfGambles() + 1);
                    userRepository.updateCurrentNumberOfGambles(user.getEmail(), user.getCurrentNumberOfGambles());
                }
            }
            requestWeight += 1;
            currentTime = System.currentTimeMillis();
            if (requestWeight == 9) {
                try {
                    Thread.sleep(1000 - (currentTime - startingTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (currentTime - startingTime > 1000) {
                do {
                    startingTime += 1000;
                } while (currentTime - startingTime > 1000);
                requestWeight = 0;
            }
        }
    }

    private void sell(User user, List<PionexCurrency> tradeableCurrencies, List<PionexBalance> balance) {
        Map<String, String> currenciesUserHas = new HashMap<>();
        for (PionexBalance balanceCurrency : balance) {
            currenciesUserHas.put(balanceCurrency.getCoin(), balanceCurrency.getFree());
        }
        List<PionexCurrency> availableCurrencies = tradeableCurrencies.stream()
                .filter(x ->
                        ((currenciesUserHas.containsKey(x.getCurrencyName())
                                && new BigDecimal(currenciesUserHas.get(x.getCurrencyName()))
                                .compareTo(x.getMinOrderAmount()) > 0
                                && (x.getCurrentPrice().compareTo(x.getTwoMinPrice()) < 0)
                                && (x.getCurrentPrice().compareTo(x.getFourMinPrice()) < 0)
                                && (x.getCurrentPrice().subtract(x.getFourMinPrice())
                                .compareTo(x.getSellingThresholdAbsolute()) > 0)
                                && (x.getCurrentPrice().divide(x.getFourMinPrice(), 1000, RoundingMode.HALF_UP)
                                .subtract(BigDecimal.ONE).compareTo(x.getBuyingThresholdPercentage()) > 0))
                                || (currenciesUserHas.containsKey(x.getCurrencyName())
                                && new BigDecimal(currenciesUserHas.get(x.getCurrencyName()))
                                .compareTo(x.getMinOrderAmount()) > 0
                                && (x.getCurrentPrice().divide(x.getTenMinPrice(), 1000, RoundingMode.HALF_UP)
                                .subtract(BigDecimal.ONE).abs().compareTo(BigDecimal.valueOf(0.001)) < 0))))
                .toList();
        log.info("User " + user.getEmail() + " can sell: " + availableCurrencies.stream()
                .map(PionexCurrency::getCurrencyName).toList());
        int requestWeight = 0;
        long startingTime = System.currentTimeMillis();
        long currentTime;
        for (PionexCurrency currency : availableCurrencies) {
            String volume = getSellingAmount(currenciesUserHas.get(currency.getCurrencyName()),
                    currency);
            OrderDto order = new OrderDto(currency.getCurrencyName(),
                    "SELL",
                    "MARKET",
                    volume,
                    null);
            boolean success = postAnOrder(user.getPionexApiKey(), user.getPionexApiSecret(), order, currency.getCurrentPrice());
            if (success) {
                user.setCurrentNumberOfGambles(user.getCurrentNumberOfGambles() - 1);
                userRepository.updateCurrentNumberOfGambles(user.getEmail(), user.getCurrentNumberOfGambles());
            }
            requestWeight += 1;
            currentTime = System.currentTimeMillis();
            if (requestWeight == 9) {
                try {
                    Thread.sleep(1000 - (currentTime - startingTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (currentTime - startingTime > 1000) {
                do {
                    startingTime += 1000;
                } while (currentTime - startingTime > 1000);
                requestWeight = 0;
            }
        }
    }

    private String getSellingAmount(String volume, PionexCurrency currency) {
        return new BigDecimal(volume).round(new MathContext(currency.getPrecision(), RoundingMode.FLOOR))
                .toPlainString();
    }

    private BigDecimal getBuyingOrderAmount(User user, PionexCurrency currency) {
        return user.getGamblingLimit().divide(currency.getCurrentPrice(),
                currency.getPrecision(), RoundingMode.FLOOR);
    }

    private boolean isGainer(PionexCurrency currency) {
        int count = 0;
        if (currency.getTwoMinPrice().compareTo(currency.getCurrentPrice()) > 0) {
            return false;
        }
        if (currency.getFourMinPrice().compareTo(currency.getCurrentPrice()) < 0) {
            count++;
        }
        if (currency.getSixMinPrice().compareTo(currency.getCurrentPrice()) < 0) {
            count++;
        }
        if (currency.getEightMinPrice().compareTo(currency.getCurrentPrice()) < 0) {
            count++;
        }
        if (currency.getTenMinPrice().compareTo(currency.getCurrentPrice()) < 0) {
            count++;
        }
        return count >= 3;
    }

    private void saveNewCurrency(PionexSymbol symbol) {
        PionexCurrency currency = new PionexCurrency(
                symbol.getBaseCurrency(),
                resolveSymbolPrice(symbol),
                new BigDecimal(symbol.getMinAmount()),
                new BigDecimal(symbol.getMaxTradeDumping()),
                DEFAULT_BUYING_THRESHOLD_PERCENTAGE,
                DEFAULT_BUYING_THRESHOLD_ABSOLUTE,
                DEFAULT_SELLING_THRESHOLD_PERCENTAGE,
                DEFAULT_SELLING_THRESHOLD_ABSOLUTE,
                false,
                false,
                symbol.getBasePrecision()
        );
        pionexCurrencyRepository.save(currency);
    }

    private void updatePrices(PionexCurrency currencyFromDb, PionexSymbol symbol) {
        currencyFromDb.setTenMinPrice(currencyFromDb.getEightMinPrice());
        currencyFromDb.setEightMinPrice(currencyFromDb.getSixMinPrice());
        currencyFromDb.setSixMinPrice(currencyFromDb.getFourMinPrice());
        currencyFromDb.setFourMinPrice(currencyFromDb.getTwoMinPrice());
        currencyFromDb.setTwoMinPrice(currencyFromDb.getCurrentPrice());
        currencyFromDb.setCurrentPrice(resolveSymbolPrice(symbol));
        currencyFromDb.setPrecision(symbol.getBasePrecision());
        currencyFromDb.setMinOrderAmount(new BigDecimal(symbol.getMinAmount()));
        pionexCurrencyRepository.save(currencyFromDb);
    }

    private BigDecimal resolveSymbolPrice(PionexSymbol symbol) {
        String url = BASE_URL.concat(MARKET_TRADES_URL)
                .concat("?symbol=")
                .concat(symbol.getSymbol())
                .concat("&limit=1");
        return new BigDecimal(restTemplate.getForObject(url, MarketTradesWrapper.class).getData().getTrades().get(0).getPrice());
    }

    private String encodeSignature(String key, String signature) {
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Hex.encodeHexString(sha256_HMAC.doFinal(signature.getBytes(StandardCharsets.UTF_8)));
    }

}
*/
