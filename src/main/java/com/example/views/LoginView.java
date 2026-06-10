package com.example.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route("login")
@PageTitle("Login | ERP")
// @PermitAll
public class LoginView extends VerticalLayout
        implements BeforeEnterObserver {

    private final LoginForm loginForm =
            new LoginForm();

    private final Span errorMessage =
            new Span();

    public LoginView() {

        setSizeFull();

        setAlignItems(Alignment.CENTER);

        setJustifyContentMode(JustifyContentMode.CENTER);

        loginForm.setAction("login");

        loginForm.setForgotPasswordButtonVisible(false);

        errorMessage.setVisible(false);

        errorMessage.getStyle()

                .set("color", "#dc2626")

                .set("font-weight", "600")

                .set("font-size", "14px")

                .set("margin-bottom", "10px")

                .set("padding", "10px 14px")

                .set("background", "#fef2f2")

                .set("border", "1px solid #fecaca")

                .set("border-radius", "8px")

                .set("max-width", "320px")

                .set("text-align", "center");

        add(
                loginForm,
                errorMessage
        );
    }

    @Override
    public void beforeEnter(
            BeforeEnterEvent event
    ) {

        if(event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("inactive")) {

            errorMessage.setText(
                    "Your account is inactive. Please contact administrator."
            );

            errorMessage.setVisible(true);

            loginForm.setError(false);
        }

        else if(event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {

            errorMessage.setText(
                    "Incorrect username or password."
            );

            errorMessage.setVisible(true);

            loginForm.setError(false);
        }
    }
}