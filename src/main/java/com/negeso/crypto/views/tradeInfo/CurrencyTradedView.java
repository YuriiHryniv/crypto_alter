package com.negeso.crypto.views.tradeInfo;

import com.negeso.crypto.data.dto.OrderDto;
import com.negeso.crypto.data.entity.PionexCurrency;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.entity.UserOrder;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.negeso.crypto.data.service.PionexService;
import com.negeso.crypto.data.service.UserOrderService;
import com.negeso.crypto.security.AuthenticatedUser;
import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Traded Currencies")
@Route(value = "traded-currencies", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class CurrencyTradedView extends Div {
    private static final String EMPTY_LIST_MESSAGE = "There are no traded currencies yet";
    private final AuthenticatedUser authenticatedUser;
    private final Grid<OrderDto> pionexCurrencyGrid
            = new Grid<>(OrderDto.class, false);
    private final TextField filterText = new TextField();
    private final BinanceFetcherJob binanceFetcherJob;
    private final UserOrderService userOrderService;
    private CurrencyTradedForm form;

    public CurrencyTradedView(AuthenticatedUser authenticatedUser,
                              BinanceFetcherJob binanceFetcherJob,
                              UserOrderService userOrderService) {
        this.authenticatedUser = authenticatedUser;
        this.binanceFetcherJob = binanceFetcherJob;
        this.userOrderService = userOrderService;

        addClassName("currency-traded-view");
        setSizeFull();
        configureCurrencyTradedGrid();
        configureCurrencyTradedForm();
        add(getToolbar(), getContent());
        updateTradedCurrencies();
        closeEditor();
    }

    private void closeEditor() {
        form.setCurrencyTraded(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateTradedCurrencies() {
        /*
        pionexCurrencyGrid.setItems(pionexService.getCurrenciesByFilter(filterText.getValue())
                .stream()
                .peek(x -> x.setBuyingThresholdPercentage(x.getBuyingThresholdPercentage().multiply(BigDecimal.valueOf(100))))
                .peek(x -> x.setSellingThresholdPercentage(x.getSellingThresholdPercentage().multiply(BigDecimal.valueOf(100))))
                .collect(Collectors.toList()));

         */
    }

    private HorizontalLayout getContent() {
        /*List<PionexCurrency> allCurrenciesTraded = pionexService.getAllCurrencies().stream()
                .peek(x -> x.setBuyingThresholdPercentage(x.getBuyingThresholdPercentage().multiply(BigDecimal.valueOf(100))))
                .peek(x -> x.setSellingThresholdPercentage(x.getSellingThresholdPercentage().multiply(BigDecimal.valueOf(100))))
                .collect(Collectors.toList());
        if (allCurrenciesTraded.isEmpty()) {
            return new HorizontalLayout (new Text(EMPTY_LIST_MESSAGE));
        } else {
            pionexCurrencyGrid.setItems(allCurrenciesTraded);
            HorizontalLayout content = new HorizontalLayout(pionexCurrencyGrid, form);
            content.setFlexGrow(3, pionexCurrencyGrid);
            content.setFlexGrow(1, form);
            content.setSizeFull();
            return content;*/
        User user = authenticatedUser.get().get();
        List<UserOrder> userOrders = userOrderService.getAllTradable()
                .stream()
                .filter(o -> o.getUser().getEmail().equals(user.getEmail()))
                .toList();
        List<OrderDto> dtos = new ArrayList<>();
        userOrders.forEach(userOrder -> {
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderType(userOrder.getOrderType());
            orderDto.setPeriodicity(String.valueOf(userOrder.getPeriodicity()));
            orderDto.setThreshold(userOrder.getThreshold());
            orderDto.setTradable(String.valueOf(userOrder.isTradable()));
            orderDto.setCurrencySymbol(userOrder.getCurrencySymbol());
            orderDto.setTypeOfPayment(userOrder.getTypeOfPayment());
            orderDto.setQuantity(userOrder.getQuantity());
            orderDto.setOperationType(userOrder.getOperationType());
            orderDto.setCurrentPrice(binanceFetcherJob.getPrice(
                    orderDto.getCurrencySymbol() + orderDto.getTypeOfPayment()));
            dtos.add(orderDto);
        });
        if (userOrders.isEmpty()) {
            return new HorizontalLayout (new Text(EMPTY_LIST_MESSAGE));
        } else {
            pionexCurrencyGrid.setItems(dtos);
            HorizontalLayout content = new HorizontalLayout(pionexCurrencyGrid, form);
            content.setFlexGrow(3, pionexCurrencyGrid);
            content.setFlexGrow(1, form);
            content.setSizeFull();
            return content;
        }
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateTradedCurrencies());
        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        return toolbar;
    }

    private void configureCurrencyTradedForm() {
        form = new CurrencyTradedForm();
        form.setWidth("25em");
        //form.addListener(CurrencyTradedForm.SaveEvent.class, this::saveCurrencyTraded);
        form.addListener(CurrencyTradedForm.CloseEvent.class, event -> closeEditor());
    }

    /*
    private void saveCurrencyTraded(CurrencyTradedForm.SaveEvent event) {
        pionexService.updateTradedCurrency(event.getCurrencyTraded());
        updateTradedCurrencies();
        closeEditor();
    }

     */

    private void configureCurrencyTradedGrid() {
        pionexCurrencyGrid.addClassName("currency-traded-grid");
        pionexCurrencyGrid.setSizeFull();
        /*
        pionexCurrencyGrid.setColumns("currencyName", "currentPrice", "autoTrading", "buyingThresholdPercentage",
                "buyingThresholdAbsolute", "sellingThresholdPercentage", "sellingThresholdAbsolute");

         */
        pionexCurrencyGrid.setColumns("currencySymbol", "currentPrice", "tradable", "threshold", "periodicity",
                "typeOfPayment", "orderType", "quantity", "operationType");
        pionexCurrencyGrid.getColumns().forEach(col -> col.setAutoWidth(true).setFlexGrow(1));

        /*pionexCurrencyGrid.asSingleSelect().addValueChangeListener(selection ->
                editCurrencyTraded(selection.getValue()));

         */
    }

    /*private void editCurrencyTraded(PionexCurrency pionexCurrency) {
        if (pionexCurrency == null) {
            closeEditor();
        } else {
            form.setCurrencyTraded(pionexCurrency);
            form.setVisible(true);
            addClassName("editing");
        }
    }*/
/*
    private void editCurrencyTraded(UserOrder userOrder) {
        if (userOrder == null) {
            closeEditor();
        } else {
            form.setCurrencyTraded(userOrder);
            form.setVisible(true);
            addClassName("editing");
        }

 */
}
