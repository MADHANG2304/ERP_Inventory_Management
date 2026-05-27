package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.InventoryCategoryDTO;
import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryStockDTO;
import com.example.enums.ApprovalType;
import com.example.enums.ItemStatus;
import com.example.enums.UnitType;
import com.example.service.InventoryItemService;
import com.example.service.InventoryStockService;
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
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "inventory-management", layout = MainLayout.class)
@PageTitle("Inventory Management")
@RolesAllowed({
        "SUPER_ADMIN",
        "INVENTORY_ADMIN"
})
public class InventoryManagementView extends VerticalLayout {

    private final InventoryItemService itemService;
    private final InventoryStockService stockService;

    private final Grid<InventoryItemDTO> itemGrid =
            new Grid<>(InventoryItemDTO.class, false);

    private final Grid<InventoryStockDTO> stockGrid =
            new Grid<>(InventoryStockDTO.class, false);

    private final Dialog dialog = new Dialog();

    // ITEM FIELDS
    private final ComboBox<InventoryCategoryDTO> category =
            new ComboBox<>("Category");

    private final TextField itemName =
            new TextField("Item Name");

    private final TextField itemCode =
            new TextField("Item Code");

    private final ComboBox<ApprovalType> approvalType =
            new ComboBox<>("Approval Type");

    private final ComboBox<UnitType> unitType =
            new ComboBox<>("Unit Type");

    private final IntegerField minimumStock =
            new IntegerField("Minimum Stock");

    private final ComboBox<ItemStatus> status =
            new ComboBox<>("Status");

    private final TextArea description =
            new TextArea("Description");

    private final Checkbox reusable =
            new Checkbox("Reusable");

    private final Checkbox allowReturn =
            new Checkbox("Allow Return");

    // STOCK FIELDS
    private final IntegerField availableQty =
            new IntegerField("Available Quantity");

    private final IntegerField damagedQty =
            new IntegerField("Damaged Quantity");

    private InventoryItemDTO selectedItem;

    private final Button saveButton = new Button("Save");

    public InventoryManagementView(
            InventoryItemService itemService,
            InventoryStockService stockService
    ) {

        this.itemService = itemService;
        this.stockService = stockService;

        setSizeFull();

        getStyle()
                .set("background", "#f4f7fb")
                .set("padding", "24px");

        createHeader();

        configureItemGrid();

        configureStockGrid();

        configureDialog();

        refreshData();
    }

    private void createHeader() {

        H2 heading =
                new H2("Inventory Management");

        heading.getStyle()
                .set("margin", "0")
                .set("font-size", "38px")
                .set("font-weight", "700")
                .set("color", "#0f172a");

        Span sub =
                new Span(
                        "Manage inventory items and stock together"
                );

        sub.getStyle()
                .set("color", "#64748b");

        VerticalLayout left =
                new VerticalLayout(
                        heading,
                        sub
                );

        left.setPadding(true);
        left.setSpacing(true);

        Button addButton =
                new Button(
                        "Add Inventory",
                        VaadinIcon.PLUS.create()
                );

        addButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        addButton.getStyle()
                .set("background",
                        "linear-gradient(135deg,#2563eb,#1d4ed8)")
                .set("border-radius", "12px")
                .set("height", "42px");

        addButton.addClickListener(e -> {

            clearForm();

            dialog.open();
        });

        HorizontalLayout header =
                new HorizontalLayout(
                        left,
                        addButton
                );

        header.setWidthFull();

        header.expand(left);

        header.setAlignItems(Alignment.CENTER);

        // TABS
        Tab itemTab = new Tab("Items");

        Tab stockTab = new Tab("Stock");

        Tabs tabs = new Tabs(itemTab, stockTab);

        tabs.setWidthFull();

        VerticalLayout content =
                new VerticalLayout(itemGrid);

        tabs.addSelectedChangeListener(event -> {

            content.removeAll();

            if(event.getSelectedTab() == itemTab){

                content.add(itemGrid);

            } else {

                content.add(stockGrid);
            }
        });

        add(
                header,
                tabs,
                content
        );
    }

    private void configureItemGrid() {

        itemGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS
        );

        itemGrid.addColumn(
                InventoryItemDTO::getItemName
        ).setHeader("Item");

        itemGrid.addColumn(
                InventoryItemDTO::getItemCode
        ).setHeader("Code");

        itemGrid.addColumn(
                InventoryItemDTO::getMinimumStock
        ).setHeader("Minimum");

        itemGrid.addColumn(
                InventoryItemDTO::getStatus
        ).setHeader("Status");

        itemGrid.setHeight("650px");

        itemGrid.setWidthFull();

        itemGrid.getStyle()
                .set("background", "white")
                .set("border-radius", "18px");

        itemGrid.asSingleSelect()
                .addValueChangeListener(event -> {

                    if(event.getValue() != null){

                        selectedItem =
                                event.getValue();

                        loadItemToForm();

                        dialog.open();
                    }
                });
    }

    private void configureStockGrid() {

        stockGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS
        );

        stockGrid.addColumn(
                InventoryStockDTO::getItemName
        ).setHeader("Item");

        stockGrid.addColumn(
                InventoryStockDTO::getItemCode
        ).setHeader("Code");

        stockGrid.addColumn(
                InventoryStockDTO::getAvailableQuantity
        ).setHeader("Available");

        stockGrid.addColumn(
                InventoryStockDTO::getIssuedQuantity
        ).setHeader("Issued");

        stockGrid.addColumn(
                InventoryStockDTO::getDamagedQuantity
        ).setHeader("Damaged");

        stockGrid.setHeight("650px");

        stockGrid.setWidthFull();

        stockGrid.getStyle()
                .set("background", "white")
                .set("border-radius", "18px");
    }

    private void configureDialog() {

        category.setItems(
                itemService.getActiveCategories()
        );

        category.setItemLabelGenerator(
                InventoryCategoryDTO::getCategoryName
        );

        approvalType.setItems(
                ApprovalType.values()
        );

        unitType.setItems(
                UnitType.values()
        );

        status.setItems(
                ItemStatus.values()
        );

        FormLayout form =
                new FormLayout();

        form.add(

                category,
                itemName,

                itemCode,
                approvalType,

                unitType,
                minimumStock,

                status,
                description,

                availableQty,
                damagedQty,

                reusable,
                allowReturn
        );

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1),
                new FormLayout.ResponsiveStep("800px",2)
        );

        Button cancel =
                new Button("Cancel");

        saveButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        cancel.addThemeVariants(
                ButtonVariant.LUMO_ERROR
        );

        saveButton.addClickListener(event -> saveData());

        cancel.addClickListener(event -> {

            dialog.close();

            clearForm();
        });

        HorizontalLayout buttons =
                new HorizontalLayout(
                        saveButton,
                        cancel
                );

        VerticalLayout layout =
                new VerticalLayout(
                        form,
                        buttons
                );

        layout.setWidth("900px");

        dialog.add(layout);

        dialog.setHeaderTitle(
                "Inventory"
        );
    }

    private void saveData() {

        try {

            boolean isUpdate =
                    selectedItem != null;

            InventoryItemDTO item =
                    new InventoryItemDTO();

            item.setItemId(
                    isUpdate
                            ? selectedItem.getItemId()
                            : null
            );

            item.setCategoryId(
                    category.getValue().getCategoryId()
            );

            item.setItemName(
                    itemName.getValue()
            );

            item.setItemCode(
                    itemCode.getValue()
            );

            item.setApprovalType(
                    approvalType.getValue()
            );

            item.setUnitType(
                    unitType.getValue()
            );

            item.setMinimumStock(
                    minimumStock.getValue()
            );

            item.setStatus(
                    status.getValue()
            );

            item.setDescription(
                    description.getValue()
            );

            item.setIsReusable(
                    reusable.getValue()
            );

            item.setAllowReturn(
                    allowReturn.getValue()
            );

            InventoryItemDTO savedItem =
                    itemService.saveItem(item);

            InventoryStockDTO existingStock =
                    stockService.getAllStocks()
                            .stream()

                            .filter(stock ->

                                    stock.getItemId()
                                            .equals(
                                                    savedItem.getItemId()
                                            )
                            )

                            .findFirst()

                            .orElse(null);

            InventoryStockDTO stockDTO =
                    new InventoryStockDTO();

            if(existingStock != null){

                stockDTO.setStockId(
                        existingStock.getStockId()
                );
            }

            stockDTO.setItemId(
                    savedItem.getItemId()
            );

            stockDTO.setAvailableQuantity(
                    availableQty.getValue()
            );

            stockDTO.setDamagedQuantity(
                    damagedQty.getValue()
            );

            stockDTO.setIssuedQuantity(

                    existingStock != null

                            ? existingStock.getIssuedQuantity()

                            : 0
            );

            stockService.saveStock(stockDTO);

            // NOTIFICATION
            if(isUpdate){

                NotificationUtil.success(
                        "Updated Successfully"
                );

            } else {

                NotificationUtil.success(
                        "Saved Successfully"
                );
            }

            refreshData();

            dialog.close();

            clearForm();

        } catch (Exception e){

            NotificationUtil.error(
                    e.getMessage()
            );
        }
    }

    private void loadItemToForm() {

        itemName.setValue(
                selectedItem.getItemName() != null
                        ? selectedItem.getItemName()
                        : ""
        );

        itemCode.setValue(
                selectedItem.getItemCode() != null
                        ? selectedItem.getItemCode()
                        : ""
        );

        minimumStock.setValue(
                selectedItem.getMinimumStock() != null
                        ? selectedItem.getMinimumStock()
                        : 0
        );

        description.setValue(
                selectedItem.getDescription() != null
                        ? selectedItem.getDescription()
                        : ""
        );

        reusable.setValue(
                Boolean.TRUE.equals(
                        selectedItem.getIsReusable()
                )
        );

        allowReturn.setValue(
                Boolean.TRUE.equals(
                        selectedItem.getAllowReturn()
                )
        );

        if(selectedItem.getApprovalType() != null){

            approvalType.setValue(
                    selectedItem.getApprovalType()
            );
        }

        if(selectedItem.getUnitType() != null){

            unitType.setValue(
                    selectedItem.getUnitType()
            );
        }

        if(selectedItem.getStatus() != null){

            status.setValue(
                    selectedItem.getStatus()
            );
        }

        // CATEGORY MAPPING
        if(selectedItem.getCategoryId() != null){

            category.setValue(

                    itemService.getActiveCategories()
                            .stream()

                            .filter(cat ->

                                    cat.getCategoryId()
                                            .equals(
                                                    selectedItem.getCategoryId()
                                            )
                            )

                            .findFirst()
                            .orElse(null)
            );
        }

        // STOCK MAPPING
        InventoryStockDTO stock =
                stockService.getAllStocks()
                        .stream()

                        .filter(s ->

                                s.getItemId()
                                        .equals(
                                                selectedItem.getItemId()
                                        )
                        )

                        .findFirst()

                        .orElse(null);

        if(stock != null){

            availableQty.setValue(
                    stock.getAvailableQuantity()
            );

            damagedQty.setValue(
                    stock.getDamagedQuantity()
            );
        }

        saveButton.setText("Update");
    }

    private void clearForm() {

        selectedItem = null;

        category.clear();

        itemName.clear();

        itemCode.clear();

        approvalType.clear();

        unitType.clear();

        minimumStock.clear();

        status.clear();

        description.clear();

        availableQty.clear();

        damagedQty.clear();

        reusable.clear();

        allowReturn.clear();

        saveButton.setText("Save");
    }

    private void refreshData() {

        itemGrid.setItems(
                itemService.getAllItems()
        );

        stockGrid.setItems(
                stockService.getAllStocks()
        );
    }
}