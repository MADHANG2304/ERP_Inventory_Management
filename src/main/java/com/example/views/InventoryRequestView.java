package com.example.views;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.base.ui.MainLayout;
import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryRequestDTO;
import com.example.dto.RequestItemDTO;
import com.example.dto.ReturnedItemDTO;
import com.example.entity.Employee;
import com.example.entity.User;
import com.example.enums.IssueStatus;
import com.example.enums.RequestStatus;
import com.example.repository.UserRepository;
import com.example.security.SecurityService;
import com.example.service.ApprovalProgressService;
import com.example.service.InventoryRequestService;
import com.example.service.IssueService;
import com.example.service.ReturnService;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Span;

import com.example.dto.InventoryRequestFilterDTO;
import com.example.dto.IssuedItemDTO;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;

import jakarta.annotation.security.PermitAll;

@Route(value = "inventory-requests", layout = MainLayout.class)
@PageTitle("Inventory Requests")
@PermitAll
public class InventoryRequestView extends VerticalLayout {

    private final InventoryRequestService inventoryRequestService;

    private final UserRepository userRepository;

    private final SecurityService securityService;

    private final ApprovalProgressService approvalProgressService;

    private final IssueService issuedService;
    
    private final ReturnService returnService;

    private boolean editMode = false;

    private final Grid<RequestItemDTO> itemGrid = new Grid<>(RequestItemDTO.class, false);

    private final Grid<IssuedItemDTO> issuedGrid = new Grid<>(IssuedItemDTO.class, false);

        private final Grid<ReturnedItemDTO> returnedGrid = new Grid<>(ReturnedItemDTO.class, false);
        
        private final VerticalLayout contentLayout = new VerticalLayout();

        private Button requestsTab;

        private Button approvedTab;

        private Button issuedTab;

        private Button returnedTab;

        private Button requestedTab;

    private final Grid<InventoryRequestDTO> requestGrid = new Grid<>(InventoryRequestDTO.class, false);

    private final ComboBox<InventoryItemDTO> itemComboBox = new ComboBox<>("Inventory Item");

    private final IntegerField quantityField = new IntegerField("Quantity");

    private final TextArea remarks = new TextArea("Remarks");

    private final TextField requestNumber = new TextField("Request Number");

    private final TextField requestStatus = new TextField("Request Status");

    private final List<RequestItemDTO> requestItems = new ArrayList<>();

    private InventoryRequestDTO currentRequest = new InventoryRequestDTO();

    private InventoryRequestDTO selectedRequest = new InventoryRequestDTO();

        private final TextField requestSearchField = new TextField();

        private final TextField employeeSearchField = new TextField();

        private final ComboBox<RequestStatus> statusFilter = new ComboBox<>();

        private final DatePicker fromDateFilter = new DatePicker();

        private final DatePicker toDateFilter = new DatePicker();

        private final Button clearFilterButton = new Button("Clear");

        private final Dialog requestDialog = new Dialog();

        private final Button openDialogButton = new Button(VaadinIcon.PLUS.create());

    private Employee loggedInEmployee;

     Button saveDraftButton = new Button("Save Draft");

     private Boolean isEdit = false;

    public InventoryRequestView(
        InventoryRequestService inventoryRequestService,
        UserRepository userRepository,
        SecurityService securityService,
        ApprovalProgressService approvalProgressService,
        IssueService issuedService,
        ReturnService returnService) {

        this.inventoryRequestService = inventoryRequestService;

        this.userRepository = userRepository;

        this.securityService = securityService;

        this.approvalProgressService = approvalProgressService;

        this.issuedService = issuedService;

        this.returnService = returnService;

        setWidthFull();

        setPadding(true);

        setSpacing(true);

        getStyle()

                .set("background", "#f4f7fb")

                .set("padding", "24px")

                .set("overflow", "auto");

        loadLoggedInEmployee();

        H2 heading = new H2("Inventory Request Management");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "34px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span subtitle = new Span("Manage inventory requests, approvals and stock workflows");

        subtitle.getStyle()

                .set("font-size", "15px")

                .set("color", "#64748b");

        VerticalLayout headingSection = new VerticalLayout(heading, subtitle);

        headingSection.setPadding(true);

        headingSection.setSpacing(true);

        openDialogButton.setText("New Request");

        openDialogButton.setIcon(VaadinIcon.PLUS.create());

        openDialogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        openDialogButton.getStyle()

                .set("margin-left", "auto")

                .set("background", "linear-gradient(135deg,#2563eb,#1d4ed8)")

                .set("border-radius", "12px")

                .set("font-weight", "600")

                .set("height", "42px")

                .set("padding", "0 18px")

                .set("box-shadow", "0 4px 14px rgba(37,99,235,0.35)");

        openDialogButton.addClickListener(event -> {

                clearForm();
                currentRequest.setRequestNumber(generateRequestNumber());

                requestNumber.setValue(currentRequest.getRequestNumber());

                requestDialog.open();
        });

        HorizontalLayout headerLayout = new HorizontalLayout(headingSection);

        if(securityService.getAuthenticatedRole().equals("ROLE_EMPLOYEE")){
                headerLayout.add(openDialogButton);
        }

        headerLayout.setWidthFull();

        headerLayout.expand(headingSection);

        headerLayout.setAlignItems(Alignment.CENTER);

        configureHeaderFields();

        configureItemSection();

        configureItemGrid();

        configureRequestGrid();

        HorizontalLayout requestLayout = new HorizontalLayout(requestNumber, requestStatus);

        requestLayout.setWidthFull();

        requestLayout.expand(requestNumber);

        HorizontalLayout itemLayout = new HorizontalLayout(itemComboBox, quantityField);

        itemLayout.setWidthFull();

        itemLayout.expand(itemComboBox);

        configureFilters();

        HorizontalLayout filterLayout = new HorizontalLayout(requestSearchField);

        if(!securityService.getAuthenticatedRole().equals("ROLE_EMPLOYEE")) {
                filterLayout.add(
                        employeeSearchField,
                        // statusFilter,
                        fromDateFilter,
                        toDateFilter,
                        clearFilterButton
                );

        } else {
                filterLayout.add(
                        // statusFilter,
                        fromDateFilter,
                        toDateFilter,
                        clearFilterButton
                );
        }

        filterLayout.setWidthFull();

        filterLayout.getStyle()

                .set("background", "white")

                .set("padding", "18px")

                .set("border-radius", "18px")

                .set("box-shadow",
                        "0 4px 18px rgba(0,0,0,0.06)");

        Button addItemButton = new Button("Add Item");

        addItemButton.setIcon(VaadinIcon.PLUS.create());

        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addItemButton.getStyle()

                .set("border-radius", "10px")

                .set("font-weight", "600");

        addItemButton.addClickListener(event -> addItem());

        Button cancelButton = new Button("Cancel");

        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        cancelButton.getStyle().set("border-radius", "10px");

        cancelButton.addClickListener(event -> cancelEditing());

        Button submitButton = new Button("Submit Request");

        submitButton.setIcon(VaadinIcon.CHECK.create());

        saveDraftButton.addClickListener(event -> saveDraft());

        saveDraftButton.getStyle().set("border-radius", "10px");

        submitButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        submitButton.getStyle()

                .set("border-radius", "10px")

                .set("font-weight", "600");

        submitButton.addClickListener(event -> submitRequest());

        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        saveDraftButton,
                        submitButton,
                        cancelButton
                );

        VerticalLayout dialogLayout =
                new VerticalLayout(

                        requestLayout,

                        remarks,

                        itemLayout, 

                        addItemButton,

                        itemGrid,

                        buttonLayout
                );
                

        dialogLayout.setWidth("950px");

        dialogLayout.setHeight("500px");

        dialogLayout.setPadding(true);

        dialogLayout.setSpacing(true);

        dialogLayout.getStyle()

                .set("background", "white")

                .set("border-radius", "18px");

        requestDialog.add(dialogLayout);

        requestDialog.setHeaderTitle("Inventory Request");

        requestDialog.setModal(true);

        requestDialog.setDraggable(true);

        requestDialog.setResizable(true);

        requestDialog.setWidth("1050px");

        requestDialog.setHeight("720px");

        createTabs();

        contentLayout.setWidthFull();

        contentLayout.setPadding(false);

        contentLayout.setSpacing(false);

        showRequests();

        if(!securityService.getAuthenticatedRole().equals("ROLE_EMPLOYEE")){
                add(
                        headerLayout,
                        createTabsLayout(),
                        filterLayout,
                        contentLayout
                );
        }
        else{
                add(
                        headerLayout,
                        createTabsLayout(),
                        filterLayout,
                        contentLayout
                );
        }


        refreshRequestGrid();
     }

        private void configureHeaderFields() {

                requestNumber.setReadOnly(true);

                requestStatus.setReadOnly(true);

                requestStatus.setValue(RequestStatus.DRAFT.name());

                requestNumber.setPlaceholder("Generated Automatically");

                requestNumber.setWidthFull();

                requestStatus.setWidth("220px");

                remarks.setWidthFull();

                remarks.setHeight("90px");

                remarks.setPlaceholder("Enter request remarks...");

                requestNumber.getStyle().set("border-radius", "10px");

                requestStatus.getStyle().set("border-radius", "10px");

                remarks.getStyle().set("border-radius", "10px");
        }

    private void configureItemSection() {

        itemComboBox.setItems(inventoryRequestService.getAvailableItems());

        itemComboBox.setItemLabelGenerator(
                item -> item.getItemName() + " - " + item.getItemCode()
        );

        quantityField.setMin(1);

        quantityField.setValue(1);
    }

    private void configureItemGrid() {

                itemGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

                itemGrid.addColumn(
                        RequestItemDTO::getItemName
                )
                .setHeader("Item Name")
                .setAutoWidth(true);

                itemGrid.addColumn(
                        RequestItemDTO::getItemCode
                )
                .setHeader("Item Code");

                itemGrid.addColumn(
                        RequestItemDTO::getRequestedQuantity
                )
                .setHeader("Quantity");

                itemGrid.addComponentColumn(item -> {

                        Button removeButton = new Button(VaadinIcon.TRASH.create());

                        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                        removeButton.getStyle()

                                .set("border-radius", "10px")

                                .set("cursor", "pointer");

                        removeButton.addClickListener(event -> {

                                requestItems.remove(item);

                                itemGrid.setItems(requestItems);
                        });

                        return removeButton;

                }).setHeader("Action");

                itemGrid.setMinHeight("150px");

                itemGrid.setWidthFull();

                itemGrid.getStyle()

                        .set("border-radius", "16px")

                        .set("overflow", "hidden")

                        .set("box-shadow",
                                "0 4px 16px rgba(0,0,0,0.08)");
        }

    private void configureFilters() {

                requestSearchField.setPlaceholder("Search Request No");

                requestSearchField.setPrefixComponent(VaadinIcon.SEARCH.create());

                requestSearchField.setWidth("260px");

                employeeSearchField.setPlaceholder("Search Employee");

                employeeSearchField.setPrefixComponent(VaadinIcon.USER.create());

                employeeSearchField.setWidth("220px");

                statusFilter.setPlaceholder("Status");

                statusFilter.setItems(RequestStatus.values());

                fromDateFilter.setPlaceholder("From Date");

                toDateFilter.setPlaceholder("To Date");

                clearFilterButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                clearFilterButton.getStyle().set("border-radius", "10px");

                requestSearchField.addValueChangeListener(event -> applyFilters());

                employeeSearchField.addValueChangeListener(event -> applyFilters());

                statusFilter.addValueChangeListener(event -> applyFilters());

                fromDateFilter.addValueChangeListener(event -> applyFilters());

                toDateFilter.addValueChangeListener(event -> applyFilters());

                clearFilterButton.addClickListener(event -> clearFilters());
        }

        private void applyFilters() {

                InventoryRequestFilterDTO filterDTO = new InventoryRequestFilterDTO();

                filterDTO.setRequestNumber(requestSearchField.getValue());

                filterDTO.setEmployeeName(employeeSearchField.getValue());

                filterDTO.setRequestStatus(statusFilter.getValue());

                filterDTO.setFromDate(fromDateFilter.getValue());

                filterDTO.setToDate(toDateFilter.getValue());

                requestGrid.setItems(inventoryRequestService.filterRequests(filterDTO));

        }

        private void clearFilters() {

                requestSearchField.clear();

                employeeSearchField.clear();

                statusFilter.clear();

                fromDateFilter.clear();

                toDateFilter.clear();

                refreshRequestGrid();
        }

        private void configureIssuedGrid() {

                issuedGrid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES
                );

                issuedGrid.addColumn(
                        IssuedItemDTO::getIssueReferenceNumber
                ).setHeader("Issue Reference");

                issuedGrid.addColumn(
                        IssuedItemDTO::getItemName
                ).setHeader("Item");

                issuedGrid.addColumn(
                        IssuedItemDTO::getItemCode
                ).setHeader("Item Code");

                issuedGrid.addColumn(
                        IssuedItemDTO::getIssuedQuantity
                ).setHeader("Quantity");

                issuedGrid.addColumn(
                        item -> "ISSUED"
                ).setHeader("Status");

                issuedGrid.setWidthFull();

                issuedGrid.setHeight("550px");
        }

        private void configureReturnedGrid() {

                returnedGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

                // returnedGrid.addColumn(
                //         ReturnedItemDTO::getReturnReferenceNumber
                // ).setHeader("Return Reference");

                returnedGrid.addColumn(
                        ReturnedItemDTO::getIssueReferenceNumber
                ).setHeader("Issue Reference");

                returnedGrid.addColumn(
                        ReturnedItemDTO::getItemName
                ).setHeader("Item");

                returnedGrid.addColumn(
                        ReturnedItemDTO::getItemCode
                ).setHeader("Item Code");

                returnedGrid.addColumn(
                        ReturnedItemDTO::getReturnQuantity
                ).setHeader("Returned Qty");

                returnedGrid.addComponentColumn(item -> {

                        Span status =
                                new Span(item.getIssueStatus().name());

                        String color = "#475569";

                        if(item.getIssueStatus() == IssueStatus.CLOSED) {
                                color = "#475569";
                        }

                        status.getStyle()

                                .set("background", color)

                                .set("color", "white")

                                .set("padding", "6px 12px")

                                .set("border-radius", "14px")

                                .set("font-size", "12px")

                                .set("font-weight", "700");

                        return status;

                }).setHeader("Status");

                        returnedGrid.addComponentColumn(item -> {

                        HorizontalLayout actions = new HorizontalLayout();

                        if(securityService.getAuthenticatedRole().equals("ROLE_SUPER_ADMIN")

                                ||

                                securityService.getAuthenticatedRole().equals("ROLE_INVENTORY_ADMIN")
                        ) {


                                if(item.getIssueStatus() != IssueStatus.CLOSED) {

                                        Button closeButton =
                                                new Button(
                                                        "Close",
                                                        VaadinIcon.LOCK.create()
                                                );

                                        closeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

                                        closeButton.addClickListener(event -> {

                                                returnService.closeReturn(item.getIssuedItemId());

                                                NotificationUtil.success("Return closed successfully");

                                                showReturnedItems();
                                        });

                                        actions.add(closeButton);
                                }
                        }
                        else{
                                if(item.getIssueStatus() != IssueStatus.CLOSED){

                                        Button cancelButton =
                                                        new Button(
                                                                "Cancel Return",
                                                                VaadinIcon.ROTATE_LEFT.create()
                                                        );

                                                cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                                                cancelButton.addClickListener(event -> {

                                                        returnService.cancelReturn(item.getIssuedItemId());

                                                        NotificationUtil.success("Return cancelled successfully");

                                                        showReturnedItems();
                                                });
                                        actions.add(cancelButton);
                                }
                        }
                        return actions;

                }).setHeader("Actions");

                returnedGrid.setWidthFull();

                returnedGrid.setHeight("550px");
        }

        private HorizontalLayout createTabsLayout() {

                HorizontalLayout tabsLayout = new HorizontalLayout();

                // EMPLOYEE TABS

                if(securityService.getAuthenticatedRole().equals("ROLE_EMPLOYEE")) {

                        requestsTab = createTabButton(
                                "All Requests",
                                inventoryRequestService
                                        .getAllRequests()
                                        .size(),
                                "#838585"
                        );

                        requestedTab = createTabButton(
                                "Requested",
                                (int) inventoryRequestService
                                        .getAllRequests()
                                        .stream()
                                        .filter(request ->
                                                request.getRequestStatus() == RequestStatus.PENDING_APPROVAL).count(),
                                "#838585"
                        );

                        approvedTab = createTabButton(
                                "Approved",
                                (int) inventoryRequestService
                                        .getAllRequests()
                                        .stream()
                                        .filter(request ->
                                                request.getRequestStatus() == RequestStatus.APPROVED).count(),
                                "#838585"
                        );

                        issuedTab = createTabButton(
                                "Issued",
                                (int) issuedService.getIssuedHistory().size(),
                                "#838585"
                        );

                        returnedTab = createTabButton(
                                "Returned",
                                (int) returnService.getReturnedHistory().size(),
                                "#838585"
                        );

                        setActiveTab(requestsTab);

                        requestsTab.addClickListener(event -> {

                                setActiveTab(requestsTab);

                                showRequests();
                        });

                        requestedTab.addClickListener(event -> {

                                setActiveTab(requestedTab);

                                showRequestedRequests();
                        });

                        approvedTab.addClickListener(event -> {

                                setActiveTab(approvedTab);

                                showApprovedRequests();
                        });

                        issuedTab.addClickListener(event -> {

                                setActiveTab(issuedTab);

                                showIssuedItems();
                        });

                        returnedTab.addClickListener(event -> {

                                setActiveTab(returnedTab);

                                showReturnedItems();
                        });

                        tabsLayout.add(
                                requestsTab,
                                requestedTab,
                                approvedTab,
                                issuedTab,
                                returnedTab
                        );
                }

                // ADMIN TABS

                else {
                        requestsTab = createTabButton(
                                "All Requests",
                                inventoryRequestService.getAllRequests().size(),
                                "#838585"
                        );


                        requestsTab.addClickListener(event -> {

                                setActiveTab(requestsTab);

                                showRequests();
                        });

                        returnedTab = createTabButton(
                                "Returned",
                                (int) returnService.getReturnedHistory().size(),
                                "#838585"
                        );

                        
                        returnedTab.addClickListener(event -> {

                                setActiveTab(returnedTab);

                                showReturnedItems();
                        });

                        setActiveTab(requestsTab);

                        tabsLayout.add(requestsTab, returnedTab);
                }

                tabsLayout.setSpacing(true);

                tabsLayout.getStyle().set("margin-bottom", "8px");

                return tabsLayout;
        }

        private void setActiveTab(Button activeTab) {

                List<Button> tabs = new ArrayList<>();

                if(requestsTab != null) {
                        tabs.add(requestsTab);
                }

                if(requestedTab != null) {
                        tabs.add(requestedTab);
                }

                if(approvedTab != null) {
                        tabs.add(approvedTab);
                }

                if(issuedTab != null) {
                        tabs.add(issuedTab);
                }

                if(returnedTab != null) {
                        tabs.add(returnedTab);
                }

                for(Button tab : tabs) {

                        tab.getStyle()

                                .set("background", "white")

                                .set("color", "#64748b")

                                .set("border", "1px solid #d1d5db");
                }

                activeTab.getStyle()

                        .set("background", "#eff6ff")

                        .set("color", "#2563eb")

                        .set("border", "1px solid #2563eb");
        }


        private Button createTabButton(String label, int count, String color) {

                        Span labelSpan = new Span(label);

                        labelSpan.getStyle()
                                
                                .set("font-weight", "600");

                        Span countSpan = new Span(String.valueOf(count));

                        countSpan.getStyle()

                                .set("background", color)

                                .set("color", "white")

                                .set("padding", "2px 8px")

                                .set("border-radius", "10px")

                                .set("font-size", "12px")

                                .set("font-weight", "700");

                        HorizontalLayout content =
                                new HorizontalLayout(
                                        labelSpan,
                                        countSpan
                                );

                        content.setSpacing(true);

                        content.setAlignItems(Alignment.CENTER);

                        Button button = new Button(content);

                        button.getStyle()

                                .set("background", "white")

                                .set("border", "1px solid #dbe4f0")

                                .set("border-radius", "12px")

                                .set("padding", "10px 16px")

                                .set("cursor", "pointer")

                                .set("box-shadow",    "0 2px 8px rgba(0,0,0,0.04)");

                        return button;
        }

        private void createTabs() {

                configureIssuedGrid();

                configureReturnedGrid();
        }

        private void showRequests() {

                contentLayout.removeAll();

                requestGrid.setItems(
                        inventoryRequestService.getAllRequests()
                );

                contentLayout.add(requestGrid);
        
        }

        private void showApprovedRequests() {

                contentLayout.removeAll();

                requestGrid.setItems(

                        inventoryRequestService
                                .getAllRequests()
                                .stream()

                                .filter(request ->
                                        request.getRequestStatus().name().equals("APPROVED"))
                                .toList()
                );

                contentLayout.add(requestGrid);
        }

        private void showRequestedRequests() {

                contentLayout.removeAll();

                List<InventoryRequestDTO> requestedRequests =
                        inventoryRequestService
                                .getAllRequests()
                                .stream()

                                .filter(request ->
                                        request.getRequestStatus() == RequestStatus.PENDING_APPROVAL
                                )

                                .toList();

                requestGrid.setItems(requestedRequests);

                contentLayout.add(requestGrid);
        }

        private void showIssuedItems() {

                contentLayout.removeAll();

                issuedGrid.setItems(
                        issuedService.getIssuedHistory()
                );

                contentLayout.add(issuedGrid);
        }

        private void showReturnedItems() {

                contentLayout.removeAll();

                returnedGrid.setItems(
                        returnService.getReturnedHistory()
                );

                contentLayout.add(returnedGrid);
        }


        private void loadRequestToForm(InventoryRequestDTO dto) {

                if(dto.getRequestStatus() != RequestStatus.DRAFT) {

                        NotificationUtil.error("Only DRAFT requests can be edited");

                        isEdit = false;

                        clearForm();

                        return;
                }

                currentRequest = dto;

                requestNumber.setValue(dto.getRequestNumber());

                requestStatus.setValue(dto.getRequestStatus().name());

                remarks.setValue(dto.getRemarks() != null? dto.getRemarks() : "");

                requestItems.clear();

                requestItems.addAll(dto.getRequestItems());

                itemGrid.setItems(requestItems);
                
                itemGrid.getDataProvider().refreshAll();

                editMode = true;

                NotificationUtil.success("Draft loaded successfully");

                requestDialog.open();
        }

        private void configureRequestGrid() {

                requestGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

                requestGrid.addColumn(
                        InventoryRequestDTO::getRequestNumber
                )
                .setHeader("Request No")
                .setAutoWidth(true);

                requestGrid.addColumn(
                        InventoryRequestDTO::getEmployeeName
                )
                .setHeader("Employee");

                requestGrid.addComponentColumn(request -> {

                        Span statusSpan = new Span(request.getRequestStatus().name());

                        String backgroundColor = "#2563eb";

                        switch (request.getRequestStatus()) {

                        case DRAFT ->
                                backgroundColor = "#f59e0b";

                        case PENDING_APPROVAL ->
                                backgroundColor = "#2563eb";

                        case APPROVED ->
                                backgroundColor = "#16a34a";

                        case REJECTED ->
                                backgroundColor = "#dc2626";

                        case ISSUED ->
                                backgroundColor = "#7c3aed";
                        }

                        statusSpan.getStyle()

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-size", "12px")

                                .set("font-weight", "700")

                                .set("color", "white")

                                .set("background", backgroundColor)

                                .set("cursor", "pointer");

                        statusSpan.addClickListener(event -> {

                                ApprovalProgressDialog dialog = new ApprovalProgressDialog(request.getRequestId(), approvalProgressService);

                                dialog.open();
                        });

                        return statusSpan;

                }).setHeader("Status");

                requestGrid.addColumn(request -> {

                        if(request.getRequestDate() == null) {
                                return "-";
                        }

                        return request.getRequestDate()
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                }).setHeader("Request Date");

                requestGrid.setHeight("620px");

                requestGrid.setWidthFull();

                requestGrid.getStyle()

                        .set("background", "white")

                        .set("border-radius", "18px")

                        .set("overflow", "hidden")

                        .set("box-shadow",
                                "0 6px 18px rgba(0,0,0,0.08)");

                requestGrid.asSingleSelect()
                        .addValueChangeListener(event -> {

                                if(event.getValue() != null) {

                                        selectedRequest = event.getValue();

                                        saveDraftButton.setText("Update Draft");

                                        isEdit = true;

                                        loadRequestToForm(selectedRequest);
                                }
                        });
        }

    private void addItem() {

                if(itemComboBox.getValue() == null) {

                        NotificationUtil.error("Select inventory item");

                        return;
                }

                if(quantityField.getValue() == null || quantityField.getValue() <= 0) {

                        NotificationUtil.error("Enter valid quantity");

                        return;
                }

                Long itemId = itemComboBox.getValue().getItemId();

                RequestItemDTO existingItem = requestItems
                        .stream()
                        .filter(item -> item.getItemId().equals(itemId))
                        .findFirst()
                        .orElse(null);

                if(existingItem != null) {

                        existingItem.setRequestedQuantity(
                                existingItem.getRequestedQuantity() + quantityField.getValue()
                        );

                } else {

                        RequestItemDTO dto = new RequestItemDTO();

                        dto.setItemId(itemId);

                        dto.setItemName(itemComboBox.getValue().getItemName());

                        dto.setItemCode(itemComboBox.getValue().getItemCode());

                        dto.setRequestedQuantity(quantityField.getValue());

                        requestItems.add(dto);
                }

                // itemGrid.setItems(requestItems);
                mergeRequestItems();

                itemComboBox.clear();

                quantityField.setValue(1);
        }

        private void mergeRequestItems() {

                List<RequestItemDTO> mergedItems = new ArrayList<>();

                for(RequestItemDTO item : requestItems) {

                        RequestItemDTO existing = mergedItems
                                .stream()
                                .filter(i -> i.getItemId().equals(item.getItemId()))
                                .findFirst()
                                .orElse(null);

                        if(existing != null) {

                                existing.setRequestedQuantity(
                                        existing.getRequestedQuantity() + item.getRequestedQuantity()
                                );

                        } else {

                                RequestItemDTO newItem = new RequestItemDTO();

                                newItem.setRequestItemId(item.getRequestItemId());

                                newItem.setItemId(item.getItemId());

                                newItem.setItemName(item.getItemName());

                                newItem.setItemCode(item.getItemCode());

                                newItem.setRequestedQuantity(item.getRequestedQuantity());

                                mergedItems.add(newItem);
                        }
                }

                requestItems.clear();

                requestItems.addAll(mergedItems);

                itemGrid.setItems(requestItems);

                itemGrid.getDataProvider().refreshAll();
        }

    private void saveDraft() {

        try {

            prepareRequest();

            InventoryRequestDTO savedRequest = inventoryRequestService.saveDraft(currentRequest);

            NotificationUtil.success("Draft saved successfully");

            requestNumber.setValue(savedRequest.getRequestNumber());

            requestStatus.setValue(savedRequest.getRequestStatus().name());
            
            clearForm();
            
            refreshRequestGrid();
            
            requestDialog.close();

        } catch (Exception e) {
            NotificationUtil.error(e.getMessage());
        }
    }

    private void submitRequest() {

        try {

            prepareRequest();

            InventoryRequestDTO savedRequest = inventoryRequestService.submitRequest(currentRequest);

            NotificationUtil.success("Request Submitted Successfully");

            requestNumber.setValue(savedRequest.getRequestNumber());

            requestStatus.setValue(savedRequest.getRequestStatus().name());

            clearForm();

            refreshRequestGrid();

            requestDialog.close();

        } catch (Exception e) {
            NotificationUtil.error(e.getMessage());
        }
    }

        private void prepareRequest() {

                if(currentRequest == null) {
                        currentRequest = new InventoryRequestDTO();
                }

                currentRequest.setEmployeeId(loggedInEmployee.getEmployeeId());

                currentRequest.setRequestNumber(requestNumber.getValue());

                currentRequest.setRemarks(remarks.getValue());

                currentRequest.setRequestItems(new ArrayList<>(requestItems));
        }

    private void loadLoggedInEmployee() {

        String username = securityService.getAuthenticatedUser();
        
        User user =
                userRepository.findAll()
                        .stream()
                        .filter(u -> u.getUsername().equals(username))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("User not found")
                        );

        if(user.getEmployee() == null){
                throw new RuntimeException("Logged in user is not mapped to employee");
        }

        loggedInEmployee = user.getEmployee();
    }

    private String generateRequestNumber() {

                return "REQ-" + UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();
        }

    private void refreshRequestGrid() {

        requestGrid.setItems(
                inventoryRequestService.getAllRequests()
        );
    }

        private void clearForm() {

                currentRequest = new InventoryRequestDTO();

                saveDraftButton.setText("Save Draft");

                requestItems.clear();

                itemGrid.setItems(requestItems);

                itemComboBox.clear();

                quantityField.setValue(1);

                remarks.clear();

                requestNumber.clear();

                requestStatus.setValue(
                        RequestStatus.DRAFT.name()
                );

                 editMode = false;
        }

        private void cancelEditing() {
                requestGrid.deselectAll();

                clearForm();

                requestDialog.close();

                NotificationUtil.success("Request Cancelled");
        }
}