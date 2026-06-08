package com.example.views;

import java.time.format.DateTimeFormatter;

import com.example.dto.ApprovalProgressDTO;
import com.example.dto.ApprovalProgressItemDTO;
import com.example.dto.RequestTrackingDTO;
import com.example.service.ApprovalProgressService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class RequestTrackingDialog extends Dialog {

    public RequestTrackingDialog(
            RequestTrackingDTO request,
            ApprovalProgressService approvalProgressService
    ) {

        setWidth("1200px");
        setHeight("750px");

        VerticalLayout mainLayout =
                new VerticalLayout();

        mainLayout.setSizeFull();


        H3 requestInfoTitle =
                new H3("Request Information");

        Grid<String[]> requestInfoGrid =
                new Grid<>();

        requestInfoGrid.addColumn(data -> data[0])
                .setHeader("Field");

        requestInfoGrid.addColumn(data -> data[1])
                .setHeader("Value");

        requestInfoGrid.setItems(

                new String[]{
                        "Request Number",
                        request.getRequestNumber()
                },

                new String[]{
                        "Employee",
                        request.getEmployeeName()
                },

                new String[]{
                        "Department",
                        request.getDepartmentName()
                },

                new String[]{
                        "Status",
                        request.getRequestStatus().name()
                },

                new String[]{
                        "Remarks",
                        (request.getRemarks() == null || request.getRemarks().length() == 0)
                                ? "No Remarks"
                                : request.getRemarks()
                },

                new String[]{
                        "Request Date",
                        String.valueOf(
                                request.getRequestDate()
                        )
                }
        );

        requestInfoGrid.setHeight("320px");



        H3 itemSummaryTitle =
                new H3("Item Summary");

        Grid<ApprovalProgressItemDTO> itemGrid =
                new Grid<>(
                        ApprovalProgressItemDTO.class,
                        false
                );

        itemGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS
        );

        itemGrid.addColumn(
                ApprovalProgressItemDTO::getItemCode
        ).setHeader("Item Code");

        itemGrid.addColumn(
                ApprovalProgressItemDTO::getItemName
        ).setHeader("Item Name");

        itemGrid.addColumn(
                ApprovalProgressItemDTO::getRequestedQuantity
        ).setHeader("Requested Qty");

        itemGrid.addColumn(
                ApprovalProgressItemDTO::getApprovedQuantity
        ).setHeader("Approved Qty");

        itemGrid.addColumn(
                ApprovalProgressItemDTO::getIssuedQuantity
        ).setHeader("Issued Qty");

        itemGrid.addColumn(
                ApprovalProgressItemDTO::getRemainingQuantity
        ).setHeader("Remaining Qty");

        itemGrid.setItems(
                approvalProgressService
                        .getRequestItemSummary(
                                request.getRequestId()
                        )
        );

        itemGrid.setHeight("220px");



        H3 approvalTitle =
                new H3("Approval Timeline");

        Grid<ApprovalProgressDTO> approvalGrid =
                new Grid<>(
                        ApprovalProgressDTO.class,
                        false
                );

        approvalGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS
        );

        approvalGrid.addColumn(
                ApprovalProgressDTO::getApprovalLevel
        ).setHeader("Level");

        approvalGrid.addColumn(
                dto ->
                        dto.getApprovalRole()
                                .name()
        ).setHeader("Role");

        approvalGrid.addComponentColumn(dto -> {

            Span badge =
                    new Span(
                            dto.getApprovalStatus()
                                    .name()
                    );

            String color = "#f59e0b";

            switch (dto.getApprovalStatus()) {

                case APPROVED ->
                        color = "#16a34a";

                case REJECTED ->
                        color = "#dc2626";

                case PENDING ->
                        color = "#f59e0b";

                default ->
                        color = "#64748b";
            }

            badge.getStyle()

                    .set("background", color)

                    .set("color", "white")

                    .set("padding", "4px 12px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "600");

            return badge;

        }).setHeader("Status");

        approvalGrid.addColumn(

                approval -> {

                        if(approval.getActionDate() == null) {
                                return "-";
                        }

                        return approval.getActionDate()
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
        ).setHeader("Updated Date");

        approvalGrid.setItems(
                approvalProgressService.getApprovalProgress(request.getRequestId())
        );

        approvalGrid.setHeight("220px");

        Button closeButton = new Button("Close");

        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        closeButton.addClickListener(event -> close());

        HorizontalLayout footer = new HorizontalLayout(closeButton);

        footer.setWidthFull();

        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        mainLayout.add(

                requestInfoTitle,
                requestInfoGrid,

                itemSummaryTitle,
                itemGrid,

                approvalTitle,
                approvalGrid,

                footer
        );

        add(mainLayout);
    }
}