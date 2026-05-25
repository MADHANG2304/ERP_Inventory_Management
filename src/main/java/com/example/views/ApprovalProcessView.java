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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

        private final ApprovalProcessService approvalProcessService;

        private final InventoryRequestService inventoryRequestService;

        private final Grid<RequestApprovalDTO> grid = new Grid<>(RequestApprovalDTO.class, false);

        private final TextField requestNumber = new TextField("Request Number");

        private final TextField approvalRole = new TextField("Approval Role");

        private final TextField approvalLevel = new TextField("Approval Level");

        private final TextField approvalStatus = new TextField("Approval Status");

        private final Grid<RequestItemDTO> itemGrid = new Grid<>(RequestItemDTO.class, false);

        private final TextArea requestRemarks = new TextArea("Request Remarks");

        private final TextArea comments = new TextArea("Comments");

        private RequestApprovalDTO selectedApproval;

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
        ApprovalProcessService approvalProcessService,
        SecurityService securityService,
        InventoryRequestService inventoryRequestService
                ) {

                this.approvalProcessService =
                        approvalProcessService;

                this.inventoryRequestService =
                        inventoryRequestService;

                this.username =
                        securityService.getAuthenticatedUser();
                
                this.userRole =
                        securityService.getAuthenticatedRole();

                setSizeFull();

                setPadding(true);

                setSpacing(true);

                getStyle()

                        .set("background",
                                "linear-gradient(to bottom right, #f8fafc, #eef2ff)")

                        .set("padding", "24px")

                        .set("overflow", "auto");

                H2 heading =
                        new H2("Approval Workflow Inbox");

                heading.getStyle()

                        .set("margin", "0")

                        .set("font-size", "34px")

                        .set("font-weight", "700")

                        .set("color", "#0f172a");

                Span subHeading =
                        new Span(
                                "Manage and process pending approval requests"
                        );

                subHeading.getStyle()

                        .set("font-size", "15px")

                        .set("color", "#64748b");

                VerticalLayout headingSection =
                        new VerticalLayout(
                                heading,
                                subHeading
                        );

                headingSection.setPadding(true);

                headingSection.setSpacing(true);

                configureGrid();

                configureHeaderFields();

                configureItemGrid();

                configureFilters();

                HorizontalLayout filterLayout = new HorizontalLayout(requestSearchField);

                if(!userRole.equals("ROLE_MANAGER") && !userRole.equals("ROLE_INVENTORY_ADMIN")){
                        filterLayout.add(statusFilter);
                        filterLayout.add(roleFilter);
                        filterLayout.add(currentLevelFilter);
                        filterLayout.add(clearFilterButton);
                }
                else{
                        filterLayout.add(clearFilterButton);
                }

                filterLayout.setWidthFull();

                FormLayout detailsLayout =
                        new FormLayout();

                detailsLayout.add(

                        requestNumber,

                        approvalRole,

                        approvalLevel,

                        approvalStatus
                );

                detailsLayout.setResponsiveSteps(

                        new FormLayout.ResponsiveStep(
                                "0",
                                1
                        ),

                        new FormLayout.ResponsiveStep(
                                "700px",
                                2
                        )
                );

                detailsLayout.getStyle()

                        .set("background", "white")

                        .set("padding", "20px")

                        .set("border-radius", "18px")

                        .set("box-shadow",
                                "0 4px 14px rgba(0,0,0,0.08)");

                requestRemarks.setWidthFull();

                requestRemarks.setHeight("100px");

                requestRemarks.setReadOnly(true);

                requestRemarks.getStyle()

                        .set("background", "#f8fafc")

                        .set("border-radius", "12px");

                comments.setWidthFull();

                comments.setHeight("120px");

                comments.setPlaceholder(
                        "Enter approval comments..."
                );

                comments.getStyle()

                        .set("background", "white")

                        .set("border-radius", "12px");

                
                currentLevelFilter.getStyle()
                        .set("padding", "5px");

                Button approveButton =
                        new Button(
                                "Approve"
                        );

                approveButton.addThemeVariants(
                        ButtonVariant.LUMO_SUCCESS
                );

                approveButton.setIcon(
                        VaadinIcon.CHECK.create()
                );

                approveButton.getStyle()

                        .set("border-radius", "12px")

                        .set("font-weight", "600")

                        .set("height", "42px");

                approveButton.addClickListener(
                        event -> approveRequest()
                );

                Button rejectButton =
                        new Button(
                                "Reject"
                        );

                rejectButton.addThemeVariants(
                        ButtonVariant.LUMO_ERROR
                );

                rejectButton.setIcon(
                        VaadinIcon.CLOSE.create()
                );

                rejectButton.getStyle()

                        .set("border-radius", "12px")

                        .set("font-weight", "600")

                        .set("height", "42px");

                rejectButton.addClickListener(
                        event -> rejectRequest()
                );

                Button clearButton =
                        new Button(
                                "Clear Selection"
                        );

                clearButton.addThemeVariants(
                        ButtonVariant.LUMO_CONTRAST
                );

                clearButton.setIcon(
                        VaadinIcon.ERASER.create()
                );

                clearButton.getStyle()

                        .set("border-radius", "12px")

                        .set("font-weight", "600")

                        .set("height", "42px");

                clearButton.addClickListener(event -> {

                        clearForm();
                });

                HorizontalLayout actions =
                        new HorizontalLayout(

                                approveButton,

                                rejectButton,

                                clearButton
                        );

                VerticalLayout rightPanel =
                        new VerticalLayout(

                                detailsLayout,

                                requestRemarks,

                                itemGrid,

                                comments,

                                actions
                        );

                rightPanel.setSpacing(true);

                rightPanel.setPadding(false);

                rightPanel.setWidthFull();

                VerticalLayout gridSection =
                        new VerticalLayout(grid);

                gridSection.setWidth("45%");

                gridSection.setPadding(false);

                VerticalLayout detailsSection =
                        new VerticalLayout(rightPanel);

                detailsSection.setWidth("55%");

                detailsSection.setPadding(false);

                HorizontalLayout mainLayout =
                        new HorizontalLayout(

                                gridSection,

                                detailsSection
                        );

                mainLayout.setWidthFull();

                mainLayout.setSpacing(true);

                add(
                        headingSection,
                        filterLayout,
                        mainLayout
                );

                refreshGrid();
        }

        private void configureGrid() {

                grid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES
                );

                grid.addColumn(
                        RequestApprovalDTO::getRequestNumber
                ).setHeader("Request Number");

                grid.addColumn(
                        RequestApprovalDTO::getApprovalOrder
                ).setHeader("Level");

                grid.addComponentColumn(approval -> {

                        Span roleBadge =
                                new Span(
                                        approval.getApprovalRole().name()
                                );

                        roleBadge.getStyle()

                                .set("padding", "6px 14px")

                                .set("border-radius", "999px")

                                .set("font-size", "12px")

                                .set("font-weight", "700")

                                .set("background", "#dbeafe")

                                .set("color", "#1d4ed8");

                        return roleBadge;

                }).setHeader("Role");

                grid.addComponentColumn(approval -> {

                        Span statusBadge =
                                new Span(
                                        approval.getApprovalStatus().name()
                                );

                        statusBadge.getStyle()

                                .set("padding", "6px 14px")

                                .set("border-radius", "999px")

                                .set("font-size", "12px")

                                .set("font-weight", "700")

                                .set("color", "white");

                        switch (approval.getApprovalStatus()) {

                        case PENDING ->

                                statusBadge.getStyle()
                                        .set("background", "#ea580c");

                        case APPROVED ->

                                statusBadge.getStyle()
                                        .set("background", "#16a34a");

                        case REJECTED ->

                                statusBadge.getStyle()
                                        .set("background", "#dc2626");
                        }

                        return statusBadge;

                }).setHeader("Status");

                grid.addComponentColumn(approval -> {

                        Span currentBadge =
                                new Span(

                                        Boolean.TRUE.equals(
                                                approval.getIsCurrentLevel()
                                        )

                                                ? "ACTIVE"

                                                : "-"
                                );

                        currentBadge.getStyle()

                                .set("font-weight", "700")

                                .set("color",

                                        Boolean.TRUE.equals(
                                                approval.getIsCurrentLevel()
                                        )

                                                ? "#16a34a"

                                                : "#94a3b8"
                                );

                        return currentBadge;

                }).setHeader("Current");

                grid.setWidthFull();

                grid.setHeight("760px");

                grid.getStyle()

                        .set("background", "white")

                        .set("border-radius", "18px")

                        .set("overflow", "hidden")

                        .set("box-shadow",
                                "0 6px 18px rgba(0,0,0,0.08)");

                grid.asSingleSelect()
                        .addValueChangeListener(event -> {

                                selectedApproval =
                                        event.getValue();

                                if(selectedApproval != null) {

                                loadRequestDetails(
                                        selectedApproval
                                );
                                }
                        });
        }

        private void configureHeaderFields() {

                requestNumber.setReadOnly(true);

                approvalRole.setReadOnly(true);

                approvalLevel.setReadOnly(true);

                approvalStatus.setReadOnly(true);

                requestRemarks.setReadOnly(true);

                requestRemarks.setWidthFull();
        }

        private void configureItemGrid() {

                itemGrid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES
                );

                itemGrid.addColumn(
                        RequestItemDTO::getItemName
                ).setHeader("Item Name");

                itemGrid.addColumn(
                        RequestItemDTO::getItemCode
                ).setHeader("Item Code");

                itemGrid.addColumn(
                        RequestItemDTO::getRequestedQuantity
                ).setHeader("Requested Qty");

                itemGrid.setWidthFull();

                itemGrid.setHeight("260px");

                itemGrid.getStyle()

                        .set("background", "white")

                        .set("border-radius", "16px")

                        .set("overflow", "hidden")

                        .set("box-shadow",
                                "0 4px 14px rgba(0,0,0,0.08)");
        }

        private void configureFilters() {

                requestSearchField.setPlaceholder(
                        "Search Request No"
                );

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
                        // if(!userRole.equals()){
                        //         true

                        // }
                        !userRole.equals("ROLE_SUPER_ADMIN")
                                ? true
                                : null
                );

                grid.setItems(
                        approvalProcessService.filterApprovals(filterDTO)
                );
        }


        private void clearFilters() {

                requestSearchField.clear();

                statusFilter.clear();

                roleFilter.clear();

                currentLevelFilter.clear();

                refreshGrid();
        }



        private void loadRequestDetails(
                RequestApprovalDTO approvalDTO) {

                requestNumber.setValue(
                        approvalDTO.getRequestNumber()
                );

                approvalRole.setValue(
                        approvalDTO.getApprovalRole()
                                .name()
                );

                approvalLevel.setValue(
                        String.valueOf(
                                approvalDTO.getApprovalOrder()
                        )
                );

                approvalStatus.setValue(
                        approvalDTO.getApprovalStatus()
                                .name()
                );

                InventoryRequestDTO request =
                        inventoryRequestService
                                .getRequestById(
                                        approvalDTO.getRequestId()
                                );

                requestRemarks.setValue(

                        request.getRemarks() != null

                                ? request.getRemarks()

                                : ""
                );

                itemGrid.setItems(
                        request.getRequestItems()
                );
        }

        private void approveRequest() {

                try {

                        if (selectedApproval == null) {

                                // showError(
                                //                 "Select request first");
                                NotificationUtil.warning("Select a request first");

                                return;
                        }

                        approvalProcessService
                                        .approveRequest(

                                                        selectedApproval
                                                                        .getApprovalId(),

                                                        comments.getValue());

                        // showSuccess(
                        //                 "Request approved successfully");
                        NotificationUtil.success("Request approved successfully");
                        
                        comments.clear();
                        
                        refreshGrid();

                        clearForm();
                        
                } catch (Exception e) {
                        
                        // showError(
                                //                 e.getMessage());
                        NotificationUtil.error(e.getMessage());
                }
        }

        private void rejectRequest() {

                try {

                        if (selectedApproval == null) {

                                // showError(
                                //                 "Select request first");
                                NotificationUtil.success("Select request first");

                                return;
                        }

                        approvalProcessService
                                        .rejectRequest(
                                                        selectedApproval
                                                                        .getApprovalId(),

                                                        comments.getValue());

                        // showSuccess(
                        //                 "Request rejected successfully");
                        NotificationUtil.success("Request rejected successfully");

                        comments.clear();

                        refreshGrid();

                        clearForm();

                } catch (Exception e) {

                        // showError(
                        //                 e.getMessage());
                        NotificationUtil.error(e.getMessage());
                }
        }

        private void refreshGrid() {

                grid.setItems(

                                approvalProcessService
                                                .getPendingApprovals(
                                                                username));
        }

        private void clearForm() {

                selectedApproval = null;

                requestNumber.clear();

                approvalRole.clear();

                approvalLevel.clear();

                approvalStatus.clear();

                requestRemarks.clear();

                comments.clear();

                itemGrid.setItems();

                grid.deselectAll();

                NotificationUtil.success(
                        "Selection cleared"
                );
        }
}
