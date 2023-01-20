package com.negeso.crypto.data.job.impl;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import com.binance.api.client.exception.BinanceApiException;
import com.negeso.crypto.config.BinanceRestConfig;
import com.negeso.crypto.data.dto.TopGainerDto;
import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.negeso.crypto.data.service.PriceService;
import com.negeso.crypto.data.service.impl.CodeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.binance.api.client.domain.account.NewOrder.marketBuy;
import static com.binance.api.client.domain.account.NewOrder.marketSell;

@Component
@AllArgsConstructor
@Slf4j
@EnableScheduling
public class BinanceFetcherJobImpl implements BinanceFetcherJob {
    private final CodeService codeService;
    private final PriceService priceService;
    private final BinanceRestConfig binanceRestConfig;

    @Scheduled(fixedRate = 120_000)
    public void call() {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(null, null);
        List<TickerPrice> tickerPrices = binanceApiRestClient.getAllPrices();
        BigDecimal multiply = new BigDecimal("100000000");
        tickerPrices.forEach(tickerPrice -> {
            String codeName = tickerPrice.getSymbol();
            Code saveCode = codeService.get(codeName);
            String price = tickerPrice.getPrice();
            priceService.save(saveCode, price, LocalDateTime.now());
        });
        log.info(LocalDateTime.now() + " : data was fetched from binance");
    }

    @Scheduled(fixedRate = 120_000)
    public void clear() {
        priceService.clear(LocalDateTime.now().minusHours(4));
    }

    public List<AssetBalance> getBalance(String apiKey, String apiSecret) {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(apiKey, apiSecret);
        log.info("---------- " + LocalDateTime.now() + " : balance query");
        return binanceApiRestClient
                .getAccount()              // baqJ0LEdPXexHLLodJArryurI8RWrjlDH2Qvwt5VSf8ROIMObTJI17JIoMKhb13U
                .getBalances()             // quLcHDCU6KCK4RfYJZepVAcf4zSIhtLTmyNUXSqljs00rw3XVD0wiHibgLOezTFR
                .stream()
                .filter(n -> new BigDecimal(n.getFree()).compareTo(BigDecimal.ZERO) > 0)
                .toList();

    }

    public List<TopGainerDto> getGainers() {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(null, null);
        List<TopGainerDto> topGainerDtos = new ArrayList<>();
        List<TickerStatistics> all24HrPriceStatistics = binanceApiRestClient.getAll24HrPriceStatistics();
        all24HrPriceStatistics.forEach(stat -> {
            TopGainerDto topGainerDto = new TopGainerDto();
            topGainerDto.setCurrencyName(stat.getSymbol());
            topGainerDto.setPriceDifferencePercentage(stat.getPriceChangePercent() + "%");
            topGainerDto.setPriceDifferenceAbsolute(stat.getPriceChange() + "$");
            topGainerDtos.add(topGainerDto);
        });
        log.info("---------- " + LocalDateTime.now() + " : gainers query");
        return topGainerDtos;
    }

    public BigDecimal getTotalCapitalization(List<AssetBalance> balances) {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(null, null);
        BigDecimal total = BigDecimal.ZERO;
        for (AssetBalance asset: balances) {
            BigDecimal currentPrice =
                    new BigDecimal(binanceApiRestClient.getPrice(asset.getAsset() + "USDT").getPrice());
            BigDecimal amount = new BigDecimal(asset.getFree());
            total = total.add(currentPrice.multiply(amount));
        }
        return total;
    }

    public void placeBuyMarketOrder(String symbol, String quantity, User user) {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(user.getBinanceApiKey(),
                user.getBinanceApiSecret());
        NewOrderResponse newOrderResponse = binanceApiRestClient.newOrder(marketBuy(symbol, quantity));

        binanceApiRestClient.newOrderTest(marketBuy(symbol, quantity));
        log.info("---------- " + LocalDateTime.now() + " : buy market order was placed. Crypto: "
                + symbol + ", quantity: " + quantity + ", user: " + user.getEmail());

    }

    public void sellBuyMarketOrder(String symbol, String quantity, User user) {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(user.getBinanceApiKey(),
                user.getBinanceApiSecret());
        NewOrderResponse newOrderResponse = binanceApiRestClient.newOrder(marketSell(symbol, quantity));
        log.info("---------- " + LocalDateTime.now() + " : sell market order was placed. Crypto: "
                + symbol + ", quantity: " + quantity + ", user: " + user.getEmail());
    }

    public void getOrders(String key, String secret) {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(key, secret);
    }


    public String getPrice(String symbol) {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(null, null);
        return binanceApiRestClient.getPrice(symbol).getPrice();
    }

    public boolean exists(String codeName) {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(null, null);
        try {
            return binanceApiRestClient.getPrice(codeName + "USDT") != null;
        } catch (BinanceApiException e) {
            return false;
        }
    }
}
