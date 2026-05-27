package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.DepartmentDTO;
import com.example.service.DepartmentService;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
 
import jakarta.annotation.security.RolesAllowed;

@Route(value = "departments", layout = MainLayout.class)
@PageTitle("Departments")
@RolesAllowed("SUPER_ADMIN")
public class DepartmentView extends VerticalLayout{
    private final DepartmentService departmentService;

    private final Grid<DepartmentDTO> grid = new Grid<>(DepartmentDTO.class, false);

    private final BeanValidationBinder<DepartmentDTO> binder = new BeanValidationBinder<>(DepartmentDTO.class);
    
    private DepartmentDTO currentDepartment = new DepartmentDTO();

    private final TextField departmentName = new TextField("Department Name");
    private final TextField departmentCode = new TextField("Department Code");

    private final Checkbox isActive = new Checkbox("Active");

    Button saveButton = new Button("Save");

    private final Dialog departmentDialog =
        new Dialog();

    private final Button openDialogButton =
            new Button(VaadinIcon.PLUS.create());

    private final TextField searchField =
            new TextField();

    private DepartmentDTO selectedDepartment;

    private Boolean isEdit = false;

    public DepartmentView(DepartmentService departmentService){

        this.departmentService = departmentService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()
                .set("background", "#f4f7fb")
                .set("padding", "20px");

        H2 heading =
                new H2("Department Management");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "32px")

                .set("font-weight", "700")

                .set("color", "#1e293b");

        Span subHeading =
                new Span(
                        "Manage all departments and organizational structure"
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

        openDialogButton.setText("Add Department");

        openDialogButton.setIcon(
                VaadinIcon.PLUS.create()
        );

        openDialogButton.getStyle()

                .set("border-radius", "10px")

                .set("height", "42px")

                .set("font-weight", "600")

                .set("background",
                        "linear-gradient(135deg,#2563eb,#1d4ed8)");

        openDialogButton.addClickListener(event -> {

                clearForm();

                departmentDialog.open();
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
                event -> saveDepartment()
        );

        clearButton.addClickListener(
                event -> clearForm()
        );

        deleteButton.addClickListener(event -> {

                ConfirmDialogUtil.showConfirmDialog(

                        "Delete",

                        "Are you sure you want to delete?",

                        this::deleteDepartment
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

                departmentDialog.close();
        });

        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        saveButton,
                        // clearButton,
                        // deleteButton,
                        cancelButton
                );

        departmentName.setWidthFull();

        departmentCode.setWidthFull();

        VerticalLayout formSection =
                new VerticalLayout(

                        departmentName,

                        departmentCode,

                        isActive
                );

        formSection.setPadding(false);

        formSection.setSpacing(true);

        VerticalLayout dialogLayout =
                new VerticalLayout(

                        formSection,

                        buttonLayout
                );

        dialogLayout.getStyle()

                .set("background", "white")

                .set("border-radius", "18px")

                .set("padding", "20px");

        dialogLayout.setWidth("550px");

        departmentDialog.add(dialogLayout);

        departmentDialog.setHeaderTitle(
                "Department Details"
        );

        departmentDialog.setModal(true);

        departmentDialog.setDraggable(true);

        departmentDialog.setResizable(true);

        departmentDialog.setWidth("600px");

        searchField.setPlaceholder(
                "Search department..."
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

                        departmentService.searchDepartments(
                                searchField.getValue()
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
       binder.forField(departmentName)
            .asRequired("Department Name is Required")
            .bind(
                    DepartmentDTO::getDepartmentName,
                    DepartmentDTO::setDepartmentName
            );

        binder.forField(departmentCode)
                .asRequired("Department Code is Required")
                .bind(
                        DepartmentDTO::getDepartmentCode,
                        DepartmentDTO::setDepartmentCode
                );

        binder.forField(isActive)
                .bind(
                        DepartmentDTO::getIsActive,
                        DepartmentDTO::setIsActive
                );

        binder.setBean(currentDepartment);
    }

    private void configureGrid(){

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                DepartmentDTO::getDepartmentName
        )
        .setHeader("Department Name")
        .setAutoWidth(true);

        grid.addColumn(
                DepartmentDTO::getDepartmentCode
        )
        .setHeader("Department Code");

        grid.addComponentColumn(department -> {

                com.vaadin.flow.component.html.Span status =
                        new com.vaadin.flow.component.html.Span(

                                Boolean.TRUE.equals(
                                        department.getIsActive()
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
                                        department.getIsActive()
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

                        if(event.getValue() != null) {

                        selectedDepartment =
                                event.getValue();

                        loadDepartmentToForm(
                                selectedDepartment
                        );

                        departmentDialog.open();
                        }
                });
        }

    private void loadDepartmentToForm(
        DepartmentDTO department
        ) {

        currentDepartment = department;

        binder.setBean(currentDepartment);

        departmentName.setValue(
                department.getDepartmentName()
        );

        departmentCode.setValue(
                department.getDepartmentCode()
        );

        isActive.setValue(
                department.getIsActive()
        );

        saveButton.setText("Update Department");

        isEdit = true;
        }

    public void saveDepartment() {

        try {

            binder.writeBean(currentDepartment);

            departmentService.saveDepartment(
                    currentDepartment
            );

            if(isEdit) {

                NotificationUtil.success(
                        "Department updated successfully"
                );

            } else {

                NotificationUtil.success(
                        "Department saved successfully"
                );
            }

            clearForm();

            refreshGrid();

            departmentDialog.close();

        } catch (ValidationException e) {

            NotificationUtil.error(
                    "Validation Failed"
            );

        } catch (Exception e) {

            NotificationUtil.error(
                    e.getMessage()
            );
        }
    }

    private void deleteDepartment(){
        if(currentDepartment.getDepartmentId() == null ){
            NotificationUtil.warning("Select a department first");
            
            return;
        }

        departmentService.deleteDepartment(currentDepartment.getDepartmentId());
        
        NotificationUtil.success("Department deleted successfully");

        clearForm();

        refreshGrid();
    }

   private void clearForm() {

        currentDepartment =
                new DepartmentDTO();

        binder.setBean(currentDepartment);

        selectedDepartment = null;

        departmentName.clear();

        departmentCode.clear();

        isActive.setValue(true);

        saveButton.setText("Save");

        isEdit = false;

        grid.deselectAll();
}

    private void refreshGrid() {

        grid.setItems(
                departmentService.getAllDepartments()
        );
    }


}
