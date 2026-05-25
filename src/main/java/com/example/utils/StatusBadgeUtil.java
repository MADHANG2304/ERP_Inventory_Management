package com.example.utils;


import com.vaadin.flow.component.html.Span;

public class StatusBadgeUtil {

    public static Span createBadge(
            String status
    ) {

        Span badge =
                new Span(status);

        badge.getStyle()
                .set("padding",
                        "4px 10px");

        badge.getStyle()
                .set("border-radius",
                        "12px");

        badge.getStyle()
                .set("font-size",
                        "12px");

        badge.getStyle()
                .set("font-weight",
                        "bold");

        badge.getStyle()
                .set("color",
                        "white");

        switch (status) {

            case "APPROVED" ->
                    badge.getStyle()
                            .set("background",
                                    "#2e7d32");

            case "REJECTED" ->
                    badge.getStyle()
                            .set("background",
                                    "#d32f2f");

            case "PENDING_APPROVAL" ->
                    badge.getStyle()
                            .set("background",
                                    "#ed6c02");

            case "ISSUED" ->
                    badge.getStyle()
                            .set("background",
                                    "#1565c0");

            case "RETURNED" ->
                    badge.getStyle()
                            .set("background",
                                    "#6a1b9a");

            default ->
                    badge.getStyle()
                            .set("background",
                                    "gray");
        }

        return badge;
    }
}
