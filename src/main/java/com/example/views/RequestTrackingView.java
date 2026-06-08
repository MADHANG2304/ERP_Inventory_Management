package com.example.views;

import java.time.format.DateTimeFormatter;

import com.example.base.ui.MainLayout;
import com.example.dto.RequestTrackingDTO;
import com.example.security.SecurityService;
import com.example.service.ApprovalProgressService;
import com.example.service.RequestTrackingService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;


@PermitAll
@Route(
        value = "request-tracking",
        layout = MainLayout.class
)
@PageTitle("Request Tracking")
public class RequestTrackingView extends VerticalLayout {

    private final RequestTrackingService requestTrackingService;

    private final ApprovalProgressService approvalProgressService;

    private final SecurityService securityService;

    private final Grid<RequestTrackingDTO> grid =
            new Grid<>(
                    RequestTrackingDTO.class,
                    false
            );

    public RequestTrackingView(
            RequestTrackingService requestTrackingService,
            ApprovalProgressService approvalProgressService,
            SecurityService securityService
    ) {

        this.requestTrackingService =
                requestTrackingService;

        this.approvalProgressService =
                approvalProgressService;

        this.securityService =
                securityService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        add(
                createHeader()
        );

        configureGrid();

        add(
                grid
        );

        loadData();
    }

    private HorizontalLayout createHeader() {

        H2 title =
                new H2(
                        "Request Tracking"
                );

        title.getStyle()

                .set("margin", "0")

                .set("font-size", "28px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span description =
                new Span(
                        "Track request status, approvals and issued items"
                );

        description.getStyle()

                .set("color", "#64748b")

                .set("font-size", "14px");

        VerticalLayout left =
                new VerticalLayout(
                        title,
                        description
                );

        left.setPadding(true);

        left.setSpacing(true);

        HorizontalLayout header =
                new HorizontalLayout(
                        left
                );

        header.setWidthFull();

        return header;
    }

    private void configureGrid() {

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                RequestTrackingDTO::getRequestNumber
        )
        .setHeader("Request No")
        .setAutoWidth(true);

        grid.addColumn(
                RequestTrackingDTO::getEmployeeName
        )
        .setHeader("Requester")
        .setAutoWidth(true);

        grid.addColumn(
                RequestTrackingDTO::getDepartmentName
        )
        .setHeader("Department")
        .setAutoWidth(true);

        grid.addComponentColumn(dto -> {

            Span badge =
                    new Span(
                            dto.getRequestStatus()
                                    .name()
                    );

            String color = "#2563eb";

            switch (dto.getRequestStatus()) {

                case APPROVED ->
                        color = "#16a34a";

                case ISSUED ->
                        color = "#0ea5e9";

                case PARTIALLY_ISSUED ->
                        color = "#f59e0b";

                case REJECTED ->
                        color = "#dc2626";

                case PENDING_APPROVAL ->
                        color = "#f97316";

                default ->
                        color = "#64748b";
            }

            badge.getStyle()

                    .set("background", color)

                    .set("color", "white")

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "600");

            return badge;

        }).setHeader("Status");

        grid.addColumn(request -> {

                        if(request.getRequestDate() == null) {
                                return "-";
                        }

                        return request.getRequestDate()
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        })
        .setHeader("Request Date")
        .setAutoWidth(true);

        grid.addComponentColumn(dto -> {

            Button viewButton =
                    new Button(
                            "View",
                            VaadinIcon.EYE.create()
                    );

            viewButton.addThemeVariants(
                    ButtonVariant.LUMO_PRIMARY
            );

            viewButton.addClickListener(event -> {

                RequestTrackingDialog dialog =
                        new RequestTrackingDialog(
                                dto,
                                approvalProgressService
                        );

                dialog.open();
            });

            return viewButton;

        }).setHeader("Action");

        grid.setSizeFull();

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "18px")

                .set("overflow", "hidden")

                .set("box-shadow",
                        "0 6px 18px rgba(0,0,0,0.08)");
    }

    private void loadData() {

        String username =
                securityService
                        .getAuthenticatedUser();

        String role =
                securityService
                        .getAuthenticatedRole();

        grid.setItems(

                requestTrackingService
                        .getTrackingRequests(
                                username,
                                role
                        )
        );
    }
}