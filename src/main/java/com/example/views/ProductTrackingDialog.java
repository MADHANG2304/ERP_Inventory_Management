package com.example.views;

import com.example.dto.ProductTrackingDTO;
import com.example.entity.IssuedItem;
import com.example.entity.ReturnedItem;
import com.example.service.ProductTrackingService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ProductTrackingDialog extends Dialog {

    public ProductTrackingDialog(
            Long assetItemId,
            ProductTrackingService productTrackingService
    ) {

        setWidth("1300px");

        setHeight("800px");

        ProductTrackingDTO asset =
                productTrackingService
                        .getAssetDetails(
                                assetItemId
                        );

        VerticalLayout mainLayout =
                new VerticalLayout();

        mainLayout.setSizeFull();

        mainLayout.setPadding(true);

        mainLayout.setSpacing(true);



        VerticalLayout headerLayout = new VerticalLayout();

        headerLayout.setPadding(true);

        headerLayout.setSpacing(true);

        headerLayout.getStyle()

                .set("background",
                        "linear-gradient(135deg,#1e3a8a,#2563eb)")

                .set("color", "white")

                .set("border-radius", "16px");

        H3 title = new H3("Asset Tracking Details");

        title.getStyle()

                .set("margin", "0")

                .set("color", "white");

        Span subtitle = new Span(asset.getAssetReferenceNumber());

        subtitle.getStyle().set("opacity", "0.9");

        headerLayout.add(
                title,
                subtitle
        );



        H4 assetInfoTitle = new H4("Asset Information");

        Grid<String[]> assetInfoGrid =
                new Grid<>();

        assetInfoGrid.addColumn(data -> data[0])
                .setHeader("Field")
                .setAutoWidth(true);

        assetInfoGrid.addColumn(data -> data[1])
                .setHeader("Value")
                .setAutoWidth(true);

        assetInfoGrid.setItems(

                new String[]{
                        "Asset Reference",
                        asset.getAssetReferenceNumber()
                },

                new String[]{
                        "Item Name",
                        asset.getItemName()
                },

                new String[]{
                        "Item Code",
                        asset.getItemCode()
                },

                new String[]{
                        "Model Number",
                        asset.getModelNumber() == null
                                ? "-"
                                : asset.getModelNumber()
                },

                new String[]{
                        "Purchase Date",
                        asset.getPurchaseDate() == null
                                ? "-"
                                : asset.getPurchaseDate().toString()
                },

                new String[]{
                        "Purchase Price",
                        asset.getPurchasePrice()
                },

                new String[]{
                        "Current Holder",
                        asset.getCurrentHolder()
                },

                new String[]{
                        "Asset Status",
                        asset.getAssetStatus()
                },

                new String[]{
                        "Issued By",
                        asset.getIssuedBy() == null
                                ? "-"
                                : asset.getIssuedBy()
                },

                new String[]{
                        "Issue Reference",
                        asset.getIssueReferenceNumber() == null
                                ? "-"
                                : asset.getIssueReferenceNumber()
                }
        );

        assetInfoGrid.setHeight("260px");



        H4 issueHistoryTitle =
                new H4(
                        "Issue History"
                );

        Grid<IssuedItem> issueGrid =
                new Grid<>(
                        IssuedItem.class,
                        false
                );

        issueGrid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        issueGrid.addColumn(
                IssuedItem::getIssueReferenceNumber
        ).setHeader("Issue Ref No");

        issueGrid.addColumn(item ->

                item.getIssuedToEmployee() == null
                        ? "-"
                        : item.getIssuedToEmployee()
                                .getEmployeeName()

        ).setHeader("Issued To");

        issueGrid.addColumn(item ->

                item.getIssuedBy() == null
                        ? "-"
                        : item.getIssuedBy()
                                .getEmployeeName()

        ).setHeader("Issued By");

        issueGrid.addColumn(
                IssuedItem::getIssuedQuantity
        ).setHeader("Quantity");

        issueGrid.addColumn(
                IssuedItem::getIssuedDate
        ).setHeader("Issued Date");

        issueGrid.addColumn(item ->

                item.getIssueStatus() == null
                        ? "-"
                        : item.getIssueStatus()
                                .name()

        ).setHeader("Status");

        issueGrid.setItems(

                productTrackingService
                        .getIssueHistory(
                                assetItemId
                        )
        );

        issueGrid.setHeight("220px");



        H4 returnHistoryTitle =
                new H4(
                        "Return History"
                );

        Grid<ReturnedItem> returnGrid =
                new Grid<>(
                        ReturnedItem.class,
                        false
                );

        returnGrid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        returnGrid.addColumn(
                ReturnedItem::getReturnReferenceNumber
        ).setHeader("Return Ref No");

        returnGrid.addColumn(
                ReturnedItem::getReturnedQuantity
        ).setHeader("Quantity");

        returnGrid.addColumn(item ->

                item.getReturnCondition() == null
                        ? "-"
                        : item.getReturnCondition()
                                .name()

        ).setHeader("Condition");

        returnGrid.addColumn(
                ReturnedItem::getReturnRemarks
        ).setHeader("Remarks");

        returnGrid.addColumn(
                ReturnedItem::getReturnedDate
        ).setHeader("Returned Date");

        returnGrid.setItems(

                productTrackingService
                        .getReturnHistory(
                                assetItemId
                        )
        );

        returnGrid.setHeight("220px");



        Button closeButton =
                new Button("Close");

        closeButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        closeButton.addClickListener(
                event -> close()
        );

        HorizontalLayout footer =
                new HorizontalLayout(
                        closeButton
                );

        footer.setWidthFull();

        footer.setJustifyContentMode(
                FlexComponent.JustifyContentMode.END
        );



        mainLayout.add(

                headerLayout,

                assetInfoTitle,
                assetInfoGrid,

                issueHistoryTitle,
                issueGrid,

                returnHistoryTitle,
                returnGrid,

                footer
        );

        add(mainLayout);
    }
}