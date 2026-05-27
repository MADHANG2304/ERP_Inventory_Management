package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.ReturnedItemDTO;
import com.example.enums.ReturnCondition;
import com.example.service.ReturnService;
import com.example.utils.ConfirmDialogUtil;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.component.combobox.ComboBox;
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

    private final Grid<ReturnedItemDTO> grid =
            new Grid<>(ReturnedItemDTO.class, false);

    public ReturnView(ReturnService returnService) {

        this.returnService = returnService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()

                .set("background", "#f4f6f9")

                .set("padding", "20px");


        H2 heading =
                new H2("Return Items");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "32px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span subHeading =
                new Span(
                        "Return issued inventory items"
                );

        subHeading.getStyle()

                .set("font-size", "15px")

                .set("color", "#64748b");

        VerticalLayout headerLayout =
                new VerticalLayout(
                        heading,
                        subHeading
                );

        headerLayout.setPadding(true);

        headerLayout.setSpacing(true);


        configureGrid();

        add(
                headerLayout,
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

        grid.addColumn(
                ReturnedItemDTO::getIssuedQuantity
        ).setHeader("Issued Qty");

        grid.addComponentColumn(item -> {

            Span badge =
                    new Span("ISSUED");

            badge.getStyle()

                    .set("background", "#dcfce7")

                    .set("color", "#166534")

                    .set("padding", "5px 14px")

                    .set("border-radius", "14px")

                    .set("font-size", "12px")

                    .set("font-weight", "600");

            return badge;

        }).setHeader("Status");


        grid.addComponentColumn(item -> {

            Button returnButton =
                    new Button(
                            "Return",
                            VaadinIcon.ROTATE_LEFT.create()
                    );

            returnButton.addThemeVariants(
                    ButtonVariant.LUMO_PRIMARY
            );

            returnButton.getStyle()

                    .set("border-radius", "8px")

                    .set("font-size", "13px")

                    .set("font-weight", "600");

            returnButton.addClickListener(event ->
                    openReturnDialog(item)
            );

            return returnButton;

        }).setHeader("Action");

        grid.setWidthFull();

        grid.setHeight("650px");

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "12px")

                .set("border", "1px solid #dbe2ea");
    }

    private void openReturnDialog(
            ReturnedItemDTO item
    ) {

        Dialog dialog =
                new Dialog();

        dialog.setWidth("500px");

        dialog.setHeaderTitle(
                "Return Item"
        );


        Span itemInfo =
                new Span(

                        "Item : "

                                + item.getItemName()

                                + " | Available Qty : "

                                + item.getIssuedQuantity()
                );

        itemInfo.getStyle()

                .set("font-size", "14px")

                .set("font-weight", "600")

                .set("color", "#475569");


        IntegerField returnQuantity =
                new IntegerField(
                        "Return Quantity"
                );

        returnQuantity.setWidthFull();

        returnQuantity.setMin(1);

        returnQuantity.setValue(
                item.getIssuedQuantity()
        );


        ComboBox<ReturnCondition>
                returnCondition =
                new ComboBox<>(
                        "Return Condition"
                );

        returnCondition.setItems(
                ReturnCondition.values()
        );

        returnCondition.setWidthFull();


        TextArea remarks =
                new TextArea(
                        "Remarks"
                );

        remarks.setWidthFull();

        remarks.setHeight("100px");


        Button submitButton =
                new Button(
                        "Submit Return"
                );

        submitButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        submitButton.getStyle()

                .set("border-radius", "8px")

                .set("font-weight", "600");

        Button cancelButton =
                new Button(
                        "Cancel"
                );

        cancelButton.addThemeVariants(
                ButtonVariant.LUMO_TERTIARY
        );

        cancelButton.addClickListener(event ->
                dialog.close()
        );

        submitButton.addClickListener(event -> {

            try {

                if(returnCondition.getValue() == null) {

                    NotificationUtil.warning(
                            "Select return condition"
                    );

                    return;
                }

                item.setReturnQuantity(
                        returnQuantity.getValue()
                );

                item.setReturnCondition(
                        returnCondition.getValue()
                );

                item.setReturnRemarks(
                        remarks.getValue()
                );

                ConfirmDialogUtil.showConfirmDialog(

                        "Return Item",

                        "Are you sure you want to return this item?",

                        () -> {

                            try {

                                returnService.returnItem(item);

                                NotificationUtil.success(
                                        "Item returned successfully"
                                );

                                dialog.close();

                                refreshGrid();

                            } catch (Exception e) {

                                NotificationUtil.error(
                                        e.getMessage()
                                );
                            }
                        }
                );

            } catch (Exception e) {

                NotificationUtil.error(
                        e.getMessage()
                );
            }
        });

        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        cancelButton,
                        submitButton
                );

        buttonLayout.setWidthFull();

        buttonLayout.setJustifyContentMode(
                JustifyContentMode.END
        );

        VerticalLayout dialogLayout =
                new VerticalLayout(

                        itemInfo,

                        returnQuantity,

                        returnCondition,

                        remarks,

                        buttonLayout
                );

        dialogLayout.setPadding(false);

        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);

        dialog.open();
    }

    private void refreshGrid() {

        grid.setItems(
                returnService
                        .getIssuedItemsForReturn()
        );
    }
}