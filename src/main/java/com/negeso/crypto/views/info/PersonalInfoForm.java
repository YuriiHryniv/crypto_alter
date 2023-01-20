package com.negeso.crypto.views.info;

import com.negeso.crypto.data.entity.UserInfo;
import com.negeso.crypto.security.AuthenticatedUser;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PersonalInfoForm extends FormLayout {
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final TextField address = new TextField("Address");
    private final TextField city = new TextField("City");
    private final TextField country = new TextField("Country");
    private final TextField postalCode = new TextField("Postal code");
    private final PhoneNumberField phone = new PhoneNumberField("Phone number");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button ("Cancel");
    private final AuthenticatedUser authenticatedUser;
    private final Binder<UserInfo> binder
            = new BeanValidationBinder<>(UserInfo.class);
    private UserInfo personalInfo;

    public PersonalInfoForm(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        addClassName("personal-info-form");
        binder.bindInstanceFields(this);
        add(firstName, lastName, address, city, country, postalCode, phone, createButtonLayout());
    }

    private HorizontalLayout createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, personalInfo)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, cancel);
    }

    public void setPersonalInfo(UserInfo personalInfo) {
        this.personalInfo = personalInfo;
        binder.readBean(personalInfo);
    }

    private void validateAndSave() {
        try {
            personalInfo.setEmail(authenticatedUser.get().get().getEmail());
            binder.writeBean(personalInfo);
            fireEvent(new SaveEvent(this, personalInfo));
        } catch (ValidationException e) {
            log.error("Can't write currency traded bean" + e);
        }
    }

    public static abstract class PersonalInfoFormEvent extends ComponentEvent<PersonalInfoForm> {
        private UserInfo personalInfo;

        protected PersonalInfoFormEvent(PersonalInfoForm source, UserInfo personalInfo) {
            super(source, false);
            this.personalInfo = personalInfo;
        }

        public UserInfo getPersonalInfo() {
            return personalInfo;
        }
    }

    public static class SaveEvent extends PersonalInfoForm.PersonalInfoFormEvent {
        SaveEvent(PersonalInfoForm source, UserInfo personalInfo) {
            super(source, personalInfo);
        }
    }

    public static class DeleteEvent extends PersonalInfoForm.PersonalInfoFormEvent {
        DeleteEvent(PersonalInfoForm source, UserInfo personalInfo) {
            super(source, personalInfo);
        }
    }

    public static class CloseEvent extends PersonalInfoForm.PersonalInfoFormEvent {
        CloseEvent(PersonalInfoForm source) {
            super(source, null);
        }
    }

    private static class PhoneNumberField extends CustomField<String> {
        private final ComboBox<String> countryCode = new ComboBox<>();
        private final TextField number = new TextField();

        public PhoneNumberField(String label) {
            setLabel(label);
            initializePhoneNumber();
        }

        private void initializePhoneNumber() {
            countryCode.setWidth("120px");
            countryCode.setPlaceholder("Country code");
            countryCode.setPattern("\\+\\d*");
            countryCode.setPreventInvalidInput(true);
            countryCode.setItems("+38", "+354", "+91", "+62", "+98", "+964", "+353", "+44", "+972", "+39", "+225");
            countryCode.addCustomValueSetListener(e -> countryCode.setValue(e.getDetail()));
            number.setPattern("\\d*");
            number.setPreventInvalidInput(true);
            HorizontalLayout layout = new HorizontalLayout(countryCode, number);
            layout.setFlexGrow(1.0, number);
            add(layout);
        }

        @Override
        protected String generateModelValue() {
            if (countryCode.getValue() != null && number.getValue() != null) {
                return countryCode.getValue() + " " + number.getValue();
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String phoneNumber) {
            String[] parts = phoneNumber != null ?
                    phoneNumber.split(" ", 2) : new String[0];
            if (parts.length == 1) {
                countryCode.clear();
                number.setValue(parts[0]);
            } else if (parts.length == 2) {
                countryCode.setValue(parts[0]);
                number.setValue(parts[1]);
            } else {
                countryCode.clear();
                number.clear();
            }
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
