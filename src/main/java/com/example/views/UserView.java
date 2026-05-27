package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.EmployeeDTO;
import com.example.dto.RoleDTO;
import com.example.dto.UserDTO;
import com.example.service.UserService;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users")
@RolesAllowed({
        "SUPER_ADMIN"
})
public class UserView extends VerticalLayout {

    private final UserService userService;

    private final Grid<UserDTO> grid = new Grid<>(UserDTO.class, false);

    private final BeanValidationBinder<UserDTO> binder = new BeanValidationBinder<>(UserDTO.class);

    private UserDTO currentUser = new UserDTO();

    private final ComboBox<EmployeeDTO> employeeId = new ComboBox<>("Employee");

    private final ComboBox<RoleDTO> roleId = new ComboBox<>("Role");

    private final TextField username = new TextField("Username");

    private final Checkbox isActive = new Checkbox("Active");

    private final TextField searchField = new TextField();

    private final Dialog userDialog =
        new Dialog();

    private final Button openDialogButton =
            new Button(VaadinIcon.PLUS.create());

    private UserDTO selectedUser;

    private boolean isEdit = false;

    Button saveButton = new Button("Create User");




    public UserView(UserService userService) {

        this.userService = userService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        getStyle()

                .set("background", "#f4f7fb")

                .set("padding", "24px");

        H2 heading =
                new H2("User Management");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "34px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span subHeading =
                new Span(
                        "Manage ERP system users and access permissions"
                );

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

        openDialogButton.setText("New User");

        openDialogButton.setIcon(
                VaadinIcon.PLUS.create()
        );

        openDialogButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

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

            userDialog.open();
        });

        HorizontalLayout headerLayout =
                new HorizontalLayout(
                        headingSection,
                        openDialogButton
                );

        headerLayout.setWidthFull();

        headerLayout.expand(headingSection);

        headerLayout.setAlignItems(
                Alignment.CENTER
        );

        configureForm();

        configureGrid();

        FormLayout formLayout =
                new FormLayout();

        username.setReadOnly(true);

        formLayout.add(
                employeeId,
                roleId,
                username,
                isActive
        );

        formLayout.setResponsiveSteps(

                new FormLayout.ResponsiveStep("0", 1),

                new FormLayout.ResponsiveStep("800px", 2)
        );

        formLayout.getStyle()
                .set("padding", "10px");

        saveButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY
        );

        saveButton.getStyle()

                .set("background",
                        "linear-gradient(135deg,#2563eb,#1d4ed8)")

                .set("border-radius", "12px")

                .set("font-weight", "600");

        Button clearButton =
                new Button("Clear");

        clearButton.getStyle()
                .set("border-radius", "12px");

        Button deleteButton =
                new Button("Delete");

        deleteButton.addThemeVariants(
                ButtonVariant.LUMO_ERROR
        );

        deleteButton.getStyle()
                .set("border-radius", "12px");

        saveButton.addClickListener(
                event -> saveUser()
        );

        clearButton.addClickListener(
                event -> clearForm()
        );

        deleteButton.addClickListener(event -> {

            ConfirmDialogUtil.showConfirmDialog(

                    "Delete",

                    "Are you sure you want to delete the user?",

                    this::deleteUser
            );
        });

        Button cancelButton =
                new Button("Cancel");

        cancelButton.addThemeVariants(
                ButtonVariant.LUMO_ERROR
        );

        cancelButton.getStyle()
                .set("border-radius", "12px");

        cancelButton.addClickListener(event -> {

            clearForm();

            userDialog.close();
        });

        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        saveButton,
                        // clearButton,
                        // deleteButton,
                        cancelButton
                );

        VerticalLayout dialogLayout =
                new VerticalLayout(
                        formLayout,
                        buttonLayout
                );

        dialogLayout.setWidth("900px");

        dialogLayout.getStyle()

                .set("background", "white")

                .set("border-radius", "20px");

        userDialog.add(dialogLayout);

        userDialog.setHeaderTitle(
                "User Management"
        );

        userDialog.setModal(true);

        userDialog.setDraggable(true);

        userDialog.setResizable(true);

        userDialog.setWidth("1000px");

        searchField.setPlaceholder(
                "Search User..."
        );

        searchField.setPrefixComponent(
                VaadinIcon.SEARCH.create()
        );

        searchField.setWidth("350px");

        searchField.getStyle()

                .set("background", "white")

                .set("border-radius", "14px")

                .set("margin-bottom", "10px");

        searchField.addValueChangeListener(event -> {

            grid.setItems(

                    userService.searchUsers(
                            event.getValue()
                    )
            );
        });

        employeeId.addValueChangeListener(event -> {

            if(event.getValue() != null) {

                username.setValue(
                        event.getValue().getEmail()
                );
            }
        });

        add(
                headerLayout,
                searchField,
                grid
        );

        refreshGrid();
    }

    private void configureForm() {

        employeeId.setItems(
                userService.getAvailableEmployees()
        );

        employeeId.setItemLabelGenerator(
                employee ->
                        employee.getEmployeeName()
                                + " - "
                                + employee.getEmail()
        );

        roleId.setItems(
                userService.getAllRoles()
        );

        roleId.setItemLabelGenerator(
                RoleDTO::getRoleName
        );

        employeeId.setWidthFull();

        roleId.setWidthFull();

        username.setWidthFull();

        employeeId.getStyle()
                .set("border-radius", "12px");

        roleId.getStyle()
                .set("border-radius", "12px");

        username.getStyle()
                .set("border-radius", "12px");

        binder.forField(employeeId)
                .bind(

                        employee -> {

                            if(employee.getEmployeeId() == null) {

                                return null;
                            }

                            EmployeeDTO dto =
                                    new EmployeeDTO();

                            dto.setEmployeeId(
                                    employee.getEmployeeId()
                            );

                            dto.setEmployeeName(
                                    employee.getEmployeeName()
                            );

                            dto.setEmail(
                                    employee.getEmail()
                            );

                            return dto;
                        },

                        (user, employee) -> {

                            if(employee != null) {

                                user.setEmployeeId(
                                        employee.getEmployeeId()
                                );

                                user.setEmployeeName(
                                        employee.getEmployeeName()
                                );

                                user.setEmail(
                                        employee.getEmail()
                                );

                                user.setUsername(
                                        employee.getEmail()
                                );
                            }
                        }
                );

        binder.forField(roleId)
                .bind(

                        role -> {

                            if(role.getRoleId() == null) {

                                return null;
                            }

                            RoleDTO dto =
                                    new RoleDTO();

                            dto.setRoleId(
                                    role.getRoleId()
                            );

                            dto.setRoleName(
                                    role.getRoleName()
                            );

                            return dto;
                        },

                        (user, role) -> {

                            if(role != null) {

                                user.setRoleId(
                                        role.getRoleId()
                                );

                                user.setRoleName(
                                        role.getRoleName()
                                );
                            }
                        }
                );

        binder.forField(username)

                .asRequired(
                        "User Name is required"
                )

                .bind(
                        UserDTO::getUsername,
                        UserDTO::setUsername
                );

        binder.forField(isActive)
                .bind(
                        UserDTO::getIsActive,
                        UserDTO::setIsActive
                );

        binder.setBean(currentUser);
    }

    private void configureGrid() {

        grid.addThemeVariants(

                GridVariant.LUMO_ROW_STRIPES,

                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(
                UserDTO::getEmployeeName
        )
        .setHeader("Employee")
        .setAutoWidth(true);

        grid.addColumn(
                UserDTO::getUsername
        )
        .setHeader("Username");

        grid.addColumn(
                UserDTO::getRoleName
        )
        .setHeader("Role");

        grid.addComponentColumn(user -> {

            Span statusBadge =
                    new Span(

                            Boolean.TRUE.equals(
                                    user.getIsActive()
                            )

                            ? "ACTIVE"

                            : "INACTIVE"
                    );

            if(Boolean.TRUE.equals(user.getIsActive())) {

                statusBadge.getStyle()

                        .set("background", "#dcfce7")

                        .set("color", "#15803d");

            } else {

                statusBadge.getStyle()

                        .set("background", "#fee2e2")

                        .set("color", "#dc2626");
            }

            statusBadge.getStyle()

                    .set("padding", "6px 14px")

                    .set("border-radius", "20px")

                    .set("font-size", "12px")

                    .set("font-weight", "700");

            return statusBadge;

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

                    if(event.getValue() != null) {

                        selectedUser =
                                event.getValue();

                        currentUser =
                                selectedUser;

                        binder.setBean(currentUser);

                        username.setValue(
                                currentUser.getUsername()
                        );

                        EmployeeDTO selectedEmployee =
                                userService
                                        .getAvailableEmployees()
                                        .stream()

                                        .filter(employee ->

                                                employee.getEmployeeId()
                                                        .equals(
                                                                currentUser.getEmployeeId()
                                                        )
                                        )

                                        .findFirst()
                                        .orElse(null);

                        employeeId.setValue(
                                selectedEmployee
                        );

                        RoleDTO selectedRole =
                                userService
                                        .getAllRoles()
                                        .stream()

                                        .filter(role ->

                                                role.getRoleId()
                                                        .equals(
                                                                currentUser.getRoleId()
                                                        )
                                        )

                                        .findFirst()
                                        .orElse(null);

                        roleId.setValue(
                                selectedRole
                        );

                        saveButton.setText(
                                "Update User"
                        );

                        isEdit = true;

                        userDialog.open();
                    }
                });
    }

    private void saveUser() {

        try {

            binder.writeBean(currentUser);

            UserDTO savedUser = userService.saveUser(currentUser);

            // Notification.show("User created successfully.\nPassword : " + savedUser.getGeneratedPassword())
            //         .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            if(isEdit){
                NotificationUtil.success("User updated successfully.");
            }
            else{
                NotificationUtil.success("User created successfully.\nPassword : " + savedUser.getGeneratedPassword());
            }

            isEdit = !isEdit;

            clearForm();

            refreshGrid();

            userDialog.close();

        } catch (ValidationException e) {
            // Notification.show("Validation failed").addThemeVariants(NotificationVariant.LUMO_ERROR);
            NotificationUtil.error("Validation Failed");

        } catch (Exception e) {
            // Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            NotificationUtil.success(e.getMessage());
        }
    }

    private void deleteUser() {

        if(currentUser.getUserId() == null) {

            Notification.show("Select user first");

            return;
        }

        userService.deleteUser(currentUser.getUserId());

        // Notification.show("User deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        NotificationUtil.success("User deleted successfully");

        clearForm();

        refreshGrid();
    }

    private void clearForm() {

        currentUser =
                new UserDTO();

        selectedUser = null;

        binder.setBean(currentUser);

        saveButton.setText(
                "Create User"
        );

        employeeId.clear();

        roleId.clear();

        username.clear();

        isActive.setValue(true);

        isEdit = false;

        grid.deselectAll();
    }

    private void refreshGrid() {
        grid.setItems(userService.getAllUsers());
    }
}