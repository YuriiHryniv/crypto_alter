package com.negeso.crypto.data.service;


import com.negeso.crypto.data.dto.OrderDto;

import java.math.BigDecimal;

public interface ExcelExportService {
    void writeOrderToFile(OrderDto order, BigDecimal currencyPrice, BigDecimal orderPrice, String success);
    void writeCsvOrderToFile(OrderDto order, BigDecimal currencyPrice, BigDecimal orderPrice, String success);
}
