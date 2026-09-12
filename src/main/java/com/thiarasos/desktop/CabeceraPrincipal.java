package com.thiarasos.desktop;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

final class CabeceraPrincipal {

    private final Runnable alCrearActividad;
    private final Runnable alRefrescar;

    private double posicionInicialX;
    private double posicionInicialY;

    CabeceraPrincipal(
            Runnable alCrearActividad,
            Runnable alRefrescar
    ) {
        this.alCrearActividad = alCrearActividad;
        this.alRefrescar = alRefrescar;
    }

    HBox crear() {

        ImageView icono = new ImageView(
                new Image(
                        getClass().getResourceAsStream("/thiaras-icon.png")
                )
        );

        icono.setFitWidth(48);
        icono.setFitHeight(48);
        icono.setPreserveRatio(true);

        Label marca = new Label("THIARAS OS");
        marca.getStyleClass().add("marca");

        Label autor = new Label("Creado por Uber Barillas");
        autor.getStyleClass().add("autor");

        VBox identidadTexto = new VBox(
                1,
                marca,
                autor
        );

        HBox identidad = new HBox(
                10,
                icono,
                identidadTexto
        );

        identidad.setAlignment(Pos.CENTER_LEFT);

        Label seccion = new Label("Dashboard");
        seccion.getStyleClass().add("seccion");

        Region espacio = new Region();
        HBox.setHgrow(
                espacio,
                Priority.ALWAYS
        );

        Button nuevaActividad =
                new Button("Nueva actividad");

        nuevaActividad.getStyleClass().add("primario");

        nuevaActividad.setOnAction(
                evento -> alCrearActividad.run()
        );

        Button recargar =
                new Button("Actualizar");

        recargar.getStyleClass().add("secundario");

        recargar.setOnAction(
                evento -> alRefrescar.run()
        );

        Button minimizar =
                new Button("—");

        minimizar.getStyleClass().add("control-ventana");

        minimizar.setOnAction(evento -> {

            Stage escenario =
                    (Stage) minimizar
                            .getScene()
                            .getWindow();

            escenario.setIconified(true);
        });

        Button maximizar =
                new Button("□");

        maximizar.getStyleClass().add("control-ventana");

        maximizar.setOnAction(evento -> {

            Stage escenario =
                    (Stage) maximizar
                            .getScene()
                            .getWindow();

            escenario.setMaximized(
                    !escenario.isMaximized()
            );
        });

        Button cerrar =
                new Button("×");

        cerrar.getStyleClass().add("control-cerrar");

        cerrar.setOnAction(evento -> {

            Stage escenario =
                    (Stage) cerrar
                            .getScene()
                            .getWindow();

            escenario.close();
        });

        HBox controlesVentana =
                new HBox(
                        2,
                        minimizar,
                        maximizar,
                        cerrar
                );

        controlesVentana.setAlignment(
                Pos.CENTER_RIGHT
        );

        HBox cabecera =
                new HBox(
                        14,
                        identidad,
                        seccion,
                        espacio,
                        nuevaActividad,
                        recargar,
                        controlesVentana
                );

        cabecera.setAlignment(
                Pos.CENTER_LEFT
        );

        cabecera.getStyleClass().add("cabecera");

        configurarMovimientoVentana(cabecera);

        return cabecera;
    }

    private void configurarMovimientoVentana(
            HBox cabecera
    ) {

        cabecera.setOnMousePressed(evento -> {

            Stage escenario =
                    (Stage) cabecera
                            .getScene()
                            .getWindow();

            if (!escenario.isMaximized()) {

                posicionInicialX =
                        escenario.getX()
                                - evento.getScreenX();

                posicionInicialY =
                        escenario.getY()
                                - evento.getScreenY();
            }
        });

        cabecera.setOnMouseDragged(evento -> {

            Stage escenario =
                    (Stage) cabecera
                            .getScene()
                            .getWindow();

            if (!escenario.isMaximized()) {

                escenario.setX(
                        evento.getScreenX()
                                + posicionInicialX
                );

                escenario.setY(
                        evento.getScreenY()
                                + posicionInicialY
                );
            }
        });

        cabecera.setOnMouseClicked(evento -> {

            if (evento.getClickCount() == 2) {

                Stage escenario =
                        (Stage) cabecera
                                .getScene()
                                .getWindow();

                escenario.setMaximized(
                        !escenario.isMaximized()
                );
            }
        });
    }
}