package com.thiarasos.desktop;

import com.thiarasos.persistencia.ActividadInterfaz;
import com.thiarasos.persistencia.EstadoAplicacion;
import com.thiarasos.persistencia.NuevaActividad;
import com.thiarasos.persistencia.NotificacionPendiente;
import com.thiarasos.persistencia.TipoActividadBaseDatos;
import com.thiarasos.servicio.ServicioActividad;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.thiarasos.persistencia.ErrorPersistencia;

final class PanelPrincipal {

    private final ServicioActividad servicio;
    private final ObservableList<ActividadInterfaz> actividades = FXCollections.observableArrayList();
    private final ObservableList<NotificacionPendiente> notificaciones = FXCollections.observableArrayList();
    private final Label hoy = new Label("--");
    private final Label semana = new Label("--");
    private final Label estadoConexion = new Label("Conectando...");
    private final TableView<ActividadInterfaz> tablaActividades = new TableView<>(actividades);
    private final TableView<NotificacionPendiente> tablaNotificaciones = new TableView<>(notificaciones);
    private double posicionInicialX;
    private double posicionInicialY;
    
    PanelPrincipal(ServicioActividad servicio) {
        this.servicio = servicio;
    }

    BorderPane crear() {
        BorderPane raiz = new BorderPane();
        raiz.setTop(crearCabecera());
        raiz.setCenter(crearContenido());
        raiz.setBottom(crearPie());
        return raiz;
    }

    void refrescar() {
        ejecutarAsync(servicio::refrescar, this::aplicarEstado);
    }

    private HBox crearCabecera() {
    return new CabeceraPrincipal(
            this::abrirFormulario,
            this::refrescar
    ).crear();
    }

    private VBox crearContenido() {
        VBox panelActividades = crearPanelActividades();
        VBox contenido = new VBox(18, crearResumen(), panelActividades, crearPanelNotificaciones());
        contenido.setPadding(new Insets(24));
        VBox.setVgrow(panelActividades, Priority.ALWAYS);
        return contenido;
    }

    private HBox crearResumen() {
        VBox hoyBloque = new VBox(4, new Label("HOY"), hoy);
        VBox semanaBloque = new VBox(4, new Label("SEMANA"), semana);
        hoyBloque.getStyleClass().add("metrica");
        semanaBloque.getStyleClass().add("metrica");
        return new HBox(28, hoyBloque, semanaBloque);
    }

    private VBox crearPanelActividades() {
        Label titulo = new Label("Actividades");
        titulo.getStyleClass().add("titulo-seccion");
        configurarTablaActividades();
        VBox panel = new VBox(10, titulo, tablaActividades);
        VBox.setVgrow(tablaActividades, Priority.ALWAYS);
        return panel;
    }

    private VBox crearPanelNotificaciones() {
        Label titulo = new Label("Notificaciones pendientes");
        titulo.getStyleClass().add("titulo-seccion");
        configurarTablaNotificaciones();
        VBox panel = new VBox(10, titulo, tablaNotificaciones);
        panel.setMaxHeight(210);
        VBox.setVgrow(tablaNotificaciones, Priority.NEVER);
        return panel;
    }

    private HBox crearPie() {
        HBox pie = new HBox(estadoConexion);
        pie.setPadding(new Insets(10, 24, 14, 24));
        pie.getStyleClass().add("pie");
        return pie;
    }

    private void configurarTablaActividades() {
        TableColumn<ActividadInterfaz, String> titulo = 
        ColumnasTabla.crear ("Actividad", ActividadInterfaz::titulo, 0.35);
        
        
       TableColumn<ActividadInterfaz, String> tipo =
        ColumnasTabla.crear ("Materia", ActividadInterfaz::materia, 0.18);

        TableColumn<ActividadInterfaz, String> fecha =
        ColumnasTabla.crear ("Fecha", actividad -> {
            if (actividad.fechaInicio() == null) {
                return "Sin fecha";
            }

            if (actividad.fechaVencimiento() == null
                    || actividad.fechaVencimiento().equals(actividad.fechaInicio())) {
                return actividad.fechaInicio().toString();
            }

            return actividad.fechaInicio() + " → " + actividad.fechaVencimiento();
        }, 0.20);

        TableColumn<ActividadInterfaz, String> horario =
        ColumnasTabla.crear ("Horario", actividad ->
                actividad.hora() == null ? "Sin hora" :
                actividad.hora().toString(), 0.14);
        TableColumn<ActividadInterfaz, String> estado = 
        ColumnasTabla.crear ("Estado", ActividadInterfaz::estado, 0.14);
        TableColumn<ActividadInterfaz, ActividadInterfaz> acciones = 
        new TableColumn<>("Acciones");
        acciones.setCellValueFactory(celda -> new javafx.beans.property.SimpleObjectProperty<>(celda.getValue()));
        acciones.setCellFactory(columna ->
        new CeldaAcciones(
                id -> ejecutarAsync(
                        () -> servicio.iniciarYRefrescar(id),
                        this::aplicarEstado
                ),
                id -> ejecutarAsync(
                        () -> servicio.completarYRefrescar(id),
                        this::aplicarEstado
                ),
                id -> ejecutarAsync(
                        () -> servicio.obtenerParaEdicion(id),
                        this::abrirFormularioEdicion
                )
        )
);
        acciones.setPrefWidth(190);
        

        tablaActividades.getColumns().setAll(
        titulo, tipo, fecha, horario, estado, acciones);
        tablaActividades.setPlaceholder(new Label("No hay actividades activas."));
        tablaActividades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void configurarTablaNotificaciones() {
        TableColumn<NotificacionPendiente, String> tipo = 
        ColumnasTabla.crear ("Tipo", NotificacionPendiente::tipo, 0.18);
        TableColumn<NotificacionPendiente, String> titulo = 
        ColumnasTabla.crear ("Titulo", NotificacionPendiente::titulo, 0.52);
        TableColumn<NotificacionPendiente, NotificacionPendiente> leer = new TableColumn<>("");
        leer.setCellValueFactory(celda -> new javafx.beans.property.SimpleObjectProperty<>(celda.getValue()));
        leer.setCellFactory(columna ->
        new CeldaLeer(
                id -> ejecutarAsync(
                        () -> servicio.marcarNotificacionLeidaYRefrescar(id),
                        this::aplicarEstado
                )
        )
);  
        leer.setPrefWidth(130);
        tablaNotificaciones.getColumns().setAll(tipo, titulo, leer);
        tablaNotificaciones.setPlaceholder(new Label("No hay notificaciones pendientes."));
        tablaNotificaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void aplicarEstado(EstadoAplicacion estado) {
        actividades.setAll(estado.actividades());
        notificaciones.setAll(estado.notificacionesPendientes());
        hoy.setText(estado.resumenDashboard().actividadesHoy() + " actividades | " +
                estado.resumenDashboard().minutosHoy() + " min");
        semana.setText(estado.resumenDashboard().actividadesSemana() + " actividades | " +
                estado.resumenDashboard().minutosSemana() + " min");
        estadoConexion.setText("Actualizado");
    }

    private void abrirFormulario() {
    ejecutarAsync(
            servicio::listarTipos,
            tipos -> new FormularioActividad(
                    tipos,
                    servicio::existeConflictoHorario,
                    this::crearActividad
            ).mostrar()
    );
}

private void abrirFormularioEdicion(
        com.thiarasos.persistencia.ActividadEditable actividad
) {
    ejecutarAsync(
            servicio::listarTipos,
            tipos -> new FormularioActividad(
                    tipos,
                    servicio::existeConflictoHorario,
                    actividad,
                    (id, nuevaActividad) ->
                            ejecutarAsync(
                                    () -> servicio.modificarYRefrescar(
                                            id,
                                            nuevaActividad
                                    ),
                                    this::aplicarEstado
                            )
            ).mostrar()
    );
}
    private void crearActividad(NuevaActividad actividad) {
        ejecutarAsync(() -> servicio.crearYRefrescar(actividad), this::aplicarEstado);
    }

    private <T> void ejecutarAsync(Callable<T> accion, Consumer<T> exito) {
        estadoConexion.setText("Actualizando...");
        Task<T> tarea = new Task<>() {
            @Override
            protected T call() throws Exception {
                return accion.call();
            }
        };
        tarea.setOnSucceeded(evento -> exito.accept(tarea.getValue()));
        tarea.setOnFailed(evento -> {
            estadoConexion.setText("Operación fallida");
            tarea.getException().printStackTrace();
            mostrarError(tarea.getException());
        });
        Thread hilo = new Thread(tarea, "thiaras-base-datos");
        hilo.setDaemon(true);
        hilo.start();
    }
    private void mostrarError(Throwable error) {

        String mensaje =
            error.getMessage() != null
                    ? error.getMessage()
                    : "Ocurrió un error inesperado.";

        Alert alerta =
            new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("THIARAS OS");
        alerta.setHeaderText(
            "No se pudo completar la operación"
        );
        alerta.setContentText(mensaje);

        error.printStackTrace();

        alerta.show();
    }

}
