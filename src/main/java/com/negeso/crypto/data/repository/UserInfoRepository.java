package com.negeso.crypto.data.repository;

import com.negeso.crypto.data.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    Optional<UserInfo> findByEmail(String email);
    void deleteByEmail(String email);
    @Query("SELECT ui.firstName FROM UserInfo ui WHERE ui.email = :email")
    String findFirstNameByEmail(@Param("email") String email);
    @Query("SELECT ui.lastName FROM UserInfo ui WHERE ui.email = :email")
    String findLastNameByEmail(@Param("email") String email);
}