package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.AuditLogDTO;
import com.example.service.AuditLogService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "audit-logs",
        layout = MainLayout.class)

@PageTitle("Audit Logs")

@RolesAllowed({
        "SUPER_ADMIN",
        "INVENTORY_ADMIN"
})
public class AuditLogView
        extends VerticalLayout {

    private final AuditLogService
            auditLogService;

    private final Grid<AuditLogDTO>
            grid =
            new Grid<>(
                    AuditLogDTO.class,
                    false
            );

    private final TextField
            searchField =
            new TextField();

    public AuditLogView(
            AuditLogService auditLogService
    ) {

        this.auditLogService =
                auditLogService;

        setSizeFull();

        setPadding(true);

        getStyle()

                .set("background",
                        "#f4f7fb")

                .set("padding",
                        "24px");

        H2 heading =
                new H2(
                        "Audit Logs"
                );

        heading.getStyle()

                .set("font-size",
                        "36px")

                .set("font-weight",
                        "700")

                .set("margin",
                        "0")

                .set("color",
                        "#0f172a");

        Span subHeading =
                new Span(
                        "Track all system activities and user actions"
                );

        subHeading.getStyle()

                .set("color",
                        "#64748b")

                .set("font-size",
                        "15px");

        searchField.setPlaceholder(
                "Search audit logs..."
        );

        searchField.setPrefixComponent(
                VaadinIcon.SEARCH.create()
        );

        searchField.setWidth(
                "350px"
        );

        searchField.addValueChangeListener(event -> {

            grid.setItems(

                    auditLogService
                            .searchLogs(
                                    event.getValue()
                            )
            );
        });

        configureGrid();

        add(
                heading,
                subHeading,
                searchField,
                grid
        );

        refreshGrid();
    }

    private void configureGrid() {

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                AuditLogDTO::getUsername
        ).setHeader("Username");

        grid.addColumn(
                AuditLogDTO::getRoleName
        ).setHeader("Role");

        grid.addColumn(
                AuditLogDTO::getModuleName
        ).setHeader("Module");

        grid.addColumn(
                AuditLogDTO::getActionType
        ).setHeader("Action");

        grid.addColumn(
                AuditLogDTO::getDescription
        ).setHeader("Description");

        grid.addColumn(
                AuditLogDTO::getActionTime
        ).setHeader("Action Time");

        grid.setSizeFull();
    }

    private void refreshGrid() {

        grid.setItems(
                auditLogService
                        .getAllLogs()
        );
    }
}