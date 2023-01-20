package com.negeso.crypto.data.job.impl;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.negeso.crypto.config.BinanceRestConfig;
import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.Price;
import com.negeso.crypto.data.entity.UserOrder;
import com.negeso.crypto.data.job.BinanceUpdateJob;
import com.negeso.crypto.data.service.PriceService;
import com.negeso.crypto.data.service.UserOrderService;
import com.negeso.crypto.data.service.impl.CodeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
@EnableScheduling
public class BinanceUpdateJobImpl implements BinanceUpdateJob {
    private final BinanceRestConfig binanceRestConfig;
    private final UserOrderService userOrderService;
    private final PriceService priceService;
    private final CodeService codeService;

    // Production
    /*
    @Scheduled(fixedRate = 60_000)
    public void updateMaxValues() {
        List<UserOrder> orders = userOrderService.getAllTradable();
        if (!orders.isEmpty()) {
            for (int i = 0; i < orders.size(); i++) {
                User user = orders.get(i).getUser();
                List<Code> codes = user.getCodes();
                int finalI = i;
                Code code = codes
                        .stream()
                        .filter(c -> c.getName().equals(orders.get(finalI).getCurrencySymbol()
                                + orders.get(finalI).getTypeOfPayment()))
                        .findFirst()
                        .get();
                Price price = priceService.getLastPrice(code).get();
                orders.get(i).setLatest_max(String.valueOf(price.getVal().divide(BigInteger.valueOf(100000000))));
            }
        }
    }

     */

    // Testing
    @Scheduled(fixedRate = 120_000, initialDelay = 100)
    public void updateMaxValues() {
        BinanceApiRestClient binanceApiRestClient = binanceRestConfig.binanceApiRestClient(null, null);
        List<UserOrder> orders = userOrderService.getAllTradable();
        if (!orders.isEmpty()) {
           /* orders.forEach(order -> {
                User user = order.getUser();
                List<Code> codes = user.getCodes();
                Code code = codes
                        .stream()
                        .filter(c -> c.getName().equals(order.getCurrencySymbol()))
                        .findFirst()
                        .get();
                Price price = priceService.getLastPrice(code).get();
                order.setLatest_max(String.valueOf(price.getVal().divide(BigInteger.valueOf(100000000))));
            });*/
            for (int i = 0; i < orders.size(); i++) {
                UserOrder userOrder = orders.get(i);
                String currencySymbol = orders.get(i).getCurrencySymbol();
                String typeOfPayment = orders.get(i).getTypeOfPayment();

                Code code = codeService.get(userOrder.getCurrencySymbol() + userOrder.getTypeOfPayment());
                String val = priceService.getLastPrice(code).get().getVal();

                TickerPrice price1 = binanceApiRestClient.getPrice(currencySymbol + typeOfPayment);
                if (userOrder.getLatest_max() == null
                        || new BigDecimal(val).compareTo(new BigDecimal(userOrder.getLatest_max())) > 0) {
                    userOrder.setLatest_max(val);
                    userOrderService.save(userOrder);
                }
/*
                List<Code> codes = user.getCodes();
                int finalI = i;
                Code code = codes
                        .stream()
                        .filter(c -> c.getName().equals(orders.get(finalI).getCurrencySymbol()
                                + orders.get(finalI).getTypeOfPayment()))
                        .findFirst()
                        .get();
                Price price = priceService.getLastPrice(code).get();
                orders.get(i).setLatest_max(String.valueOf(price.getVal().divide(BigInteger.valueOf(100000000))));

 */
            }
        }
    }


}
