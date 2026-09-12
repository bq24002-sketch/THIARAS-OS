package com.thiarasos.desktop;

import com.thiarasos.persistencia.ActividadInterfaz;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.util.function.LongConsumer;

import java.util.function.Consumer;


final class CeldaAcciones
        extends TableCell<ActividadInterfaz, ActividadInterfaz> {

    private final Button iniciar = new Button("Iniciar");
    private final Button completar = new Button("Completar");
    private final Button editar = new Button("Editar");

    private final HBox contenido =
            new HBox(6, iniciar, completar, editar);

    CeldaAcciones(
            LongConsumer alIniciar,
            LongConsumer alCompletar,
            LongConsumer alEditar
    ) {

        iniciar.getStyleClass().add("boton-iniciar");
        completar.getStyleClass().add("boton-completar");
        editar.getStyleClass().add("boton-editar");

        iniciar.setOnAction(evento -> {

            ActividadInterfaz actividad = getItem();

            if (actividad == null) {
                return;
            }

            alIniciar.accept(actividad.id());
        });

        completar.setOnAction(evento -> {

            ActividadInterfaz actividad = getItem();

            if (actividad == null) {
                return;
            }

            alCompletar.accept(actividad.id());
        });

        editar.setOnAction(evento -> {

            ActividadInterfaz actividad = getItem();

            if (actividad == null) {
                return;
            }

            alEditar.accept(actividad.id());
        });

        contenido.setAlignment(Pos.CENTER);
    }

    @Override
    protected void updateItem(
            ActividadInterfaz actividad,
            boolean vacia
    ) {

        super.updateItem(actividad, vacia);

        if (vacia || actividad == null) {
            setGraphic(null);
            return;
        }

        iniciar.setDisable(
                "EN_PROGRESO".equals(actividad.estado())
        );

        completar.setDisable(
                !"EN_PROGRESO".equals(actividad.estado())
        );

        editar.setDisable(false);

        setGraphic(contenido);
    }
}