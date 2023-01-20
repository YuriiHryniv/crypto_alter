package com.negeso.crypto.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "pionex_currencies")
public class PionexCurrency {
    @Id
    @Column(name = "currency_name")
    private String currencyName;
    @Column(name = "current_price_in_usd")
    private BigDecimal currentPrice;
    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;
    @Column(name = "max_order_amount")
    private BigDecimal maxOrderAmount;
    @Column(name = "two_min_price")
    private BigDecimal twoMinPrice;
    @Column(name = "four_min_price")
    private BigDecimal fourMinPrice;
    @Column(name = "six_min_price")
    private BigDecimal sixMinPrice;
    @Column(name = "eight_min_price")
    private BigDecimal eightMinPrice;
    @Column(name = "ten_min_price")
    private BigDecimal tenMinPrice;
    @Column(name = "buying_threshold_percentage")
    private BigDecimal buyingThresholdPercentage;
    @Column(name = "buying_threshold_absolute_in_usd")
    private BigDecimal buyingThresholdAbsolute;
    @Column(name = "selling_threshold_percentage")
    private BigDecimal sellingThresholdPercentage;
    @Column(name = "selling_threshold_absolute_in_usd")
    private BigDecimal sellingThresholdAbsolute;
    @Column(name = "auto_trading")
    private Boolean autoTrading;
    @Column(name = "top_gainer")
    private Boolean topGainer;
    @Column(name = "precision")
    private Integer precision;

    public PionexCurrency() {
    }

    public PionexCurrency(String currencyName,
                          BigDecimal currentPrice,
                          BigDecimal minOrderAmount,
                          BigDecimal maxOrderAmount,
                          BigDecimal buyingThresholdPercentage,
                          BigDecimal buyingThresholdAbsolute,
                          BigDecimal sellingThresholdPercentage,
                          BigDecimal sellingThresholdAbsolute,
                          Boolean autoTrading,
                          Boolean topGainer,
                          Integer precision) {
        this.currencyName = currencyName;
        this.currentPrice = currentPrice;
        this.minOrderAmount = minOrderAmount;
        this.maxOrderAmount = maxOrderAmount;
        this.buyingThresholdPercentage = buyingThresholdPercentage;
        this.buyingThresholdAbsolute = buyingThresholdAbsolute;
        this.sellingThresholdPercentage = sellingThresholdPercentage;
        this.sellingThresholdAbsolute = sellingThresholdAbsolute;
        this.autoTrading = autoTrading;
        this.topGainer = topGainer;
        this.precision = precision;
    }
}
