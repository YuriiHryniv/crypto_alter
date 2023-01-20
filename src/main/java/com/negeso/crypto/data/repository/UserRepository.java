package com.negeso.crypto.data.repository;

import com.negeso.crypto.data.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, String> {

    void deleteByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.email = :newEmail WHERE u.email = :oldEmail")
    void updateEmail(@Param("newEmail") String newEmail, @Param("oldEmail") String oldEmail);


    List<User> findAllByAutoTradingTrue();

    List<User> findAllByEmailEquals(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.profilePictureUrl = :profilePictureUrl, u.autoTrading = :autoTrading, " +
            "u.gamblingLimit = :gamblingLimit, u.binanceApiKey = :binanceApiKey, u.binanceApiSecret = :binanceApiSecret, " +
            "u.maxConcurrentGambles = :maxNumberOfGambles, u.codes = :codes WHERE u.email = :email")
    void updateAccountInfo(@Param("profilePictureUrl") String profilePictureUrl,
                           @Param("autoTrading") Boolean autoTrading,
                           @Param("gamblingLimit") BigDecimal gamblingLimit,
                           @Param("binanceApiKey") String binanceApiKey,
                           @Param("binanceApiSecret") String binanceApiSecret,
                           @Param("email") String email,
                           @Param("maxNumberOfGambles") Integer maxNumberOfGambles,
                           @Param("codes") List<String> codes);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.currentNumberOfGambles = :currentNumberOfGambles WHERE u.email = :email")
    void updateCurrentNumberOfGambles(@Param("email") String email,
                                      @Param("currentNumberOfGambles") Integer currentNumberOfGambles);
}