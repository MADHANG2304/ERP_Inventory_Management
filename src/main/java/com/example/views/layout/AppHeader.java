package com.example.views.layout;

import com.example.security.SecurityService;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppHeader extends HorizontalLayout {

    public AppHeader(SecurityService securityService) {

        setWidthFull();

        setPadding(true);

        setSpacing(true);

        setAlignItems(Alignment.CENTER);

        getStyle()

                .set("background",
                        "rgba(255,255,255,0.88)")

                .set("backdrop-filter",
                        "blur(14px)")

                .set("border-bottom",
                        "1px solid #e2e8f0")

                .set("padding",
                        "18px 32px")

                .set("box-shadow",
                        "0 2px 10px rgba(15,23,42,0.05)");

        // DRAWER TOGGLE

        DrawerToggle toggle =
                new DrawerToggle();

        toggle.getStyle()

                .set("border-radius", "14px")

                .set("background", "white")

                .set("padding", "10px")

                .set("box-shadow",
                        "0 4px 14px rgba(0,0,0,0.08)")

                .set("border",
                        "2px solid #2563eb");

        // TITLE

        H2 title =
                new H2("ERP Inventory System");

        title.getStyle()

                .set("margin", "0")

                .set("font-size", "26px")

                .set("font-weight", "800")

                .set("color", "#0f172a")

                .set("letter-spacing", "-0.7px");

        // LOGOUT BUTTON

        Button logoutButton =
                new Button(
                        "Logout",
                        VaadinIcon.SIGN_OUT.create()
                );

        logoutButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        logoutButton.getStyle()

                .set("border-radius", "14px")

                .set("padding", "12px 20px")

                .set("font-size", "15px")

                .set("font-weight", "700")

                .set("background",
                        "linear-gradient(135deg, #2563eb, #4f46e5)")

                .set("color", "white")

                .set("box-shadow",
                        "0 8px 18px rgba(37,99,235,0.25)");

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