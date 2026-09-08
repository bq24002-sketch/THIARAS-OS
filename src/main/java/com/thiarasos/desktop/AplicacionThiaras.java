package com.thiarasos.desktop;

import com.thiarasos.config.Configuracion;
import com.thiarasos.servicio.ServiciosPersistencia;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class AplicacionThiaras extends Application {

    @Override
    public void start(Stage escenario) {
        PanelPrincipal panel = new PanelPrincipal(
                ServiciosPersistencia.crearServicioActividad(new Configuracion())
        );
        Scene escena = new Scene(panel.crear(), 1180, 760);
        escena.getStylesheets().add(getClass().getResource("/thiaras.css").toExternalForm());

        escenario.setTitle("THIARAS OS");
        escenario.setMinWidth(940);
        escenario.setMinHeight(620);
        escenario.setScene(escena);
        escenario.show();
        panel.refrescar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
