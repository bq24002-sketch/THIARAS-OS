package com.thiarasos.desktop;

import com.thiarasos.persistencia.ActividadSemanal;
import com.thiarasos.persistencia.NuevaActividad;
import com.thiarasos.persistencia.TipoActividadBaseDatos;
import com.thiarasos.servicio.ServicioActividad;
import com.thiarasos.servicio.ServicioDiario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

final class VentanaSemanal {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM");

    private static final String[] DIAS = {
            "LUNES", "MARTES", "MIÉRCOLES", "JUEVES",
            "VIERNES", "SÁBADO", "DOMINGO"
    };

    private final ServicioActividad servicio;
    private final ServicioDiario servicioDiario;
    private final Runnable alActualizarPrincipal;
    private final ObservableList<ActividadSemanal> actividades =
            FXCollections.observableArrayList();
    private final VBox contenido = new VBox(12);
    private final Label estado = new Label("Cargando...");
    private Stage ventana;

    VentanaSemanal(
            ServicioActividad servicio,
            ServicioDiario servicioDiario,
            Runnable alActualizarPrincipal
    ) {
        this.servicio = servicio;
        this.servicioDiario = servicioDiario;
        this.alActualizarPrincipal = alActualizarPrincipal;
    }

    void mostrar() {

    if (ventana != null) {
        if (ventana.isShowing()) {
            ventana.toFront();
            ventana.requestFocus();
            return;
            }
        }

        ventana = new Stage();

        ventana.setTitle("THIARAS OS — Semana");
        ventana.initModality(Modality.NONE);

        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(18));
        raiz.getStyleClass().add("ventana-semanal");

        raiz.setTop(crearCabecera());

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("scroll-semanal");

        BorderPane.setMargin(
            scroll,
            new Insets(16, 0, 0, 0)
        );

        raiz.setCenter(scroll);

        Scene escena = new Scene(raiz, 1250, 700);

        escena.getStylesheets().add(
            getClass()
                .getResource("/thiaras.css")
                .toExternalForm()
        );

        ventana.setScene(escena);

        ventana.setOnHidden(evento -> ventana = null);

        ventana.show();

        cargar();
    }

    private VBox crearCabecera() {
        Label titulo = new Label("Plan semanal");
        titulo.getStyleClass().add("titulo-seccion");

        LocalDate lunes = LocalDate.now()
                .with(DayOfWeek.MONDAY);
        LocalDate domingo = lunes.plusDays(6);

        Label periodo = new Label(
                lunes.format(FECHA) + " — " + domingo.format(FECHA)
        );
        periodo.getStyleClass().add("autor");

        Label descripcion = new Label(
                "Actividades programadas de lunes a domingo"
        );

        Button nueva = new Button("Nueva actividad");
        nueva.getStyleClass().add("primario");
        nueva.setOnAction(evento -> abrirFormulario());

        HBox fila = new HBox(14, titulo, periodo);
        fila.setAlignment(Pos.CENTER_LEFT);

        HBox acciones = new HBox(8, nueva);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        HBox superior = new HBox(fila, acciones);
        HBox.setHgrow(fila, Priority.ALWAYS);
        superior.setAlignment(Pos.CENTER_LEFT);

        return new VBox(4, superior, descripcion, estado);
    }

    private void cargar() {
        ejecutarAsync(
                servicio::listarSemana,
                datos -> {
                    actividades.setAll(datos);
                    construirDias();
                    estado.setText(
                            actividades.size() + " actividad" +
                            (actividades.size() == 1 ? "" : "es") +
                            " programada" +
                            (actividades.size() == 1 ? "" : "s")
                );
            }
        );
    }

    private void construirDias() {
        contenido.getChildren().clear();

        LocalDate lunes = LocalDate.now()
                .with(DayOfWeek.MONDAY);

        HBox semana = new HBox(10);
        semana.setFillHeight(true);
        semana.setMinWidth(1420);

        for (int i = 0; i < 7; i++) {
            LocalDate fecha = lunes.plusDays(i);
            VBox dia = crearDia(i, fecha);
            HBox.setHgrow(dia, Priority.ALWAYS);
            semana.getChildren().add(dia);
        }

        contenido.getChildren().add(semana);
    }

    private VBox crearDia(int indice, LocalDate fecha) {
        Label nombre = new Label(DIAS[indice]);
        nombre.getStyleClass().add("titulo-seccion");

        Label fechaLabel = new Label(fecha.format(FECHA));

        VBox tarjetas = new VBox(8);
        tarjetas.setFillWidth(true);

        List<ActividadSemanal> delDia = actividades.stream()
                .filter(a -> a.fecha().equals(fecha))
                .toList();

        if (delDia.isEmpty()) {
            Label vacio = new Label("Sin actividades");
            vacio.setWrapText(true);
            tarjetas.getChildren().add(vacio);
        } else {
            for (ActividadSemanal actividad : delDia) {
                tarjetas.getChildren().add(crearTarjeta(actividad));
            }
        }

        VBox columna = new VBox(7, nombre, fechaLabel, tarjetas);
        columna.setPadding(new Insets(12));

        columna.setMinWidth(200);
        columna.setPrefWidth(200);
        columna.setMaxWidth(200);

        VBox.setVgrow(tarjetas, Priority.ALWAYS);

        return columna;
    }

    private VBox crearTarjeta(ActividadSemanal actividad) {
        String hora = actividad.hora() == null
            ? "Sin hora"
            : actividad.hora().toString();

        String duracion = actividad.duracionMinutos() == null
            ? ""
            : actividad.duracionMinutos() + " min";

        Label titulo = new Label(actividad.titulo());
        titulo.setWrapText(true);

        Label detalle = new Label(
            hora + (duracion.isBlank() ? "" : " · " + duracion)
        );
        detalle.setWrapText(true);

        Label estado = new Label(
            actividad.estado()
        );

        Button iniciar = new Button("▶");
        Button completar = new Button("✓");
        Button diario = new Button("📖");
        
        iniciar.getStyleClass().add("accion-semanal");
        completar.getStyleClass().add("accion-semanal");
        diario.getStyleClass().add("accion-semanal");

        iniciar.setOnAction(evento ->
            ejecutarAsync(
                    () -> servicio.iniciarYRefrescar(
                        actividad.id(),
                        actividad.fecha()
                    ),
                    resultado -> {
                        cargar();
                        alActualizarPrincipal.run();
                    }
            )
        );

        completar.setOnAction(evento ->
            ejecutarAsync(
                    () -> servicio.completarYRefrescar(actividad.id()),
                    resultado -> {
                        cargar();
                        alActualizarPrincipal.run();
                    }
            )
        );

        diario.setOnAction(evento ->
            new VentanaDiario(
                    servicioDiario,
                    actividad.id()
            ).mostrar()
        );

        boolean enProgreso = "EN_PROGRESO".equals(actividad.estado());

        iniciar.setDisable(enProgreso);
        completar.setDisable(!enProgreso);

        HBox acciones = new HBox(
            6,
            iniciar,
            completar,
            diario
        );

        acciones.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(
            4,
            titulo,
            detalle,
            estado,
            acciones
        );

        tarjeta.setPadding(new Insets(8));
        tarjeta.getStyleClass().add("metrica");

        return tarjeta;
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

    private void crearActividad(NuevaActividad actividad) {
        ejecutarAsync(
                () -> {
                    var estadoPrincipal = servicio.crearYRefrescar(actividad);
                    cargar();
                    return estadoPrincipal;
                },
                resultado -> alActualizarPrincipal.run()
        );
    }

    private <T> void ejecutarAsync(
            Callable<T> accion,
            Consumer<T> exito
    ) {
        Task<T> tarea = new Task<>() {
            @Override
            protected T call() throws Exception {
                return accion.call();
            }
        };

        tarea.setOnSucceeded(evento -> exito.accept(tarea.getValue()));
        tarea.setOnFailed(evento -> {
            estado.setText("No se pudo actualizar la semana");
            tarea.getException().printStackTrace();
        });

        Thread hilo = new Thread(tarea, "thiaras-semana");
        hilo.setDaemon(true);
        hilo.start();
    }
}
