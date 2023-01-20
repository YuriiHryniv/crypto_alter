package com.negeso.crypto.data.service;

import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.Price;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceService {
    Price save(Code code, String string, LocalDateTime localDateTime);

    void clear(LocalDateTime upToTime);

    Optional<Price> getLastPrice(Code code);
}
