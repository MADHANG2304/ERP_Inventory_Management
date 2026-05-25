package com.example.views;

import com.example.dto.InventoryTransactionDTO;
import com.example.dto.InventoryTransactionFilterDTO;
import com.example.enums.ReferenceType;
import com.example.enums.TransactionType;
import com.example.service.InventoryTransactionService;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "inventory-transactions", layout = MainLayout.class)
@PageTitle("Inventory Transactions")
@RolesAllowed({
                "SUPER_ADMIN",
                "INVENTORY_ADMIN"
})
public class InventoryTransactionView
                extends VerticalLayout {

        private final InventoryTransactionService inventoryTransactionService;

        private final Grid<InventoryTransactionDTO> grid = new Grid<>(InventoryTransactionDTO.class, false);

        private final TextField itemNameFilter = new TextField();

        private final ComboBox<TransactionType> transactionTypeFilter = new ComboBox<>();

        private final ComboBox<ReferenceType> referenceTypeFilter = new ComboBox<>();

        private final TextField referenceNumberFilter = new TextField();

        private final Button clearFilterButton = new Button("Clear");

        public InventoryTransactionView(InventoryTransactionService inventoryTransactionService) {

                this.inventoryTransactionService = inventoryTransactionService;

                setSizeFull();

                setPadding(true);

                setSpacing(true);

                getStyle()

                        .set("background", "#f4f7fb")

                        .set("padding", "24px");

                H2 heading = new H2("Inventory Transaction History");

                heading.getStyle()

                        .set("margin", "0")

                        .set("font-size", "34px")

                        .set("font-weight", "700")

                        .set("color", "#0f172a");

                Span subHeading = new Span("Track all inventory movements, issues, returns and stock activities");

                subHeading.getStyle()

                        .set("font-size", "15px")

                        .set("color", "#64748b");

                VerticalLayout headingSection = new VerticalLayout(
                                heading,
                                subHeading);

                headingSection.setPadding(true);

                headingSection.setSpacing(true);

                configureFilters();

                configureGrid();

                HorizontalLayout filterLayout = new HorizontalLayout(

                                itemNameFilter,

                                transactionTypeFilter,

                                referenceTypeFilter,

                                referenceNumberFilter,

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
                        headingSection,
                        filterLayout,
                        grid
                );

                refreshGrid();
        }


        private void configureGrid() {

                grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

                grid.addColumn(InventoryTransactionDTO::getItemName).setHeader("Item");

                grid.addComponentColumn(transaction -> {

                        String type = transaction.getTransactionType() != null ? transaction.getTransactionType().name() : "-";

                        Span badge = new Span(type);

                        badge.getStyle()

                                        .set("padding", "6px 14px")

                                        .set("border-radius", "20px")

                                        .set("font-size", "12px")

                                        .set("font-weight", "700");

                        if (type.equals("ISSUE")) {

                                badge.getStyle()

                                                .set("background", "#fee2e2")

                                                .set("color", "#dc2626");

                        } else if (type.equals("RETURN")) {

                                badge.getStyle()

                                                .set("background", "#dcfce7")

                                                .set("color", "#15803d");

                        } else {

                                badge.getStyle()

                                                .set("background", "#dbeafe")

                                                .set("color", "#2563eb");
                        }

                        return badge;

                }).setHeader("Transaction Type");

                grid.addComponentColumn(transaction -> {

                        String reference = transaction.getReferenceType() != null ? transaction.getReferenceType().name() : "-";

                        Span badge = new Span(reference);

                        badge.getStyle()

                                        .set("padding", "6px 14px")

                                        .set("border-radius", "20px")

                                        .set("font-size", "12px")

                                        .set("font-weight", "700")

                                        .set("background", "#f1f5f9")

                                        .set("color", "#334155");

                        return badge;

                }).setHeader("Reference Type");

                grid.addColumn(InventoryTransactionDTO::getReferenceNumber).setHeader("Reference Number");

                grid.addComponentColumn(transaction -> {

                        Span quantity = new Span(String.valueOf(transaction.getQuantity()));

                        quantity.getStyle()

                                        .set("font-weight", "700")

                                        .set("font-size", "14px");

                        if (transaction.getQuantity() < 0) {
                                quantity.getStyle().set("color", "#dc2626");

                        } else {
                                quantity.getStyle().set("color", "#15803d");
                        }

                        return quantity;

                }).setHeader("Quantity");

                grid.addColumn(InventoryTransactionDTO::getRemarks).setHeader("Remarks");

                grid.addColumn(InventoryTransactionDTO::getTransactionDate).setHeader("Transaction Date");

                grid.setWidthFull();

                grid.setHeight("650px");

                grid.getStyle()

                        .set("background", "white")

                        .set("border-radius", "20px")

                        .set("overflow", "hidden")

                        .set("box-shadow", "0 6px 18px rgba(0,0,0,0.08)");
        }

        private void configureFilters() {

                itemNameFilter.setPlaceholder("Item Name");

                transactionTypeFilter.setPlaceholder("Transaction Type");

                referenceTypeFilter.setPlaceholder("Reference Type");

                referenceNumberFilter.setPlaceholder("Reference Number");

                transactionTypeFilter.setItems(TransactionType.values());

                referenceTypeFilter.setItems(ReferenceType.values());

                itemNameFilter.addValueChangeListener(event -> applyFilters());

                transactionTypeFilter.addValueChangeListener(event -> applyFilters());

                referenceTypeFilter.addValueChangeListener(event -> applyFilters());

                referenceNumberFilter.addValueChangeListener(event -> applyFilters());

                clearFilterButton.addClickListener(event -> clearFilters());
        
        }

        private void applyFilters() {

                InventoryTransactionFilterDTO filterDTO = new InventoryTransactionFilterDTO();

                filterDTO.setItemName(itemNameFilter.getValue());

                filterDTO.setTransactionType(transactionTypeFilter.getValue());

                filterDTO.setReferenceType(referenceTypeFilter.getValue());

                filterDTO.setReferenceNumber(referenceNumberFilter.getValue());

                grid.setItems(
                        inventoryTransactionService.filterTransactions(filterDTO)
                );
        }

        private void clearFilters() {

                itemNameFilter.clear();

                transactionTypeFilter.clear();

                referenceTypeFilter.clear();

                referenceNumberFilter.clear();

                refreshGrid();
        }

        private void refreshGrid() {

                grid.setItems(inventoryTransactionService.getAllTransactions());
        }
}