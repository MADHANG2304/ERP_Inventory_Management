package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.ApprovalFilterDTO;
import com.example.dto.InventoryRequestDTO;
import com.example.dto.RequestApprovalDTO;
import com.example.dto.RequestItemDTO;
import com.example.enums.ApprovalRole;
import com.example.enums.ApprovalStatus;
import com.example.security.SecurityService;
import com.example.service.ApprovalProcessService;
import com.example.service.InventoryRequestService;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "approval-process", layout = MainLayout.class)
@PageTitle("Approval Process")
@RolesAllowed({
        "MANAGER",
        "INVENTORY_ADMIN",
        "SUPER_ADMIN",
        "DEPARTMENT_HEAD"
})
public class ApprovalProcessView extends VerticalLayout {

    private final ApprovalProcessService
            approvalProcessService;

    private final InventoryRequestService
            inventoryRequestService;

    private final Grid<RequestApprovalDTO>
            grid =
            new Grid<>(RequestApprovalDTO.class, false);

    private final String username;

    private final String userRole;

    private final TextField
            requestSearchField =
            new TextField();

    private final ComboBox<ApprovalStatus>
            statusFilter =
            new ComboBox<>();

    private final ComboBox<ApprovalRole>
            roleFilter =
            new ComboBox<>();

    private final Checkbox
            currentLevelFilter =
            new Checkbox(
                    "Current Level Only"
            );

    private final Button
            clearFilterButton =
            new Button("Clear");

    public ApprovalProcessView(

            ApprovalProcessService
                    approvalProcessService,

            SecurityService securityService,

            InventoryRequestService
                    inventoryRequestService
    ) {

        this.approvalProcessService =
                approvalProcessService;

        this.inventoryRequestService =
                inventoryRequestService;

        this.username =
                securityService
                        .getAuthenticatedUser();

        this.userRole =
                securityService
                        .getAuthenticatedRole();

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()

                .set("background", "#f5f7fb")

                .set("padding", "20px");

        // PAGE HEADER

        H2 heading =
                new H2(
                        "Approval Requests"
                );

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "32px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span subHeading =
                new Span(
                        "Manage pending approval requests"
                );

        subHeading.getStyle()

                .set("font-size", "15px")

                .set("color", "#64748b");

        VerticalLayout headingLayout =
                new VerticalLayout(
                        heading,
                        subHeading
                );

        headingLayout.setPadding(true);

        headingLayout.setSpacing(true);

        // FILTERS

        configureFilters();

        HorizontalLayout filterLayout =
                new HorizontalLayout();

        filterLayout.setWidthFull();

        filterLayout.setSpacing(true);

        filterLayout.setAlignItems(
                Alignment.END
        );

        filterLayout.add(
                requestSearchField
        );

        if(!userRole.equals("ROLE_MANAGER")
                &&
                !userRole.equals("ROLE_INVENTORY_ADMIN")) {

            filterLayout.add(
                    statusFilter,
                    roleFilter,
                    currentLevelFilter
            );
        }

        filterLayout.add(
                clearFilterButton
        );

        // GRID

        configureGrid();

        add(
                headingLayout,
                filterLayout,
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
                RequestApprovalDTO::getRequestNumber
        ).setHeader("Request No");

        grid.addColumn(
                RequestApprovalDTO::getApprovalOrder
        ).setHeader("Level");

        grid.addColumn(approval ->

                approval.getApprovalRole()
                        .name()

        ).setHeader("Role");

        grid.addComponentColumn(approval -> {

            Span badge =
                    new Span(
                            approval.getApprovalStatus()
                                    .name()
                    );

            badge.getStyle()

                    .set("padding", "5px 12px")

                    .set("border-radius", "12px")

                    .set("font-size", "12px")

                    .set("font-weight", "600")

                    .set("color", "white");

            switch (
                    approval.getApprovalStatus()
            ) {

                case PENDING ->

                        badge.getStyle()
                                .set("background", "#f59e0b");

                case APPROVED ->

                        badge.getStyle()
                                .set("background", "#16a34a");

                case REJECTED ->

                        badge.getStyle()
                                .set("background", "#dc2626");
            }

            return badge;

        }).setHeader("Status");

        grid.addComponentColumn(approval -> {

            Button viewButton =
                    new Button(
                            "View"
                    );

            viewButton.addThemeVariants(
                    ButtonVariant.LUMO_PRIMARY
            );

            viewButton.getStyle()

                    .set("border-radius", "8px")

                    .set("font-size", "13px");

            viewButton.addClickListener(event ->
                    openApprovalDialog(approval)
            );

            return viewButton;

        }).setHeader("Action");

        grid.setWidthFull();

        grid.setHeight("700px");

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "12px")

                .set("border", "1px solid #dbe2ea");
    }

    private void openApprovalDialog(
            RequestApprovalDTO approvalDTO
    ) {

        Dialog dialog =
                new Dialog();

        dialog.setWidth("700px");

        dialog.setHeaderTitle(
                "Approval Details"
        );

        InventoryRequestDTO request =
                inventoryRequestService
                        .getRequestById(
                                approvalDTO.getRequestId()
                        );

        // REQUEST INFO

        Span requestInfo =
                new Span(

                        "Request No : "

                                + approvalDTO.getRequestNumber()

                                + " | Role : "

                                + approvalDTO.getApprovalRole()
                                        .name()
                );

        requestInfo.getStyle()

                .set("font-size", "14px")

                .set("font-weight", "600")

                .set("color", "#475569");

        // REMARKS

        TextArea requestRemarks =
                new TextArea(
                        "Request Remarks"
                );

        requestRemarks.setWidthFull();

        requestRemarks.setReadOnly(true);

        requestRemarks.setValue(

                request.getRemarks() != null

                        ? request.getRemarks()

                        : ""
        );

        // ITEM GRID

        Grid<RequestItemDTO> itemGrid =
                new Grid<>(
                        RequestItemDTO.class,
                        false
                );

        itemGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES
        );

        itemGrid.addColumn(
                RequestItemDTO::getItemName
        ).setHeader("Item");

        itemGrid.addColumn(
                RequestItemDTO::getItemCode
        ).setHeader("Code");

        itemGrid.addColumn(
                RequestItemDTO::getRequestedQuantity
        ).setHeader("Qty");

        itemGrid.setItems(
                request.getRequestItems()
        );

        itemGrid.setHeight("220px");

        // COMMENTS

        TextArea comments =
                new TextArea(
                        "Comments"
                );

        comments.setWidthFull();

        comments.setPlaceholder(
                "Enter comments..."
        );

        // BUTTONS

        Button approveButton =
                new Button(
                        "Approve",
                        VaadinIcon.CHECK.create()
                );

        approveButton.addThemeVariants(
                ButtonVariant.LUMO_SUCCESS
        );

        Button rejectButton =
                new Button(
                        "Reject",
                        VaadinIcon.CLOSE.create()
                );

        rejectButton.addThemeVariants(
                ButtonVariant.LUMO_ERROR
        );

        Button cancelButton =
                new Button(
                        "Close"
                );

        cancelButton.addThemeVariants(
                ButtonVariant.LUMO_TERTIARY
        );

        approveButton.addClickListener(event -> {

            try {

                approvalProcessService
                        .approveRequest(

                                approvalDTO.getApprovalId(),

                                comments.getValue()
                        );

                NotificationUtil.success(
                        "Request approved successfully"
                );

                dialog.close();

                refreshGrid();

            } catch (Exception e) {

                NotificationUtil.error(
                        e.getMessage()
                );
            }
        });

        rejectButton.addClickListener(event -> {

            try {

                approvalProcessService
                        .rejectRequest(

                                approvalDTO.getApprovalId(),

                                comments.getValue()
                        );

                NotificationUtil.success(
                        "Request rejected successfully"
                );

                dialog.close();

                refreshGrid();

            } catch (Exception e) {

                NotificationUtil.error(
                        e.getMessage()
                );
            }
        });

        cancelButton.addClickListener(event ->
                dialog.close()
        );

        HorizontalLayout buttonLayout =
                new HorizontalLayout(

                        cancelButton,

                        rejectButton,

                        approveButton
                );

        buttonLayout.setWidthFull();

        buttonLayout.setJustifyContentMode(
                JustifyContentMode.END
        );

        VerticalLayout layout =
                new VerticalLayout(

                        requestInfo,

                        // requestRemarks,

                        itemGrid,

                        comments,

                        buttonLayout
                );

        layout.setSpacing(true);

        layout.setPadding(false);

        dialog.add(layout);

        dialog.open();
    }

    private void configureFilters() {

        requestSearchField.setPlaceholder(
                "Search Request No"
        );

        requestSearchField.setPrefixComponent(
                VaadinIcon.SEARCH.create()
        );

        requestSearchField.setWidth("260px");

        statusFilter.setPlaceholder(
                "Approval Status"
        );

        roleFilter.setPlaceholder(
                "Approval Role"
        );

        statusFilter.setItems(
                ApprovalStatus.values()
        );

        roleFilter.setItems(
                ApprovalRole.values()
        );

        requestSearchField
                .addValueChangeListener(
                        event -> applyFilters()
                );

        statusFilter
                .addValueChangeListener(
                        event -> applyFilters()
                );

        roleFilter
                .addValueChangeListener(
                        event -> applyFilters()
                );

        currentLevelFilter
                .addValueChangeListener(
                        event -> applyFilters()
                );

        clearFilterButton.addThemeVariants(
                ButtonVariant.LUMO_TERTIARY
        );

        clearFilterButton.addClickListener(
                event -> clearFilters()
        );
    }

    private void applyFilters() {

        ApprovalFilterDTO filterDTO =
                new ApprovalFilterDTO();

        filterDTO.setRequestNumber(
                requestSearchField.getValue()
        );

        filterDTO.setApprovalStatus(
                statusFilter.getValue()
        );

        filterDTO.setApprovalRole(
                roleFilter.getValue()
        );

        filterDTO.setCurrentLevel(

                !userRole.equals("ROLE_SUPER_ADMIN")

                        ? true

                        : null
        );

        grid.setItems(

                approvalProcessService
                        .filterApprovals(filterDTO)
        );
    }

    private void clearFilters() {

        requestSearchField.clear();

        statusFilter.clear();

        roleFilter.clear();

        currentLevelFilter.clear();

        refreshGrid();
    }

    private void refreshGrid() {

        grid.setItems(

                approvalProcessService
                        .getPendingApprovals(
                                username
                        )
        );
    }
}