package com.negeso.crypto.views.tradeInfo;

import com.negeso.crypto.data.dto.TopGainerDto;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.negeso.crypto.data.repository.PionexCurrencyRepository;
import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Top Gainers")
@Route(value = "top-gainers", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class CurrencyTopGainersView extends Div {
    private final Grid<TopGainerDto> gainersGrid = new Grid<>(TopGainerDto.class, false);
    private final PionexCurrencyRepository pionexCurrencyRepository;
    private final BinanceFetcherJob binanceFetcherJob;

    public CurrencyTopGainersView(PionexCurrencyRepository pionexCurrencyRepository,
                                  BinanceFetcherJob binanceFetcherJob) {
        this.pionexCurrencyRepository = pionexCurrencyRepository;
        this.binanceFetcherJob = binanceFetcherJob;

        addClassName("top-gainers-view");
        setSizeFull();
        configureGainersGrid();
        add(getContent());
    }

    private Component getContent() {
        //List<PionexCurrency> topGainers = pionexCurrencyRepository.findTopGainers();
        List<TopGainerDto> gainers = binanceFetcherJob.getGainers();
        if (gainers.isEmpty()) {
            return new Text("Please, update the page one more time");
        } else {
            gainersGrid.setItems(gainers);
            return gainersGrid;
        }
    }
/*
    private List<TopGainerDto> mapToTopGainerDto(List<PionexCurrency> topGainers) {
        return topGainers.stream()
                .map(x -> new TopGainerDto(x.getCurrencyName(),
                        x.getCurrentPrice().divide(x.getTwoMinPrice(), RoundingMode.HALF_UP)
                                .subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100)).toString().concat("%"),
                        x.getCurrentPrice().subtract(x.getTwoMinPrice()).toString().concat("$")))
                .collect(Collectors.toList());
    }

 */
    private void configureGainersGrid() {
        gainersGrid.addClassName("top-gainers-grid");
        gainersGrid.setSizeFull();
        gainersGrid.addColumn(TopGainerDto::getCurrencyName).setHeader("Currency name");
        gainersGrid.addColumn(TopGainerDto::getPriceDifferencePercentage).setHeader("Price difference (percentage)");
        gainersGrid.addColumn(TopGainerDto::getPriceDifferenceAbsolute).setHeader("Price difference (absolute)");
        gainersGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        gainersGrid.getColumns().forEach(col -> col.setSortable(true));
    }
}
