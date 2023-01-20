package com.negeso.crypto.views.info;

import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.exception.BinanceApiException;
import com.negeso.crypto.data.dto.BalanceWrapper;
import com.negeso.crypto.data.dto.PionexBalance;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.negeso.crypto.data.job.impl.BinanceFetcherJobImpl;
import com.negeso.crypto.data.service.PionexService;
import com.negeso.crypto.security.AuthenticatedUser;
import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@PageTitle("Balance Info")
@Route(value = "balanceinfo", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class BalanceInfoView extends Div {
    private final AuthenticatedUser authenticatedUser;
    private final BinanceFetcherJob binanceFetcherJob;
    private Grid<AssetBalance> balanceGrid = new Grid<>(AssetBalance.class, false);

    public BalanceInfoView(AuthenticatedUser authenticatedUser,
                           BinanceFetcherJob binanceFetcherJob) {
        this.authenticatedUser = authenticatedUser;
        this.binanceFetcherJob = binanceFetcherJob;

        addClassName("balance-info-view");
        setSizeFull();
        configureBalanceGrid();
        add(getContent());
    }

    private HorizontalLayout getContent() {
        try {
            HorizontalLayout horizontalLayout
                    = new HorizontalLayout(getFirstVerticalLayout(), getSecondVerticalLayout());
            horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
            return horizontalLayout;
        } catch (BinanceApiException apiException) {
            return new HorizontalLayout(getErrorComponent());
        }
    }

    private Component getSecondVerticalLayout() {
        return new VerticalLayout(new H3("Your estimated capitalization:"),
                calculateEstimatedCapitalization());
    }

    private Component getFirstVerticalLayout() {
        return new VerticalLayout(new H3("Your available balance:"),
                //getKrakenBalanceComponent()
                getBinanceBalanceComponent()
        );
    }

/*
    private Component getKrakenBalanceComponent() {
        User user = authenticatedUser.get().get();
        BalanceWrapper balance = pionexService.getAccountBalance(user.getPionexApiKey(), user.getPionexApiSecret());
        if (balance.getData().getBalances().size() == 0) {
            return new Text("Api key/secret is invalid or balance is empty");
        } else {
            return balanceGrid;
        }
    }

 */

    private Component getBinanceBalanceComponent() {
        User user = authenticatedUser.get().get();
        if (user.getBinanceApiKey() == null || user.getBinanceApiSecret() == null  ) {
            return new Text("You should enter your API key and secret in account info to see your balance");
        } else {
            List<AssetBalance> balance = binanceFetcherJob.getBalance(user.getBinanceApiKey(), user.getBinanceApiSecret());
            balanceGrid.setItems(balance);
            return balanceGrid;
        }
    }

    private Component getErrorComponent() {
        return new Text("You should enter your API key and secret in account info to see your balance and be able to trade");
    }



    private void configureBalanceGrid() {
        balanceGrid.addClassName("balance-grid");
        balanceGrid.setColumns("asset", "free", "locked");
        balanceGrid.getColumns().forEach(col -> col.setFrozenToEnd(true));
        balanceGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        balanceGrid.setAllRowsVisible(true);
        balanceGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
    }

    private Component calculateEstimatedCapitalization() {
        User user = authenticatedUser.get().get();
        List<AssetBalance> balances = binanceFetcherJob.getBalance(user.getBinanceApiKey(), user.getBinanceApiSecret());
        BigDecimal userBalance = binanceFetcherJob.getTotalCapitalization(balances);
        return new Text(userBalance.setScale(2, RoundingMode.CEILING) + " USD");
    }
}
