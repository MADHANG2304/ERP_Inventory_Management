package com.example.views;

import java.security.Principal;

import com.example.base.ui.MainLayout;
import com.example.dto.ReturnedItemDTO;
import com.example.security.SecurityService;
import com.example.service.ReturnService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "my-issued-items",
        layout = MainLayout.class)
@PageTitle("My Issued Items")
@RolesAllowed("EMPLOYEE")
public class EmployeeIssuedItemsView
        extends VerticalLayout {

    private final ReturnService returnService;

    private final SecurityService securityService;

    private final Grid<ReturnedItemDTO>
            grid =
            new Grid<>(ReturnedItemDTO.class,
                    false);

    private final String username;

    public EmployeeIssuedItemsView(
            ReturnService returnService,
            SecurityService securityService
    ) {

        this.returnService =
                returnService;

        this.securityService = securityService;

        this.username =
                securityService.getAuthenticatedUser();
                

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        H2 heading =
                new H2("My Issued Items");

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
                        ReturnedItemDTO
                                ::getIssueReferenceNumber
                )
                .setHeader("Issue Reference");

        grid.addColumn(
                        ReturnedItemDTO
                                ::getItemName
                )
                .setHeader("Item");

        grid.addColumn(
                        ReturnedItemDTO
                                ::getItemCode
                )
                .setHeader("Item Code");

        grid.addColumn(
                        ReturnedItemDTO
                                ::getIssuedQuantity
                )
                .setHeader("Issued Quantity");

        grid.addComponentColumn(item -> {

            Span status =
                    new Span(
                            item.getIssueStatus()
                                    .name()
                    );

            switch (
                    item.getIssueStatus()
            ) {

                case ISSUED ->
                        status.getStyle()
                                .set("color",
                                        "blue");

                case PARTIALLY_RETURNED ->
                        status.getStyle()
                                .set("color",
                                        "orange");

                case RETURNED ->
                        status.getStyle()
                                .set("color",
                                        "green");

                case LOST ->
                        status.getStyle()
                                .set("color",
                                        "red");

                case DAMAGED ->
                        status.getStyle()
                                .set("color",
                                        "brown");
            }

            status.getStyle()
                    .set("font-weight",
                            "bold");

            return status;
        })
        .setHeader("Issue Status");

        grid.setWidthFull();

        grid.setHeight("600px");
    }

    private void refreshGrid() {

        grid.setItems(

                returnService
                        .getIssuedItemsForReturn()
                        .stream()

                        .filter(item ->

                                item.getEmployeeName()
                                        != null
                        )

                        .toList()
        );
    }
}