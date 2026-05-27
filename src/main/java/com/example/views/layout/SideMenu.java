package com.example.views.layout;

import com.example.security.SecurityService;
import com.example.views.ApprovalConfigView;
import com.example.views.ApprovalProcessView;
import com.example.views.AuditLogView;
import com.example.views.ChangePasswordView;
import com.example.views.DashboardView;
import com.example.views.DepartmentView;
import com.example.views.DesignationView;
import com.example.views.EmployeeIssuedItemsView;
import com.example.views.EmployeeView;
import com.example.views.InventoryCategoryView;
import com.example.views.InventoryManagementView;
import com.example.views.InventoryRequestView;
import com.example.views.InventoryTransactionView;
import com.example.views.IssueView;
import com.example.views.ReturnView;
import com.example.views.UserView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class SideMenu extends SideNav {

    public SideMenu(
            SecurityService securityService
    ) {

        String role =
                securityService.getAuthenticatedRole();

        setWidth("280px");

        getStyle()

                .set("background",
                        "linear-gradient(180deg, #ffffff 0%, #f8fafc 100%)")

                .set("height", "100%")

                .set("padding", "16px")

                .set("overflow", "auto")

                .set("border-right",
                        "1px solid #e2e8f0")

                .set("box-shadow",
                        "2px 0 12px rgba(0,0,0,0.05)");

        // LOGO SECTION

        Div logoContainer =
                new Div();

        logoContainer.getStyle()

                .set("display", "flex")

                .set("align-items", "center")

                .set("gap", "12px")

                .set("padding", "12px 10px 20px 10px")

                .set("margin-bottom", "14px")

                .set("border-bottom",
                        "1px solid #e2e8f0");

        Icon logoIcon =
                VaadinIcon.CUBE.create();

        logoIcon.setSize("28px");

        logoIcon.setColor("#2563eb");

        Span logoText =
                new Span("ERP Inventory");

        logoText.getStyle()

                .set("font-size", "24px")

                .set("font-weight", "700")

                .set("color", "#0f172a")

                .set("letter-spacing", "0.4px");

        logoContainer.add(
                logoIcon,
                logoText
        );

        getElement().appendChild(
                logoContainer.getElement()
        );

        // DASHBOARD

        addItem(
                createModernNavItem(
                        "Dashboard",
                        DashboardView.class,
                        VaadinIcon.DASHBOARD,
                        "#2563eb"
                )
        );

        // SUPER ADMIN

        if(role.equals("ROLE_SUPER_ADMIN")) {

            addSectionTitle("ORGANIZATION");

            addItem(
                    createModernNavItem(
                            "Departments",
                            DepartmentView.class,
                            VaadinIcon.OFFICE,
                            "#7c3aed"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Designations",
                            DesignationView.class,
                            VaadinIcon.BRIEFCASE,
                            "#06b6d4"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Employees",
                            EmployeeView.class,
                            VaadinIcon.USERS,
                            "#10b981"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Users",
                            UserView.class,
                            VaadinIcon.USER,
                            "#f59e0b"
                    )
            );

            addSectionTitle("WORKFLOW");

            addItem(
                    createModernNavItem(
                            "Approval Config",
                            ApprovalConfigView.class,
                            VaadinIcon.COGS,
                            "#ef4444"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Audit Logs",
                            AuditLogView.class,
                            VaadinIcon.COGS,
                            "#ef4444"
                    )
            );

        }

        // SUPER ADMIN + INVENTORY ADMIN

        if(role.equals("ROLE_SUPER_ADMIN")
                || role.equals("ROLE_INVENTORY_ADMIN")) {

            addSectionTitle("INVENTORY");

            addItem(
                    createModernNavItem(
                            "Inventory Categories",
                            InventoryCategoryView.class,
                            VaadinIcon.ARCHIVES,
                            "#ec4899"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Inventory Management",
                            InventoryManagementView.class,
                            VaadinIcon.STORAGE,
                            "#22c55e"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Inventory Request",
                            InventoryRequestView.class,
                            VaadinIcon.CART,
                            "#f97316"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Transactions",
                            InventoryTransactionView.class,
                            VaadinIcon.EXCHANGE,
                            "#14b8a6"
                    )
            );

            addSectionTitle("APPROVAL");

            addItem(
                    createModernNavItem(
                            "Approval Process",
                            ApprovalProcessView.class,
                            VaadinIcon.CLIPBOARD_CHECK,
                            "#6366f1"
                    )
            );

            addSectionTitle("OPERATIONS");

            addItem(
                    createModernNavItem(
                            "Issue Items",
                            IssueView.class,
                            VaadinIcon.UPLOAD_ALT,
                            "#16a34a"
                    )
            );
        }

        // MANAGER

        if(role.equals("ROLE_MANAGER")) {

            addSectionTitle("MANAGER");

            addItem(
                    createModernNavItem(
                            "Approval Process",
                            ApprovalProcessView.class,
                            VaadinIcon.CLIPBOARD_CHECK,
                            "#6366f1"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Change Password",
                            ChangePasswordView.class,
                            VaadinIcon.LOCK,
                            "#f59e0b"
                    )
            );
        }

        // EMPLOYEE

        if(role.equals("ROLE_EMPLOYEE")) {

            addSectionTitle("EMPLOYEE");

            addItem(
                    createModernNavItem(
                            "Inventory Request",
                            InventoryRequestView.class,
                            VaadinIcon.CART,
                            "#2563eb"
                    )
            );

        //     addItem(
        //             createModernNavItem(
        //                     "My Issued Items",
        //                     EmployeeIssuedItemsView.class,
        //                     VaadinIcon.PACKAGE,
        //                     "#10b981"
        //             )
        //     );

            addItem(
                    createModernNavItem(
                            "Return Items",
                            ReturnView.class,
                            VaadinIcon.DOWNLOAD_ALT,
                            "#dc2626"
                    )
            );

            addItem(
                    createModernNavItem(
                            "Change Password",
                            ChangePasswordView.class,
                            VaadinIcon.LOCK,
                            "#f59e0b"
                    )
            );
        }


        if(role.equals("ROLE_INVENTORY_ADMIN")) {

            addSectionTitle("INVENTORY ADMIN");

            addItem(
                    createModernNavItem(
                            "Change Password",
                            ChangePasswordView.class,
                            VaadinIcon.LOCK,
                            "#f59e0b"
                    )
            );
        }


    }

    private SideNavItem createModernNavItem(

            String label,

            Class<? extends Component> navigationTarget,

            VaadinIcon icon,

            String color
    ) {

        SideNavItem item =
                new SideNavItem(
                        label,
                        navigationTarget
                );

        Icon navIcon = icon.create();

        navIcon.setSize("18px");

        navIcon.setColor(color);

        item.setPrefixComponent(navIcon);

        item.getStyle()

                .set("border-radius", "14px")

                .set("margin-bottom", "10px")

                .set("padding", "12px 14px")

                .set("font-size", "16px")

                .set("font-weight", "600")

                .set("color", "#1e293b")

                .set("background", "transparent")

                .set("transition", "all 0.25s ease")

                .set("cursor", "pointer");


        return item;
    }

    private void addSectionTitle(
            String title
    ) {

        Span section =
                new Span(title);

        section.getStyle()

                .set("color", "#64748b")

                .set("font-size", "12px")

                .set("font-weight", "700")

                .set("letter-spacing", "1px")

                .set("margin-top", "18px")

                .set("margin-bottom", "10px")

                .set("padding-left", "12px")

                .set("display", "block");

        getElement().appendChild(
                section.getElement()
        );
    }
}