package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.DepartmentDTO;
import com.example.dto.DesignationDTO;
import com.example.dto.EmployeeDTO;
import com.example.service.EmployeeService;
import com.example.utils.ConfirmDialogUtil;
import com.example.utils.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "employees", layout = MainLayout.class)
@PageTitle("Employees")
@RolesAllowed({
        "SUPER_ADMIN",
        "INVENTORY_ADMIN"
})
public class EmployeeView extends VerticalLayout {

    private final EmployeeService employeeService;

    private final Grid<EmployeeDTO> grid = new Grid<>(EmployeeDTO.class, false);

    private final BeanValidationBinder<EmployeeDTO> binder = new BeanValidationBinder<>(EmployeeDTO.class);

    private EmployeeDTO currentEmployee = new EmployeeDTO();

    private final ComboBox<DepartmentDTO> departmentId = new ComboBox<>("Department");

    private final ComboBox<DesignationDTO> designationId = new ComboBox<>("Designation");

    private final TextField employeeName = new TextField("Employee Name");

    private final TextField mobileNumber = new TextField("Mobile Number");

    private final EmailField email = new EmailField("Email");

    private final ComboBox<String> gender = new ComboBox<>("Gender");

    private final TextField state = new TextField("State");

    private final TextField city = new TextField("City");

    private final Checkbox isActive = new Checkbox("Active");

    private final TextField searchField = new TextField();

    Button saveButton = new Button("Save");

    private final Dialog employeeDialog = new Dialog();

    private final Button openDialogButton = new Button(VaadinIcon.PLUS.create());

    private EmployeeDTO selectedEmployee;

    private Boolean isEdit = false;


    public EmployeeView(EmployeeService employeeService) {

        this.employeeService = employeeService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()
                .set("background", "#f4f7fb")
                .set("padding", "20px");

        H2 heading = new H2("Employee Management");

        heading.getStyle()
                .set("margin", "0")
                .set("font-size", "32px")
                .set("font-weight", "700")
                .set("color", "#1e293b");

        Span subHeading = new Span("Manage employee details, departments and designations");

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

        openDialogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        openDialogButton.setText("Add Employee");

        openDialogButton.setIcon(VaadinIcon.PLUS.create());

        openDialogButton.getStyle()
                .set("border-radius", "10px")
                .set("height", "42px")
                .set("font-weight", "600")
                .set("background",
                        "linear-gradient(135deg,#2563eb,#1d4ed8)");

        openDialogButton.addClickListener(event -> {

            clearForm();

            employeeDialog.open();
        });

        HorizontalLayout headerLayout =
                new HorizontalLayout(
                        headingLayout,
                        openDialogButton
                );

        headerLayout.setWidthFull();

        headerLayout.setAlignItems(Alignment.CENTER);

        headerLayout.expand(headingLayout);

        configureForm();

        configureGrid();

        FormLayout formLayout = new FormLayout();

        formLayout.add(
                departmentId,
                designationId,
                employeeName,
                mobileNumber,
                email,
                gender,
                state,
                city,
                isActive
        );

        formLayout.setResponsiveSteps(

                new FormLayout.ResponsiveStep("0", 1),

                new FormLayout.ResponsiveStep("900px", 2)
        );

        formLayout.getStyle()
                .set("padding", "10px");

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.getStyle()
                .set("border-radius", "10px")
                .set("font-weight", "600");

        Button clearButton = new Button("Clear");

        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        clearButton.getStyle()
                .set("border-radius", "10px");

        Button deleteButton = new Button("Delete");

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        deleteButton.getStyle()
                .set("border-radius", "10px");

        saveButton.addClickListener(event -> saveEmployee());

        clearButton.addClickListener(event -> clearForm());

        deleteButton.addClickListener(event -> {

            ConfirmDialogUtil.showConfirmDialog(

                    "Delete",

                    "Are you sure you want to delete?",

                    this::deleteEmployee
            );
        });

        Button cancelButton = new Button("Cancel");

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        cancelButton.getStyle()
                .set("border-radius", "10px");

        cancelButton.addClickListener(event -> {

            clearForm();

            employeeDialog.close();
        });

        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        saveButton,
                        clearButton,
                        deleteButton,
                        cancelButton
                );

        searchField.setPlaceholder("Search employees...");

        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());

        searchField.setWidth("350px");

        searchField.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow",
                        "0 2px 8px rgba(0,0,0,0.08)");

        searchField.addValueChangeListener(event -> {

        grid.setItems(employeeService.searchEmployees(event.getValue()));
        });

        HorizontalLayout toolbar = new HorizontalLayout(searchField);

        toolbar.setWidthFull();

        VerticalLayout dialogLayout =
                new VerticalLayout(

                        formLayout,

                        buttonLayout
                );

        dialogLayout.getStyle()
                .set("background", "white")
                .set("border-radius", "18px")
                .set("padding", "20px");

        employeeDialog.add(dialogLayout);

        employeeDialog.setHeaderTitle("Employee Details");

        employeeDialog.setModal(true);

        employeeDialog.setDraggable(true);

        employeeDialog.setResizable(true);

        employeeDialog.setWidth("1000px");

        employeeDialog.setHeight("510px");

        add(
                headerLayout,
                toolbar,
                grid
        );

        refreshGrid();
    }

    private void configureForm() {

        departmentId.setItems(employeeService.getAllDepartments());

        departmentId.setItemLabelGenerator(DepartmentDTO::getDepartmentName);

        designationId.setItems(employeeService.getAllDesignations());

        designationId.setItemLabelGenerator(DesignationDTO::getDesignationName);

        gender.setItems(
                "Male",
                "Female",
                "Other"
        );

        binder.forField(departmentId)
                .bind(
                        employee -> {

                            if(employee.getDepartmentId() == null) {
                                return null;
                            }

                            DepartmentDTO dto = new DepartmentDTO();

                            dto.setDepartmentId(employee.getDepartmentId());

                            dto.setDepartmentName(employee.getDepartmentName());

                            return dto;
                        },

                        // (BEAN_OBJECT, FIELD_VALUE) :-
                        (employee, department) -> {

                            if(department != null) {

                                employee.setDepartmentId(department.getDepartmentId());

                                employee.setDepartmentName(department.getDepartmentName());
                            }
                        }
                );

        binder.forField(designationId)
                .bind(
                        employee -> {

                            if(employee.getDesignationId() == null) {
                                return null;
                            }

                            DesignationDTO dto = new DesignationDTO();

                            dto.setDesignationId(employee.getDesignationId());

                            dto.setDesignationName(employee.getDesignationName());

                            return dto;
                        },

                        (employee, designation) -> {

                            if(designation != null) {

                                employee.setDesignationId(designation.getDesignationId());

                                employee.setDesignationName(designation.getDesignationName());
                            }
                        }
                );

        binder.forField(employeeName)
        .bind(
            EmployeeDTO::getEmployeeName,
            EmployeeDTO::setEmployeeName
        );

        binder.forField(mobileNumber)
        .bind(
            EmployeeDTO::getMobileNumber,
            EmployeeDTO::setMobileNumber
        );

        binder.forField(email)
        .bind(
            EmployeeDTO::getEmail,
            EmployeeDTO::setEmail
        );

        binder.forField(gender)
        .bind(
            EmployeeDTO::getGender,
            EmployeeDTO::setGender
        );

        binder.forField(state)
        .bind(
            EmployeeDTO::getState,
            EmployeeDTO::setState
        );

        binder.forField(city)
        .bind(
            EmployeeDTO::getCity,
            EmployeeDTO::setCity
        );

        binder.forField(isActive)
        .bind(
            EmployeeDTO::getIsActive,
            EmployeeDTO::setIsActive
        );

        binder.setBean(currentEmployee);
    }

    private void configureGrid() {

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(EmployeeDTO::getEmployeeName)
                .setHeader("Employee")
                .setAutoWidth(true);

        grid.addColumn(EmployeeDTO::getEmail)
                .setHeader("Email");

        grid.addColumn(EmployeeDTO::getMobileNumber)
                .setHeader("Mobile");

        grid.addColumn(EmployeeDTO::getDepartmentName)
                .setHeader("Department");

        grid.addColumn(EmployeeDTO::getDesignationName)
                .setHeader("Designation");

        grid.addColumn(EmployeeDTO::getGender)
                .setHeader("Gender");

        grid.addComponentColumn(employee -> {

            Span status =
                    new Span(
                        Boolean.TRUE.equals(employee.getIsActive()) ? "ACTIVE" : "INACTIVE"
                    );

            status.getStyle()

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "700")

                    .set("color", "white")

                    .set("background",

                            Boolean.TRUE.equals(
                                    employee.getIsActive()
                            )

                            ? "#16a34a"

                            : "#dc2626"
                    );

            return status;

        }).setHeader("Status");

        grid.setHeight("600px");

        grid.setWidthFull();

        grid.getStyle()

                .set("background", "white")

                .set("border-radius", "18px")

                .set("overflow", "hidden")

                .set("box-shadow",
                        "0 6px 18px rgba(0,0,0,0.08)");

        grid.asSingleSelect()
                .addValueChangeListener(event -> {

                    if(event.getValue() != null) {

                        selectedEmployee = event.getValue();

                        currentEmployee = selectedEmployee;

                        binder.setBean(currentEmployee);

                        DepartmentDTO selectedDepartment = employeeService
                                .getAllDepartments()
                                .stream()
                                .filter(department -> department.getDepartmentId().equals(currentEmployee.getDepartmentId()))
                                .findFirst()
                                .orElse(null);

                        departmentId.setValue(selectedDepartment);

                        DesignationDTO selectedDesignation = employeeService
                                .getAllDesignations()
                                .stream()
                                .filter(designation -> designation.getDesignationId().equals(currentEmployee.getDesignationId()))
                                .findFirst()
                                .orElse(null);

                        designationId.setValue(selectedDesignation);

                        saveButton.setText("Update");

                        isEdit = true;

                        employeeDialog.open();
                    }
                });
    }

    private void saveEmployee() {

        try {

            binder.writeBean(currentEmployee);

            employeeService.saveEmployee(currentEmployee);
             
            if(isEdit){
                NotificationUtil.success("Employee updated successfully");
            }
            else{
                NotificationUtil.success("Employee saved successfully");
            }
            
            isEdit = !isEdit;

            clearForm();

            refreshGrid();

            employeeDialog.close();

        } catch (ValidationException e) {

            NotificationUtil.error("Validation failed");

        } catch (Exception e) {

            NotificationUtil.error(e.getMessage());
        }
    }

    private void deleteEmployee() {

        if(currentEmployee.getEmployeeId() == null) {
            NotificationUtil.warning("Select an employee first");

            return;
        }

        employeeService.deleteEmployee(currentEmployee.getEmployeeId());

        NotificationUtil.success("Employee deleted Successfully");

        clearForm();

        refreshGrid();
    }

    private void clearForm() {

        currentEmployee = new EmployeeDTO();

        selectedEmployee = null;

        binder.setBean(currentEmployee);

        saveButton.setText("Save");

        departmentId.clear();

        designationId.clear();

        employeeName.clear();

        mobileNumber.clear();

        email.clear();

        gender.clear();

        state.clear();

        city.clear();

        isActive.setValue(true);

        isEdit = false;

        grid.deselectAll();
    }

    private void refreshGrid() {

        grid.setItems(employeeService.getAllEmployees());
    }
}