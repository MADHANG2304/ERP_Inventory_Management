package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.InventoryCategoryDTO;
import com.example.service.InventoryCategoryService;
import com.example.utils.ConfirmDialogUtil;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value="inventory-categories", layout= MainLayout.class)
@PageTitle("Inventory Categories")
@RolesAllowed({
    "SUPER_ADMIN",
    "INVENTORY_ADMIN"
})
public class InventoryCategoryView extends VerticalLayout{
    
    private final InventoryCategoryService service;

    private final Grid<InventoryCategoryDTO> grid = new Grid<>(InventoryCategoryDTO.class, false);

    private final BeanValidationBinder<InventoryCategoryDTO> binder = new BeanValidationBinder<>(InventoryCategoryDTO.class);

    private InventoryCategoryDTO currentCategory = new InventoryCategoryDTO();

    private final TextField categoryName = new TextField("Category Name");

    private final TextField description = new TextField("Description");

    private final Checkbox isActive = new Checkbox("Active");

    private final TextField searchField = new TextField();

    private final Dialog categoryDialog = new Dialog();

    private final Button openDialogButton = new Button(VaadinIcon.PLUS.create());

    private InventoryCategoryDTO selectedCategory;

    Button saveButton = new Button("Save");

    private Boolean isEdit = false;

    
    public InventoryCategoryView(InventoryCategoryService service){

        this.service = service;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()

                .set("background", "#f4f7fb")

                .set("padding", "24px");

        H2 heading = new H2("Inventory Category Management");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "34px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span subHeading = new Span("Manage inventory categories and organize inventory structure");

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

        openDialogButton.setText("Add Category");

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

            categoryDialog.open();
        });

        HorizontalLayout headerLayout =
                new HorizontalLayout(
                        headingSection,
                        openDialogButton
                );

        headerLayout.setWidthFull();

        headerLayout.expand(headingSection);

        headerLayout.setAlignItems(Alignment.CENTER);

        configureForm();

        configureGrid();

        FormLayout formLayout = new FormLayout();

        formLayout.add(
                categoryName,
                description,
                isActive
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("700px", 2)
        );

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

        Button deleteButton = new Button("Delete");

        Button cancelButton = new Button("Cancel");

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        clearButton.getStyle()
                .set("border-radius", "12px");

        deleteButton.getStyle()
                .set("border-radius", "12px");

        cancelButton.getStyle()
                .set("border-radius", "12px");

        saveButton.addClickListener(event -> saveCategory());

        clearButton.addClickListener(event -> clearForm());

        deleteButton.addClickListener(event -> {

            ConfirmDialogUtil.showConfirmDialog(

                    "Delete",

                    "Are you sure you want to delete?",

                    this::deleteCategory
            );
        });

        cancelButton.addClickListener(event -> {

            clearForm();

            categoryDialog.close();
        });

        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        saveButton,
                        // clearButton,
                        deleteButton,
                        cancelButton
                );

        VerticalLayout dialogLayout =
                new VerticalLayout(
                        formLayout,
                        buttonLayout
                );

        dialogLayout.setWidth("700px");

        dialogLayout.getStyle()

                .set("background", "white")

                .set("border-radius", "20px");

        categoryDialog.add(dialogLayout);

        categoryDialog.setHeaderTitle("Inventory Category");

        categoryDialog.setModal(true);

        categoryDialog.setDraggable(true);

        categoryDialog.setResizable(true);

        categoryDialog.setWidth("800px");

        searchField.setPlaceholder("Search Category...");

        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());

        searchField.setWidth("350px");

        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.getStyle()

                .set("background", "white")

                .set("border-radius", "14px")

                .set("margin-bottom", "10px");

        searchField.addValueChangeListener(event -> {

            grid.setItems(
                service.searchCategory(event.getValue())
            );
        });

        add(
                headerLayout,
                searchField,
                grid
        );

        refreshGrid();
    }

    private void configureForm(){
        
        binder.forField(categoryName)
            .asRequired("Category Name is Required")
            .bind(
                InventoryCategoryDTO::getCategoryName,
                InventoryCategoryDTO::setCategoryName
            );
        binder.forField(description)
            .bind(
                InventoryCategoryDTO::getDescription,
                InventoryCategoryDTO::setDescription
            );
        binder.forField(isActive)
            .bind(
                InventoryCategoryDTO::getIsActive,
                InventoryCategoryDTO::setIsActive
            );
        
        binder.setBean(currentCategory);
    }

    private void configureGrid(){

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                InventoryCategoryDTO::getCategoryName
        ).setHeader("Category Name");

        grid.addColumn(
                InventoryCategoryDTO::getDescription
        ).setHeader("Description");

        grid.addComponentColumn(category -> {

            Span badge = new Span(Boolean.TRUE.equals(category.getIsActive()) ? "ACTIVE" : "INACTIVE");

            badge.getStyle()

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "700");

            if(Boolean.TRUE.equals(category.getIsActive())) {

                badge.getStyle()

                        .set("background", "#dcfce7")

                        .set("color", "#15803d");

            } else {

                badge.getStyle()

                        .set("background", "#fee2e2")

                        .set("color", "#dc2626");
            }

            return badge;

        }).setHeader("Status");

        grid.setHeight("620px");

        grid.setWidthFull();

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "20px")

                .set("overflow", "hidden")

                .set("box-shadow",
                        "0 6px 18px rgba(0,0,0,0.08)");

        grid.asSingleSelect()
                .addValueChangeListener(event -> {

                    if(event.getValue() != null){

                        selectedCategory = event.getValue();

                        currentCategory = selectedCategory;

                        binder.setBean(currentCategory);

                        saveButton.setText("Update Category");

                        isEdit = true;

                        categoryDialog.open();
                    }
                });
    }

    private void saveCategory(){
            try{

            binder.writeBean(currentCategory);

            service.saveCategory(currentCategory);

            if(isEdit){
                NotificationUtil.success("Category updated successfully");
            }
            else{
                NotificationUtil.success("Category saved successfully");
            }

            isEdit = !isEdit;

            clearForm();

            refreshGrid();

            categoryDialog.close();
        }
        catch(ValidationException e){
            NotificationUtil.error("Validation failed");
        }
        catch (Exception e) {
            NotificationUtil.error(e.getMessage());
        }
    }

    private void deleteCategory(){
        if(currentCategory.getCategoryId() == null) {
            NotificationUtil.warning("Select a category first");
            return;
        }

        service.deleteCategory(currentCategory.getCategoryId());

        // Notification.show("Category deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        NotificationUtil.success("Category deleted successfully");

        clearForm();
        refreshGrid();
    }

    private void clearForm() {

        currentCategory =
                new InventoryCategoryDTO();

        selectedCategory = null;

        saveButton.setText("Save");

        binder.setBean(currentCategory);

        categoryName.clear();

        description.clear();

        isActive.setValue(true);

        isEdit = false;

        grid.deselectAll();
    }

    private void refreshGrid() {
        grid.setItems(service.getAllCategory());
    }
}
