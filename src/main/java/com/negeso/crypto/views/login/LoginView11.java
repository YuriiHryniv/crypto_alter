/*
package com.negeso.crypto.views.login;

import com.negeso.crypto.data.service.UserService;
import com.negeso.crypto.security.AuthenticatedUser;
import com.negeso.crypto.views.registration.RegistrationView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.server.Page;

import javax.security.sasl.AuthenticationException;
import java.io.Serializable;

import static org.aspectj.weaver.UnresolvedType.add;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver, Serializable {
    private static final long serialVersionUID = 6009442170907349114L;
    private final Button register = new Button("Register");
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;

    public LoginView(UserService userService, AuthenticatedUser authenticatedUser) {
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Crypto");
        i18n.getHeader().setDescription("Login using your email and password");
        setI18n(i18n);
        addLoginListener(login -> authenticate(login.getUsername(), login.getPassword()));
        createButtonLayout();
        register.addClickListener(event -> transferToRegistration());
        setForgotPasswordButtonVisible(false);
        setOpened(true);


    }

    private Component createButtonLayout() {
        */
/*
        addClassName("register-button");
        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return new HorizontalLayout(register);

         *//*

        RouterLink register1 = new RouterLink("Register", RegistrationView.class);
        return new HorizontalLayout(register1);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    private void authenticate(String username, String password) {
        try {
            userService.authenticate(username, password);
        } catch (AuthenticationException e) {
            Notification.show("Invalid credentials");
        }
    }

    private void transferToRegistration() {
        Page.getCurrent().setLocation("registration");
    }
}
*/
