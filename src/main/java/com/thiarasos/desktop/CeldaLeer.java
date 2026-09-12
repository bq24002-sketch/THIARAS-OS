package com.thiarasos.desktop;

import com.thiarasos.persistencia.NotificacionPendiente;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

import java.util.function.LongConsumer;

final class CeldaLeer
        extends TableCell<NotificacionPendiente, NotificacionPendiente> {

    private final Button leer = new Button("Marcar leida");

    CeldaLeer(LongConsumer alMarcarLeida) {

        leer.getStyleClass().add("boton-leer");

        leer.setOnAction(evento -> {

            NotificacionPendiente notificacion = getItem();

            if (notificacion == null) {
                return;
            }

            alMarcarLeida.accept(notificacion.id());
        });
    }

    @Override
    protected void updateItem(
            NotificacionPendiente notificacion,
            boolean vacia
    ) {
        super.updateItem(notificacion, vacia);

        if (vacia || notificacion == null) {
            setGraphic(null);
            return;
        }

        setGraphic(leer);
    }
}