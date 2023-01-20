package com.negeso.crypto.views.order;

import com.binance.api.client.exception.BinanceApiException;
import com.negeso.crypto.data.dto.OrderDto;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.entity.UserOrder;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.negeso.crypto.data.service.UserOrderService;
import com.negeso.crypto.security.AuthenticatedUser;
import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.log4j.Log4j2;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@PageTitle("Orders")
@Route(value = "orders", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class OrderView extends Div {
    private final TextField currency = new TextField("Currency");
    private final TextField thresholdToSell = new TextField("Threshold");
    private final TextField quantity = new TextField("Amount");
    private final Select<String> periodicity = new Select<>();
    private final Select<String> operationType = new Select<>();
    private final Select<String> orderType = new Select<>();
    private final Select<String> typeOfPayment = new Select<>();
    private final Select<String> tradable = new Select<>();
    private final Button execute = new Button("Execute order");
    private final Button cancel = new Button("Cancel");
    private final Binder<OrderDto> binder = new BeanValidationBinder<>(OrderDto.class);
    private final AuthenticatedUser authenticatedUser;
    private final BinanceFetcherJob binanceFetcherJob;
    private final UserOrderService userOrderService;

    public OrderView(AuthenticatedUser authenticatedUser,
                     BinanceFetcherJob binanceFetcherJob,
                     UserOrderService userOrderService) {
        this.authenticatedUser = authenticatedUser;
        this.binanceFetcherJob = binanceFetcherJob;
        this.userOrderService = userOrderService;
        addClassName("order-view");
        setSizeFull();
        add(getContent());
        bindEntity();
        clearForm();
        cancel.addClickListener(event -> clearForm());
        execute.addClickListener(event -> executeOrder());
    }

    private Component getContent() {
        User user = authenticatedUser.get().get();
        VerticalLayout newOrderLayout = new VerticalLayout(new H3("Add new order:"),
                createOrderForm(),
                createButtonLayout());
        newOrderLayout.addClassName("content");
        newOrderLayout.setSizeFull();
        return newOrderLayout;
    }

    private Component createOrderForm() {
        List<String> tradeOptions = new ArrayList<>();
        tradeOptions.add("True");
        tradeOptions.add("False");
        List<String> minutesList = new ArrayList<>();
        fillMinutesOfPeriodicity(minutesList);
        List<String> availableCryptoToBuy = new ArrayList<>();
        fillCryptoToBeUsedForPurchase(availableCryptoToBuy);
        addClassName("order-form");
        FormLayout orderForm = new FormLayout();
        List<String> operationTypeList = new ArrayList<>();
        operationTypeList.add("BUY");
        operationTypeList.add("SELL");
        List<String> orderTypeList = new ArrayList<>();
        orderTypeList.add("MARKET");
        quantity.setHelperText("Amount of crypto you want to buy");
        tradable.setItems(tradeOptions);
        tradable.setLabel("Automated trading");
        tradable.setHelperText("This feature would let the application check if the price falls below the threshold. "
                + "If it does, it sells automatically in order not to lose money");
        thresholdToSell.setHelperText("Choose threshold to for selling (e.g. 0.1 for 1%, 0.01 for 0.1%, 0.3 for 3%)");
        periodicity.setLabel("Set periodicity of binance queries");
        periodicity.setHelperText("At the moment application supports only 2 minutes");
        periodicity.setItems(minutesList);
        currency.setHelperText("Currency must have altname you want to buy or sell " +
                "(e.g. SHIB, BNB, BTC)");
        //volume.setValue("0");
        // volume.setHelperText("Volume must be numeric value and be equal to or greater than the minimum order size " + "(in terms of base currency)");
        typeOfPayment.setLabel("Choose type of payment");
        typeOfPayment.setHelperText("Choose the desired currency for paying");
        typeOfPayment.setItems(availableCryptoToBuy);
        typeOfPayment.setErrorMessage("Please enter a numeric value");
        operationType.setLabel("Operation type");
        operationType.setItems(operationTypeList);
        operationType.setValue("BUY");
        operationType.setHelperText("Operation type must be \"BUY\" or \"SELL\"");
        orderType.setLabel("Order Type");
        orderType.setItems(orderTypeList);
        orderType.setHelperText("At the moment application supports only order type \"MARKET\"");
        orderType.setValue("MARKET");
        orderForm.add(currency, typeOfPayment, tradable, quantity,
                operationType, orderType, periodicity, thresholdToSell);
        return orderForm;
    }

    private Component createButtonLayout() {
        addClassName("button-layout");
        execute.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        execute.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(execute, cancel);
    }

    private void bindEntity() {
        binder.forField(currency)
                .withValidator(this::validateCurrencyName, "Please enter valid altname")
                .bind(OrderDto::getCurrencySymbol, OrderDto::setCurrencySymbol);
        binder.bind(typeOfPayment, OrderDto::getTypeOfPayment, OrderDto::setTypeOfPayment);
        binder.bind(operationType, OrderDto::getOperationType, OrderDto::setOperationType);
        binder.bind(orderType, OrderDto::getOrderType, OrderDto::setOrderType);
        binder.bind(periodicity, OrderDto::getPeriodicity, OrderDto::setPeriodicity);
        binder.forField(thresholdToSell).bind(OrderDto::getThreshold, OrderDto::setThreshold);
        binder.forField(quantity).bind(OrderDto::getQuantity, OrderDto::setQuantity);
        binder.bind(tradable, OrderDto::getTradable, OrderDto::setTradable);
    }

    private void fillCryptoToBeUsedForPurchase(List<String> list) {
        list.add("BNB");
        list.add("USDT");
        list.add("BUSD");
    }

    private void fillMinutesOfPeriodicity(List<String> list) {
        list.add("1 minute");
        list.add("2 minutes");
        list.add("3 minutes");
        list.add("4 minutes");
        list.add("5 minutes");
        list.add("10 minutes");
        list.add("15 minutes");
        list.add("30 minutes");
        list.add("1 hour");
    }

    private boolean validateCurrencyName(String code) {
        return binanceFetcherJob.exists(code);
    }

    private void clearForm() {
        binder.setBean(new OrderDto());
    }

    private void executeOrder() {
        User user = authenticatedUser.get().get();
        OrderDto orderDto = binder.getBean();
        UserOrder userOrder = new UserOrder();

        userOrder.setOrderType(orderDto.getOrderType());
        userOrder.setPeriodicity(Integer.parseInt(orderDto.getPeriodicity().split(" ")[0]));
        userOrder.setUser(user);
        userOrder.setQuantity(orderDto.getQuantity());
        userOrder.setTradable(Boolean.parseBoolean(orderDto.getTradable()));
        userOrder.setCurrencySymbol(orderDto.getCurrencySymbol());
        userOrder.setTypeOfPayment(orderDto.getTypeOfPayment());
        userOrder.setOperationType(orderDto.getOperationType());
        userOrder.setThreshold(orderDto.getThreshold());

        try {
            binanceFetcherJob.placeBuyMarketOrder(
                    userOrder.getCurrencySymbol() + userOrder.getTypeOfPayment(),
                    userOrder.getQuantity(), user);
        } catch (BinanceApiException e) {
            throw new RuntimeException("Invalid order" , e);
        }

        UserOrder saveUserOrder = userOrderService.save(userOrder);
        clearForm();
        /*


        PionexCurrency currency = pionexService.findCurrencyById(order.getSymbol());
        Integer precision = currency.getPrecision();
        order.setSize(new BigDecimal(order.getSize())
                .round(new MathContext(precision, RoundingMode.FLOOR))
                .toPlainString());
        BigDecimal currentPrice = currency.getCurrentPrice();
        pionexService.postAnOrder(user.getPionexApiKey(), user.getPionexApiSecret(), order, currentPrice);
        clearForm();

         */
    }
}
