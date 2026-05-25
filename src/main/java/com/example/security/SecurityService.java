package com.example.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public String getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getName();
    }

    public String getAuthenticatedRole() {

        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if(authentication == null || authentication.getAuthorities().isEmpty()) {
            return null;
        }

        return authentication
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority();
    }

    public void logout() {

        UI ui = UI.getCurrent();

        VaadinSession.getCurrent().getSession().invalidate();

        SecurityContextHolder.clearContext();

        if(ui != null){
            ui.getPage().setLocation("/login"); 
        }
    }
}
