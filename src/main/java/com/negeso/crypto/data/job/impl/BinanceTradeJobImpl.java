package com.negeso.crypto.data.job.impl;

import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.Price;
import com.negeso.crypto.data.entity.UserOrder;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.negeso.crypto.data.job.BinanceTradeJob;
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
public class BinanceTradeJobImpl implements BinanceTradeJob {
    private final UserOrderService userOrderService;
    private final BinanceFetcherJob binanceFetcherJob;
    private final PriceService priceService;
    private final CodeService codeService;

    @Scheduled(fixedRate = 120_000, initialDelay = 200)
    public void trade() {
        List<UserOrder> allTradable = userOrderService.getAllTradable();
        for(int i = 0; i < allTradable.size(); i++) {
            UserOrder userOrder = allTradable.get(i);
            BigDecimal maxPriceBigDecimal = new BigDecimal(userOrder.getLatest_max());
            Code code = codeService.get(userOrder.getCurrencySymbol() + userOrder.getTypeOfPayment());
            BigDecimal currentPrice = new BigDecimal(priceService.getLastPrice(code).get().getVal());
            String threshold = userOrder.getThreshold();
            log.info("----- Current maxPrice: " + maxPriceBigDecimal);
            log.info("----- Current threshold: " + threshold);
            BigDecimal numberThreshold = maxPriceBigDecimal.subtract(maxPriceBigDecimal.multiply(new BigDecimal(threshold)));
            //BigDecimal numberThreshold = percentage.divide(new BigDecimal("100")).multiply(maxPriceBigDecimal);

            if (currentPrice.compareTo(numberThreshold) < 0) {
                binanceFetcherJob.sellBuyMarketOrder(userOrder.getCurrencySymbol() + userOrder.getTypeOfPayment(),
                        userOrder.getQuantity(), userOrder.getUser());
                userOrderService.delete(userOrder);
            }
        }
    }
}
