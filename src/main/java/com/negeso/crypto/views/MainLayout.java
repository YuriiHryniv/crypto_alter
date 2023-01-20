package com.negeso.crypto.views;

import com.negeso.crypto.components.appnav.AppNav;
import com.negeso.crypto.components.appnav.AppNavItem;
import com.negeso.crypto.data.entity.User;
import com.negeso.crypto.data.service.UserInfoService;
import com.negeso.crypto.security.AuthenticatedUser;
import com.negeso.crypto.views.info.AccountInfoView;
import com.negeso.crypto.views.info.BalanceInfoView;
import com.negeso.crypto.views.info.PersonalInfoView;
import com.negeso.crypto.views.order.OrderView;
import com.negeso.crypto.views.tradeInfo.CurrencyTopGainersView;
import com.negeso.crypto.views.tradeInfo.CurrencyTradedView;
import com.negeso.crypto.views.tradeInfo.GainersAndLosersView;
import com.negeso.crypto.views.transactions.TransactionsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    private final UserInfoService userInfoService;

    public MainLayout(AuthenticatedUser authenticatedUser,
                      AccessAnnotationChecker accessChecker,
                      UserInfoService userInfoService) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.userInfoService = userInfoService;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Crypto");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private AppNav createNavigation() {
        AppNav nav = new AppNav();
        nav.addClassNames("app-nav");

        if (accessChecker.hasAccess(PersonalInfoView.class)) {
            nav.addItem(new AppNavItem("Personal info", PersonalInfoView.class, "la la-info"));

        }
        if (accessChecker.hasAccess(AccountInfoView.class)) {
            nav.addItem(new AppNavItem("Account info", AccountInfoView.class, "la la-info"));

        }
        if (accessChecker.hasAccess(BalanceInfoView.class)) {
            nav.addItem(new AppNavItem("Balance info", BalanceInfoView.class, "la la-info"));

        }

        if (accessChecker.hasAccess(OrderView.class)) {
            nav.addItem(new AppNavItem("Orders", OrderView.class, "la la-dollar-sign"));

        }

/*
        if (accessChecker.hasAccess(TransactionsView.class)) {
            nav.addItem(new AppNavItem("Transactions", TransactionsView.class, "la la-dollar-sign"));

        }

 */
        if (accessChecker.hasAccess(CurrencyTradedView.class)) {
            nav.addItem(new AppNavItem("Traded Currencies", CurrencyTradedView.class, "la la-dollar-sign"));

        }
        if (accessChecker.hasAccess(GainersAndLosersView.class)) {
            nav.addItem(new AppNavItem("Gainers and Losers", GainersAndLosersView.class, "la la-dollar-sign"));

        }
        if (accessChecker.hasAccess(CurrencyTopGainersView.class)) {
            nav.addItem(new AppNavItem("Top Gainers", CurrencyTopGainersView.class, "la la-dollar-sign"));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("app-nav-footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            String name = userInfoService.findFirstAndLastName(user.getEmail());

            Avatar avatar = new Avatar(name, user.getProfilePictureUrl());
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Logout", e -> authenticatedUser.logout());

            Span nameSpan = new Span(name);
            nameSpan.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, nameSpan);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
