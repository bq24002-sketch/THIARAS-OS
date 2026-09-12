package com.thiarasos.desktop;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.function.Function;

final class ColumnasTabla {

    private ColumnasTabla() {
    }

    static <T> TableColumn<T, String> crear(
            String nombre,
            Function<T, String> valor,
            double proporcion
    ) {
        TableColumn<T, String> columna =
                new TableColumn<>(nombre);

        columna.setCellValueFactory(celda -> {

            T elemento = celda.getValue();

            String resultado =
                    elemento == null
                            ? ""
                            : valor.apply(elemento);

            return new javafx.beans.property.SimpleStringProperty(
                    resultado == null ? "" : resultado
            );
        });

        columna.setPrefWidth(
                700 * proporcion
        );

        return columna;
    }

    static <T> void configurarTabla(
            TableView<T> tabla,
            String mensaje
    ) {
        tabla.setPlaceholder(
                new javafx.scene.control.Label(mensaje)
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
    }
}