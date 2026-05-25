package com.example.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotificationUtil {

    public static void success(
            String message
    ) {

        Notification notification =
                Notification.show(message);

        notification.addThemeVariants(
                NotificationVariant.LUMO_SUCCESS
        );

        notification.setPosition(
            Notification.Position.TOP_END
        );

        notification.setDuration(2000);
    }

    public static void error(
            String message
    ) {

        Notification notification =
                Notification.show(message);

        notification.addThemeVariants(
                NotificationVariant.LUMO_ERROR
        );

        notification.setPosition(
            Notification.Position.TOP_END
        );

        notification.setDuration(2000);
    }

    public static void warning(
            String message
    ) {

        Notification notification =
                Notification.show(message);

        notification.addThemeVariants(
                NotificationVariant.LUMO_WARNING
        );

        notification.setPosition(
            Notification.Position.TOP_END
        );

        notification.setDuration(2000);
    }
}