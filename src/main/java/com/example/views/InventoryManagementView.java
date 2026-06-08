package com.example.views;

import java.util.Arrays;

import com.example.base.ui.MainLayout;
import com.example.dto.AssetItemDTO;
import com.example.dto.InventoryCategoryDTO;
import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryStockDTO;
import com.example.enums.ApprovalType;
import com.example.enums.AssetStatus;
import com.example.enums.ItemStatus;
import com.example.enums.UnitType;
import com.example.service.AssetItemService;
import com.example.service.InventoryItemService;
import com.example.service.InventoryStockService;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
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
    private final AssetItemService assetItemService;

    private final Grid<InventoryItemDTO> itemGrid =
            new Grid<>(InventoryItemDTO.class, false);

    private final Grid<InventoryStockDTO> stockGrid =
            new Grid<>(InventoryStockDTO.class, false);

    private final Grid<AssetItemDTO> assetGrid =
            new Grid<>(AssetItemDTO.class,false);

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

        // ASSET DIALOG
        private final Dialog assetDialog =
                new Dialog();

        private final ComboBox<InventoryItemDTO> assetItem =
                new ComboBox<>("Item");

        private final TextField assetReferenceNumber =
                new TextField("Asset Reference Number");

        private final TextField modelNumber =
                new TextField("Model Number");

        private final DatePicker purchaseDate =
                new DatePicker("Purchase Date");

        private final NumberField purchasePrice =
                new NumberField("Purchase Price");

        private final ComboBox<AssetStatus> assetStatus =
                new ComboBox<>("Asset Status");

        private final Button assetSaveButton =
                new Button("Save");

        private AssetItemDTO selectedAsset;

    public InventoryManagementView(
            InventoryItemService itemService,
            InventoryStockService stockService,
            AssetItemService assetItemService
    ) {

        this.itemService = itemService;
        this.stockService = stockService;
        this.assetItemService = assetItemService;

        setSizeFull();

        getStyle()
                .set("background", "#f4f7fb")
                .set("padding", "24px");

        reusable.setValue(true);

        createHeader();

        configureItemGrid();

        configureStockGrid();

        configureAssetGrid();

        configureAssetDialog();

        configureDialog();

        refreshData();

        add(assetDialog);
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

        Tab assetTab = new Tab("Asset Items");

        Tab stockTab = new Tab("Stock");

        Tabs tabs = new Tabs(itemTab, assetTab, stockTab);

        tabs.setWidthFull();

        VerticalLayout content = new VerticalLayout(itemGrid);

        tabs.addSelectedChangeListener(event -> {

                content.removeAll();

                if(event.getSelectedTab() == itemTab){

                        content.add(itemGrid);

                        refreshData();

                }

                else if(event.getSelectedTab() == assetTab){

                        VerticalLayout assetLayout =
                                new VerticalLayout(
                                        createAssetHeader(),
                                        assetGrid
                                );

                        assetLayout.setPadding(false);

                        assetLayout.setSpacing(true);

                        assetLayout.setSizeFull();

                        content.add(assetLayout);

                        refreshData();
                }

                else {

                        content.add(stockGrid);
                        refreshData();
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
        ).setHeader("Currently Available");

        stockGrid.addColumn(
                InventoryStockDTO::getIssuedQuantity
        ).setHeader("Issued");

        stockGrid.addColumn(
                InventoryStockDTO::getDamagedQuantity
        ).setHeader("Damaged");

        stockGrid.addColumn(stock ->

                Boolean.TRUE.equals(
                        stock.getReusable()
                )

                        ? "Reusable"

                        : "Consumable"

        ).setHeader("Type");

                stockGrid.addColumn(stock ->

                Boolean.TRUE.equals(
                        stock.getReusable()
                )

                        ? "Auto Calculated"

                        : "Manual"

        ).setHeader("Stock Mode");

        stockGrid.setHeight("650px");

        stockGrid.setWidthFull();

        stockGrid.getStyle()
                .set("background", "white")
                .set("border-radius", "18px");

        refreshData();
    }

        private void configureAssetGrid() {

                assetGrid.addColumn(
                        AssetItemDTO::getItemName
                ).setHeader("Item");

                assetGrid.addColumn(
                        AssetItemDTO::getAssetReferenceNumber
                ).setHeader("Reference Number");

                assetGrid.addColumn(
                        AssetItemDTO::getModelNumber
                ).setHeader("Model Number");

                assetGrid.addColumn(
                        AssetItemDTO::getPurchaseDate
                ).setHeader("Purchase Date");

                assetGrid.addColumn(
                        AssetItemDTO::getPurchasePrice
                ).setHeader("Purchase Price");

                assetGrid.addColumn(
                        AssetItemDTO::getAssetStatus
                ).setHeader("Status");

                assetGrid.setWidthFull();

                assetGrid.setHeight("650px");

                assetGrid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES,
                        GridVariant.LUMO_COLUMN_BORDERS
                );

                assetGrid.asSingleSelect()
                        .addValueChangeListener(event -> {

                                selectedAsset =
                                        event.getValue();

                                if(selectedAsset != null) {

                                loadAssetToForm();

                                assetDialog.open();
                                }
                        });
        }

    private void configureDialog() {

        category.setItems(
                itemService.getActiveCategories()
        );

        category.setItemLabelGenerator(
                InventoryCategoryDTO::getCategoryName
        );

        approvalType.setItems(
                Arrays.stream(ApprovalType.values())
                        .filter(type -> type != ApprovalType.BULK_REQUEST)
                        .toList()
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

                // allowReturn,
                availableQty,
                // damagedQty,
                reusable
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

        reusable.addValueChangeListener(event -> {

                boolean reusableItem =
                        Boolean.TRUE.equals(
                                event.getValue()
                        );

                availableQty.setVisible(
                        !reusableItem
                );

                // damagedQty.setVisible(
                //         !reusableItem
                // );
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

        //     item.setAllowReturn(
        //             allowReturn.getValue()
        //     );

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

            stockDTO.setIssuedQuantity(

                    existingStock != null

                            ? existingStock.getIssuedQuantity()

                            : 0
            );

                if(Boolean.FALSE.equals(
                        savedItem.getIsReusable()
                )) {

                        stockDTO.setAvailableQuantity(
                                availableQty.getValue()
                        );

                        // stockDTO.setDamagedQuantity(
                        //         damagedQty.getValue()
                        // );

                        stockService.saveStock(stockDTO);
                }



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

    private void configureAssetDialog() {

                assetItem.setItems(
                        assetItemService.getReusableItems()
                );

                assetItem.setItemLabelGenerator(
                        InventoryItemDTO::getItemName
                );

                assetStatus.setItems(   
                        AssetStatus.values()
                );

                // assetStatus.setItems(
                //         AssetStatus.AVAILABLE
                // );

                // assetStatus.setValue(
                //         AssetStatus.AVAILABLE
                // );

                // assetStatus.setReadOnly(true);

                FormLayout form =
                        new FormLayout();

                form.add(

                        assetItem,
                        assetReferenceNumber,

                        modelNumber,
                        assetStatus,

                        purchaseDate,
                        purchasePrice
                );

                form.setResponsiveSteps(

                        new FormLayout.ResponsiveStep(
                                "0",
                                1
                        ),

                        new FormLayout.ResponsiveStep(
                                "700px",
                                2
                        )
                );

                Button cancel =
                        new Button("Cancel");

                cancel.addThemeVariants(
                        ButtonVariant.LUMO_ERROR
                );

                assetSaveButton.addThemeVariants(
                        ButtonVariant.LUMO_PRIMARY
                );

                assetSaveButton.addClickListener(
                        event -> saveAsset()
                );

                cancel.addClickListener(event -> {

                        assetDialog.close();

                        clearAssetForm();
                });

                HorizontalLayout buttons =
                        new HorizontalLayout(
                                assetSaveButton,
                                cancel
                        );

                VerticalLayout layout =
                        new VerticalLayout(
                                form,
                                buttons
                        );

                layout.setWidth("800px");

                assetDialog.setHeaderTitle(
                        "Asset Item"
                );

                assetDialog.add(layout);
        }

        private void saveAsset() {

                try {

                        boolean update =
                                selectedAsset != null;

                        AssetItemDTO dto =
                                new AssetItemDTO();

                        dto.setAssetItemId(

                                update

                                        ? selectedAsset.getAssetItemId()

                                        : null
                        );

                        dto.setItemId(
                                assetItem.getValue()
                                        .getItemId()
                        );

                        dto.setAssetReferenceNumber(
                                assetReferenceNumber.getValue()
                        );

                        dto.setModelNumber(
                                modelNumber.getValue()
                        );

                        dto.setPurchaseDate(
                                purchaseDate.getValue()
                        );

                        dto.setPurchasePrice(

                                purchasePrice.getValue() != null

                                        ? java.math.BigDecimal.valueOf(
                                                purchasePrice.getValue()
                                        )

                                        : null
                        );

                        dto.setAssetStatus(
                                assetStatus.getValue()
                        );

                        assetItemService.saveAsset(dto);

                        NotificationUtil.success(

                                update

                                        ? "Asset Updated Successfully"

                                        : "Asset Saved Successfully"
                        );

                        assetGrid.setItems(
                                assetItemService.getAllAssets()
                        );

                        assetDialog.close();

                        clearAssetForm();

                } catch (Exception e) {

                        NotificationUtil.error(
                                e.getMessage()
                        );
                }
        }


        private void loadAssetToForm() {

                assetItem.setValue(

                        assetItemService
                                .getReusableItems()
                                .stream()

                                .filter(item ->

                                        item.getItemId()
                                                .equals(
                                                        selectedAsset.getItemId()
                                                )
                                )

                                .findFirst()
                                .orElse(null)
                );

                assetReferenceNumber.setValue(

                        selectedAsset.getAssetReferenceNumber() != null

                                ? selectedAsset.getAssetReferenceNumber()

                                : ""
                );

                modelNumber.setValue(

                        selectedAsset.getModelNumber() != null

                                ? selectedAsset.getModelNumber()

                                : ""
                );

                purchaseDate.setValue(
                        selectedAsset.getPurchaseDate()
                );

                if(selectedAsset.getPurchasePrice() != null) {

                        purchasePrice.setValue(
                                selectedAsset.getPurchasePrice()
                                        .doubleValue()
                        );
                }

                if(selectedAsset.getAssetStatus() != null) {

                        assetStatus.setValue(
                                selectedAsset.getAssetStatus()
                        );
                }

                assetSaveButton.setText(
                        "Update"
                );

                
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

                if(selectedItem.getCategoryId() != null){

                        category.setValue(

                                itemService.getActiveCategories()
                                        .stream()

                                        .filter(cat ->

                                                cat.getCategoryId().equals(selectedItem.getCategoryId())
                                        )

                                        .findFirst()
                                        .orElse(null)
                        );
                }

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

                boolean reusableItem =
                        Boolean.TRUE.equals(
                                selectedItem.getIsReusable()
                        );

                availableQty.setVisible(
                        !reusableItem
                );

                if(stock != null && !reusableItem){

                        availableQty.setValue(
                                stock.getAvailableQuantity()
                        );
                }

                /*
                * LOCK REUSABLE FLAG
                * IF ASSETS ALREADY EXIST
                */

                boolean hasAssets =
                        assetItemService.getAllAssets()
                                .stream()

                                .anyMatch(asset ->

                                        asset.getItemId()
                                                .equals(
                                                        selectedItem.getItemId()
                                                )
                                );

                if(reusableItem && hasAssets) {

                        reusable.setEnabled(false);

                } else {

                        reusable.setEnabled(true);
                }

                saveButton.setText("Update");
        }

        private Component createAssetHeader() {

                H3 title =
                        new H3("Asset Items");

                Button addAssetButton =
                        new Button(
                                "Add Asset",
                                VaadinIcon.PLUS.create()
                        );

                addAssetButton.addThemeVariants(
                        ButtonVariant.LUMO_PRIMARY
                );

                addAssetButton.addClickListener(event -> {

                        clearAssetForm();

                        assetDialog.open();
                });

                HorizontalLayout layout =
                        new HorizontalLayout(
                                title,
                                addAssetButton
                        );

                layout.setWidthFull();

                layout.expand(title);

                layout.setAlignItems(
                        Alignment.CENTER
                );

                return layout;
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

                reusable.clear();

                reusable.setEnabled(true);

                allowReturn.clear();

                saveButton.setText("Save");
        }

        private void clearAssetForm() {

                selectedAsset = null;

                assetItem.clear();

                assetReferenceNumber.clear();

                modelNumber.clear();

                purchaseDate.clear();

                purchasePrice.clear();

                assetStatus.clear();

                assetStatus.setValue(
                        AssetStatus.AVAILABLE
                );

                assetSaveButton.setText(
                        "Save"
                );
        }

        private void refreshData() {

                itemGrid.setItems(
                        itemService.getAllItems()
                );

                assetGrid.setItems(
                        assetItemService.getAllAssets()
                );

                stockGrid.setItems(
                        stockService.getAllStocks()
                );
        }
}