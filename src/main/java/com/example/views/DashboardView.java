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

            SecurityService securityService,

            EmployeeRepository employeeRepository,

            DashboardService dashboardService
    ) {

        this.employeeRepository =
                employeeRepository;

        this.dashboardService =
                dashboardService;

        setSizeFull();

        setPadding(false);

        setSpacing(false);

        getStyle()

                .set("background",
                        "linear-gradient(135deg, #f8fafc 0%, #eef2ff 100%)")

                .set("padding", "28px")

                .set("overflow", "auto");

        DashboardStatsDTO stats =
                dashboardService.getDashboardStats();

        String role =
                securityService.getAuthenticatedRole();

        Employee employee =
        employeeRepository.findByEmail(
                securityService.getAuthenticatedUser()
        );

        String displayName =
                employee != null
                        ? employee.getEmployeeName()
                        : securityService.getAuthenticatedUser();

        // HEADER SECTION

        HorizontalLayout topSection =
                new HorizontalLayout();

        topSection.setWidthFull();

        topSection.setAlignItems(
                Alignment.CENTER
        );

        topSection.setJustifyContentMode(
                JustifyContentMode.BETWEEN
        );

        VerticalLayout leftHeader =
                new VerticalLayout();

        leftHeader.setPadding(false);

        leftHeader.setSpacing(false);

        H1 heading =
                new H1("ERP Dashboard");

        heading.getStyle()

                .set("margin", "0")

                .set("font-size", "42px")

                .set("font-weight", "800")

                .set("color", "#0f172a")

                .set("letter-spacing", "-1px");

        Span subtitle =
                new Span(
                        "Smart inventory tracking and workflow insights"
                );

        subtitle.getStyle()

                .set("font-size", "16px")

                .set("color", "#64748b")

                .set("margin-top", "6px");

        leftHeader.add(
                heading,
                subtitle
        );

        leftHeader.setPadding(true);

        leftHeader.setSpacing(true);



        HorizontalLayout userCard =
                new HorizontalLayout();

        userCard.setAlignItems(
                Alignment.CENTER
        );

        userCard.setSpacing(true);

        userCard.getStyle()

                .set("height", "80px")

                .set("background",
                        "rgba(255,255,255,0.92)")

                .set("padding",
                        "12px 18px")

                .set("border-radius",
                        "18px")

                .set("border",
                        "1px solid rgba(255,255,255,0.7)")

                .set("backdrop-filter",
                        "blur(14px)")

                .set("box-shadow",
                        "0 14px 40px rgba(15,23,42,0.08)");

        Div avatar =
                new Div();

        avatar.setText(
                displayName
                        .substring(0, 1)
                        .toUpperCase()
        );

        avatar.getStyle()

                .set("width", "80px")

                .set("height", "60px")

                .set("display", "flex")

                .set("align-items", "center")

                .set("justify-content", "center")

                .set("font-size", "30px")

                .set("font-weight", "800")

                .set("color", "white")

                .set("border-radius", "50%")

                .set("background",
                        "linear-gradient(135deg, #2563eb, #7c3aed)")

                .set("box-shadow",
                        "0 10px 24px rgba(79,70,229,0.35)");

        VerticalLayout userInfo =
                new VerticalLayout();

        userInfo.setPadding(true);

        userInfo.setSpacing(true);

        Span welcomeText =
                new Span(
                        displayName
                );

        welcomeText.getStyle()

                .set("font-size", "20px")

                .set("font-weight", "700")

                .set("color", "#0f172a");

        Span roleText =
                new Span(
                        role.replace("ROLE_", "")
                );

        roleText.getStyle()

                .set("font-size", "14px")

                .set("font-weight", "600")

                .set("padding", "4px 22px")

                .set("border-radius", "30px")

                .set("background", "#dbeafe")

                .set("color", "#1d4ed8")

                .set("width", "fit-content");

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

        

        VerticalLayout dashboardContent =
                new VerticalLayout();

        dashboardContent.setPadding(false);

        dashboardContent.setSpacing(true);

        dashboardContent.getStyle()
                .set("margin-top", "30px");

        HorizontalLayout row1 =
                new HorizontalLayout();

        row1.setWidthFull();

        row1.setSpacing(true);

        HorizontalLayout row2 =
                new HorizontalLayout();

        row2.setWidthFull();

        row2.setSpacing(true);

        

        if(role.equals("ROLE_EMPLOYEE")) {

            row1.add(

                    createCard(
                            "Total Requests",
                            String.valueOf(
                                    stats.getTotalRequests()
                            ),
                            VaadinIcon.CLIPBOARD_TEXT,
                            "#2563eb",
                            "#dbeafe"
                    ),

                    createCard(
                            "Draft Requests",
                            String.valueOf(
                                    stats.getDraftRequests()
                            ),
                            VaadinIcon.EDIT,
                            "#7c3aed",
                            "#ede9fe"
                    ),

                    createCard(
                            "Pending Requests",
                            String.valueOf(
                                    stats.getPendingRequests()
                            ),
                            VaadinIcon.CLOCK,
                            "#ea580c",
                            "#ffedd5"
                    )
            );

            row2.add(

                    createCard(
                            "Approved Requests",
                            String.valueOf(
                                    stats.getApprovedRequests()
                            ),
                            VaadinIcon.CHECK_CIRCLE,
                            "#059669",
                            "#d1fae5"
                    ),

                    createCard(
                            "Issued Requests",
                            String.valueOf(
                                    stats.getIssuedRequests()
                            ),
                            VaadinIcon.PACKAGE,
                            "#0891b2",
                            "#cffafe"
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
                            "#dc2626",
                            "#fee2e2"
                    ),

                    createCard(
                            "Approved Requests",
                            String.valueOf(
                                    stats.getApprovedRequests()
                            ),
                            VaadinIcon.CHECK,
                            "#059669",
                            "#d1fae5"
                    ),

                    createCard(
                            "Pending Requests",
                            String.valueOf(
                                    stats.getPendingRequests()
                            ),
                            VaadinIcon.TIME_BACKWARD,
                            "#ea580c",
                            "#ffedd5"
                    )
            );

            row2.add(

                    createCard(
                            "Issued Requests",
                            String.valueOf(
                                    stats.getIssuedRequests()
                            ),
                            VaadinIcon.PACKAGE,
                            "#2563eb",
                            "#dbeafe"
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
                            VaadinIcon.CLIPBOARD_CHECK,
                            "#dc2626",
                            "#fee2e2"
                    ),

                    createCard(
                            "Approved Requests",
                            String.valueOf(
                                    stats.getApprovedRequests()
                            ),
                            VaadinIcon.CHECK_CIRCLE,
                            "#059669",
                            "#d1fae5"
                    ),

                    createCard(
                            "Issued Requests",
                            String.valueOf(
                                    stats.getIssuedRequests()
                            ),
                            VaadinIcon.TRUCK,
                            "#0891b2",
                            "#cffafe"
                    )
            );

            row2.add(

                    createCard(
                            "Low Stock Items",
                            String.valueOf(
                                    stats.getLowStockItems()
                            ),
                            VaadinIcon.WARNING,
                            "#ea580c",
                            "#ffedd5"
                    ),

                    createCard(
                            "Out Of Stock",
                            String.valueOf(
                                    stats.getOutOfStockItems()
                            ),
                            VaadinIcon.CLOSE_CIRCLE,
                            "#b91c1c",
                            "#fee2e2"
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
                            "#2563eb",
                            "#dbeafe"
                    ),

                    createCard(
                            "Inventory Items",
                            String.valueOf(
                                    stats.getTotalItems()
                            ),
                            VaadinIcon.PACKAGE,
                            "#7c3aed",
                            "#ede9fe"
                    ),

                    createCard(
                            "Total Requests",
                            String.valueOf(
                                    stats.getTotalRequests()
                            ),
                            VaadinIcon.CLIPBOARD_TEXT,
                            "#059669",
                            "#d1fae5"
                    )
            );

            row2.add(

                    createCard(
                            "Pending Approvals",
                            String.valueOf(
                                    stats.getPendingApprovals()
                            ),
                            VaadinIcon.WARNING,
                            "#ea580c",
                            "#ffedd5"
                    ),

                    createCard(
                            "Low Stock",
                            String.valueOf(
                                    stats.getLowStockItems()
                            ),
                            VaadinIcon.STOCK,
                            "#0891b2",
                            "#cffafe"
                    ),

                    createCard(
                            "Out Of Stock",
                            String.valueOf(
                                    stats.getOutOfStockItems()
                            ),
                            VaadinIcon.CLOSE_CIRCLE,
                            "#dc2626",
                            "#fee2e2"
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

    private VerticalLayout createCard(

            String title,

            String value,

            VaadinIcon icon,

            String color,

            String background
    ) {

        Icon topIcon =
                icon.create();

        topIcon.setSize("26px");

        topIcon.setColor(color);

        Div iconWrapper = new Div(topIcon);

        iconWrapper.getStyle()

                .set("width", "58px")

                .set("height", "58px")

                .set("display", "flex")

                .set("align-items", "center")

                .set("justify-content", "center")

                .set("border-radius", "16px")

                .set("background", background);

        Span titleText =
                new Span(title);

        titleText.getStyle()

                .set("font-size", "16px")

                .set("font-weight", "600")

                .set("color", "#64748b");

        Span valueText =
                new Span(value);

        valueText.getStyle()

                .set("font-size", "48px")

                .set("font-weight", "800")

                .set("line-height", "1")

                .set("color", "#0f172a");

        Span analytics =
                new Span("Live ERP Statistics");

        analytics.getStyle()

                .set("font-size", "13px")

                .set("font-weight", "500")

                .set("color", color)

                .set("background", background)

                .set("padding", "6px 12px")

                .set("border-radius", "30px")

                .set("width", "fit-content");

        VerticalLayout card = new VerticalLayout();

        card.setWidthFull();

        card.setHeight("220px");

        card.setSpacing(true);

        card.setPadding(false);

        card.setAlignItems(Alignment.START);

        card.getStyle()

                .set("background",
                        "rgba(255,255,255,0.8)")

                .set("backdrop-filter",
                        "blur(12px)")

                .set("border-radius", "24px")

                .set("padding", "24px")

                .set("border",
                        "1px solid rgba(255,255,255,0.7)")

                .set("box-shadow",
                        "0 12px 40px rgba(15,23,42,0.08)")

                .set("transition",
                        "all 0.3s ease")

                .set("overflow", "hidden")

                .set("position", "relative");

        // card.getElement().executeJs("""

        //     this.addEventListener('mouseenter', () => {
        //         this.style.transform='translateY(-6px)';
        //         this.style.boxShadow='0 18px 45px rgba(37,99,235,0.18)';
        //     });

        //     this.addEventListener('mouseleave', () => {
        //         this.style.transform='translateY(0px)';
        //         this.style.boxShadow='0 12px 40px rgba(15,23,42,0.08)';
        //     });

        // """);

        card.add(
                iconWrapper,
                titleText,
                valueText,
                analytics
        );

        return card;
    }
}