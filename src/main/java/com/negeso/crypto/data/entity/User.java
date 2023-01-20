package com.negeso.crypto.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @Column(name = "email")
    private String email;
    @JsonIgnore
    @Column(name = "hashed_password")
    private String hashedPassword;
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    @Column(name = "kraken_api_key")
    private String krakenApiKey;
    @Column(name = "kraken_api_secret")
    private String krakenApiSecret;
    //@Column(name = "pionex_api_key")
    //private String pionexApiKey;
    //@Column(name = "pionex_api_secret")
    //private String pionexApiSecret;
    @Column(name = "auto_trading")
    private Boolean autoTrading;
    @Column(name = "balance")
    private BigDecimal balance;
    @Column(name = "gambling_limit")
    private BigDecimal gamblingLimit;
    @Column(name = "role")
    private String role;
    @Column(name = "max_number_of_gambles")
    private Integer maxConcurrentGambles;
    @Column(name = "current_number_of_gambles")
    private Integer currentNumberOfGambles;
    @Column(name = "binance_api_key")
    private String binanceApiKey;
    @Column(name = "binance_api_secret")
    private String binanceApiSecret;
    @OneToMany(fetch = FetchType.EAGER)
    @Column(name = "codes")
    private List<Code> codes;
}
