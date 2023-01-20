package com.negeso.crypto.views.login;


import com.negeso.crypto.data.service.UserService;
import com.negeso.crypto.security.AuthenticatedUser;
import com.negeso.crypto.views.registration.RegistrationView;
import com.vaadin.flow.router.*;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | Vaadin CRM")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private UserService userService;
    private AuthenticatedUser authenticatedUser;

    private final LoginForm login = new LoginForm();

    public LoginView(UserService userService,
                     AuthenticatedUser authenticatedUser){
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        add(new H1("Crypto Casino"), login, new RouterLink("Register", RegistrationView.class));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
