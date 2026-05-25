package com.example.views;

import java.security.Principal;

import com.example.base.ui.MainLayout;
import com.example.dto.InventoryRequestDTO;
import com.example.security.SecurityService;
import com.example.service.InventoryRequestService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "my-requests",
        layout = MainLayout.class)
@PageTitle("My Requests")
@RolesAllowed("EMPLOYEE")
public class EmployeeRequestHistoryView
        extends VerticalLayout {

    private final InventoryRequestService
            inventoryRequestService;
        
    private final SecurityService securityService;

    private final Grid<InventoryRequestDTO>
            grid =
            new Grid<>(InventoryRequestDTO.class,
                    false);

    private final String username;

    public EmployeeRequestHistoryView(
            InventoryRequestService
                    inventoryRequestService,
            SecurityService securityService
    ) {

        this.inventoryRequestService =
                inventoryRequestService;

        this.securityService = securityService;

        this.username =
                securityService.getAuthenticatedUser();

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        H2 heading =
                new H2("My Request History");

        configureGrid();

        add(
                heading,
                grid
        );

        refreshGrid();
    }

    private void configureGrid() {

        grid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES
        );

        grid.addColumn(
                        InventoryRequestDTO
                                ::getRequestNumber
                )
                .setHeader("Request Number");

        grid.addColumn(
                        request -> request
                                .getRequestDate()
                                .toLocalDate()
                )
                .setHeader("Request Date");

        grid.addColumn(
                        InventoryRequestDTO
                                ::getRemarks
                )
                .setHeader("Remarks");

        grid.addComponentColumn(request -> {

            Span status =
                    new Span(
                            request.getRequestStatus()
                                    .name()
                    );

            switch (
                    request.getRequestStatus()
            ) {

                case APPROVED ->
                        status.getStyle()
                                .set("color",
                                        "green");

                case REJECTED ->
                        status.getStyle()
                                .set("color",
                                        "red");

                case PENDING_APPROVAL ->
                        status.getStyle()
                                .set("color",
                                        "orange");

                default ->
                        status.getStyle()
                                .set("color",
                                        "blue");
            }

            status.getStyle()
                    .set("font-weight",
                            "bold");

            return status;
        })
        .setHeader("Status");

        grid.setWidthFull();

        grid.setHeight("600px");
    }

    private void refreshGrid() {

        grid.setItems(

                inventoryRequestService
                        .getAllRequests()
                        .stream()

                        .filter(request ->

                                request.getEmployeeName()
                                        .equalsIgnoreCase(
                                                username
                                        )
                        )

                        .toList()
        );
    }
}