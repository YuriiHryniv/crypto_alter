package com.negeso.crypto.data.repository;

import com.negeso.crypto.data.entity.PionexCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PionexCurrencyRepository extends JpaRepository<PionexCurrency, String> {
    @Modifying
    @Transactional
    @Query("UPDATE PionexCurrency p SET p.topGainer = false")
    int clearTopGainers();

    @Modifying
    @Transactional
    @Query("UPDATE PionexCurrency p SET p.topGainer = true WHERE p.currencyName = :currencyName")
    int addTopGainers(@Param("currencyName") String currencyName);

    @Query("SELECT p FROM PionexCurrency p WHERE p.topGainer = true")
    List<PionexCurrency> findTopGainers();

    @Query("SELECT c FROM PionexCurrency c "
            + "WHERE LOWER(c.currencyName) like LOWER(CONCAT('%', :filterText, '%'))")
    List<PionexCurrency> findCurrenciesByFilter(@Param("filterText") String filterText);

    @Modifying
    @Transactional
    @Query("UPDATE PionexCurrency p SET p.autoTrading = :autoTrading, " +
            "p.buyingThresholdPercentage = :buyingThresholdPercentage, " +
            "p.buyingThresholdAbsolute = :buyingThresholdAbsoluteInUsd, " +
            "p.sellingThresholdPercentage = :sellingThresholdPercentage, " +
            "p.sellingThresholdAbsolute = :sellingThresholdAbsoluteInUsd WHERE p.currencyName = :currencyName")
    int updateTradedCurrency(@Param("autoTrading") Boolean autoTrading,
                            @Param("buyingThresholdPercentage") BigDecimal buyingThresholdPercentage,
                             @Param("buyingThresholdAbsoluteInUsd") BigDecimal buyingThresholdAbsoluteInUsd,
                             @Param("sellingThresholdPercentage") BigDecimal sellingThresholdPercentage,
                             @Param("sellingThresholdAbsoluteInUsd") BigDecimal sellingThresholdAbsoluteInUsd,
                             @Param("currencyName") String currencyName);

    boolean existsByCurrencyName(String name);

    List<PionexCurrency> findAllByAutoTradingTrue();
}
