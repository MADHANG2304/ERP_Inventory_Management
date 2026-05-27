package com.example.views;

import com.example.base.ui.MainLayout;
import com.example.dto.DashboardStatsDTO;
import com.example.entity.Employee;
import com.example.repository.EmployeeRepository;
import com.example.security.SecurityService;
import com.example.service.DashboardService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard")
@RolesAllowed({
        "SUPER_ADMIN",
        "INVENTORY_ADMIN",
        "EMPLOYEE",
        "MANAGER"
})
public class DashboardView extends VerticalLayout {

    private final EmployeeRepository employeeRepository;

    private final DashboardService dashboardService;

    public DashboardView(
        SecurityService securityService, EmployeeRepository employeeRepository, DashboardService dashboardService) {

        this.employeeRepository = employeeRepository;

        this.dashboardService = dashboardService;

        setSizeFull();

        setPadding(true);

        setSpacing(true);

        setMargin(false);

        getStyle()

                .set("background", "#f8fafc")

                .set("margin", "0")

                .set("padding", "20px")

                .set("overflow", "auto");

        DashboardStatsDTO stats = dashboardService.getDashboardStats();

        String role = securityService.getAuthenticatedRole();

        Employee employee = employeeRepository.findByEmail(securityService.getAuthenticatedUser());

        String displayName =
                employee != null
                        ? employee.getEmployeeName()
                        : securityService.getAuthenticatedUser();


        HorizontalLayout topSection = new HorizontalLayout();

        topSection.setWidthFull();

        topSection.setAlignItems(Alignment.CENTER);

        topSection.setJustifyContentMode(JustifyContentMode.BETWEEN);

        topSection.getStyle().set("margin-bottom", "20px");


        VerticalLayout leftHeader = new VerticalLayout();

        leftHeader.setPadding(true);

        leftHeader.setSpacing(true);

        H1 heading = new H1("ERP Dashboard");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "38px")

                .set("font-weight", "700")

                .set("color", "#1e293b");

        Span subtitle = new Span("Inventory and workflow overview");

        subtitle.getStyle()

                .set("font-size", "15px")

                .set("color", "#64748b");

        leftHeader.add(
                heading,
                subtitle
        );

        // USER CARD

        HorizontalLayout userCard = new HorizontalLayout();

        userCard.setAlignItems(Alignment.CENTER);

        userCard.setSpacing(true);

        userCard.getStyle()

                .set("background", "white")

                .set("padding", "2px 18px")

                .set("border-radius", "12px")

                .set("border", "1px solid #e2e8f0");

        Div avatar = new Div();

        avatar.setText(
                displayName
                        .substring(0, 1)
                        .toUpperCase()
        );

        avatar.getStyle()

                .set("width", "65px")

                .set("height", "50px")

                .set("display", "flex")

                .set("align-items", "center")

                .set("justify-content", "center")

                .set("font-size", "24px")

                .set("font-weight", "700")

                .set("color", "white")

                .set("border-radius", "50%")

                .set("background", "#2563eb");

        VerticalLayout userInfo = new VerticalLayout();

        userInfo.setPadding(true);

        userInfo.setSpacing(true);

        Span welcomeText = new Span(displayName);

        welcomeText.getStyle()

                .set("font-size", "18px")

                .set("font-weight", "600")

                .set("color", "#1e293b");

        Span roleText = new Span(role.replace("ROLE_", ""));

        roleText.getStyle()

                .set("font-size", "13px")

                .set("color", "#64748b");

        userInfo.add(
                welcomeText,
                roleText
        );

        userCard.add(
                avatar,
                userInfo
        );

        topSection.add(
                leftHeader,
                userCard
        );


        VerticalLayout dashboardContent = new VerticalLayout();

        dashboardContent.setPadding(true);

        dashboardContent.setSpacing(true);

        HorizontalLayout row1 = new HorizontalLayout();

        row1.setWidthFull();

        row1.setSpacing(true);

        HorizontalLayout row2 = new HorizontalLayout();

        row2.setWidthFull();

        row2.setSpacing(true);

        // EMPLOYEE

        if(role.equals("ROLE_EMPLOYEE")) {

            row1.add(

                    createCard(
                            "Total Requests",
                            String.valueOf(
                                    stats.getTotalRequests()
                            ),
                            VaadinIcon.CLIPBOARD_TEXT,
                            "#2563eb"
                    ),

                    createCard(
                            "Draft Requests",
                            String.valueOf(
                                    stats.getDraftRequests()
                            ),
                            VaadinIcon.EDIT,
                            "#7c3aed"
                    ),

                    createCard(
                            "Pending Requests",
                            String.valueOf(
                                    stats.getPendingRequests()
                            ),
                            VaadinIcon.CLOCK,
                            "#ea580c"
                    )
            );

            row2.add(

                    createCard(
                            "Approved Requests",
                            String.valueOf(
                                    stats.getApprovedRequests()
                            ),
                            VaadinIcon.CHECK,
                            "#059669"
                    ),

                    createCard(
                            "Issued Requests",
                            String.valueOf(
                                    stats.getIssuedRequests()
                            ),
                            VaadinIcon.PACKAGE,
                            "#0891b2"
                    )
            );
        }

        // MANAGER

        else if(role.equals("ROLE_MANAGER")) {

            row1.add(

                    createCard(
                            "Pending Approvals",
                            String.valueOf(
                                    stats.getPendingApprovals()
                            ),
                            VaadinIcon.WARNING,
                            "#dc2626"
                    ),

                    createCard(
                            "Approved Requests",
                            String.valueOf(
                                    stats.getApprovedRequests()
                            ),
                            VaadinIcon.CHECK,
                            "#059669"
                    ),

                    createCard(
                            "Pending Requests",
                            String.valueOf(
                                    stats.getPendingRequests()
                            ),
                            VaadinIcon.CLOCK,
                            "#ea580c"
                    )
            );

            row2.add(

                    createCard(
                            "Issued Requests",
                            String.valueOf(
                                    stats.getIssuedRequests()
                            ),
                            VaadinIcon.PACKAGE,
                            "#2563eb"
                    )
            );
        }

        // INVENTORY ADMIN

        else if(role.equals("ROLE_INVENTORY_ADMIN")) {

            row1.add(

                    createCard(
                            "Pending Approvals",
                            String.valueOf(
                                    stats.getPendingApprovals()
                            ),
                            VaadinIcon.WARNING,
                            "#dc2626"
                    ),

                    createCard(
                            "Approved Requests",
                            String.valueOf(
                                    stats.getApprovedRequests()
                            ),
                            VaadinIcon.CHECK,
                            "#059669"
                    ),

                    createCard(
                            "Issued Requests",
                            String.valueOf(
                                    stats.getIssuedRequests()
                            ),
                            VaadinIcon.TRUCK,
                            "#0891b2"
                    )
            );

            row2.add(

                    createCard(
                            "Low Stock",
                            String.valueOf(
                                    stats.getLowStockItems()
                            ),
                            VaadinIcon.STOCK,
                            "#ea580c"
                    ),

                    createCard(
                            "Out Of Stock",
                            String.valueOf(
                                    stats.getOutOfStockItems()
                            ),
                            VaadinIcon.CLOSE_CIRCLE,
                            "#dc2626"
                    )
            );
        }

        // SUPER ADMIN

        else if(role.equals("ROLE_SUPER_ADMIN")) {

            row1.add(

                    createCard(
                            "Employees",
                            String.valueOf(
                                    stats.getTotalEmployees()
                            ),
                            VaadinIcon.USERS,
                            "#2563eb"
                    ),

                    createCard(
                            "Inventory Items",
                            String.valueOf(
                                    stats.getTotalItems()
                            ),
                            VaadinIcon.PACKAGE,
                            "#7c3aed"
                    ),

                    createCard(
                            "Total Requests",
                            String.valueOf(
                                    stats.getTotalRequests()
                            ),
                            VaadinIcon.CLIPBOARD_TEXT,
                            "#059669"
                    )
            );

            row2.add(

                    createCard(
                            "Pending Approvals",
                            String.valueOf(
                                    stats.getPendingApprovals()
                            ),
                            VaadinIcon.WARNING,
                            "#ea580c"
                    ),

                    createCard(
                            "Low Stock",
                            String.valueOf(
                                    stats.getLowStockItems()
                            ),
                            VaadinIcon.STOCK,
                            "#0891b2"
                    ),

                    createCard(
                            "Out Of Stock",
                            String.valueOf(
                                    stats.getOutOfStockItems()
                            ),
                            VaadinIcon.CLOSE_CIRCLE,
                            "#dc2626"
                    )
            );
        }

        dashboardContent.add(
                row1,
                row2
        );

        add(
                topSection,
                dashboardContent
        );
    }

    private VerticalLayout createCard(String title, String value, VaadinIcon icon, String color) {

        Icon topIcon = icon.create();

        topIcon.setSize("22px");

        topIcon.setColor(color);

        Div iconWrapper = new Div(topIcon);

        iconWrapper.getStyle()

                .set("width", "46px")

                .set("height", "46px")

                .set("display", "flex")

                .set("align-items", "center")

                .set("justify-content", "center")

                .set("border-radius", "10px")

                .set("background", "#f1f5f9");

        Span titleText = new Span(title);

        titleText.getStyle()

                .set("font-size", "15px")

                .set("font-weight", "600")

                .set("color", "#64748b");

        Span valueText = new Span(value);

        valueText.getStyle()

                .set("font-size", "36px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        VerticalLayout card = new VerticalLayout();

        card.setWidthFull();

        card.setHeight("170px");

        card.setSpacing(true);

        card.setPadding(true);

        card.setAlignItems(Alignment.START);

        card.getStyle()

                .set("background", "white")

                .set("border-radius", "14px")

                .set("padding", "18px")

                .set("border", "1px solid #e2e8f0");

        card.add(
                iconWrapper,
                titleText,
                valueText
        );

        return card;
    }
}