package com.example.views;

import java.util.ArrayList;
import java.util.List;

import com.example.base.ui.MainLayout;
import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryRequestDTO;
import com.example.dto.RequestItemDTO;
import com.example.entity.Employee;
import com.example.entity.User;
import com.example.enums.RequestStatus;
import com.example.repository.UserRepository;
import com.example.security.SecurityService;
import com.example.service.ApprovalProgressService;
import com.example.service.InventoryRequestService;
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

    private boolean editMode = false;

    private final Grid<RequestItemDTO> itemGrid = new Grid<>(RequestItemDTO.class, false);

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
        ApprovalProgressService approvalProgressService) {

        this.inventoryRequestService = inventoryRequestService;

        this.userRepository = userRepository;

        this.securityService = securityService;

        this.approvalProgressService = approvalProgressService;

        setWidthFull();

        setPadding(true);

        setSpacing(true);

        getStyle()

                .set("background", "#f4f7fb")

                .set("padding", "24px")

                .set("overflow", "auto");

        loadLoggedInEmployee();

        H2 heading =
                new H2("Inventory Request Management");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "34px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span subtitle = new Span("Manage inventory requests, approvals and stock workflows");

        subtitle.getStyle()

                .set("font-size", "15px")

                .set("color", "#64748b");

        VerticalLayout headingSection =
                new VerticalLayout(
                        heading,
                        subtitle
                );

        headingSection.setPadding(true);

        headingSection.setSpacing(true);

        openDialogButton.setText("New Request");

        openDialogButton.setIcon(VaadinIcon.PLUS.create());

        openDialogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        openDialogButton.getStyle()

                .set("margin-left", "auto")

                .set("background",
                        "linear-gradient(135deg,#2563eb,#1d4ed8)")

                .set("border-radius", "12px")

                .set("font-weight", "600")

                .set("height", "42px")

                .set("padding", "0 18px")

                .set("box-shadow",
                        "0 4px 14px rgba(37,99,235,0.35)");

        openDialogButton.addClickListener(event -> {

                clearForm();
                requestDialog.open();
        });

        HorizontalLayout headerLayout =
                new HorizontalLayout(
                        headingSection,
                        openDialogButton
                );

        headerLayout.setWidthFull();

        headerLayout.expand(headingSection);

        headerLayout.setAlignItems(Alignment.CENTER);

        configureHeaderFields();

        configureItemSection();

        configureItemGrid();

        configureRequestGrid();

        HorizontalLayout requestLayout =
                new HorizontalLayout(
                        requestNumber,
                        requestStatus
                );

        requestLayout.setWidthFull();

        requestLayout.expand(requestNumber);

        HorizontalLayout itemLayout =
                new HorizontalLayout(
                        itemComboBox,
                        quantityField
                );

        itemLayout.setWidthFull();

        itemLayout.expand(itemComboBox);

        configureFilters();

        HorizontalLayout filterLayout =
                new HorizontalLayout(
                        requestSearchField
                );

        if(!securityService.getAuthenticatedRole().equals("ROLE_EMPLOYEE")) {
                filterLayout.add(
                        employeeSearchField,
                        statusFilter,
                        fromDateFilter,
                        toDateFilter,
                        clearFilterButton
                );

        } else {
                filterLayout.add(
                        statusFilter,
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

        dialogLayout.setHeightFull();

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

        add(
                headerLayout,
                filterLayout,
                requestGrid
        );

        refreshRequestGrid();
     }

        private void configureHeaderFields() {

                requestNumber.setReadOnly(true);

                requestStatus.setReadOnly(true);

                requestStatus.setValue(
                        RequestStatus.DRAFT.name()
                );

                requestNumber.setPlaceholder(
                        "Generated Automatically"
                );

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
                item -> item.getItemName()
                        + " - "
                        + item.getItemCode()
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

                requestSearchField.setPlaceholder(
                        "Search Request No"
                );

                requestSearchField.setPrefixComponent(
                        VaadinIcon.SEARCH.create()
                );

                requestSearchField.setWidth("260px");

                employeeSearchField.setPlaceholder(
                        "Search Employee"
                );

                employeeSearchField.setPrefixComponent(
                        VaadinIcon.USER.create()
                );

                employeeSearchField.setWidth("220px");

                statusFilter.setPlaceholder(
                        "Status"
                );

                statusFilter.setItems(
                        RequestStatus.values()
                );

                fromDateFilter.setPlaceholder(
                        "From Date"
                );

                toDateFilter.setPlaceholder(
                        "To Date"
                );

                clearFilterButton.addThemeVariants(
                        ButtonVariant.LUMO_ERROR
                );

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

                        Span statusSpan =
                                new Span(
                                        request.getRequestStatus().name()
                                );

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

                requestGrid.addColumn(
                        InventoryRequestDTO::getRequestDate
                )
                .setHeader("Request Date");

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

                if(quantityField.getValue() == null
                        || quantityField.getValue() <= 0) {

                        NotificationUtil.error("Enter valid quantity");

                        return;
                }

                Long itemId = itemComboBox.getValue().getItemId();

                RequestItemDTO existingItem = requestItems
                        .stream()
                        .filter(item ->
                            item.getItemId()
                            .equals(itemId)
                        )
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
                                .filter(i ->
                                        i.getItemId().equals(item.getItemId())
                                )
                                .findFirst()
                                .orElse(null);

                        if(existing != null) {

                                existing.setRequestedQuantity(
                                        existing.getRequestedQuantity() + item.getRequestedQuantity()
                                );

                        } else {

                                RequestItemDTO newItem =
                                        new RequestItemDTO();

                                newItem.setRequestItemId(
                                        item.getRequestItemId()
                                );

                                newItem.setItemId(
                                        item.getItemId()
                                );

                                newItem.setItemName(
                                        item.getItemName()
                                );

                                newItem.setItemCode(
                                        item.getItemCode()
                                );

                                newItem.setRequestedQuantity(
                                        item.getRequestedQuantity()
                                );

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
                throw new RuntimeException(
                        "Logged in user is not mapped to employee"
                );
        }

        loggedInEmployee = user.getEmployee();
    }

    private void refreshRequestGrid() {

        requestGrid.setItems(
                inventoryRequestService
                        .getAllRequests()
        );
    }

        private void clearForm() {

                currentRequest =
                        new InventoryRequestDTO();

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

                NotificationUtil.success(
                        "Request Cancelled"
                );
        }
}