package com.thiarasos.desktop;

import com.thiarasos.persistencia.ActividadInterfaz;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.function.LongConsumer;

final class CeldaAcciones
        extends TableCell<ActividadInterfaz, ActividadInterfaz> {

    private final Button iniciar =
            new Button("▶");

    private final Button completar =
            new Button("✓");

    private final Button editar =
            new Button("✎");

    private final Button diario =
            new Button("▤");

    private final HBox contenido =
            new HBox(
                    5,
                    iniciar,
                    completar,
                    editar,
                    diario
            );

    private ParallelTransition animacionEntrada;
    private ParallelTransition animacionSalida;

    private TableRow<ActividadInterfaz> filaActual;

    CeldaAcciones(
            LongConsumer alIniciar,
            LongConsumer alCompletar,
            LongConsumer alEditar,
            LongConsumer alAbrirDiario
    ) {

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

        configurarAcciones(
                alIniciar,
                alCompletar,
                alEditar,
                alAbrirDiario
        );

        contenido.setAlignment(
                Pos.CENTER
        );

        contenido.setPadding(
                new Insets(4, 6, 4, 6)
        );

        contenido.setStyle("""
            -fx-background-color: rgba(28, 13, 42, 0.96);
            -fx-background-radius: 10;
            -fx-border-color: rgba(184, 111, 255, 0.45);
            -fx-border-radius: 10;
            -fx-border-width: 1;
            -fx-effect:
                dropshadow(
                    gaussian,
                    rgba(0, 0, 0, 0.45),
                    12,
                    0.25,
                    0,
                    3
                );
        """);

        contenido.setOpacity(0);
        contenido.setTranslateX(18);
        contenido.setScaleX(0.88);
        contenido.setScaleY(0.88);

        crearAnimaciones();

        setGraphic(contenido);
    }

    private void configurarBoton(
            Button boton,
            String estilo,
            String tooltip
    ) {

        boton.getStyleClass().add(estilo);
            boton.setStyle(
            "-fx-font-family: 'Segoe UI Symbol';"
            + "-fx-font-size: 15px;"
            + "-fx-font-weight: bold;"
        );

        boton.setMinSize(34, 30);
        boton.setPrefSize(34, 30);
        boton.setMaxSize(34, 30);

        Tooltip informacion =
                new Tooltip(tooltip);

        informacion.setShowDelay(
                Duration.millis(350)
        );

        boton.setTooltip(informacion);
    }


    private final javafx.beans.value.ChangeListener<Boolean>
        listenerHoverFila =
        (observable, anterior, actual) -> {

            if (actual) {
                mostrarAcciones();
            } else {
                ocultarAcciones();
            }
        };

    private void configurarAcciones(
            LongConsumer alIniciar,
            LongConsumer alCompletar,
            LongConsumer alEditar,
            LongConsumer alAbrirDiario
    ) {

        iniciar.setOnAction(evento -> {

            ActividadInterfaz actividad =
                    getItem();

            if (actividad == null) {
                return;
            }

            alIniciar.accept(
                    actividad.id()
            );
        });

        completar.setOnAction(evento -> {

            ActividadInterfaz actividad =
                    getItem();

            if (actividad == null) {
                return;
            }

            alCompletar.accept(
                    actividad.id()
            );
        });

        editar.setOnAction(evento -> {

            ActividadInterfaz actividad =
                    getItem();

            if (actividad == null) {
                return;
            }

            alEditar.accept(
                    actividad.id()
            );
        });

        diario.setOnAction(evento -> {

            ActividadInterfaz actividad =
                    getItem();

            if (actividad == null) {
                return;
            }

            alAbrirDiario.accept(
                    actividad.id()
            );
        });
    }

    private void crearAnimaciones() {

        FadeTransition aparecer =
                new FadeTransition(
                        Duration.millis(170),
                        contenido
                );

        aparecer.setFromValue(0);
        aparecer.setToValue(1);


        TranslateTransition deslizar =
                new TranslateTransition(
                        Duration.millis(190),
                        contenido
                );

        deslizar.setFromX(18);
        deslizar.setToX(0);


        ScaleTransition crecer =
                new ScaleTransition(
                        Duration.millis(190),
                        contenido
                );

        crecer.setFromX(0.88);
        crecer.setFromY(0.88);

        crecer.setToX(1);
        crecer.setToY(1);


        animacionEntrada =
                new ParallelTransition(
                        aparecer,
                        deslizar,
                        crecer
                );


        FadeTransition desaparecer =
                new FadeTransition(
                        Duration.millis(130),
                        contenido
                );

        desaparecer.setFromValue(1);
        desaparecer.setToValue(0);


        TranslateTransition regresar =
                new TranslateTransition(
                        Duration.millis(130),
                        contenido
                );

        regresar.setFromX(0);
        regresar.setToX(18);


        ScaleTransition encoger =
                new ScaleTransition(
                        Duration.millis(130),
                        contenido
                );

        encoger.setFromX(1);
        encoger.setFromY(1);

        encoger.setToX(0.88);
        encoger.setToY(0.88);


        animacionSalida =
                new ParallelTransition(
                        desaparecer,
                        regresar,
                        encoger
                );
    }

    private void mostrarAcciones() {

        if (animacionSalida != null) {
            animacionSalida.stop();
        }

        contenido.setMouseTransparent(false);

        animacionEntrada.playFromStart();
    }

    private void ocultarAcciones() {

        if (animacionEntrada != null) {
            animacionEntrada.stop();
        }

        contenido.setMouseTransparent(true);

        animacionSalida.playFromStart();
    }

    @Override
    protected void updateItem(
            ActividadInterfaz actividad,
            boolean vacia
    ) {

        super.updateItem(
                actividad,
                vacia
        );

        if (vacia || actividad == null) {

            setGraphic(null);

            desconectarFila();

            return;
        }

        setGraphic(contenido);

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

        conectarFila();
    }

    private void conectarFila() {

        TableRow<ActividadInterfaz>
                nuevaFila =
                getTableRow();

        if (nuevaFila == null) {
            return;
        }

        if (filaActual == nuevaFila) {
            return;
        }

        desconectarFila();

        filaActual = nuevaFila;

        filaActual.hoverProperty()
                .addListener(listenerHoverFila);
                

        if (filaActual.isHover()) {
            mostrarAcciones();
        } else {
                
                ocultarAcciones();
            }
        }
                

    private void desconectarFila() {

        if (filaActual != null) {
            filaActual.hoverProperty()
                    .removeListener(listenerHoverFila);
        }

        filaActual = null;

        if (contenido != null) {
            contenido.setOpacity(0);
            contenido.setTranslateX(18);
            contenido.setScaleX(0.88);
            contenido.setScaleY(0.88);
        }
    }
}