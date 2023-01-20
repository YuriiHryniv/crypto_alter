package com.negeso.crypto.data.service.impl;

import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.Price;
import com.negeso.crypto.data.repository.PriceRepository;
import com.negeso.crypto.data.service.PriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PriceServiceImpl implements PriceService {
    private final PriceRepository priceRepository;

    public PriceServiceImpl(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Optional<Price> getLastPrice(Code code) {
        return priceRepository.findFirstByCodeOrderByTimeDesc(code);
    }

    public void clear(LocalDateTime upToTime) {
        List<Price> pricesToDelete = priceRepository.findAllByTimeLessThan(upToTime);
        priceRepository.deleteAll(pricesToDelete);
    }

    public Price save(Code code, String pr, LocalDateTime localDateTime) {
        Price price = new Price();
        price.setVal(pr);
        price.setTime(localDateTime);
        price.setCode(code);
        return priceRepository.save(price);
    }
}
