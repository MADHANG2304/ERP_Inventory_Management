package com.example.views;

import com.example.dto.ChangePasswordDTO;
import com.example.service.ChangePasswordService;
import com.example.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

import com.example.security.SecurityService;

@Route(value = "change-password", layout = MainLayout.class)
@PageTitle("Change Password")
@PermitAll
public class ChangePasswordView
                extends VerticalLayout {

        private final ChangePasswordService changePasswordService;

        private final SecurityService securityService;

        private final String username;

        private final PasswordField oldPassword = new PasswordField(
                        "Old Password");

        private final PasswordField newPassword = new PasswordField(
                        "New Password");

        private final PasswordField confirmPassword = new PasswordField(
                        "Confirm Password");

        private final Span passwordValidationMessage =
                new Span();

        private final Span confirmPasswordMessage =
                new Span();

        public ChangePasswordView(

                        ChangePasswordService changePasswordService,

                        SecurityService securityService) {

                this.changePasswordService = changePasswordService;

                this.securityService = securityService;

                this.username = securityService.getAuthenticatedUser();

                setSizeFull();

                setPadding(true);

                setSpacing(true);

                setAlignItems(Alignment.CENTER);

                setJustifyContentMode(
                                JustifyContentMode.CENTER);

                getStyle()

                                .set("background",
                                                "linear-gradient(135deg, #eef2ff 0%, #f8fafc 100%)")

                                .set("padding",
                                                "40px");


                H2 heading = new H2("Change Password");

                heading.getStyle()

                                .set("margin", "0")

                                .set("font-size", "42px")

                                .set("font-weight", "800")

                                .set("letter-spacing", "-1px")

                                .set("color", "#0f172a");

                Paragraph subtitle = new Paragraph("Secure your ERP account with a strong password");

                subtitle.getStyle()

                                .set("margin-top", "6px")

                                .set("margin-bottom", "20px")

                                .set("font-size", "16px")

                                .set("color", "#64748b");

                // PASSWORD FIELDS

                oldPassword.setWidthFull();

                newPassword.setWidthFull();

                confirmPassword.setWidthFull();

                oldPassword.setPlaceholder(
                                "Enter current password");

                newPassword.setPlaceholder(
                                "Enter new password");

                confirmPassword.setPlaceholder(
                                "Re-enter new password");

                oldPassword.getStyle()
                                .set("margin-bottom", "10px");

                newPassword.getStyle()
                                .set("margin-bottom", "10px");

                confirmPassword.getStyle()
                                .set("margin-bottom", "10px");

                passwordValidationMessage.getStyle()

                        .set("font-size", "13px")

                        .set("color", "#dc2626")

                        .set("margin-top", "-6px")

                        .set("margin-bottom", "8px");

                confirmPasswordMessage.getStyle()

                        .set("font-size", "13px")

                        .set("color", "#dc2626")

                        .set("margin-top", "-6px")

                        .set("margin-bottom", "8px");

                
                newPassword.addValueChangeListener(event -> {

                        validatePassword();
                });

                confirmPassword.addValueChangeListener(event -> {

                        validateConfirmPassword();
                });

                FormLayout formLayout = new FormLayout();

                formLayout.add(

                               oldPassword,

                                newPassword,

                                passwordValidationMessage,

                                confirmPassword,

                                confirmPasswordMessage
                );

                formLayout.setResponsiveSteps(

                                new FormLayout.ResponsiveStep(
                                                "0", 1));

                formLayout.setWidthFull();

                // UPDATE BUTTON

                Button changePasswordButton = new Button(
                                "Update Password");

                changePasswordButton.addThemeVariants(
                                ButtonVariant.LUMO_PRIMARY);

                changePasswordButton.getStyle()

                                .set("background",
                                                "linear-gradient(135deg, #2563eb, #4f46e5)")

                                .set("color", "white")

                                .set("font-weight", "700")

                                .set("padding",
                                                "12px 26px")

                                .set("border-radius",
                                                "12px")

                                .set("box-shadow",
                                                "0 8px 22px rgba(37,99,235,0.35)");

                changePasswordButton
                                .addClickListener(event -> updatePassword());

                // CLEAR BUTTON

                Button clearButton = new Button("Clear");

                clearButton.addThemeVariants(
                                ButtonVariant.LUMO_TERTIARY);

                clearButton.getStyle()

                                .set("font-weight", "600")

                                .set("padding",
                                                "12px 22px")

                                .set("border-radius",
                                                "12px");

                clearButton.addClickListener(event -> clearFields());

                // CANCEL BUTTON

                Button cancelButton = new Button("Cancel");

                cancelButton.addThemeVariants(
                                ButtonVariant.LUMO_ERROR);

                cancelButton.getStyle()

                                .set("font-weight", "700")

                                .set("padding",
                                                "12px 24px")

                                .set("border-radius",
                                                "12px");

                cancelButton.addClickListener(event -> {

                        clearFields();

                        UI.getCurrent().navigate("");
                });

                HorizontalLayout buttonLayout = new HorizontalLayout(
                                changePasswordButton,
                                // clearButton,
                                cancelButton);

                buttonLayout.setSpacing(true);

                // MAIN CARD

                VerticalLayout card = new VerticalLayout(
                                heading,
                                subtitle,
                                formLayout,
                                buttonLayout);

                card.setWidth("520px");

                card.setPadding(true);

                card.setSpacing(true);

                card.setAlignItems(Alignment.STRETCH);

                card.getStyle()

                                .set("background",
                                                "rgba(255,255,255,0.88)")

                                .set("backdrop-filter",
                                                "blur(16px)")

                                .set("border-radius",
                                                "28px")

                                .set("padding",
                                                "36px")

                                .set("border",
                                                "1px solid rgba(255,255,255,0.6)")

                                .set("box-shadow",
                                                "0 20px 45px rgba(15,23,42,0.08)");

                add(card);
        }

        private void updatePassword() {

                try {

                        ChangePasswordDTO dto = new ChangePasswordDTO();

                        dto.setOldPassword(oldPassword.getValue());

                        dto.setNewPassword(newPassword.getValue());

                        dto.setConfirmPassword(confirmPassword.getValue());

                        changePasswordService.changePassword(username,dto);

                        NotificationUtil.success("Password updated successfully");

                        clearFields();

                } catch (Exception e) {

                        NotificationUtil.error(e.getMessage());
                }
        }

        private void clearFields() {

                oldPassword.clear();

                newPassword.clear();

                confirmPassword.clear();

                passwordValidationMessage.setText("");

                confirmPasswordMessage.setText("");
        }

        private void validatePassword() {

                String password = newPassword.getValue();

                List<String> errors = new ArrayList<>();

                if(password.length() < 6) {

                        errors.add("Minimum 6 characters");
                }

                else if(!password.matches(".*[A-Z].*")) {

                        errors.add("One uppercase letter required");
                }

                else if(!password.matches(".*[a-z].*")) {

                        errors.add("One lowercase letter required");
                }

                else if(!password.matches(".*[0-9].*")) {

                        errors.add("One number required");
                }

                else if(!password.matches(".*[@#$].*")) {

                        errors.add("One special character required");
                }

                if(errors.isEmpty()) {

                        passwordValidationMessage.setText(
                                "Strong password"
                        );

                        passwordValidationMessage
                                .getStyle()
                                .set("color", "#16a34a");

                } else {

                        passwordValidationMessage.setText(
                                String.join(" | ", errors)
                        );

                        passwordValidationMessage
                                .getStyle()
                                .set("color", "#dc2626");
                }
        }

        private void validateConfirmPassword() {

                if(confirmPassword.getValue()
                        .equals(newPassword.getValue())) {

                        confirmPasswordMessage.setText(
                                "Passwords matched"
                        );

                        confirmPasswordMessage
                                .getStyle()
                                .set("color", "#16a34a");

                } else {

                        confirmPasswordMessage.setText(
                                "Passwords do not match"
                        );

                        confirmPasswordMessage
                                .getStyle()
                                .set("color", "#dc2626");
                }
        }

}