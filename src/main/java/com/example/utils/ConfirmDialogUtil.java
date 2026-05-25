package com.example.utils;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class ConfirmDialogUtil {

    public static void showConfirmDialog(

            String title,

            String message,

            Runnable confirmAction
    ) {

        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader(title);

        dialog.setText(message);

        dialog.setCancelable(true);

        dialog.setConfirmText("Confirm");

        dialog.setCancelText("Cancel");

        dialog.addConfirmListener(event -> confirmAction.run());

        dialog.open();
    }
}
