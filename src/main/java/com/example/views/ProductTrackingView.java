package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.ProductTrackingDTO;
import com.example.service.ProductTrackingService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({
        "SUPER_ADMIN",
        "INVENTORY_ADMIN"
})
@Route(
        value = "product-tracking",
        layout = MainLayout.class
)
@PageTitle("Product Tracking")
public class ProductTrackingView extends VerticalLayout {

    private final ProductTrackingService productTrackingService;

    private final Grid<ProductTrackingDTO> grid =
            new Grid<>(
                    ProductTrackingDTO.class,
                    false
            );

    public ProductTrackingView(
            ProductTrackingService productTrackingService
    ) {

        this.productTrackingService =
                productTrackingService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        add(
                createHeader()
        );

        configureGrid();

        add(grid);

        expand(grid);

        loadData();
    }

    private HorizontalLayout createHeader() {

        H2 title =
                new H2(
                        "Product Tracking"
                );

        title.getStyle()

                .set("margin", "0")

                .set("font-size", "28px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span description =
                new Span(
                        "Track asset assignment, issue history and return history"
                );

        description.getStyle()

                .set("color", "#64748b")

                .set("font-size", "14px");

        VerticalLayout left =
                new VerticalLayout(
                        title,
                        description
                );

        left.setPadding(true);

        left.setSpacing(true);

        HorizontalLayout header =
                new HorizontalLayout(left);

        header.setWidthFull();

        return header;
    }

    private void configureGrid() {

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                ProductTrackingDTO::getAssetReferenceNumber
        )
        .setHeader("Asset Ref No")
        .setAutoWidth(true);

        grid.addColumn(
                ProductTrackingDTO::getItemCode
        )
        .setHeader("Item Code")
        .setAutoWidth(true);

        grid.addColumn(
                ProductTrackingDTO::getItemName
        )
        .setHeader("Item Name")
        .setAutoWidth(true);

        grid.addColumn(
                ProductTrackingDTO::getModelNumber
        )
        .setHeader("Model")
        .setAutoWidth(true);

        grid.addColumn(
                ProductTrackingDTO::getCurrentHolder
        )
        .setHeader("Current Holder")
        .setAutoWidth(true);

        grid.addComponentColumn(dto -> {

            Span badge =
                    new Span(
                            dto.getAssetStatus()
                    );

            String color = "#64748b";

            switch (dto.getAssetStatus()) {

                case "AVAILABLE" ->
                        color = "#16a34a";

                case "ISSUED" ->
                        color = "#2563eb";

                case "DAMAGED" ->
                        color = "#dc2626";

                case "UNDER_SERVICE" ->
                        color = "#f59e0b";
            }

            badge.getStyle()

                    .set("background", color)

                    .set("color", "white")

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "600");

            return badge;

        })
        .setHeader("Status")
        .setAutoWidth(true);

        grid.addColumn(
                ProductTrackingDTO::getLastIssuedDate
        )
        .setHeader("Last Issued Date")
        .setAutoWidth(true);

        grid.addComponentColumn(dto -> {

            Button viewButton =
                    new Button(
                            "View",
                            VaadinIcon.EYE.create()
                    );

            viewButton.addThemeVariants(
                    ButtonVariant.LUMO_PRIMARY
            );

            viewButton.addClickListener(event -> {

                ProductTrackingDialog dialog =
                        new ProductTrackingDialog(
                                dto.getAssetItemId(),
                                productTrackingService
                        );

                dialog.open();
            });

            return viewButton;

        })
        .setHeader("Action")
        .setAutoWidth(true);

        grid.setSizeFull();

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "18px")

                .set("overflow", "hidden")

                .set(
                        "box-shadow",
                        "0 6px 18px rgba(0,0,0,0.08)"
                );
    }

    private void loadData() {

        grid.setItems(

                productTrackingService
                        .getAllAssets()
        );
    }
}