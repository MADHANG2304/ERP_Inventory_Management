package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.AssetItemDTO;
import com.example.dto.IssuedItemDTO;
import com.example.security.SecurityService;
import com.example.service.AssetItemService;
import com.example.service.IssueService;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "issue-items", layout = MainLayout.class)
@PageTitle("Issue Items")
@RolesAllowed({
        "INVENTORY_ADMIN",
        "SUPER_ADMIN"
})
public class IssueView extends VerticalLayout {

    private final IssueService issueService;

    private final SecurityService securityService;

    private final String username;

    private final Grid<IssuedItemDTO> grid = new Grid<>(IssuedItemDTO.class, false);
    
    private final Span selectedInfo = new Span("No request selected");
    
    private final Button clearSelectionButton = new Button("Clear Selection");
    
    private final Button refreshButton = new Button("Refresh");

    private IssuedItemDTO selectedItem;

    private final AssetItemService assetItemService;

    private final MultiSelectListBox<AssetItemDTO> assetSelector =
                new MultiSelectListBox<>();

    private final IntegerField quantityField =
                new IntegerField("Issue Quantity");

    public IssueView(IssueService issueService, SecurityService securityService, AssetItemService assetItemService) {

                this.issueService = issueService;

                this.securityService = securityService;

                this.assetItemService = assetItemService;

                this.username = securityService.getAuthenticatedUser();

                setSizeFull();

                setPadding(true);

                setSpacing(true);

                getStyle()

                        .set("background", "#f4f7fb")

                        .set("padding", "24px");

                H2 heading = new H2("Inventory Issue Center");

                heading.getStyle()

                        .set("margin", "0")

                        .set("font-size", "34px")

                        .set("font-weight", "700")

                        .set("color", "#0f172a");

                Span subHeading = new Span("Manage approved inventory requests and issue products to employees");

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

                // configureAssetSelector();

                Button issueButton = new Button("Issue Items", VaadinIcon.CHECK.create());

                issueButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

                issueButton.getStyle()

                        .set("border-radius", "12px")

                        .set("font-weight", "600")

                        .set("padding", "10px 18px");

                issueButton.addClickListener(event -> issueRequest());

                clearSelectionButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

                clearSelectionButton.getStyle().set("border-radius", "12px");

                clearSelectionButton.addClickListener(event -> {

                        grid.deselectAll();

                        selectedItem = null;

                        selectedInfo.setText("No request selected");
                });

                refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                refreshButton.getStyle().set("border-radius", "12px");

                refreshButton.addClickListener(event -> {

                        refreshGrid();

                        NotificationUtil.success("Data refreshed");
                });

                selectedInfo.getStyle()

                        .set("font-weight", "600")

                        .set("color", "#334155")

                        .set("padding-left", "10px");

                HorizontalLayout actionLayout =
                        new HorizontalLayout(

                                issueButton,
                                clearSelectionButton,
                                refreshButton,
                                selectedInfo
                        );

                actionLayout.setWidthFull();

                actionLayout.setAlignItems(FlexComponent.Alignment.CENTER);

                actionLayout.getStyle()

                        .set("background", "white")

                        .set("padding", "16px")

                        .set("border-radius", "18px")

                        .set("box-shadow", "0 4px 14px rgba(0,0,0,0.08)");

                add(
                        headingSection,
                        actionLayout,
                        grid
                );

                refreshGrid();
        }

        private void configureGrid() {

                grid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES,
                        GridVariant.LUMO_COLUMN_BORDERS
                );

                grid.addColumn(
                        IssuedItemDTO::getRequestNumber
                ).setHeader("Request Number");

                grid.addColumn(
                        IssuedItemDTO::getEmployeeName
                ).setHeader("Employee");

                grid.addColumn(
                        IssuedItemDTO::getItemName
                ).setHeader("Item");

                grid.addColumn(
                        IssuedItemDTO::getItemCode
                ).setHeader("Item Code");

                grid.addColumn(
                        dto -> dto.getAssetReferenceNumber() == null
                                ? "-" : dto.getAssetReferenceNumber()
                ).setHeader("Asset Ref");

                grid.addComponentColumn(item -> {

                        Span quantityBadge =
                                new Span(String.valueOf(item.getRequestedQuantity())
                                );

                        quantityBadge.getStyle()

                                .set("background", "#dbeafe")

                                .set("color", "#2563eb")

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-weight", "700")

                                .set("font-size", "13px");

                        return quantityBadge;

                }).setHeader("Requested Qty");

                grid.addComponentColumn(item -> {

                        Span quantityBadge =
                                new Span(
                                        String.valueOf(
                                                item.getIssuedQuantity()
                                        )
                                );

                        quantityBadge.getStyle()

                                .set("background", "#dcfce7")

                                .set("color", "#16a34a")

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-weight", "700")

                                .set("font-size", "13px");

                        return quantityBadge;

                }).setHeader("Already Issued");

                grid.addComponentColumn(item -> {

                        int remaining =
                                item.getRequestedQuantity()
                                        - item.getIssuedQuantity();

                        Span quantityBadge =
                                new Span(
                                        String.valueOf(
                                                remaining
                                        )
                                );

                        quantityBadge.getStyle()

                                .set("background", "#fef3c7")

                                .set("color", "#d97706")

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-weight", "700")

                                .set("font-size", "13px");

                        return quantityBadge;

                }).setHeader("Remaining");

                grid.setWidthFull();

                grid.setHeight("650px");

                grid.getStyle()

                        .set("background", "white")

                        .set("border-radius", "20px")

                        .set("overflow", "hidden")

                        .set("box-shadow", "0 6px 18px rgba(0,0,0,0.08)");

                grid.asSingleSelect()
                .addValueChangeListener(event -> {

                        selectedItem = event.getValue();

                        if(selectedItem != null) {

                                openIssueDialog(selectedItem);

                                selectedInfo.setText(

                                        "Selected : "

                                        + selectedItem.getRequestNumber()

                                        + " | "

                                        + selectedItem.getItemName()
                                );

                        } else {

                                selectedInfo.setText(
                                        "No request selected"
                                );
                        }
                });
        }

        private void issueRequest() {

                if(selectedItem == null) {

                        NotificationUtil.warning(
                                "Select a request first"
                        );

                        return;
                }

                // openIssueDialog(selectedItem);
        }

        private void openIssueDialog(
        IssuedItemDTO item
        ) {

                Dialog dialog = new Dialog();

                dialog.setWidth("650px");

                dialog.setHeaderTitle("Issue Item");

                VerticalLayout content = new VerticalLayout();

                Span requestInfo =
                        new Span(

                                "Request : "

                                + item.getRequestNumber()

                                + " | Item : "

                                + item.getItemName()
                        );

                requestInfo.getStyle()

                        .set("font-weight", "600")

                        .set("font-size", "15px");

                int remaining =
                        item.getRequestedQuantity()
                        - item.getIssuedQuantity();

                Span remainingInfo =
                        new Span(
                                "Remaining Quantity : "
                                + remaining
                        );

                remainingInfo.getStyle()

                        .set("color", "#d97706")

                        .set("font-weight", "600");

                content.add(
                        requestInfo,
                        remainingInfo
                );

                HorizontalLayout footer = new HorizontalLayout();

                if(Boolean.TRUE.equals(item.getReusable())) {

                        MultiSelectListBox<AssetItemDTO> dialogAssetSelector =
                                new MultiSelectListBox<>();

                        dialogAssetSelector.setWidthFull();

                        dialogAssetSelector.setHeight("250px");

                        dialogAssetSelector.setItems(

                                assetItemService.getAvailableAssetsByItem(
                                        item.getItemId()
                                )
                        );

                        dialogAssetSelector.setItemLabelGenerator(asset ->

                                asset.getAssetReferenceNumber()

                                +

                                (
                                        asset.getModelNumber() != null

                                                ? " - " + asset.getModelNumber()

                                                : ""
                                )
                        );

                        content.add(dialogAssetSelector);

                        Button issueButton =
                                new Button("Issue Selected Assets");

                        issueButton.addThemeVariants(
                                ButtonVariant.LUMO_SUCCESS
                        );

                        issueButton.addClickListener(event -> {

                                try {
                                        if(dialogAssetSelector.getSelectedItems().isEmpty()) {

                                                NotificationUtil.warning("Select asset");

                                                return;
                                        }

                                        if(dialogAssetSelector.getSelectedItems().size() > remaining) {

                                                NotificationUtil.warning("Only " + remaining + " asset(s) can be issued");

                                                return;
                                        }

                                        for(AssetItemDTO asset : dialogAssetSelector.getSelectedItems()) {

                                                issueService.issueItem(

                                                        item.getRequestItemId(),

                                                        asset.getAssetItemId(),

                                                        1,

                                                        username
                                                );
                                        }

                                        NotificationUtil.success("Assets issued successfully");

                                        dialog.close();

                                        refreshGrid();

                                } catch(Exception ex) {
                                        NotificationUtil.error(ex.getMessage());
                                }
                        });

                        footer.add(issueButton);
                        // content.add(issueButton);

                } else {

                        IntegerField issueQuantity =
                                new IntegerField(
                                        "Issue Quantity"
                                );

                        issueQuantity.setMin(1);

                        issueQuantity.setMax(remaining);

                        issueQuantity.setValue(
                                remaining
                        );

                        issueQuantity.setWidthFull();

                        content.add(issueQuantity);

                        Button issueButton =
                                new Button(
                                        "Issue Quantity"
                                );

                        issueButton.addThemeVariants(
                                ButtonVariant.LUMO_SUCCESS
                        );

                        issueButton.addClickListener(event -> {

                                try {

                                        if(issueQuantity.getValue() == null
                                                ||
                                                issueQuantity.getValue() <= 0) {

                                        NotificationUtil.warning(
                                                "Enter quantity"
                                        );

                                        return;
                                        }

                                        if(issueQuantity.getValue() > remaining) {

                                        NotificationUtil.warning(

                                                "Only "

                                                + remaining

                                                + " quantity can be issued"
                                        );

                                        return;
                                        }

                                        issueService.issueItem(

                                                item.getRequestItemId(),

                                                null,

                                                issueQuantity.getValue(),

                                                username
                                        );

                                        NotificationUtil.success(
                                                "Quantity issued successfully"
                                        );

                                        dialog.close();

                                        refreshGrid();

                                } catch(Exception ex) {

                                        NotificationUtil.error(
                                                ex.getMessage()
                                        );
                                }
                        });
                        footer.add(issueButton);

                }
                Button cancelButton =
                new Button("Cancel");

                cancelButton.addThemeVariants(
                        ButtonVariant.LUMO_ERROR
                );

                cancelButton.addClickListener(event -> {

                        grid.deselectAll();

                        selectedItem = null;

                        selectedInfo.setText(
                                "No request selected"
                        );

                        dialog.close();
                });

                footer.add(cancelButton);

                content.add(footer);

                dialog.add(content);

                dialog.open();
        }

        private void refreshGrid() {

                grid.setItems(
                        issueService.getApprovedRequests()
                );
        }
}