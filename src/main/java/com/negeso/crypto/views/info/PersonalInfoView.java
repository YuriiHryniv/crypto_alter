package com.negeso.crypto.views.info;

import com.negeso.crypto.data.entity.UserInfo;
import com.negeso.crypto.data.service.UserInfoService;
import com.negeso.crypto.security.AuthenticatedUser;
import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Personal Info")
@Route(value = "personalinfo", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class PersonalInfoView extends Div {
    private static final String EMPTY_LIST_MESSAGE = "No personal info stored for the current user";
    private final Grid<UserInfo> personalInfoGrid
            = new Grid<>(UserInfo.class, false);
    private final UserInfoService userInfoService;
    private final AuthenticatedUser authenticatedUser;
    private PersonalInfoForm form;

    public PersonalInfoView(AuthenticatedUser authenticatedUser,
                            UserInfoService userInfoService) {
        this.authenticatedUser = authenticatedUser;
        this.userInfoService = userInfoService;

        addClassName("personal-info-view");
        setSizeFull();
        configurePersonalInfoGrid();
        configureCurrencyTradedForm();
        add(getContent());
        updatePersonalInfo();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        List<UserInfo> allPersonalInfo = userInfoService.findUserInfoByEmail(authenticatedUser.get().get().getEmail());
        if (allPersonalInfo.isEmpty()) {
            return new HorizontalLayout (new Text(EMPTY_LIST_MESSAGE));
        } else {
            personalInfoGrid.setItems(allPersonalInfo);
            HorizontalLayout content = new HorizontalLayout(personalInfoGrid, form);
            content.setFlexGrow(3, personalInfoGrid);
            content.setFlexGrow(1, form);
            content.setSizeFull();
            return content;
        }
    }

    private void configurePersonalInfoGrid() {
        personalInfoGrid.addClassName("personal-info-grid");
        personalInfoGrid.setSizeFull();
        personalInfoGrid.setColumns("firstName", "lastName", "address", "postalCode", "city", "country", "phone");
        personalInfoGrid.getColumns().forEach(col -> col.setAutoWidth(true).setFlexGrow(1));
        personalInfoGrid.asSingleSelect().addValueChangeListener(selection ->
                editPersonalInfo(selection.getValue()));
    }

    private void configureCurrencyTradedForm() {
        form = new PersonalInfoForm(authenticatedUser);
        form.setWidth("25em");
        form.addListener(PersonalInfoForm.SaveEvent.class, this::savePersonalInfo);
        form.addListener(PersonalInfoForm.DeleteEvent.class, this::deletePersonalInfo);
        form.addListener(PersonalInfoForm.CloseEvent.class, event -> closeEditor());
    }

    private void deletePersonalInfo(PersonalInfoForm.DeleteEvent event) {
        userInfoService.deletePersonalInfo(event.getPersonalInfo());
        updatePersonalInfo();
        closeEditor();
    }

    private void savePersonalInfo(PersonalInfoForm.SaveEvent event) {
        userInfoService.savePersonalInfo(event.getPersonalInfo());
        updatePersonalInfo();
        closeEditor();
    }

    private void updatePersonalInfo() {
        personalInfoGrid.setItems(userInfoService.findUserInfoByEmail(authenticatedUser.get().get().getEmail()));
    }

    private void editPersonalInfo(UserInfo userInfo) {
        if (userInfo == null) {
            closeEditor();
        } else {
            form.setPersonalInfo(userInfo);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setPersonalInfo(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}
