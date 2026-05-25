package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.InventoryCategoryDTO;
import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryItemFilterDTO;
import com.example.enums.ApprovalType;
import com.example.enums.ItemStatus;
import com.example.enums.UnitType;
import com.example.service.InventoryItemService;
import com.example.utils.ConfirmDialogUtil;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value="inventory-items", layout=MainLayout.class)
@PageTitle("Inventory Items")
@RolesAllowed({
    "SUPER_ADMIN",
    "INVENTORY_ADMIN"
})
public class InventoryItemView extends VerticalLayout{
    
    private final InventoryItemService service;

    private final Grid<InventoryItemDTO> grid = new Grid<>(InventoryItemDTO.class , false);

    private final BeanValidationBinder<InventoryItemDTO> binder = new BeanValidationBinder<>(InventoryItemDTO.class);

    private InventoryItemDTO currentItem = new InventoryItemDTO();

    private final ComboBox<InventoryCategoryDTO> categoryId = new ComboBox<>("Category");

    private final TextField itemName = new TextField("Item Name");

    private final TextField itemCode = new TextField("Item Code");

    private final TextArea description = new TextArea("Description");

    private final Checkbox isReusable = new Checkbox("Reusable");

    private final Checkbox allowReturn = new Checkbox("Allow Return");

    private final ComboBox<ApprovalType>approvalType = new ComboBox<>("Approval Type");

    private final IntegerField minimumStock = new IntegerField("Minimum Stock");

    private final ComboBox<UnitType> unitType = new ComboBox<>("Unit Type");

    private final ComboBox<ItemStatus> status = new ComboBox<>("Status");

    private final Dialog itemDialog = new Dialog();

    private final Button openDialogButton = new Button(VaadinIcon.PLUS.create());

    private InventoryItemDTO selectedItem;

    Button saveButton = new Button("Save");

    private Boolean isEdit = false;

    private final ComboBox<InventoryCategoryDTO> categoryFilter = new ComboBox<>();

        private final TextField itemCodeFilter = new TextField();

        private final ComboBox<ApprovalType> approvalTypeFilter = new ComboBox<>();

        private final ComboBox<ItemStatus> statusFilter = new ComboBox<>();

        private final Button clearFilterButton = new Button("Clear");
    
    
    public InventoryItemView(InventoryItemService service){

        this.service = service;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()

                .set("background", "#f4f7fb")

                .set("padding", "24px");

        H2 heading = new H2("Inventory Item Management");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "34px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span subHeading = new Span("Manage inventory items, approval types and stock rules");

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

        openDialogButton.setText("Add Item");

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

            itemDialog.open();
        });

        HorizontalLayout headerLayout =
                new HorizontalLayout(
                        headingSection,
                        openDialogButton
                );

        headerLayout.setWidthFull();

        headerLayout.expand(headingSection);

        headerLayout.setAlignItems(Alignment.CENTER);

        configureForm();

        configureGrid();

        configureFilters();

        HorizontalLayout checkBoxLayout =
                new HorizontalLayout(
                        isReusable,
                        allowReturn
                );

        FormLayout formLayout = new FormLayout();

        formLayout.add(
                categoryId,
                itemName,
                itemCode,
                approvalType,
                unitType,
                minimumStock,
                status,
                description,
                checkBoxLayout
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1),
                new FormLayout.ResponsiveStep("800px",2)
        );

        formLayout.getStyle()

                .set("background", "white")

                .set("padding", "20px")

                .set("border-radius", "18px");

        Button clearButton = new Button("Clear");

        Button deleteButton = new Button("Delete");

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        saveButton.getStyle()

                .set("background",
                        "linear-gradient(135deg,#2563eb,#1d4ed8)")

                .set("border-radius", "12px")

                .set("font-weight", "600");

        clearButton.getStyle()
                .set("border-radius", "12px");

        deleteButton.getStyle()
                .set("border-radius", "12px");

        saveButton.addClickListener(event -> saveItem());

        clearButton.addClickListener(event -> clearForm());

        deleteButton.addClickListener(event -> {

            ConfirmDialogUtil.showConfirmDialog(

                    "Delete",

                    "Are you sure you want to delete?",

                    this::deleteItem
            );
        });

        Button cancelButton =
                new Button("Cancel");

        cancelButton.addThemeVariants(
                ButtonVariant.LUMO_ERROR
        );

        cancelButton.getStyle()
                .set("border-radius", "12px");

        cancelButton.addClickListener(event -> {

            clearForm();

            itemDialog.close();
        });

        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        saveButton,
                        clearButton,
                        deleteButton,
                        cancelButton
                );

        VerticalLayout dialogLayout =
                new VerticalLayout(
                        formLayout,
                        buttonLayout
                );

        dialogLayout.setWidth("1000px");

        dialogLayout.getStyle()

                .set("background", "white")

                .set("border-radius", "20px");

        itemDialog.add(dialogLayout);

        itemDialog.setHeaderTitle(
                "Inventory Item"
        );

        itemDialog.setModal(true);

        itemDialog.setDraggable(true);

        itemDialog.setResizable(true);

        itemDialog.setWidth("1100px");

        HorizontalLayout filterLayout =
                new HorizontalLayout(

                        categoryFilter,

                        itemCodeFilter,

                        approvalTypeFilter,

                        statusFilter,

                        clearFilterButton
                );

        filterLayout.setWidthFull();

        filterLayout.getStyle()

                .set("background", "white")

                .set("padding", "18px")

                .set("border-radius", "18px")

                .set("box-shadow",
                        "0 4px 18px rgba(0,0,0,0.06)");

        isReusable.addValueChangeListener(event -> {

            if(Boolean.FALSE.equals(event.getValue())){

                allowReturn.setValue(false);

                allowReturn.setEnabled(false);

            }
            else{
                allowReturn.setEnabled(true);
            }
        });

        add(
                headerLayout,
                filterLayout,
                grid
        );

        refreshGrid();
    }

    private void configureForm(){

        categoryId.setItems(service.getActiveCategories());

        categoryId.setItemLabelGenerator(InventoryCategoryDTO::getCategoryName);

        approvalType.setItems(ApprovalType.values());

        unitType.setItems(UnitType.values());

        status.setItems(ItemStatus.values());

        binder.forField(categoryId)
            .bind(
                item -> {
                    if(item.getCategoryId() == null){
                        return null;
                    }

                    InventoryCategoryDTO dto = new InventoryCategoryDTO();
                    dto.setCategoryId(item.getCategoryId());
                    dto.setCategoryName(item.getCategoryName());

                    return dto;
                },

                (item , category) -> {
                    if(category != null) {
                        item.setCategoryId(category.getCategoryId());
                        item.setCategoryName(category.getCategoryName());
                    }
                }
            );
            

        binder.forField(itemName)
            .asRequired("Item name is required")
            .bind(
                InventoryItemDTO::getItemName,
                InventoryItemDTO::setItemName
        );

        binder.forField(itemCode)
            .asRequired("Item Code is required")
            .bind(
                InventoryItemDTO::getItemCode,
                InventoryItemDTO::setItemCode
        );

        binder.forField(approvalType)
            .asRequired("Approval Type is required")
            .bind(
                InventoryItemDTO::getApprovalType,
                InventoryItemDTO::setApprovalType
        );

        binder.forField(unitType)
            .asRequired("Unit Type is required")
            .bind(
                InventoryItemDTO::getUnitType,
                InventoryItemDTO::setUnitType
        );

        binder.forField(minimumStock)
            .asRequired("Minimum Stock is required")
            .bind(
                InventoryItemDTO::getMinimumStock,
                InventoryItemDTO::setMinimumStock
        );

        binder.forField(status)
            .asRequired("Status is required")
            .bind(
                InventoryItemDTO::getStatus,
                InventoryItemDTO::setStatus
        );

        binder.forField(isReusable)
            .asRequired("Is Reusable is required")
            .bind(
                InventoryItemDTO::getIsReusable,
                InventoryItemDTO::setIsReusable
        );

        binder.forField(allowReturn)
            .asRequired("Allow Return is required")
            .bind(
                InventoryItemDTO::getAllowReturn,
                InventoryItemDTO::setAllowReturn
        );

        binder.forField(description)
            .asRequired("Description is required")
            .bind(
                InventoryItemDTO::getDescription,
                InventoryItemDTO::setDescription
        );

        binder.setBean(currentItem);
    }

    private void configureFilters() {

                categoryFilter.setPlaceholder(
                        "Category"
                );

                categoryFilter.setItems(
                        service.getActiveCategories()
                );

                categoryFilter.setItemLabelGenerator(
                        InventoryCategoryDTO::getCategoryName
                );

                itemCodeFilter.setPlaceholder(
                        "Item Code"
                );

                approvalTypeFilter.setPlaceholder(
                        "Approval Type"
                );

                approvalTypeFilter.setItems(
                        ApprovalType.values()
                );

                statusFilter.setPlaceholder(
                        "Status"
                );

                statusFilter.setItems(
                        ItemStatus.values()
                );

                categoryFilter.addValueChangeListener(
                        event -> applyFilters()
                );

                itemCodeFilter.addValueChangeListener(
                        event -> applyFilters()
                );

                approvalTypeFilter.addValueChangeListener(
                        event -> applyFilters()
                );

                statusFilter.addValueChangeListener(
                        event -> applyFilters()
                );

                clearFilterButton.addClickListener(
                        event -> clearFilters()
                );
        }

    private void configureGrid(){

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                InventoryItemDTO::getCategoryName
        ).setHeader("Category");

        grid.addColumn(
                InventoryItemDTO::getItemName
        ).setHeader("Item Name");

        grid.addColumn(
                InventoryItemDTO::getItemCode
        ).setHeader("Item Code");

        grid.addComponentColumn(item -> {

            Span approvalBadge = new Span(item.getApprovalType().name());

            approvalBadge.getStyle()

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "700");

            if(item.getApprovalType().name().equals("HIGH_VALUE")) {

                approvalBadge.getStyle()

                        .set("background", "#fee2e2")

                        .set("color", "#dc2626");

            } else {

                approvalBadge.getStyle()

                        .set("background", "#dbeafe")

                        .set("color", "#2563eb");
            }

            return approvalBadge;

        }).setHeader("Approval Type");

        grid.addColumn(InventoryItemDTO::getMinimumStock).setHeader("Minimum Stock");

        grid.addComponentColumn(item -> {

            Span statusBadge = new Span(item.getStatus().name());

            statusBadge.getStyle()

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "700");

            if(item.getStatus().name().equals("AVAILABLE")) {

                statusBadge.getStyle()

                        .set("background", "#dcfce7")

                        .set("color", "#15803d");

            } else {

                statusBadge.getStyle()

                        .set("background", "#fee2e2")

                        .set("color", "#dc2626");
            }

            return statusBadge;

        }).setHeader("Status");

        grid.setHeight("620px");

        grid.setWidthFull();

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "20px")

                .set("overflow", "hidden")

                .set("box-shadow",
                        "0 6px 18px rgba(0,0,0,0.08)");

        grid.asSingleSelect()
                .addValueChangeListener(event -> {

                    if(event.getValue() != null){

                        selectedItem = event.getValue();

                        currentItem = selectedItem;

                        binder.setBean(currentItem);

                        InventoryCategoryDTO category = service
                                .getActiveCategories()
                                .stream()
                                .filter(c -> c.getCategoryId().equals(currentItem.getCategoryId()))
                                .findFirst()
                                .orElse(null);

                        categoryId.setValue(category);

                        saveButton.setText("Update Item");

                        isEdit = true;

                        itemDialog.open();
                    }
                });
    }


    private void saveItem(){
        try {
            binder.writeBean(currentItem);

            service.saveItem(currentItem);

            if(isEdit){
                NotificationUtil.success("Item updated successfully");
            }
            else{
                NotificationUtil.success("Item saved successfully");
            }

            isEdit = !isEdit;

            clearForm();

            refreshGrid();

            itemDialog.close();
        } 
        catch(ValidationException e){
            NotificationUtil.error("Validation failed");
        }
        catch (Exception e) {
            NotificationUtil.error(e.getMessage());
        }
    }

    private void deleteItem(){
        if(currentItem.getItemId() == null){
            NotificationUtil.warning("Select an item first");
            return;
        }

        service.deleteItem(currentItem.getItemId());

        NotificationUtil.success("Item deleted successfully");

        clearForm();
        refreshGrid();
    }

    private void applyFilters() {

                InventoryItemFilterDTO filterDTO = new InventoryItemFilterDTO();

                if(categoryFilter.getValue() != null) {

                        filterDTO.setCategoryId(
                                categoryFilter.getValue().getCategoryId()
                        );
                }

                filterDTO.setItemCode(
                        itemCodeFilter.getValue()
                );

                filterDTO.setApprovalType(
                        approvalTypeFilter.getValue()
                );

                filterDTO.setStatus(
                        statusFilter.getValue()
                );

                grid.setItems(
                        service.filterItems(filterDTO)
                );
        }

        private void clearFilters() {

                categoryFilter.clear();

                itemCodeFilter.clear();

                approvalTypeFilter.clear();

                statusFilter.clear();

                refreshGrid();
        }

    private void clearForm(){

        currentItem = new InventoryItemDTO();

        selectedItem = null;

        binder.setBean(currentItem);

        saveButton.setText("Save");

        categoryId.clear();

        itemName.clear();

        itemCode.clear();

        approvalType.clear();

        unitType.clear();

        minimumStock.clear();

        status.clear();

        description.clear();

        isReusable.setValue(false);

        allowReturn.setValue(false);

        allowReturn.setEnabled(true);

        isEdit = false;

        grid.deselectAll();
    }

    private void refreshGrid(){
        grid.setItems(service.getAllItems());
    }


}
