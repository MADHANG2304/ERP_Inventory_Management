package com.example.base.ui;

import com.example.views.DashboardView;
import com.example.views.DepartmentView;
import com.example.views.DesignationView;
import com.example.views.InventoryCategoryView;
import com.example.views.InventoryItemView;
import com.example.views.layout.AppHeader;
import com.example.views.layout.SideMenu;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import com.example.security.SecurityService;
@PermitAll
public class MainLayout extends AppLayout {

    public MainLayout(SecurityService securityService) {

        AppHeader header = new AppHeader(securityService);

        SideMenu sideMenu = new SideMenu(securityService);

        VerticalLayout drawerLayout = new VerticalLayout(sideMenu);

        drawerLayout.setPadding(false);

        drawerLayout.setSpacing(false);

        addToNavbar(header);

        addToDrawer(drawerLayout);
    }
}
