package com.example.views;

import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryStockDTO;
import com.example.dto.InventoryStockFilterDTO;
import com.example.service.InventoryStockService;
import com.example.utils.NotificationUtil;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "inventory-stock", layout = MainLayout.class)
@PageTitle("Inventory Stock")
@RolesAllowed({
                "SUPER_ADMIN",
                "INVENTORY_ADMIN"
})
public class InventoryStockView extends VerticalLayout {

        private final InventoryStockService inventoryStockService;

        private final Grid<InventoryStockDTO> grid = new Grid<>(InventoryStockDTO.class, false);

        private final BeanValidationBinder<InventoryStockDTO> binder = new BeanValidationBinder<>(InventoryStockDTO.class);

        private InventoryStockDTO currentStock = new InventoryStockDTO();

        private final ComboBox<InventoryItemDTO> itemId = new ComboBox<>("Inventory Item");

        private final IntegerField availableQuantity = new IntegerField("Available Quantity");

        private final IntegerField issuedQuantity = new IntegerField("Issued Quantity");

        private final IntegerField damagedQuantity = new IntegerField("Damaged Quantity");


        private final Dialog stockDialog = new Dialog();

        private final Button openDialogButton = new Button(VaadinIcon.PLUS.create());

        private InventoryStockDTO selectedStock;

        Button saveButton = new Button("Save");

        private Boolean isEdit = false;

        private final TextField itemNameFilter = new TextField();

        private final TextField itemCodeFilter = new TextField();

        private final ComboBox<String> stockStatusFilter = new ComboBox<>();

        private final Button clearFilterButton = new Button("Clear");

        public InventoryStockView(InventoryStockService inventoryStockService) {

                this.inventoryStockService = inventoryStockService;

                setSizeFull();

                setPadding(true);

                setSpacing(true);

                getStyle()
                        .set("background", "#f4f7fb")
                        
                        .set("padding", "24px");

                H2 heading = new H2("Inventory Stock Management");

                heading.getStyle()

                                .set("margin", "0")

                                .set("font-size", "34px")

                                .set("font-weight", "700")

                                .set("color", "#0f172a");

                Span subHeading = new Span( "Manage stock availability, issued quantity and damaged inventory");

                subHeading.getStyle()

                                .set("font-size", "15px")

                                .set("color", "#64748b");

                VerticalLayout headingSection = new VerticalLayout(heading , subHeading);

                headingSection.setPadding(true);

                headingSection.setSpacing(true);

                openDialogButton.setText("Add Stock");

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

                        stockDialog.open();
                });

                HorizontalLayout headerLayout = new HorizontalLayout(
                                headingSection,
                                openDialogButton);

                headerLayout.setWidthFull();

                headerLayout.expand(headingSection);

                headerLayout.setAlignItems(Alignment.CENTER);

                configureForm();

                configureGrid();

                configureFilters();

                FormLayout formLayout = new FormLayout();

                formLayout.add(
                                itemId,
                                availableQuantity,
                                issuedQuantity,
                                damagedQuantity
                        );

                formLayout.setResponsiveSteps(

                                new FormLayout.ResponsiveStep("0", 1),

                                new FormLayout.ResponsiveStep("800px", 2));

                formLayout.getStyle()

                                .set("background", "white")

                                .set("padding", "24px")

                                .set("border-radius", "18px");

                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                saveButton.getStyle()

                                .set("background",
                                                "linear-gradient(135deg,#2563eb,#1d4ed8)")

                                .set("border-radius", "12px")

                                .set("font-weight", "600");

                Button clearButton = new Button("Clear");

                Button cancelButton = new Button("Cancel");

                cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                clearButton.getStyle().set("border-radius", "12px");

                cancelButton.getStyle().set("border-radius", "12px");

                saveButton.addClickListener(event -> saveStock());

                clearButton.addClickListener(event -> clearForm());

                cancelButton.addClickListener(event -> {

                        clearForm();

                        stockDialog.close();
                });

                HorizontalLayout buttonLayout = new HorizontalLayout(
                                saveButton,
                                // clearButton,
                                cancelButton);

                VerticalLayout dialogLayout = new VerticalLayout(
                                formLayout,
                                buttonLayout);

                dialogLayout.setWidth("850px");

                dialogLayout.getStyle()

                                .set("background", "white")

                                .set("border-radius", "20px");

                stockDialog.add(dialogLayout);

                stockDialog.setHeaderTitle("Inventory Stock");

                stockDialog.setModal(true);

                stockDialog.setDraggable(true);

                stockDialog.setResizable(true);

                stockDialog.setWidth("950px");


                HorizontalLayout filterLayout =
                        new HorizontalLayout(

                                itemNameFilter,

                                itemCodeFilter,

                                stockStatusFilter,

                                clearFilterButton
                        );

                filterLayout.setWidthFull();

                filterLayout.getStyle()

                        .set("background", "white")

                        .set("padding", "18px")

                        .set("border-radius", "18px")

                        .set("box-shadow",
                                "0 4px 18px rgba(0,0,0,0.06)");

                add(
                        headerLayout,
                        filterLayout,
                        grid
                );

                refreshGrid();
        }

        private void configureForm() {

                itemId.setItems(inventoryStockService.getAllItems());

                itemId.setItemLabelGenerator(item -> item.getItemName() + " - " + item.getItemCode());

                binder.forField(itemId)
                                .bind(stock -> {
                                        if (stock.getItemId() == null) {
                                                return null;
                                        }

                                        InventoryItemDTO dto = new InventoryItemDTO();

                                        dto.setItemId(stock.getItemId());

                                        dto.setItemName(stock.getItemName());

                                        dto.setItemCode(stock.getItemCode());

                                        return dto;
                                },

                                (stock, item) -> {

                                        if (item != null) {
                                                stock.setItemId(item.getItemId());
                                                        
                                                stock.setItemName(item.getItemName());

                                                stock.setItemCode(item.getItemCode());
                                        }
                                });

                binder.forField(availableQuantity)
                .bind(
                        InventoryStockDTO::getAvailableQuantity,
                        InventoryStockDTO::setAvailableQuantity);

                binder.forField(issuedQuantity)
                .bind(
                        InventoryStockDTO::getIssuedQuantity,
                        InventoryStockDTO::setIssuedQuantity);

                binder.forField(damagedQuantity)
                .bind(
                        InventoryStockDTO::getDamagedQuantity,
                        InventoryStockDTO::setDamagedQuantity);

                binder.setBean(currentStock);
        }

        private void configureFilters() {

                itemNameFilter.setPlaceholder(
                        "Item Name"
                );

                itemCodeFilter.setPlaceholder(
                        "Item Code"
                );

                stockStatusFilter.setPlaceholder(
                        "Stock Status"
                );

                stockStatusFilter.setItems(
                        "LOW STOCK",
                        "NORMAL"
                );

                itemNameFilter.addValueChangeListener(
                        event -> applyFilters()
                );

                itemCodeFilter.addValueChangeListener(
                        event -> applyFilters()
                );

                stockStatusFilter.addValueChangeListener(
                        event -> applyFilters()
                );

                clearFilterButton.addClickListener(
                        event -> clearFilters()
                );
        }

        private void configureGrid() {

                grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

                grid.addColumn(InventoryStockDTO::getItemName).setHeader("Item");

                grid.addColumn(InventoryStockDTO::getItemCode).setHeader("Item Code");

                grid.addColumn(InventoryStockDTO::getAvailableQuantity).setHeader("Available");

                grid.addColumn(InventoryStockDTO::getIssuedQuantity).setHeader("Issued");

                grid.addColumn(InventoryStockDTO::getDamagedQuantity).setHeader("Damaged");

                grid.addComponentColumn(stock -> {

                        Span badge = new Span(Boolean.TRUE.equals(stock.getLowStock()) ? "LOW STOCK" : "NORMAL");

                        badge.getStyle()

                                        .set("padding", "6px 14px")

                                        .set("border-radius", "20px")

                                        .set("font-size", "12px")

                                        .set("font-weight", "700");

                        if (Boolean.TRUE.equals(stock.getLowStock())) {

                                badge.getStyle()

                                        .set("background", "#fee2e2")

                                        .set("color", "#dc2626");

                        } else {

                                badge.getStyle()

                                        .set("background", "#dcfce7")

                                        .set("color", "#15803d");
                        }

                        return badge;

                }).setHeader("Stock Status");

                grid.setHeight("620px");

                grid.setWidthFull();

                grid.getStyle()

                                .set("background", "white")

                                .set("border-radius", "20px")

                                .set("overflow", "hidden")

                                .set("box-shadow", "0 6px 18px rgba(0,0,0,0.08)");

                grid.asSingleSelect()
                                .addValueChangeListener(event -> {

                                        if (event.getValue() != null) {

                                                selectedStock = event.getValue();

                                                currentStock = selectedStock;

                                                binder.setBean(currentStock);

                                                InventoryItemDTO selectedItem = inventoryStockService
                                                                .getAllItems()
                                                                .stream()
                                                                .filter(item -> item.getItemId().equals(currentStock.getItemId()))
                                                                .findFirst()
                                                                .orElse(null);

                                                itemId.setValue(selectedItem);

                                                isEdit = true;

                                                saveButton.setText("Update Stock");

                                                stockDialog.open();
                                        }
                                });
        }

        private void saveStock() {

                try {

                        binder.writeBean(currentStock);

                        inventoryStockService.saveStock(currentStock);

                        if (isEdit) {
                                NotificationUtil.success("Stock updated successfully");
                        } else {
                                NotificationUtil.success("Stock saved successfully");
                        }

                        isEdit = !isEdit;

                        clearForm();

                        refreshGrid();

                        stockDialog.close();

                } catch (ValidationException e) {
                        NotificationUtil.error("Validation failed");

                } catch (Exception e) {
                        NotificationUtil.error(e.getMessage());
                }
        }

        private void applyFilters() {

                InventoryStockFilterDTO filterDTO =
                        new InventoryStockFilterDTO();

                filterDTO.setItemName(
                        itemNameFilter.getValue()
                );

                filterDTO.setItemCode(
                        itemCodeFilter.getValue()
                );

                if(stockStatusFilter.getValue() != null) {
                        filterDTO.setLowStock(stockStatusFilter.getValue().equals("LOW STOCK"));
                }

                grid.setItems(
                        inventoryStockService.filterStocks(filterDTO)
                );
        }

        private void clearFilters() {

                itemNameFilter.clear();

                itemCodeFilter.clear();

                stockStatusFilter.clear();

                refreshGrid();
        }

        private void clearForm() {

                currentStock = new InventoryStockDTO();

                selectedStock = null;

                saveButton.setText("Save");

                binder.setBean(currentStock);

                itemId.clear();

                availableQuantity.clear();

                issuedQuantity.clear();

                damagedQuantity.clear();

                isEdit = false;

                grid.deselectAll();
        }

        private void refreshGrid() {
                grid.setItems(inventoryStockService.getAllStocks());
        }
}