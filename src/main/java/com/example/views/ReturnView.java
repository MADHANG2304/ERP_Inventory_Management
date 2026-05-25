package com.example.views;

import com.example.dto.ReturnedItemDTO;
import com.example.enums.ReturnCondition;
import com.example.service.ReturnService;
import com.example.utils.ConfirmDialogUtil;
import com.example.utils.NotificationUtil;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "return-items", layout = MainLayout.class)
@PageTitle("Return Items")
@RolesAllowed({
        "INVENTORY_ADMIN",
        "SUPER_ADMIN",
        "EMPLOYEE"
})
public class ReturnView extends VerticalLayout {

    private final ReturnService returnService;

    private final Grid<ReturnedItemDTO> grid = new Grid<>(ReturnedItemDTO.class, false);

    private final IntegerField returnQuantity = new IntegerField("Return Quantity");

    private final ComboBox<ReturnCondition> returnCondition = new ComboBox<>("Return Condition");

    private final TextArea remarks = new TextArea("Remarks");

    private ReturnedItemDTO selectedItem;

    private final Span selectedInfo = new Span("No item selected");

    private final Button clearSelectionButton = new Button("Clear Selection");


    public ReturnView(ReturnService returnService) {

            this.returnService = returnService;

            setSizeFull();

            setPadding(true);

            setSpacing(true);

            getStyle()

                    .set("background", "#f4f7fb")

                    .set("padding", "24px");

            H2 heading = new H2("Return Management Center");

            heading.getStyle()

                    .set("margin", "0")

                    .set("font-size", "34px")

                    .set("font-weight", "700")

                    .set("color", "#0f172a");

            Span subHeading = new Span("Manage issued inventory returns and reverse logistics operations");

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

            returnCondition.setItems(ReturnCondition.values());

            returnCondition.setWidthFull();

            returnQuantity.setMin(1);

            returnQuantity.setWidthFull();

            remarks.setWidthFull();

            remarks.setHeight("70px");

            remarks.getStyle().set("border-radius", "12px");

            Button returnButton = new Button("Return Item", VaadinIcon.ROTATE_LEFT.create());

            returnButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            returnButton.getStyle()

                    .set("border-radius", "12px")

                    .set("font-weight", "600")

                    .set("padding", "10px 18px");

            returnButton.addClickListener(event -> {

                ConfirmDialogUtil.showConfirmDialog(

                        "Return",

                        "Are you sure you want to return the item?",

                        this::returnItem
                );
            });

            Button refreshButton = new Button("Refresh", VaadinIcon.REFRESH.create());

            refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            refreshButton.getStyle().set("border-radius", "12px");

            refreshButton.addClickListener(event -> {

                refreshGrid();

                NotificationUtil.success(
                        "Data refreshed"
                );
            });

            clearSelectionButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

            clearSelectionButton.getStyle().set("border-radius", "12px");

            clearSelectionButton.addClickListener(event -> {
                clearForm();
            });

            selectedInfo.getStyle()

                    .set("font-weight", "600")

                    .set("color", "#334155")

                    .set("padding-left", "12px");

            HorizontalLayout actionLayout =
                    new HorizontalLayout(

                            returnButton,

                            clearSelectionButton,

                            refreshButton,

                            selectedInfo
                    );

            actionLayout.setWidthFull();

            actionLayout.setAlignItems(
                    FlexComponent.Alignment.CENTER
            );

            actionLayout.getStyle()

                    .set("background", "white")

                    .set("padding", "10px")

                    .set("border-radius", "18px")

                    .set("box-shadow",
                            "0 4px 14px rgba(0,0,0,0.08)");

            HorizontalLayout formLayout =
                    new HorizontalLayout(

                            returnQuantity,

                            returnCondition
                    );

            formLayout.setWidthFull();

            formLayout.setFlexGrow(
                    1,
                    returnQuantity,
                    returnCondition
            );

            VerticalLayout formCard =
                    new VerticalLayout(

                            formLayout,

                            remarks
                    );

            formCard.setWidthFull();

            formCard.getStyle()

                    .set("background", "white")

                    .set("padding", "10px")

                    .set("border-radius", "10px")

                    .set("box-shadow",
                            "0 6px 18px rgba(0,0,0,0.08)");

            add(

                headingSection,

                actionLayout,

                grid,

                formCard
            );

            refreshGrid();
    }

    private void configureGrid() {

            grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

            grid.addColumn(
                    ReturnedItemDTO::getIssueReferenceNumber
            ).setHeader("Issue Ref");

            grid.addColumn(
                    ReturnedItemDTO::getEmployeeName
            ).setHeader("Employee");

            grid.addColumn(
                    ReturnedItemDTO::getItemName
            ).setHeader("Item");

            grid.addColumn(
                    ReturnedItemDTO::getItemCode
            ).setHeader("Item Code");

            grid.addComponentColumn(item -> {

                Span quantityBadge = new Span(String.valueOf(item.getIssuedQuantity()));

                quantityBadge.getStyle()

                        .set("background", "#dbeafe")

                        .set("color", "#2563eb")

                        .set("padding", "6px 14px")

                        .set("border-radius", "20px")

                        .set("font-weight", "700")

                        .set("font-size", "13px");

                return quantityBadge;

            }).setHeader("Issued Qty");

            grid.addComponentColumn(item -> {

                String status = item.getIssueStatus() != null ? item.getIssueStatus().name() : "PENDING";

                Span badge = new Span(status);

                badge.getStyle()

                        .set("background", status.equals("ISSUED") ? "#dcfce7" : "#fee2e2")

                        .set("color", status.equals("ISSUED") ? "#166534" : "#991b1b")

                        .set("padding", "6px 14px")

                        .set("border-radius", "20px")

                        .set("font-weight", "700")

                        .set("font-size", "12px");

                return badge;

            }).setHeader("Issue Status");

            grid.setWidthFull();

            grid.setHeight("550px");

            grid.getStyle()

                    .set("background", "white")

                    .set("border-radius", "20px")

                    .set("overflow", "hidden")

                    .set("box-shadow", "0 6px 18px rgba(0,0,0,0.08)");

            grid.asSingleSelect()
                    .addValueChangeListener(event -> {

                        selectedItem = event.getValue();

                        if(selectedItem != null) {

                            returnQuantity.setValue(selectedItem.getIssuedQuantity());

                            selectedInfo.setText(

                                    "Selected : "

                                    + selectedItem.getIssueReferenceNumber()

                                    + " | "

                                    + selectedItem.getItemName()
                            );

                        } else {
                            selectedInfo.setText("No item selected");
                        }
                    });
    }

    private void returnItem() {

        try {

            if (selectedItem == null) {

                NotificationUtil.warning("Select an item first");

                return;
            }

            if (returnCondition.getValue() == null) {

                NotificationUtil.warning("Select a return condition");

                return;
            }

            selectedItem.setReturnQuantity(returnQuantity.getValue());

            selectedItem.setReturnCondition(returnCondition.getValue());

            selectedItem.setReturnRemarks(remarks.getValue());

            returnService.returnItem(selectedItem);

            NotificationUtil.success("Item returned successfully");

            refreshGrid();

            clearForm();

        } catch (Exception e) {
            NotificationUtil.error(e.getMessage());
        }
    }

    private void refreshGrid() {
        grid.setItems(returnService.getIssuedItemsForReturn());
    }

    private void clearForm() {

        selectedItem = null;

        returnQuantity.clear();

        returnCondition.clear();

        remarks.clear();

        selectedInfo.setText("No item selected");

        grid.deselectAll();
    }

}