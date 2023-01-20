package com.negeso.crypto.views.registration;

import com.negeso.crypto.data.dto.RegistrationDto;
import com.negeso.crypto.data.service.UserService;
import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Registration")
@Route(value = "registration", layout = MainLayout.class)
@RouteAlias(value = "registration", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class RegistrationView extends Div {
    private final EmailField email = new EmailField("Email address");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField repeatPassword = new PasswordField("Repeat password");
    private final TextField profilePictureUrl = new TextField("Profile picture url");
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Binder<RegistrationDto> binder = new Binder<>(RegistrationDto.class);

    public RegistrationView(UserService userService) {
        addClassName("registration-view");
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
        bindEntity();
        clearForm();
        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (!binder.getBean().getPassword().equals(binder.getBean().getRepeatPassword())){
                password.setInvalid(true);
            }
            userService.registerUser(binder.getBean());
            Notification.show("Successful registration!");
            clearForm();
        });
    }

    private void bindEntity() {
        binder.bind(email, RegistrationDto::getEmail, RegistrationDto::setEmail);
        binder.bind(password, RegistrationDto::getPassword, RegistrationDto::setPassword);
        binder.bind(repeatPassword, RegistrationDto::getRepeatPassword, RegistrationDto::setRepeatPassword);
        binder.bind(profilePictureUrl, RegistrationDto::getProfilePictureUrl, RegistrationDto::setProfilePictureUrl);
    }

    private void clearForm() {
        binder.setBean(new RegistrationDto());
    }

    private Component createTitle() {
        return new H3("Registration");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        email.setPattern("^.*@negeso\\.com$");
        email.setErrorMessage("Please enter a valid email address");
        password.setHelperText("A password must be at least 8 characters. It has to have at least one letter and one digit.");
        password.setPattern("^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$");
        password.setErrorMessage("Not a valid password");
        repeatPassword.setErrorMessage("Passwords do not match");
        formLayout.add(email, password, repeatPassword, profilePictureUrl);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

}
