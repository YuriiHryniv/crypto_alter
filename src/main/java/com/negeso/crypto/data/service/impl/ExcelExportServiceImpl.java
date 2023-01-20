package com.negeso.crypto.data.service.impl;

import com.negeso.crypto.data.dto.OrderDto;
import com.negeso.crypto.data.service.ExcelExportService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Objects;

/*
@Service
@Log4j2
public class ExcelExportServiceImpl implements ExcelExportService {
    @Override
    public void writeOrderToFile(OrderDto order,
                                 BigDecimal currencyPrice,
                                 BigDecimal orderPrice,
                                 String success) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("orders.csv")).getFile());
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File \"orders.csv\" does not exist", e);
        }
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            workbook = new HSSFWorkbook();
            workbook.createSheet();
        }
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getLastRowNum();
        Row row = sheet.createRow(++rowCount);
        Cell successCell = row.createCell(0);
        successCell.setCellValue(success);
        Cell currencyName = row.createCell(1);
        //currencyName.setCellValue(order.getSymbol());
        Cell operationType = row.createCell(2);
        operationType.setCellValue(order.getOperationType());
        Cell amount = row.createCell(3);
        switch (order.getOperationType()) {
            //case "BUY" -> amount.setCellValue(order.getAmount());
            //case "SELL" -> amount.setCellValue(order.getSize());
            default -> throw new RuntimeException("Invalid order operation type");
        }
        Cell currentCurrencyPrice = row.createCell(4);
        currentCurrencyPrice.setCellValue(currencyPrice.toPlainString());
        Cell totalOrderPrice = row.createCell(5);
        totalOrderPrice.setCellValue(orderPrice.toPlainString());
        Cell time = row.createCell(6);
        time.setCellValue(LocalDateTime.now());
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream", e);
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            workbook.write(os);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write updated data to the file", e);
        }
        try {
            workbook.close();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close output stream or workbook", e);
        }
    }

    @Override
    public void writeCsvOrderToFile(OrderDto order, BigDecimal currencyPrice, BigDecimal orderPrice, String success) {
        File file = new File("/var/www/share-new/sites-prod/negeso.com/www/customers/site/media/crypto/orders.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("Unable to create file for orders");
            }
        }
        Path path = file.toPath();
        String tab = "\t";
        String amount = null;
        switch (order.getOperationType()) {
            case "BUY" -> amount = order.getAmount();
            //case "SELL" -> amount = order.getSize();
            default -> throw new RuntimeException("Invalid order operation type");
        }
        String orderLine = new StringBuilder().append("STATUS:").append(success).append(tab)
                .append(order.getSymbol()).append(tab)
                .append(order.getOperationType()).append(tab)
                .append("AMOUNT:").append(amount).append(tab)
                .append("CURRENCY PRICE:").append(currencyPrice.toPlainString()).append(tab)
                .append("TOTAL ORDER PRICE:").append(orderPrice.toPlainString()).append(tab)
                .append(LocalDateTime.now()).append(tab).append("\n").toString();
        try {
            Files.write(path, orderLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
 */
