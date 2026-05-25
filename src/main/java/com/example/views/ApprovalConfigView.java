package com.example.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.base.ui.MainLayout;
import com.example.dto.ApprovalConfigDTO;
import com.example.dto.ApprovalConfigLevelDTO;
import com.example.enums.ApprovalRole;
import com.example.enums.RequestType;
import com.example.service.ApprovalConfigService;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "approval-config", layout = MainLayout.class)
@PageTitle("Approval Config")
@RolesAllowed("SUPER_ADMIN")
public class ApprovalConfigView extends VerticalLayout {

    private final ApprovalConfigService
            approvalConfigService;

    private final Grid<ApprovalConfigDTO>
            configGrid =
            new Grid<>(ApprovalConfigDTO.class, false);

    private final Grid<ApprovalConfigLevelDTO>
            levelGrid =
            new Grid<>(ApprovalConfigLevelDTO.class, false);

    private final TextField configName =
            new TextField("Config Name");

    private final ComboBox<RequestType>
            requestType =
            new ComboBox<>("Request Type");

    private final Checkbox isActive =
            new Checkbox("Active");

    private final IntegerField approvalOrder =
            new IntegerField("Approval Order");

    private final ComboBox<ApprovalRole>
            approvalRole =
            new ComboBox<>("Approval Role");

    private final TextField searchField =
            new TextField();

    private final List<ApprovalConfigLevelDTO>
            levelList =
            new ArrayList<>();

    private ApprovalConfigDTO currentConfig =
            new ApprovalConfigDTO();

    Button saveButton =
                new Button("Save Config");

    private Boolean isEdit = false;

    private final Dialog configDialog =
        new Dialog();

    private final Button openDialogButton =
        new Button(
                VaadinIcon.PLUS.create()
        );

    private final Span selectedFlowInfo =
        new Span("No workflow selected");



        

        public ApprovalConfigView(
        ApprovalConfigService approvalConfigService
                ) {

                this.approvalConfigService =
                        approvalConfigService;

                setWidthFull();

                setPadding(true);

                setSpacing(true);

                getStyle()

                        .set("background", "#f4f7fb")

                        .set("padding", "24px")

                        .set("overflow", "auto");

                H2 heading =
                        new H2("Approval Workflow Engine");

                heading.getStyle()

                        .set("margin", "0")

                        .set("font-size", "34px")

                        .set("font-weight", "700")

                        .set("color", "#0f172a");

                Span subHeading =
                        new Span(
                                "Manage approval routing and workflow hierarchy"
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

                configureForm();

                configureLevelGrid();

                configureConfigGrid();

                openDialogButton.addThemeVariants(
                        ButtonVariant.LUMO_PRIMARY
                );

                openDialogButton.getStyle()

                        .set("margin-left", "auto")

                        .set("border-radius", "14px")

                        .set("height", "42px")

                        .set("width", "42px")

                        .set("box-shadow",
                                "0 4px 12px rgba(37,99,235,0.25)");

                openDialogButton.addClickListener(event -> {

                        clearForm();

                        configDialog.open();
                });

                HorizontalLayout headerLayout =
                        new HorizontalLayout(
                                headingSection,
                                openDialogButton
                        );

                headerLayout.setWidthFull();

                headerLayout.setAlignItems(
                        Alignment.CENTER
                );

                searchField.setPlaceholder(
                        "Search Workflow Config..."
                );

                searchField.setPrefixComponent(
                        VaadinIcon.SEARCH.create()
                );

                searchField.setWidth("350px");

                searchField.getStyle()

                        .set("background", "white")

                        .set("border-radius", "12px");

                searchField.addValueChangeListener(event -> {

                        configGrid.setItems(
                                approvalConfigService
                                        .searchConfigs(
                                                event.getValue()
                                        )
                        );
                });

                FormLayout formLayout =
                        new FormLayout();

                formLayout.add(
                        configName,
                        requestType,
                        isActive
                );

                formLayout.setResponsiveSteps(

                        new FormLayout.ResponsiveStep(
                                "0",
                                1
                        ),

                        new FormLayout.ResponsiveStep(
                                "700px",
                                2
                        )
                );

                HorizontalLayout levelForm =
                        new HorizontalLayout(
                                approvalOrder,
                                approvalRole
                        );

                levelForm.setWidthFull();

                levelForm.setFlexGrow(
                        1,
                        approvalOrder,
                        approvalRole
                );

                Button addLevelButton =
                        new Button(
                                "Add Level",
                                VaadinIcon.PLUS.create()
                        );

                addLevelButton.addThemeVariants(
                        ButtonVariant.LUMO_PRIMARY
                );

                addLevelButton.getStyle()

                        .set("border-radius", "12px")

                        .set("font-weight", "600");

                addLevelButton.addClickListener(
                        event -> addLevel()
                );

                saveButton.addThemeVariants(
                        ButtonVariant.LUMO_SUCCESS
                );

                saveButton.getStyle()

                        .set("border-radius", "12px")

                        .set("font-weight", "600");

                saveButton.addClickListener(
                        event -> saveConfig()
                );

                Button clearButton =
                        new Button(
                                "Clear",
                                VaadinIcon.ERASER.create()
                        );

                clearButton.addThemeVariants(
                        ButtonVariant.LUMO_CONTRAST
                );

                clearButton.getStyle()

                        .set("border-radius", "12px");

                clearButton.addClickListener(
                        event -> clearForm()
                );

                Button deleteButton =
                        new Button(
                                "Delete",
                                VaadinIcon.TRASH.create()
                        );

                deleteButton.addThemeVariants(
                        ButtonVariant.LUMO_ERROR
                );

                deleteButton.getStyle()

                        .set("border-radius", "12px");

                deleteButton.addClickListener(event -> {

                        ConfirmDialogUtil.showConfirmDialog(

                                "Delete",

                                "Are you sure you want to delete?",

                                this::deleteConfig
                        );
                });

                Button cancelButton =
                        new Button(
                                "Cancel"
                        );

                cancelButton.addThemeVariants(
                        ButtonVariant.LUMO_ERROR
                );

                cancelButton.getStyle()

                        .set("border-radius", "12px");

                cancelButton.addClickListener(event -> {

                        clearForm();

                        configDialog.close();
                });

                HorizontalLayout actionButtons =
                        new HorizontalLayout(
                                saveButton,
                                clearButton,
                                deleteButton,
                                cancelButton
                        );

                VerticalLayout levelSection =
                        new VerticalLayout(

                                levelForm,

                                addLevelButton,

                                levelGrid
                        );

                levelSection.setPadding(false);

                levelSection.setSpacing(true);

                VerticalLayout dialogLayout =
                        new VerticalLayout(

                                formLayout,

                                levelSection,

                                actionButtons
                        );

                dialogLayout.setWidth("950px");

                dialogLayout.setPadding(true);

                dialogLayout.setSpacing(true);

                configDialog.add(dialogLayout);

                configDialog.setHeaderTitle(
                        "Approval Workflow Configuration"
                );

                configDialog.setModal(true);

                configDialog.setDraggable(true);

                configDialog.setResizable(true);

                configDialog.setWidth("1000px");

                configDialog.setHeight("700px");

                configGrid.getStyle()

                        .set("background", "white")

                        .set("border-radius", "18px")

                        .set("overflow", "hidden")

                        .set("box-shadow",
                                "0 6px 18px rgba(0,0,0,0.08)");

                add(
                        headerLayout,
                        searchField,
                        configGrid
                );

                refreshGrid();
        }

    private void configureForm() {

        requestType.setItems(
                RequestType.values()
        );

        approvalRole.setItems(
                ApprovalRole.values()
        );

        approvalOrder.setMin(1);

        isActive.setValue(true);
    }

    private void configureLevelGrid() {

                levelGrid.addThemeVariants(

                        GridVariant.LUMO_ROW_STRIPES,

                        GridVariant.LUMO_COLUMN_BORDERS
                );

                levelGrid.addComponentColumn(level -> {

                        Span orderBadge =
                                new Span(
                                        "LEVEL " +
                                        level.getApprovalOrder()
                                );

                        orderBadge.getStyle()

                                .set("background", "#dbeafe")

                                .set("color", "#2563eb")

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-weight", "700")

                                .set("font-size", "12px");

                        return orderBadge;

                }).setHeader("Approval Level");

                levelGrid.addComponentColumn(level -> {

                        Span roleBadge =
                                new Span(
                                        level.getApprovalRole()
                                                .name()
                                );

                        roleBadge.getStyle()

                                .set("background", "#ede9fe")

                                .set("color", "#6d28d9")

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-weight", "700")

                                .set("font-size", "12px");

                        return roleBadge;

                }).setHeader("Approval Role");

                levelGrid.addComponentColumn(level -> {

                        Button removeButton =
                                new Button(
                                        VaadinIcon.TRASH.create()
                                );

                        removeButton.addThemeVariants(
                                ButtonVariant.LUMO_ERROR
                        );

                        removeButton.getStyle()

                                .set("border-radius", "10px");

                        removeButton.addClickListener(event -> {

                        levelList.remove(level);

                        refreshLevelGrid();
                        });

                        return removeButton;
                });

                levelGrid.setHeight("220px");

                levelGrid.setWidthFull();

                levelGrid.getStyle()

                        .set("border-radius", "16px")

                        .set("overflow", "hidden")

                        .set("box-shadow",
                                "0 4px 12px rgba(0,0,0,0.06)");
        }

    private void configureConfigGrid() {

                configGrid.addThemeVariants(
                        GridVariant.LUMO_ROW_STRIPES
                );

                configGrid.addColumn(
                        ApprovalConfigDTO::getConfigName
                ).setHeader("Config Name");

                configGrid.addComponentColumn(config -> {

                        Span typeBadge =
                                new Span(
                                        config.getRequestType().name()
                                );

                        typeBadge.getStyle()

                                .set("padding", "6px 14px")

                                .set("border-radius", "999px")

                                .set("font-size", "13px")

                                .set("font-weight", "700")

                                .set("color", "white")

                                .set("text-transform", "uppercase");

                        switch (config.getRequestType()) {

                        case LOW_VALUE ->

                                typeBadge.getStyle()
                                        .set("background", "#02ed58");

                        case HIGH_VALUE ->

                                typeBadge.getStyle()
                                        .set("background", "#ec7d42");

                        case BULK_REQUEST ->

                                typeBadge.getStyle()
                                        .set("background", "#ea3939");
                        }

                        return typeBadge;

                }).setHeader("Request Type");

                // configGrid.addColumn(config ->

                //         Boolean.TRUE.equals(
                //                 config.getIsActive()
                //         )

                //                 ? "ACTIVE"

                //                 : "INACTIVE"

                // ).setHeader("Status");

                configGrid.addComponentColumn(config -> {

                        boolean active =
                                Boolean.TRUE.equals(
                                        config.getIsActive()
                                );

                        Span statusBadge =
                                new Span(
                                        active
                                                ? "ACTIVE"
                                                : "INACTIVE"
                                );

                        statusBadge.getStyle()

                                .set("background",
                                        active
                                                ? "#dcfce7"
                                                : "#fee2e2")

                                .set("color",
                                        active
                                                ? "#166534"
                                                : "#991b1b")

                                .set("padding", "6px 14px")

                                .set("border-radius", "20px")

                                .set("font-weight", "700")

                                .set("font-size", "12px");

                        return statusBadge;

                }).setHeader("Status");

                configGrid.addColumn(config ->

                        config.getLevels() != null

                                ? config.getLevels().size()

                                : 0

                ).setHeader("Levels");

                configGrid.setWidthFull();

                configGrid.setHeight("550px");

                configGrid.getStyle()

                        .set("background", "white")

                        .set("border-radius", "18px")

                        .set("overflow", "hidden")

                        .set("box-shadow",
                                "0 6px 18px rgba(0,0,0,0.08)");

                configGrid.asSingleSelect()
                        .addValueChangeListener(event -> {

                                if(event.getValue() != null) {

                                loadConfigToForm(
                                        event.getValue()
                                );
                                }
                        });
        }

    private void addLevel() {

        if(approvalOrder.getValue() == null
                || approvalOrder.getValue() <= 0) {

        //     showError(
        //             "Invalid approval order"
        //     );

        NotificationUtil.error("Invalid approval order");

            return;
        }

        if(approvalRole.getValue() == null) {

        //     showError(
        //             "Select approval role"
        //     );
        NotificationUtil.error("Select an approval role");

            return;
        }

        boolean duplicateOrder =
                levelList.stream()
                        .anyMatch(level ->

                                level.getApprovalOrder()
                                        .equals(
                                                approvalOrder
                                                        .getValue()
                                        )
                        );

        if(duplicateOrder) {

        //     showError(
        //             "Approval order already exists"
        //     );
        NotificationUtil.warning("Approval order already exists");

            return;
        }

        boolean duplicateRole =
                levelList.stream()
                        .anyMatch(level ->

                                level.getApprovalRole()
                                        .equals(
                                                approvalRole
                                                        .getValue()
                                        )
                        );

        if(duplicateRole) {

        //     showError(
        //             "Approval role already added"
        //     );
        NotificationUtil.warning("Approval role already added");

            return;
        }

        ApprovalConfigLevelDTO dto =
                new ApprovalConfigLevelDTO();

        dto.setApprovalOrder(
                approvalOrder.getValue()
        );

        dto.setApprovalRole(
                approvalRole.getValue()
        );

        levelList.add(dto);

        refreshLevelGrid();

        approvalOrder.clear();

        approvalRole.clear();
    }

    private void saveConfig() {

        try {

            currentConfig.setConfigName(
                    configName.getValue()
            );

            currentConfig.setRequestType(
                    requestType.getValue()
            );

            currentConfig.setIsActive(
                    isActive.getValue()
            );

            currentConfig.setLevels(
                    levelList
            );

            approvalConfigService
                    .saveConfig(currentConfig);

        //     showSuccess(
        //             "Approval config saved successfully"
        //     );
        if(isEdit){
                NotificationUtil.success("Approval config updated successfully");       
        }
        else{
                NotificationUtil.success("Approval config saved successfully");       
        }

            isEdit = !isEdit;

            clearForm();

            refreshGrid();

        } catch (Exception e) {

        //     showError(
        //             e.getMessage()
        //     );
        NotificationUtil.error(e.getMessage());
        }
    }

    private void deleteConfig() {

        if(currentConfig.getConfigId() == null) {

        //     showError(
        //             "Select config first"
        //     );

        NotificationUtil.warning("Select a config first");

            return;
        }

        approvalConfigService.deleteConfig(
                currentConfig.getConfigId()
        );

        // showSuccess(
        //         "Config deleted successfully"
        // );
        NotificationUtil.success("Config deleted successfully");

        clearForm();

        refreshGrid();
    }

    private void loadConfigToForm(
        ApprovalConfigDTO dto
                ) {

                currentConfig = dto;

                configName.setValue(
                        dto.getConfigName()
                );

                requestType.setValue(
                        dto.getRequestType()
                );

                isActive.setValue(
                        dto.getIsActive()
                );

                levelList.clear();

                levelList.addAll(
                        dto.getLevels()
                );

                refreshLevelGrid();

                saveButton.setText(
                        "Update Config"
                );

                isEdit = true;

                configDialog.open();
        }

    private void refreshLevelGrid() {

        levelGrid.setItems(
                levelList.stream()
                        .sorted(
                                Comparator.comparing(
                                        ApprovalConfigLevelDTO
                                                ::getApprovalOrder
                                )
                        )
                        .toList()
        );
    }

        private void refreshGrid() {

                        configGrid.setItems(
                                approvalConfigService
                                        .getAllConfigs()
                        );
                }

                private void clearForm() {

                currentConfig =
                        new ApprovalConfigDTO();

                saveButton.setText(
                        "Save Config"
                );

                configName.clear();

                requestType.clear();

                isActive.setValue(true);

                approvalOrder.clear();

                approvalRole.clear();

                levelList.clear();

                refreshLevelGrid();

                isEdit = false;

                configGrid.deselectAll();
        }
}