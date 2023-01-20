package com.negeso.crypto.views.tradeInfo;

import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Gainers and Losers")
@Route(value = "gainers-losers", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class GainersAndLosersView extends Div {
    /*private final static String ERROR_MESSAGE
            = "Something went wrong and page was not scraped. Please refresh the page once again or contact Negeso support " +
            "if you have any questions by email at support@negeso.com";
    private final CoinglassService coinglassService;
    private final CurrencyTradedService currencyTradedService;
    private final KrakenClient krakenClient;
    private final Grid<CurrencyChangeInfo> currenciesGrid
            = new Grid<>(CurrencyChangeInfo.class, false);
    private final Button addTradedCurrencyButton = new Button("Add to traded");
    private String tradedCurrencyToAddName;


    public GainersAndLosersView(CurrencyTradedService currencyTradedService,
                                CoinglassService coinglassService,
                                KrakenClient krakenClient) {
        this.coinglassService = coinglassService;
        this.currencyTradedService = currencyTradedService;
        this.krakenClient = krakenClient;

        addClassName("gainers-losers-view");
        setSizeFull();
        configureTopGainersGrid();
        add(getToolbar(), getContent());
    }

    private Component getContent() {
        List<CurrencyChangeInfo> gainersAndLosersInfo = coinglassService.scrapePage();
        if (gainersAndLosersInfo.isEmpty()) {
            return new Text(ERROR_MESSAGE);
        } else {
            currenciesGrid.setItems(gainersAndLosersInfo);
            return currenciesGrid;
        }
    }

    private Component getToolbar() {
        addTradedCurrencyButton.setEnabled(false);
        addTradedCurrencyButton.addClickListener(click -> addCurrencyToTradedCurrenciesList());
        HorizontalLayout toolbar = new HorizontalLayout(addTradedCurrencyButton);
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        return toolbar;
    }

    private void addCurrencyToTradedCurrenciesList() {
        Optional<CurrencyTraded> currencyTraded
                = currencyTradedService.getCurrencyTradedByName(tradedCurrencyToAddName);
        if (currencyTraded.isPresent()) {
            Notification.show("Currency is recently present in Traded Currencies");
        } else {
            if (krakenClient.getAssetInfo(tradedCurrencyToAddName)) {
                currencyTradedService.addCurrencyToTraded(tradedCurrencyToAddName);
                Notification.show("Currency was successfully added to Traded Currencies");
            } else {
                Notification.show("Currency does not supported by Kraken");
            }
        }
    }

    private void configureTopGainersGrid() {
        currenciesGrid.addClassName("gainers-losers-grid");
        currenciesGrid.setSizeFull();
        currenciesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        currenciesGrid.addColumn(CurrencyChangeInfo::getCurrencyName).setHeader("Currency name").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getPrice).setHeader("Price").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getFiveMinChange).setHeader("5Min Change").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getFifteenMinChange).setHeader("15Min Change").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getThirtyMinChange).setHeader("30Min Change").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getOneHourChange).setHeader("1H Change").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getFourHoursChange).setHeader("4H Change").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getTwelveHoursChange).setHeader("12H Change").setFrozen(true);
        currenciesGrid.addColumn(CurrencyChangeInfo::getTwentyFourHoursChange).setHeader("24H Change").setFrozen(true);
        currenciesGrid.getColumns().forEach(col -> col.setAutoWidth(true).setFlexGrow(1));
        currenciesGrid.getColumns().forEach(col -> col.setSortable(true));
        currenciesGrid.addSelectionListener(selection -> {
            if (selection.getFirstSelectedItem().isPresent()) {
                tradedCurrencyToAddName = selection.getFirstSelectedItem().get().getCurrencyName();
                addTradedCurrencyButton.setEnabled(true);
            }
        });
    }*/
}
