package com.negeso.crypto.data.repository;

import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    List<UserOrder> getUserOrderByTradableIsTrue();

    UserOrder getUserOrderById(Long id);
}
