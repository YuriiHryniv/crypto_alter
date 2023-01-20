package com.negeso.crypto.views.info;

import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.security.AuthenticatedUser;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class AccountInfoForm extends FormLayout {
    private final TextField email = new TextField("Email");
    private final TextField profilePictureUrl = new TextField("Profile picture url");
    private final TextField binanceApiKey = new TextField("Binance API key");
    private final TextField binanceApiSecret = new TextField("Binance API secret");
    private final Select<Boolean> autoTrading = new Select<>();
    private final TextField gamblingLimit = new TextField("Gambling limit");
    private final TextField maxConcurrentGambles = new TextField("Max concurrent gambles");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button ("Cancel");
    private final AuthenticatedUser authenticatedUser;
    private final Binder<User> binder
            = new BeanValidationBinder<>(User.class);
    private User user;

    public AccountInfoForm(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        addClassName("account-info-form");
        binder.bindInstanceFields(this);
        List<Boolean> autoTradingOptions = new ArrayList<>();
        autoTradingOptions.add(true);
        autoTradingOptions.add(false);
        autoTrading.setLabel("Auto trading");
        autoTrading.setItems(autoTradingOptions);
        add(email, profilePictureUrl, binanceApiKey, binanceApiSecret, autoTrading, gamblingLimit, maxConcurrentGambles, createButtonLayout());
    }

    private HorizontalLayout createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new AccountInfoForm.DeleteEvent(this, user)));
        cancel.addClickListener(event -> fireEvent(new AccountInfoForm.CloseEvent(this)));

        return new HorizontalLayout(save, delete, cancel);
    }

    public void setAccountInfo(User user) {
        this.user = user;
        binder.readBean(user);
    }

    private void validateAndSave() {
        try {
            user.setEmail(authenticatedUser.get().get().getEmail());
            binder.writeBean(user);
            fireEvent(new AccountInfoForm.SaveEvent(this, user));
        } catch (ValidationException e) {
            log.error("Can't write currency traded bean" + e);
        }
    }

    public static abstract class AccountInfoFormEvent extends ComponentEvent<AccountInfoForm> {
        private User user;

        protected AccountInfoFormEvent(AccountInfoForm source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class SaveEvent extends AccountInfoForm.AccountInfoFormEvent {
        SaveEvent(AccountInfoForm source, User user) {
            super(source, user);
        }
    }

    public static class DeleteEvent extends AccountInfoForm.AccountInfoFormEvent {
        DeleteEvent(AccountInfoForm source, User user) {
            super(source, user);
        }
    }

    public static class CloseEvent extends AccountInfoForm.AccountInfoFormEvent {
        CloseEvent(AccountInfoForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
