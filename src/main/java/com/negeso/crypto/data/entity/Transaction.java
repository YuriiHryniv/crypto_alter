package com.negeso.crypto.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "sold_currency_type")
    private String soldCurrencyType;
    @Column(name = "sold_currency_amount")
    private BigDecimal soldCurrencyAmount;
    @Column(name = "bought_currency_type")
    private String boughtCurrencyType;
    @Column(name = "bought_currency_amount")
    private BigDecimal boughtCurrencyAmount;
}
