package com.negeso.crypto.data.service.impl;

import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.entity.UserOrder;
import com.negeso.crypto.data.repository.UserOrderRepository;
import com.negeso.crypto.data.service.UserOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserOrderServiceImpl implements UserOrderService {
    private final UserOrderRepository userOrderRepository;

    public UserOrderServiceImpl(UserOrderRepository userOrderRepository) {
        this.userOrderRepository = userOrderRepository;
    }


    @Override
    public UserOrder save(UserOrder userOrder) {
        return userOrderRepository.save(userOrder);
    }

    public List<UserOrder> getAllTradable() {
        return userOrderRepository.getUserOrderByTradableIsTrue();
    }

    public void updateMaxValue(UserOrder userOrder, Long id) {
        UserOrder userOrderById = userOrderRepository.getUserOrderById(id);
        userOrderById.setLatest_max(userOrder.getLatest_max());
    }

    public void delete(UserOrder userOrder) {
        userOrderRepository.delete(userOrder);
    }
}
