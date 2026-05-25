package com.example.views;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route("login")
@PageTitle("Login | ERP")
@PermitAll
public class LoginView extends VerticalLayout implements BeforeEnterObserver{
    
    private final LoginForm loginForm = new LoginForm();

    public LoginView(){
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginForm.setAction("login");

        add(loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        if(event.getLocation()
            .getQueryParameters()
            .getParameters()
            .containsKey("error")){

                loginForm.setError(true);
            }
    }
}
