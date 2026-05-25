package com.example.utils;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

public class GridUtil {

    public static <T> void configureGrid(
            Grid<T> grid
    ) {

        grid.setWidthFull();

        grid.setHeight("600px");

        grid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES
        );

        grid.addThemeVariants(
                GridVariant.LUMO_COLUMN_BORDERS
        );
    }
}
