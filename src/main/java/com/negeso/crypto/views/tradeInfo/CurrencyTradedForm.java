package com.negeso.crypto.views.tradeInfo;

import com.negeso.crypto.data.entity.PionexCurrency;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CurrencyTradedForm extends FormLayout {
    private final TextField currencyName = new TextField("Currency name");
    private final Select<Boolean> autoTrading = new Select<>();
    private final BigDecimalField currentPrice = new BigDecimalField("Current price");
    private final BigDecimalField buyingThresholdPercentage = new BigDecimalField("Buying threshold (percentage)");
    private final BigDecimalField buyingThresholdAbsolute = new BigDecimalField("Buying threshold (absolute)");
    private final BigDecimalField sellingThresholdPercentage = new BigDecimalField("Selling threshold (percentage)");
    private final BigDecimalField sellingThresholdAbsolute = new BigDecimalField("Selling threshold (absolute)");
    private final Button save = new Button("Save");
    private final Button cancel = new Button ("Cancel");
    private final Binder<PionexCurrency> binder
            = new BeanValidationBinder<>(PionexCurrency.class);
    private PionexCurrency pionexCurrency;



    public CurrencyTradedForm() {
        addClassName("currency-traded-form");
        binder.bindInstanceFields(this);
        List<Boolean> autoTradingOptions = new ArrayList<>();
        autoTradingOptions.add(true);
        autoTradingOptions.add(false);
        autoTrading.setLabel("Auto trading");
        autoTrading.setItems(autoTradingOptions);
        add(currencyName, currentPrice, autoTrading, buyingThresholdPercentage, buyingThresholdAbsolute,
                sellingThresholdPercentage, sellingThresholdAbsolute, createButtonLayout());
    }

    public void setCurrencyTraded(PionexCurrency pionexCurrency) {
        this.pionexCurrency = pionexCurrency;
        binder.readBean(pionexCurrency);
    }

    private HorizontalLayout createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, cancel);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(pionexCurrency);
            fireEvent(new SaveEvent(this, pionexCurrency));
        } catch (ValidationException e) {
            log.error("Can't write currency traded bean" + e);
        }
    }

    // Events
    public static abstract class CurrencyTradedFormEvent extends ComponentEvent<CurrencyTradedForm> {
        private PionexCurrency pionexCurrency;

        protected CurrencyTradedFormEvent(CurrencyTradedForm source, PionexCurrency pionexCurrency) {
            super(source, false);
            this.pionexCurrency = pionexCurrency;
        }

        public PionexCurrency getCurrencyTraded() {
            return pionexCurrency;
        }
    }

    public static class SaveEvent extends CurrencyTradedFormEvent {
        SaveEvent(CurrencyTradedForm source, PionexCurrency pionexCurrency) {
            super(source, pionexCurrency);
        }
    }

    public static class CloseEvent extends CurrencyTradedFormEvent {
        CloseEvent(CurrencyTradedForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
