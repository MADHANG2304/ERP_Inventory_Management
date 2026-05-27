package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.DesignationDTO;
import com.example.service.DesignationService;
import com.example.utils.ConfirmDialogUtil;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value= "designations", layout= MainLayout.class)
@PageTitle("Designations")
@RolesAllowed("SUPER_ADMIN")
public class DesignationView extends VerticalLayout{
    
    private final DesignationService designationService;
    
    private final Grid<DesignationDTO> grid = new Grid<>(DesignationDTO.class, false);

    private final BeanValidationBinder<DesignationDTO> binder = new BeanValidationBinder<>(DesignationDTO.class);

    private DesignationDTO currentDesignation = new DesignationDTO();

    private final TextField designationName = new TextField("Designation Name");

    private final TextField designationCode = new TextField("Designation Code");

    private final Checkbox isActive = new Checkbox("Active");

    private final TextField searchField = new TextField();

    private Boolean isEdit = false;

    Button saveButton = new Button("Save");

    private final Dialog designationDialog =
        new Dialog();

    private final Button openDialogButton =
            new Button(VaadinIcon.PLUS.create());

    private DesignationDTO selectedDesignation;

    public DesignationView(DesignationService designationService){

        this.designationService = designationService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()
                .set("background", "#f4f7fb")
                .set("padding", "20px");

        H2 heading =
                new H2("Designation Management");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "32px")

                .set("font-weight", "700")

                .set("color", "#1e293b");

        Span subHeading =
                new Span(
                        "Manage employee roles and organizational positions"
                );

        subHeading.getStyle()

                .set("color", "#64748b")

                .set("font-size", "15px");

        VerticalLayout headingLayout =
                new VerticalLayout(
                        heading,
                        subHeading
                );

        headingLayout.setPadding(true);

        headingLayout.setSpacing(true);

        openDialogButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        openDialogButton.setText("Add Designation");

        openDialogButton.setIcon(
                VaadinIcon.PLUS.create()
        );

        openDialogButton.getStyle()

                .set("border-radius", "10px")

                .set("height", "42px")

                .set("font-weight", "600")

                .set("background",
                        "linear-gradient(135deg,#7c3aed,#6d28d9)");

        openDialogButton.addClickListener(event -> {

            clearForm();

            designationDialog.open();
        });

        HorizontalLayout headerLayout =
                new HorizontalLayout(
                        headingLayout,
                        openDialogButton
                );

        headerLayout.setWidthFull();

        headerLayout.expand(headingLayout);

        headerLayout.setAlignItems(
                Alignment.CENTER
        );

        configureForm();

        configureGrid();

        saveButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        saveButton.getStyle()

                .set("border-radius", "10px")

                .set("font-weight", "600");

        Button clearButton =
                new Button("Clear");

        clearButton.addThemeVariants(
                ButtonVariant.LUMO_CONTRAST
        );

        clearButton.getStyle()
                .set("border-radius", "10px");

        Button deleteButton =
                new Button("Delete");

        deleteButton.addThemeVariants(
                ButtonVariant.LUMO_ERROR
        );

        deleteButton.getStyle()
                .set("border-radius", "10px");

        saveButton.addClickListener(
                event -> saveDesignation()
        );

        clearButton.addClickListener(
                event -> clearForm()
        );

        deleteButton.addClickListener(event -> {

            ConfirmDialogUtil.showConfirmDialog(

                    "Delete",

                    "Are you sure you want to delete?",

                    this::deleteDesignation
            );
        });

        Button cancelButton =
                new Button("Cancel");

        cancelButton.addThemeVariants(
                ButtonVariant.LUMO_TERTIARY
        );

        cancelButton.getStyle()
                .set("border-radius", "10px");

        cancelButton.addClickListener(event -> {

            clearForm();

            designationDialog.close();
        });

        HorizontalLayout dialogButtonLayout =
                new HorizontalLayout(
                        saveButton,
                        // clearButton,
                        deleteButton,
                        cancelButton
                );

        designationName.setWidthFull();

        designationCode.setWidthFull();

        VerticalLayout formSection =
                new VerticalLayout(

                        designationName,

                        designationCode,

                        isActive
                );

        formSection.setPadding(false);

        formSection.setSpacing(true);

        VerticalLayout dialogLayout =
                new VerticalLayout(

                        formSection,

                        dialogButtonLayout
                );

        dialogLayout.getStyle()

                .set("background", "white")

                .set("border-radius", "18px")

                .set("padding", "20px");

        dialogLayout.setWidth("550px");

        designationDialog.add(dialogLayout);

        designationDialog.setHeaderTitle(
                "Designation Details"
        );

        designationDialog.setModal(true);

        designationDialog.setDraggable(true);

        designationDialog.setResizable(true);

        designationDialog.setWidth("600px");

        designationDialog.setHeight("420px");

        searchField.setPlaceholder(
                "Search designation..."
        );

        searchField.setPrefixComponent(
                VaadinIcon.SEARCH.create()
        );

        searchField.setWidth("350px");

        searchField.getStyle()

                .set("background", "white")

                .set("border-radius", "12px")

                .set("box-shadow",
                        "0 2px 8px rgba(0,0,0,0.08)");

        searchField.addValueChangeListener(event -> {

            grid.setItems(

                    designationService.searchDesignation(
                            event.getValue()
                    )
            );
        });

        HorizontalLayout toolbar =
                new HorizontalLayout(
                        searchField
                );

        toolbar.setWidthFull();

        add(
                headerLayout,
                toolbar,
                grid
        );

        refreshGrid();
    }

    private void configureForm(){
        
        binder.forField(designationName)
            .asRequired("Designation Name is Required")
            .bind(
                DesignationDTO::getDesignationName,
                DesignationDTO::setDesignationName
            );
        binder.forField(designationCode)
            .asRequired("Designation Name is Required")
            .bind(
                DesignationDTO::getDesignationCode,
                DesignationDTO::setDesignationCode
            );
        binder.forField(isActive)
            .bind(
                DesignationDTO::getIsActive,
                DesignationDTO::setIsActive
            );
        
        binder.setBean(currentDesignation);
    }

    private void configureGrid(){

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                DesignationDTO::getDesignationName
        )
        .setHeader("Designation Name")
        .setAutoWidth(true);

        grid.addColumn(
                DesignationDTO::getDesignationCode
        )
        .setHeader("Designation Code");

        grid.addComponentColumn(designation -> {

            Span status =
                    new Span(

                            Boolean.TRUE.equals(
                                    designation.getIsActive()
                            )

                            ? "ACTIVE"

                            : "INACTIVE"
                    );

            status.getStyle()

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "700")

                    .set("color", "white")

                    .set("background",

                            Boolean.TRUE.equals(
                                    designation.getIsActive()
                            )

                            ? "#16a34a"

                            : "#dc2626"
                    );

            return status;

        }).setHeader("Status");

        grid.setWidthFull();

        grid.setHeight("600px");

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "18px")

                .set("overflow", "hidden")

                .set("box-shadow",
                        "0 6px 18px rgba(0,0,0,0.08)");

        grid.asSingleSelect()
                .addValueChangeListener(event -> {

                    if(event.getValue() != null){

                        selectedDesignation =
                                event.getValue();

                        currentDesignation =
                                selectedDesignation;

                        binder.setBean(
                                currentDesignation
                        );

                        saveButton.setText(
                                "Update Designation"
                        );

                        isEdit = true;

                        designationDialog.open();
                    }
                });
    }

    private void saveDesignation(){
        try{
            binder.writeBean(currentDesignation);

            designationService.saveDesignation(currentDesignation);

            if(isEdit){
                NotificationUtil.success("Designation updated successfully");
            }
            else{
                NotificationUtil.success("Designation saved successfully");
            }

            isEdit = !isEdit; 

            clearForm();

            refreshGrid();

            designationDialog.close();
        }
        catch(ValidationException e){
            NotificationUtil.error("Validation Failed");
        }
    } 

    private void deleteDesignation(){
        if(currentDesignation.getDesignationId() == null ){
            // Notification.show("Select a Designation first");
            NotificationUtil.warning("Select a Designation first");
            
            return;
        }

        designationService.deleteDesignation(currentDesignation.getDesignationId());
        
        // Notification.show("Designation deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        NotificationUtil.success("Designation deleted successfully");

        clearForm();

        refreshGrid();
    }

    private void clearForm() {

        currentDesignation =
                new DesignationDTO();

        selectedDesignation = null;

        binder.setBean(currentDesignation);

        designationName.clear();

        designationCode.clear();

        isActive.setValue(true);

        saveButton.setText("Save");

        isEdit = false;

        grid.deselectAll();
    }

    private void refreshGrid() {

        grid.setItems(
                designationService.getAllDesignation()
        );
    }
} 
