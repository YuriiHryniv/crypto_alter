package com.negeso.crypto.data.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "orders")
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @JoinColumn(name = "tradable")
    private boolean tradable;
    @JoinColumn(name = "periodicity")
    private int periodicity;
    @JoinColumn(name = "typeOfPayment")
    private String typeOfPayment;
    @JoinColumn(name = "orderType")
    private String orderType;
    @JoinColumn(name = "operationType")
    private String operationType;
    @JoinColumn(name = "currencySymbol")
    private String currencySymbol;
    @JoinColumn(name = "quantity")
    private String quantity;
    @JoinColumn(name = "latest_max")
    private String latest_max;
    @JoinColumn(name = "threshold")
    private String threshold;
}
