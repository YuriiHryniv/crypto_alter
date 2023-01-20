package com.negeso.crypto.views.info;

import com.binance.api.client.domain.account.AssetBalance;
import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.job.BinanceFetcherJob;
import com.negeso.crypto.data.service.UserService;
import com.negeso.crypto.data.service.impl.CodeService;
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
import java.util.stream.Collectors;

@PageTitle("Account Info")
@Route(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class AccountInfoView extends Div {
    private static final String EMPTY_LIST_MESSAGE = "No account info stored for the current user";
    private final Grid<User> userGrid
            = new Grid<>(User.class, false);
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final BinanceFetcherJob binanceFetcherJob;
    private final CodeService codeService;
    private AccountInfoForm form;

    public AccountInfoView(AuthenticatedUser authenticatedUser,
                           UserService userService,
                           BinanceFetcherJob binanceFetcherJob, CodeService codeService) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.binanceFetcherJob = binanceFetcherJob;
        this.codeService = codeService;
        addClassName("personal-info-view");
        setSizeFull();
        configureAccountInfoGrid();
        configureAccountInfoForm();
        add(getContent());
        updateAccountInfo();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        List<User> allUsersByEmail = userService.getUserByEmail(authenticatedUser.get().get().getEmail());
        if (allUsersByEmail.isEmpty()) {
            return new HorizontalLayout (new Text(EMPTY_LIST_MESSAGE));
        } else {
            userGrid.setItems(allUsersByEmail);
            HorizontalLayout content = new HorizontalLayout(userGrid, form);
            content.setFlexGrow(3, userGrid);
            content.setFlexGrow(1, form);
            content.setSizeFull();
            return content;
        }
    }

    private void configureAccountInfoGrid() {
        userGrid.addClassName("account-info-grid");
        userGrid.setSizeFull();
        userGrid.setColumns("email", "autoTrading", "gamblingLimit", "maxConcurrentGambles",
                "profilePictureUrl", "binanceApiKey",
                "binanceApiSecret", "balance");
        userGrid.getColumns().forEach(col -> col.setAutoWidth(true).setFlexGrow(1));
        userGrid.asSingleSelect().addValueChangeListener(selection ->
                editPersonalInfo(selection.getValue()));
    }

    private void configureAccountInfoForm() {
        form = new AccountInfoForm(authenticatedUser);
        form.setWidth("25em");
        form.addListener(AccountInfoForm.SaveEvent.class, this::saveAccountInfo);
        form.addListener(AccountInfoForm.DeleteEvent.class, this::deleteAccount);
        form.addListener(AccountInfoForm.CloseEvent.class, event -> closeEditor());
    }

    private void deleteAccount(AccountInfoForm.DeleteEvent event) {
        userService.delete(authenticatedUser.get().get().getEmail());
        updateAccountInfo();
        closeEditor();
    }

    private void saveAccountInfo(AccountInfoForm.SaveEvent event) {
        User user = event.getUser();
        List<String> codesStr = binanceFetcherJob.getBalance(user.getBinanceApiKey(),
                        user.getBinanceApiSecret()).stream()
                .map(AssetBalance::getAsset)
                .toList();
        List<Code> codes = codesStr.stream()
                .map(c -> codeService.get(c + "USDT"))
                .toList();
        user.setCodes(codes);
        userService.updateAccountInfo(user);
        updateAccountInfo();
        closeEditor();
    }

    private void updateAccountInfo() {
        userGrid.setItems(userService.getUserByEmail(authenticatedUser.get().get().getEmail()));
    }

    private void editPersonalInfo(User user) {
        if (user == null) {
            closeEditor();
        } else {
            form.setAccountInfo(user);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setAccountInfo(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}
