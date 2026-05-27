package com.example.views;

import com.example.dto.ApprovalProgressDTO;
import com.example.service.ApprovalProgressService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ApprovalProgressDialog extends Dialog {

    public ApprovalProgressDialog(
        Long requestId,
        ApprovalProgressService approvalProgressService
        ) {

        setWidth("1000px");

        setHeight("600px");

        getElement().getStyle()
                .set("border-radius", "22px")
                .set("overflow", "hidden");

        VerticalLayout mainLayout =
                new VerticalLayout();

        mainLayout.setSizeFull();

        mainLayout.setPadding(false);

        mainLayout.setSpacing(false);

        mainLayout.getStyle()
                .set("background", "#f8fafc");

        // HEADER SECTION

        VerticalLayout headerLayout =
                new VerticalLayout();

        headerLayout.setPadding(true);

        headerLayout.setSpacing(false);

        headerLayout.getStyle()

                .set("background",
                        "linear-gradient(135deg,#1e3a8a,#2563eb)")

                .set("padding", "22px")

                .set("color", "white");

        H2 heading = new H2("Approval Workflow Progress");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "30px")

                .set("font-weight", "700")

                .set("color", "white");

        Span subHeading = new Span("Track request approval flow and current approval stage");

        subHeading.getStyle()

                .set("font-size", "14px")

                .set("opacity", "0.9")

                .set("margin-top", "4px");

        headerLayout.add(
                heading,
                subHeading
        );

        // GRID

        Grid<ApprovalProgressDTO> grid =
                new Grid<>(
                        ApprovalProgressDTO.class,
                        false
                );

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                ApprovalProgressDTO::getRequestNumber
        )
        .setHeader("Request No")
        .setAutoWidth(true);

        grid.addColumn(
                ApprovalProgressDTO::getApprovalLevel
        )
        .setHeader("Approval Level")
        .setAutoWidth(true);

        grid.addComponentColumn(dto -> {

                Span roleBadge =
                        new Span(
                                dto.getApprovalRole().name()
                        );

                roleBadge.getStyle()

                        .set("background", "#dbeafe")

                        .set("color", "#1d4ed8")

                        .set("padding", "6px 14px")

                        .set("border-radius", "30px")

                        .set("font-size", "12px")

                        .set("font-weight", "700");

                return roleBadge;

        }).setHeader("Approval Role");

        grid.addComponentColumn(dto -> {

                String backgroundColor = "#2563eb";

                switch (dto.getApprovalStatus()) {

                case APPROVED ->
                        backgroundColor = "#16a34a";

                case REJECTED ->
                        backgroundColor = "#dc2626";

                case PENDING ->
                        backgroundColor = "#f59e0b";
                }

                Span statusBadge =
                        new Span(
                                dto.getApprovalStatus().name()
                        );

                statusBadge.getStyle()

                        .set("background", backgroundColor)

                        .set("color", "white")

                        .set("padding", "6px 14px")

                        .set("border-radius", "30px")

                        .set("font-size", "12px")

                        .set("font-weight", "700");

                return statusBadge;

        }).setHeader("Approval Status");

        grid.addComponentColumn(dto -> {

                if(Boolean.TRUE.equals(dto.getCurrentLevel())) {

                        Span current = new Span("IN PROGRESS");

                        current.getStyle()

                                .set("background", "#dcfce7")

                                .set("color", "#15803d")

                                .set("padding", "6px 14px")

                                .set("border-radius", "30px")

                                .set("font-size", "12px")

                                .set("font-weight", "700");

                        return current;
                }

                Span completed = new Span("-");

                completed.getStyle()

                        .set("color", "#94a3b8")

                        .set("font-weight", "600");

                return completed;

        }).setHeader("Current Stage");

        grid.addColumn(ApprovalProgressDTO::getActionDate)
                .setHeader("Updated Date")
                .setAutoWidth(true);

        grid.setItems(approvalProgressService.getApprovalProgress(requestId));

        grid.setSizeFull();

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "18px")

                .set("overflow", "hidden")

                .set("box-shadow",
                        "0 6px 18px rgba(0,0,0,0.08)");

        VerticalLayout gridWrapper = new VerticalLayout(grid);

        gridWrapper.setPadding(true);

        gridWrapper.setSizeFull();


        Button closeButton = new Button("Close");

        closeButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        closeButton.getStyle()

                .set("background",
                        "linear-gradient(135deg,#2563eb,#1d4ed8)")

                .set("border-radius", "10px")

                .set("font-weight", "600")

                .set("height", "40px")

                .set("padding", "0 20px");

        closeButton.addClickListener(event -> close());

        HorizontalLayout footer =
                new HorizontalLayout(closeButton);

        footer.setWidthFull();

        footer.setJustifyContentMode(
                JustifyContentMode.END
        );

        footer.getStyle()

                .set("padding", "0 20px 20px 20px");

        mainLayout.add(
                headerLayout,
                gridWrapper,
                footer
        );

        mainLayout.expand(gridWrapper);

        add(mainLayout);
        }
}