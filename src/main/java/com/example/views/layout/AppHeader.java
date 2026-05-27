package com.example.views.layout;

import com.example.security.SecurityService;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppHeader extends HorizontalLayout {

    public AppHeader(SecurityService securityService) {

        setWidthFull();

        setPadding(false);

        setMargin(false);

        setSpacing(true);

        setAlignItems(Alignment.CENTER);

        // getStyle()

        //         .set("background", "#ffffff")

        //         .set("padding", "14px 24px")

        //         .set("margin", "0")

        //         .set("border-bottom",
        //                 "1px solid #dbe2ea")

        //         .set("box-shadow",
        //                 "0 1px 4px rgba(0,0,0,0.04)");


        DrawerToggle toggle = new DrawerToggle();

        toggle.getStyle()

                .set("background", "white")

                .set("border-radius", "12px")

                .set("padding", "8px")

                .set("border",
                        "1px solid #dbe2ea")

                .set("box-shadow",
                        "0 2px 6px rgba(0,0,0,0.06)");


        H2 title = new H2("ERP Inventory System");

        title.getStyle()

                .set("margin", "0")

                .set("font-size", "22px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        // LOGOUT BUTTON

        Button logoutButton =
                new Button(
                        "Logout",
                        VaadinIcon.SIGN_OUT.create()
                );

        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        logoutButton.getStyle()

                .set("border-radius", "12px")

                .set("padding", "10px 18px")

                .set("font-size", "14px")

                .set("font-weight", "600");

        logoutButton.addClickListener(event ->
                securityService.logout()
        );

        expand(title);

        add(
                toggle,
                title,
                logoutButton
        );
    }
}