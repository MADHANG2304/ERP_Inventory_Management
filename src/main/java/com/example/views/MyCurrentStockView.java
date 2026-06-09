package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.CurrentAssetDTO;
import com.example.dto.CurrentConsumableDTO;
import com.example.dto.ReturnedItemDTO;
import com.example.service.MyCurrentStockService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(
        value = "my-current-stock",
        layout = MainLayout.class
)
@PageTitle("My Current Stock")
@RolesAllowed({
        "EMPLOYEE",
        "MANAGER"
})
public class MyCurrentStockView extends VerticalLayout {

        private final MyCurrentStockService myCurrentStockService;

        private final Grid<CurrentAssetDTO> assetGrid =
                new Grid<>(CurrentAssetDTO.class, false);

        private final Grid<CurrentConsumableDTO> consumableGrid =
                new Grid<>(CurrentConsumableDTO.class, false);

        private final Grid<ReturnedItemDTO> pendingReturnGrid =
                new Grid<>(ReturnedItemDTO.class, false);

        private final VerticalLayout contentLayout =
                new VerticalLayout();

        private final Button assetsTab =
                new Button("Assets");

        private final Button consumablesTab =
                new Button("Consumables");

        private final Button pendingReturnsTab =
                new Button("Pending Returns");

        public MyCurrentStockView(
                MyCurrentStockService myCurrentStockService
        ) {

                this.myCurrentStockService =
                        myCurrentStockService;

                setSizeFull();

                setPadding(true);

                setSpacing(true);

                add(createHeader());

                configureAssetGrid();

                configureConsumableGrid();

                configurePendingReturnGrid();

                HorizontalLayout tabs =
                        new HorizontalLayout(
                                assetsTab,
                                consumablesTab,
                                pendingReturnsTab
                        );

                tabs.setSpacing(true);

                add(tabs);

                contentLayout.setSizeFull();

                add(contentLayout);

                assetsTab.addClickListener(
                        e -> showAssets()
                );

                consumablesTab.addClickListener(
                        e -> showConsumables()
                );

                pendingReturnsTab.addClickListener(
                        e -> showPendingReturns()
                );

                showAssets();
        }

        private VerticalLayout createHeader() {

                H2 title = new H2("My Current Stock");

                title.getStyle()

                        .set("margin", "0")

                        .set("font-size", "28px")

                        .set("font-weight", "700");

                Span description =
                        new Span(
                                "Track all assets, consumables and pending returns currently assigned to you"
                        );

                description.getStyle()

                        .set("color", "#64748b")

                        .set("font-size", "14px");

                VerticalLayout layout =
                        new VerticalLayout(
                                title,
                                description
                        );

                layout.setPadding(true);

                layout.setSpacing(true);

                return layout;
        }

        private void configureAssetGrid() {

                assetGrid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES,
                        GridVariant.LUMO_COLUMN_BORDERS
                );

                assetGrid.addColumn(
                        CurrentAssetDTO::getIssueReferenceNumber
                ).setHeader("Issue Ref");

                assetGrid.addColumn(
                        CurrentAssetDTO::getItemName
                ).setHeader("Item");

                assetGrid.addColumn(
                        CurrentAssetDTO::getItemCode
                ).setHeader("Item Code");

                assetGrid.addColumn(
                        CurrentAssetDTO::getAssetReferenceNumber
                ).setHeader("Asset Ref");

                assetGrid.addColumn(
                        CurrentAssetDTO::getModelNumber
                ).setHeader("Model");

                assetGrid.addColumn(
                        dto ->

                                dto.getPurchaseDate() == null

                                        ? "-"

                                        : dto.getPurchaseDate()
                                                .toString()

                ).setHeader("Purchase Date");

                assetGrid.addColumn(
                        dto ->

                                dto.getIssuedDate() == null

                                        ? "-"

                                        : dto.getIssuedDate()
                                                .toLocalDate()
                                                .toString()

                ).setHeader("Issued Date");

                assetGrid.setSizeFull();

                assetGrid.setWidth("1000px");

                assetGrid.setHeight("380px");
        }

        private void configureConsumableGrid() {

                consumableGrid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES,
                        GridVariant.LUMO_COLUMN_BORDERS
                );

                consumableGrid.addColumn(
                        CurrentConsumableDTO::getItemName
                ).setHeader("Item");

                consumableGrid.addColumn(
                        CurrentConsumableDTO::getItemCode
                ).setHeader("Item Code");

                consumableGrid.addColumn(
                        CurrentConsumableDTO::getTotalIssued
                ).setHeader("Total Issued");

                // consumableGrid.addColumn(
                //         CurrentConsumableDTO::getTotalReturned
                // ).setHeader("Total Returned");

                // consumableGrid.addColumn(
                //         CurrentConsumableDTO::getCurrentBalance
                // ).setHeader("Current Balance");

                consumableGrid.setSizeFull();

                consumableGrid.setWidth("1000px");

                consumableGrid.setHeight("380px");
        }

        private void configurePendingReturnGrid() {

                pendingReturnGrid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES,
                        GridVariant.LUMO_COLUMN_BORDERS
                );

                pendingReturnGrid.addColumn(
                        ReturnedItemDTO::getIssueReferenceNumber
                ).setHeader("Issue Ref");

                pendingReturnGrid.addColumn(
                        ReturnedItemDTO::getReturnReferenceNumber
                ).setHeader("Return Ref");

                pendingReturnGrid.addColumn(
                        ReturnedItemDTO::getItemName
                ).setHeader("Item");

                pendingReturnGrid.addColumn(
                        ReturnedItemDTO::getItemCode
                ).setHeader("Item Code");

                pendingReturnGrid.addColumn(
                        dto ->

                                dto.getReturnedDate() == null

                                        ? "-"

                                        : dto.getReturnedDate()
                                                .toLocalDate()
                                                .toString()

                ).setHeader("Return Date");

                pendingReturnGrid.addColumn(
                        dto ->

                                dto.getIssueStatus() == null

                                        ? "-"

                                        : dto.getIssueStatus()
                                                .name()

                ).setHeader("Status");

                pendingReturnGrid.setSizeFull();

                pendingReturnGrid.setWidth("1000px");

                pendingReturnGrid.setHeight("380px");
        }

        private void showAssets() {

                assetGrid.setItems(myCurrentStockService.getCurrentAssets());

                contentLayout.removeAll();

                contentLayout.add(new Scroller(assetGrid));
        }

        private void showConsumables() {

                consumableGrid.setItems(
                        myCurrentStockService
                                .getCurrentConsumables()
                );

                contentLayout.removeAll();

                contentLayout.add(
                        new Scroller(consumableGrid)
                );
        }

        private void showPendingReturns() {

                pendingReturnGrid.setItems(
                        myCurrentStockService
                                .getPendingReturns()
                );

                contentLayout.removeAll();

                contentLayout.add(
                        new Scroller(pendingReturnGrid)
                );
        }
}