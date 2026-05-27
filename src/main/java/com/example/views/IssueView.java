package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.IssuedItemDTO;
import com.example.security.SecurityService;
import com.example.service.IssueService;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    public IssueView(IssueService issueService, SecurityService securityService) {

                this.issueService = issueService;

                this.securityService = securityService;

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

                grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

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

                grid.addComponentColumn(item -> {

                        Span quantityBadge = new Span(String.valueOf(item.getRequestedQuantity()));

                        quantityBadge.getStyle()

                                .set("background", "#dbeafe")

                                .set("color", "#2563eb")

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-weight", "700")

                                .set("font-size", "13px");

                        return quantityBadge;

                }).setHeader("Requested Qty");

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

                                selectedInfo.setText(

                                        "Selected : "

                                        + selectedItem.getRequestNumber()

                                        + " | "

                                        + selectedItem.getItemName()
                                );

                                } else {

                                        selectedInfo.setText("No request selected");
                                }
                        });
        }

    private void issueRequest() {

        try {

            if(selectedItem == null) {

                NotificationUtil.warning("Select a request first");
                
                return;
            }

            issueService.issueItem(selectedItem.getRequestItemId(), username);
            
            NotificationUtil.success("Item issued successfully");
            
            refreshGrid();
                
        } catch (Exception e) {
                
                NotificationUtil.error(e.getMessage());
        }
    }

    private void refreshGrid() {

        grid.setItems(
                issueService.getApprovedRequests()
        );
    }
}