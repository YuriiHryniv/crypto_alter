package com.negeso.crypto.data.service;

import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.entity.UserOrder;

import java.util.List;

public interface UserOrderService {
    UserOrder save(UserOrder userOrder);

    List<UserOrder> getAllTradable();

    void updateMaxValue(UserOrder userOrder, Long id);

    void delete(UserOrder userOrder);
}
