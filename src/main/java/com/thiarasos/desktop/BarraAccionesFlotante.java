package com.thiarasos.desktop;

import com.thiarasos.persistencia.ActividadInterfaz;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.function.LongConsumer;

final class BarraAccionesFlotante extends HBox {

    private final Button iniciar = new Button();
    private final Button completar = new Button();
    private final Button editar = new Button();
    private final Button diario = new Button();

    private final LongConsumer alIniciar;
    private final LongConsumer alCompletar;
    private final LongConsumer alEditar;
    private final LongConsumer alDiario;

    private SVGPath crearIcono(String contenido) {

        SVGPath icono = new SVGPath();

        icono.setContent(contenido);

        icono.setFill(
            Color.web("#E9D5FF")
        );

        return icono;
        }

    private final ChangeListener<Boolean> hoverListener =
            (observable, anterior, actual) -> {
                if (actual) {
                    mostrar();
                }
            };

    BarraAccionesFlotante(
            LongConsumer alIniciar,
            LongConsumer alCompletar,
            LongConsumer alEditar,
            LongConsumer alDiario
    ) {

        this.alIniciar = alIniciar;
        this.alCompletar = alCompletar;
        this.alEditar = alEditar;
        this.alDiario = alDiario;

        setSpacing(5);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(4, 6, 4, 6));
        setPrefWidth(164);
        setPrefHeight(38);

        setMinWidth(164);
        setMinHeight(38);

        setMaxWidth(164);
        setMaxHeight(38);

        setManaged(false);

        setStyle("""
            -fx-background-color: rgba(28, 13, 42, 0.97);
            -fx-background-radius: 10;
            -fx-border-color: rgba(184, 111, 255, 0.55);
            -fx-border-radius: 10;
            -fx-border-width: 1;
            -fx-effect:
                dropshadow(
                    gaussian,
                    rgba(0, 0, 0, 0.50),
                    14,
                    0.25,
                    0,
                    4
                );
        """);

        configurarBoton(
                iniciar,
                "boton-iniciar",
                "Iniciar actividad"
        );

        configurarBoton(
                completar,
                "boton-completar",
                "Completar actividad"
        );

        configurarBoton(
                editar,
                "boton-editar",
                "Editar actividad"
        );

        configurarBoton(
                diario,
                "boton-diario",
                "Abrir diario"
        );

        iniciar.setGraphic(
                crearIcono(
                        "M 2 1 L 13 7 L 2 13 Z"
                )
        );

        completar.setGraphic(
                crearIcono(
                        "M 1 7 L 5 11 L 13 2 L 11 1 L 5 8 L 3 6 Z"
                )
        );

        editar.setGraphic(
                crearIcono(
                        "M 2 11 L 3 7 L 10 0 L 14 4 L 7 11 Z"
                )
        );

        diario.setGraphic(
                crearIcono(
                        "M 2 1 L 12 1 L 12 13 L 2 13 Z"
                )
        );

        getChildren().addAll(
                iniciar,
                completar,
                editar,
                diario
        );

        iniciar.setOnAction(
                evento -> ejecutar(
                        alIniciar
                )
        );

        completar.setOnAction(
                evento -> ejecutar(
                        alCompletar
                )
        );

        editar.setOnAction(
                evento -> ejecutar(
                        alEditar
                )
        );

        diario.setOnAction(
                evento -> ejecutar(
                        alDiario
                )
        );

        setMouseTransparent(false);
        setOnMouseEntered(
        evento -> mostrar()
        );

        setOnMouseExited(
                evento -> ocultar()
        );

        setOpacity(0);
        setTranslateX(18);
        setScaleX(0.88);
        setScaleY(0.88);
    }

    private void ejecutar(
            LongConsumer accion
    ) {

        ActividadInterfaz actividad =
                getActividad();

        if (actividad == null) {
            return;
        }

        accion.accept(
                actividad.id()
        );
    }

    private ActividadInterfaz actividadActual;

    void mostrarPara(
            ActividadInterfaz actividad
    ) {

        this.actividadActual = actividad;

        iniciar.setDisable(
                "EN_PROGRESO".equals(
                        actividad.estado()
                )
        );

        completar.setDisable(
                !"EN_PROGRESO".equals(
                        actividad.estado()
                )
        );

        editar.setDisable(false);
        diario.setDisable(false);

        mostrar();
    }

    void ocultar() {

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(120),
                        this
                );

        fade.setFromValue(
                getOpacity()
        );

        fade.setToValue(0);

        TranslateTransition translate =
                new TranslateTransition(
                        Duration.millis(120),
                        this
                );

        translate.setFromX(
                getTranslateX()
        );

        translate.setToX(18);

        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(120),
                        this
                );

        scale.setFromX(
                getScaleX()
        );

        scale.setFromY(
                getScaleY()
        );

        scale.setToX(0.88);
        scale.setToY(0.88);

        new ParallelTransition(
                fade,
                translate,
                scale
        ).play();
    }

    private void mostrar() {

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(170),
                        this
                );

        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition translate =
                new TranslateTransition(
                        Duration.millis(190),
                        this
                );

        translate.setFromX(18);
        translate.setToX(0);

        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(190),
                        this
                );

        scale.setFromX(0.88);
        scale.setFromY(0.88);

        scale.setToX(1);
        scale.setToY(1);

        new ParallelTransition(
                fade,
                translate,
                scale
        ).play();
    }

    private ActividadInterfaz getActividad() {
        return actividadActual;
    }

    private void configurarBoton(
            Button boton,
            String clase,
            String descripcion
    ) {

        boton.getStyleClass().add(
                clase
        );

        boton.setMinSize(34, 30);
        boton.setPrefSize(34, 30);
        boton.setMaxSize(34, 30);

        Tooltip tooltip =
                new Tooltip(descripcion);

        tooltip.setShowDelay(
                Duration.millis(350)
        );

        boton.setTooltip(tooltip);
    }
}