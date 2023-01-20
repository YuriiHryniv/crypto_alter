package com.negeso.crypto.views.transactions;

import com.negeso.crypto.views.MainLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Transactions")
@Route(value = "transactions", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
public class TransactionsView extends Div {
    public TransactionsView() {
        Anchor link = new Anchor("https://www.negeso.com/media/crypto/orders.csv", "Download transactions history");
        add(link);
    }
}
