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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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

@SuppressWarnings("unchecked")
final class PanelPrincipal {

    private final ServicioActividad servicio;
    private final ObservableList<ActividadInterfaz> actividades = FXCollections.observableArrayList();
    private final ObservableList<NotificacionPendiente> notificaciones = FXCollections.observableArrayList();
    private final Label hoy = new Label("--");
    private final Label semana = new Label("--");
    private final Label estadoConexion = new Label("Conectando...");
    private final TableView<ActividadInterfaz> tablaActividades = new TableView<>(actividades);
    private final TableView<NotificacionPendiente> tablaNotificaciones = new TableView<>(notificaciones);

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

    private ToolBar crearCabecera() {
        Label marca = new Label("THIARAS OS");
        marca.getStyleClass().add("marca");
        Label seccion = new Label("Dashboard");
        seccion.getStyleClass().add("seccion");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Button nuevaActividad = new Button("Nueva actividad");
        nuevaActividad.getStyleClass().add("primario");
        nuevaActividad.setOnAction(evento -> abrirFormulario());
        Button recargar = new Button("Actualizar");
        recargar.setOnAction(evento -> refrescar());
        return new ToolBar(marca, seccion, espacio, nuevaActividad, recargar);
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
        TableColumn<ActividadInterfaz, String> titulo = columna("Actividad", ActividadInterfaz::titulo, 0.35);
        TableColumn<ActividadInterfaz, String> tipo = columna("Materia", ActividadInterfaz::materia, 0.18);
        TableColumn<ActividadInterfaz, String> horario = columna("Horario", actividad ->
                actividad.hora() == null ? "Sin hora" : actividad.hora().toString(), 0.14);
        TableColumn<ActividadInterfaz, String> estado = columna("Estado", ActividadInterfaz::estado, 0.14);
        TableColumn<ActividadInterfaz, ActividadInterfaz> acciones = new TableColumn<>("Acciones");
        acciones.setCellValueFactory(celda -> new javafx.beans.property.SimpleObjectProperty<>(celda.getValue()));
        acciones.setCellFactory(columna -> new CeldaAcciones());
        acciones.setPrefWidth(190);
        tablaActividades.getColumns().setAll(titulo, tipo, horario, estado, acciones);
        tablaActividades.setPlaceholder(new Label("No hay actividades activas."));
        tablaActividades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void configurarTablaNotificaciones() {
        TableColumn<NotificacionPendiente, String> tipo = columna("Tipo", NotificacionPendiente::tipo, 0.18);
        TableColumn<NotificacionPendiente, String> titulo = columna("Titulo", NotificacionPendiente::titulo, 0.52);
        TableColumn<NotificacionPendiente, NotificacionPendiente> leer = new TableColumn<>("");
        leer.setCellValueFactory(celda -> new javafx.beans.property.SimpleObjectProperty<>(celda.getValue()));
        leer.setCellFactory(columna -> new CeldaLeer());
        leer.setPrefWidth(130);
        tablaNotificaciones.getColumns().setAll(tipo, titulo, leer);
        tablaNotificaciones.setPlaceholder(new Label("No hay notificaciones pendientes."));
        tablaNotificaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private <T> TableColumn<T, String> columna(
            String nombre,
            java.util.function.Function<T, String> valor,
            double proporcion
    ) {
        TableColumn<T, String> columna = new TableColumn<>(nombre);
        columna.setCellValueFactory(celda -> new javafx.beans.property.SimpleStringProperty(
                valor.apply(celda.getValue()) == null ? "" : valor.apply(celda.getValue())
        ));
        columna.setPrefWidth(700 * proporcion);
        return columna;
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
        ejecutarAsync(servicio::listarTipos, tipos -> new FormularioActividad(tipos, this::crearActividad).mostrar());
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
            estadoConexion.setText("No se pudo conectar");
            mostrarError(tarea.getException());
        });
        Thread hilo = new Thread(tarea, "thiaras-base-datos");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrarError(Throwable error) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("THIARAS OS");
        alerta.setHeaderText("No se pudo completar la operacion");
        alerta.setContentText(error.getMessage());
        alerta.show();
    }

    private final class CeldaAcciones extends TableCell<ActividadInterfaz, ActividadInterfaz> {
        private final Button iniciar = new Button("Iniciar");
        private final Button completar = new Button("Completar");
        private final HBox contenido = new HBox(6, iniciar, completar);

        private CeldaAcciones() {
            iniciar.setOnAction(evento -> ejecutarAsync(
                    () -> servicio.iniciarYRefrescar(getItem().id()), PanelPrincipal.this::aplicarEstado
            ));
            completar.setOnAction(evento -> ejecutarAsync(
                    () -> servicio.completarYRefrescar(getItem().id()), PanelPrincipal.this::aplicarEstado
            ));
        }

        @Override
        protected void updateItem(ActividadInterfaz actividad, boolean vacia) {
            super.updateItem(actividad, vacia);
            if (vacia || actividad == null) {
                setGraphic(null);
                return;
            }
            iniciar.setDisable("EN_PROGRESO".equals(actividad.estado()));
            completar.setDisable(!"EN_PROGRESO".equals(actividad.estado()));
            setGraphic(contenido);
        }
    }

    private final class CeldaLeer extends TableCell<NotificacionPendiente, NotificacionPendiente> {
        private final Button leer = new Button("Marcar leida");

        private CeldaLeer() {
            leer.setOnAction(evento -> ejecutarAsync(
                    () -> servicio.marcarNotificacionLeidaYRefrescar(getItem().id()), PanelPrincipal.this::aplicarEstado
            ));
        }

        @Override
        protected void updateItem(NotificacionPendiente notificacion, boolean vacia) {
            super.updateItem(notificacion, vacia);
            setGraphic(vacia || notificacion == null ? null : leer);
        }
    }

    private static final class FormularioActividad {
        private final List<TipoActividadBaseDatos> tipos;
        private final Consumer<NuevaActividad> alGuardar;

        private FormularioActividad(List<TipoActividadBaseDatos> tipos, Consumer<NuevaActividad> alGuardar) {
            this.tipos = tipos;
            this.alGuardar = alGuardar;
        }

        private void mostrar() {
            javafx.scene.control.Dialog<Void> dialogo = new javafx.scene.control.Dialog<>();
            dialogo.setTitle("Nueva actividad");
            dialogo.getDialogPane().getButtonTypes().addAll(
                    javafx.scene.control.ButtonType.CANCEL
            );

            TextField titulo = new TextField();
            TextField materia = new TextField();
            ComboBox<String> tipo = new ComboBox<>();
            tipo.getItems().setAll(tipos.stream().map(TipoActividadBaseDatos::nombre).toList());
            tipo.getSelectionModel().selectFirst();
            Spinner<Integer> minutos = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, 30));
            DatePicker inicio = new DatePicker(LocalDate.now());
            DatePicker vencimiento = new DatePicker();
            TextField hora = new TextField();
            hora.setPromptText("HH:mm");
            Spinner<Integer> anticipacion = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10080, 15));
            CheckBox recurrente = new CheckBox("Recurrente");
            TextArea contenido = new TextArea();
            contenido.setPrefRowCount(3);
            Label error = new Label();
            error.getStyleClass().add("error-formulario");

            GridPane formulario = new GridPane();
            formulario.setHgap(12);
            formulario.setVgap(10);
            formulario.setPadding(new Insets(8));
            formulario.addRow(0, new Label("Titulo"), titulo);
            formulario.addRow(1, new Label("Materia"), materia);
            formulario.addRow(2, new Label("Tipo"), tipo);
            formulario.addRow(3, new Label("Duracion (min)"), minutos);
            formulario.addRow(4, new Label("Fecha inicio"), inicio);
            formulario.addRow(5, new Label("Vencimiento"), vencimiento);
            formulario.addRow(6, new Label("Hora"), hora);
            formulario.addRow(7, new Label("Aviso previo (min)"), anticipacion);
            formulario.addRow(8, new Label(""), recurrente);
            formulario.addRow(9, new Label("Notas"), contenido);
            formulario.add(error, 1, 10);
            GridPane.setHgrow(titulo, Priority.ALWAYS);
            dialogo.getDialogPane().setContent(formulario);
            dialogo.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);

            Button botonOk = (Button) dialogo.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK);
            botonOk.setText("Guardar");
            botonOk.addEventFilter(javafx.event.ActionEvent.ACTION, evento -> {
                try {
                    NuevaActividad actividad = construirActividad(
                            titulo, materia, contenido, tipo, minutos, inicio, vencimiento, hora, recurrente, anticipacion
                    );
                    alGuardar.accept(actividad);
                } catch (IllegalArgumentException e) {
                    error.setText(e.getMessage());
                    evento.consume();
                }
            });
            dialogo.showAndWait();
        }

        private NuevaActividad construirActividad(
                TextField titulo, TextField materia, TextArea contenido, ComboBox<String> tipo,
                Spinner<Integer> minutos, DatePicker inicio, DatePicker vencimiento, TextField hora,
                CheckBox recurrente, Spinner<Integer> anticipacion
        ) {
            if (titulo.getText().isBlank()) {
                throw new IllegalArgumentException("El titulo es obligatorio.");
            }
            if (tipo.getValue() == null) {
                throw new IllegalArgumentException("Selecciona un tipo de actividad.");
            }
            if (vencimiento.getValue() != null && vencimiento.getValue().isBefore(inicio.getValue())) {
                throw new IllegalArgumentException("El vencimiento no puede ser anterior al inicio.");
            }
            LocalTime horaActividad = null;
            if (!hora.getText().isBlank()) {
                try {
                    horaActividad = LocalTime.parse(hora.getText());
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("La hora debe usar formato HH:mm.");
                }
            }
            return new NuevaActividad(
                    titulo.getText().trim(), materia.getText().trim(), contenido.getText().trim(),
                    minutos.getValue(), tipo.getValue(), inicio.getValue(), vencimiento.getValue(), horaActividad,
                    recurrente.isSelected(), inicio.getValue().getDayOfWeek().getValue(), anticipacion.getValue()
            );
        }
    }
}
