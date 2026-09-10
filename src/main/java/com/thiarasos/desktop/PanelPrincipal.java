package com.thiarasos.desktop;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
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
import java.util.function.Predicate;

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

    VBox identidadTexto = new VBox(1, marca, autor);

    HBox identidad = new HBox(10, icono, identidadTexto);
    identidad.setAlignment(Pos.CENTER_LEFT);

    Label seccion = new Label("Dashboard");
    seccion.getStyleClass().add("seccion");

    Region espacio = new Region();
    HBox.setHgrow(espacio, Priority.ALWAYS);

    Button nuevaActividad = new Button("Nueva actividad");
    nuevaActividad.getStyleClass().add("primario");
    nuevaActividad.setOnAction(evento -> abrirFormulario());

    Button recargar = new Button("Actualizar");
    recargar.getStyleClass().add("secundario");
    recargar.setOnAction(evento -> refrescar());

    Button minimizar = new Button("—");
    minimizar.getStyleClass().add("control-ventana");
    minimizar.setOnAction(evento -> {
        Stage escenario = (Stage) minimizar.getScene().getWindow();
        escenario.setIconified(true);
    });

    Button maximizar = new Button("□");
    maximizar.getStyleClass().add("control-ventana");
    maximizar.setOnAction(evento -> {
        Stage escenario = (Stage) maximizar.getScene().getWindow();
        escenario.setMaximized(!escenario.isMaximized());
    });

    Button cerrar = new Button("×");
    cerrar.getStyleClass().add("control-cerrar");
    cerrar.setOnAction(evento -> {
        Stage escenario = (Stage) cerrar.getScene().getWindow();
        escenario.close();
    });

    HBox controlesVentana = new HBox(
            2,
            minimizar,
            maximizar,
            cerrar
    );

    controlesVentana.setAlignment(Pos.CENTER_RIGHT);

    HBox cabecera = new HBox(
            14,
            identidad,
            seccion,
            espacio,
            nuevaActividad,
            recargar,
            controlesVentana
    );

    cabecera.setAlignment(Pos.CENTER_LEFT);
    cabecera.getStyleClass().add("cabecera");

    /*
     * Permitir mover la ventana arrastrando la barra.
     */
    cabecera.setOnMousePressed(evento -> {

        Stage escenario =
                (Stage) cabecera.getScene().getWindow();

        if (!escenario.isMaximized()) {
            posicionInicialX =
                    escenario.getX() - evento.getScreenX();

            posicionInicialY =
                    escenario.getY() - evento.getScreenY();
        }
    });

    cabecera.setOnMouseDragged(evento -> {

        Stage escenario =
                (Stage) cabecera.getScene().getWindow();

        if (!escenario.isMaximized()) {
            escenario.setX(
                    evento.getScreenX() + posicionInicialX
            );

            escenario.setY(
                    evento.getScreenY() + posicionInicialY
            );
        }
    });

    /*
     * Doble clic en la barra:
     * maximizar / restaurar.
     */
    cabecera.setOnMouseClicked(evento -> {

        if (evento.getClickCount() == 2) {

            Stage escenario =
                    (Stage) cabecera.getScene().getWindow();

            escenario.setMaximized(
                    !escenario.isMaximized()
            );
        }
    });

    return cabecera;
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
        
        
       TableColumn<ActividadInterfaz, String> tipo =
        columna("Materia", ActividadInterfaz::materia, 0.18);

        TableColumn<ActividadInterfaz, String> fecha =
        columna("Fecha", actividad -> {
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
        columna("Horario", actividad ->
                actividad.hora() == null ? "Sin hora" :
                actividad.hora().toString(), 0.14);
        TableColumn<ActividadInterfaz, String> estado = columna("Estado", ActividadInterfaz::estado, 0.14);
        TableColumn<ActividadInterfaz, ActividadInterfaz> acciones = new TableColumn<>("Acciones");
        acciones.setCellValueFactory(celda -> new javafx.beans.property.SimpleObjectProperty<>(celda.getValue()));
        acciones.setCellFactory(columna -> new CeldaAcciones());
        acciones.setPrefWidth(190);
        

        tablaActividades.getColumns().setAll(
        titulo, tipo, fecha, horario, estado, acciones);
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
                    servicio::modificarYRefrescar
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
            estadoConexion.setText("No se pudo conectar");
            tarea.getException().printStackTrace();
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

   private final class CeldaAcciones
        extends TableCell<ActividadInterfaz, ActividadInterfaz> {

    private final Button iniciar = new Button("Iniciar");
    private final Button completar = new Button("Completar");
    private final Button editar = new Button("Editar");

    private final HBox contenido =
            new HBox(6, iniciar, completar, editar);

    private CeldaAcciones() {

        iniciar.getStyleClass().add("boton-iniciar");
        completar.getStyleClass().add("boton-completar");
        editar.getStyleClass().add("boton-editar");

        iniciar.setOnAction(evento -> {

            ActividadInterfaz actividad = getItem();

            if (actividad == null) {
                return;
            }

            ejecutarAsync(
                    () -> servicio.iniciarYRefrescar(actividad.id()),
                    PanelPrincipal.this::aplicarEstado
            );
        });

        completar.setOnAction(evento -> {

            ActividadInterfaz actividad = getItem();

            if (actividad == null) {
                return;
            }

            ejecutarAsync(
                    () -> servicio.completarYRefrescar(actividad.id()),
                    PanelPrincipal.this::aplicarEstado
            );
        });

        editar.setOnAction(evento -> {

            ActividadInterfaz actividad = getItem();

            if (actividad == null) {
                return;
            }

            ejecutarAsync(
                    () -> servicio.obtenerParaEdicion(actividad.id()),
                    datos -> abrirFormularioEdicion(datos)
            );
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
    private final Predicate<NuevaActividad> existeConflictoHorario;

    private final Consumer<NuevaActividad> alGuardar;
    private final java.util.function.BiConsumer<Long, NuevaActividad> alModificar;
    private final com.thiarasos.persistencia.ActividadEditable actividadEditar;

    // =========================================================
    // CONSTRUCTOR PARA NUEVA ACTIVIDAD
    // =========================================================

    private FormularioActividad(
            List<TipoActividadBaseDatos> tipos,
            Predicate<NuevaActividad> existeConflictoHorario,
            Consumer<NuevaActividad> alGuardar
    ) {
        this.tipos = tipos;
        this.existeConflictoHorario = existeConflictoHorario;
        this.alGuardar = alGuardar;
        this.alModificar = null;
        this.actividadEditar = null;
    }

    // =========================================================
    // CONSTRUCTOR PARA EDITAR ACTIVIDAD
    // =========================================================

     private FormularioActividad(
        List<TipoActividadBaseDatos> tipos,
        Predicate<NuevaActividad> existeConflictoHorario,
        com.thiarasos.persistencia.ActividadEditable actividadEditar,
        java.util.function.BiConsumer<Long, NuevaActividad> alModificar
    ) {
        this.tipos = tipos;
        this.existeConflictoHorario = existeConflictoHorario;
        this.alGuardar = null;
        this.alModificar = alModificar;
        this.actividadEditar = actividadEditar;
}
    // =========================================================
    // MOSTRAR FORMULARIO
    // =========================================================

    private void mostrar() {

        javafx.scene.control.Dialog<Void> dialogo =
                new javafx.scene.control.Dialog<>();

        boolean editando = actividadEditar != null;

        dialogo.setTitle(
                editando
                        ? "Editar actividad"
                        : "Nueva actividad"
        );

        dialogo.getDialogPane().getButtonTypes().addAll(
                javafx.scene.control.ButtonType.CANCEL,
                javafx.scene.control.ButtonType.OK
        );

        // =====================================================
        // CAMPOS
        // =====================================================

        TextField titulo = new TextField();

        TextField materia = new TextField();

        ComboBox<String> tipo = new ComboBox<>();

        tipo.getItems().setAll(
                tipos.stream()
                        .map(TipoActividadBaseDatos::nombre)
                        .toList()
        );

        Spinner<Integer> minutos =
                new Spinner<>(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                1,
                                1440,
                                30
                        )
                );

        DatePicker inicio =
                new DatePicker(LocalDate.now());

        DatePicker vencimiento =
                new DatePicker();

        TextField hora =
                new TextField();

        hora.setPromptText("HH:mm");

        Spinner<Integer> anticipacion =
                new Spinner<>(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                0,
                                10080,
                                15
                        )
                );

        CheckBox recurrente =
                new CheckBox("Recurrente");

        ComboBox<String> diaSemana =
                new ComboBox<>();

        diaSemana.getItems().addAll(
                "Lunes",
                "Martes",
                "Miércoles",
                "Jueves",
                "Viernes",
                "Sábado",
                "Domingo"
        );

        TextArea contenido =
                new TextArea();

        contenido.setPrefRowCount(3);

        Label error = new Label();
        error.getStyleClass().add("error-formulario");

        Label disponibilidad = new Label();
        disponibilidad.getStyleClass().add(
                "disponibilidad-formulario"
        );

        // =====================================================
        // CARGAR DATOS SI ESTAMOS EDITANDO
        // =====================================================

        if (editando) {

            titulo.setText(
                    actividadEditar.titulo() == null
                            ? ""
                            : actividadEditar.titulo()
            );

            materia.setText(
                    actividadEditar.materia() == null
                            ? ""
                            : actividadEditar.materia()
            );

            contenido.setText(
                    actividadEditar.contenido() == null
                            ? ""
                            : actividadEditar.contenido()
            );

            if (actividadEditar.duracionMinutos() != null) {
                minutos.getValueFactory().setValue(
                        actividadEditar.duracionMinutos()
                );
            }

            tipo.getSelectionModel().select(
                    actividadEditar.tipo()
            );

            if (actividadEditar.fechaInicio() != null) {
                inicio.setValue(
                        actividadEditar.fechaInicio()
                );
            }

            vencimiento.setValue(
                    actividadEditar.fechaVencimiento()
            );

            if (actividadEditar.hora() != null) {
                hora.setText(
                        actividadEditar.hora().toString()
                );
            }

            anticipacion.getValueFactory().setValue(
                    actividadEditar.minutosAnticipacion()
            );

            recurrente.setSelected(
                    actividadEditar.recurrente()
            );

            int dia = actividadEditar.diaSemana();

            if (dia >= 1 && dia <= 7) {
                diaSemana.getSelectionModel().select(dia - 1);
            }

        } else {

            tipo.getSelectionModel().selectFirst();
            diaSemana.getSelectionModel().selectFirst();
        }

        diaSemana.setDisable(
                !recurrente.isSelected()
        );

        recurrente.selectedProperty().addListener(
                (observable, anterior, seleccionado) ->
                        diaSemana.setDisable(!seleccionado)
        );

        // =====================================================
        // FORMULARIO
        // =====================================================

        GridPane formulario = new GridPane();

        formulario.setHgap(12);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(8));

        formulario.addRow(
                0,
                new Label("Título"),
                titulo
        );

        formulario.addRow(
                1,
                new Label("Materia"),
                materia
        );

        formulario.addRow(
                2,
                new Label("Tipo"),
                tipo
        );

        formulario.addRow(
                3,
                new Label("Duración (min)"),
                minutos
        );

        formulario.addRow(
                4,
                new Label("Fecha inicio"),
                inicio
        );

        formulario.addRow(
                5,
                new Label("Vencimiento"),
                vencimiento
        );

        formulario.addRow(
                6,
                new Label("Hora"),
                hora
        );

        formulario.addRow(
                7,
                new Label("Aviso previo (min)"),
                anticipacion
        );

        formulario.addRow(
                8,
                new Label(""),
                recurrente
        );

        formulario.addRow(
                9,
                new Label("Día"),
                diaSemana
        );

        formulario.addRow(
                10,
                new Label("Notas"),
                contenido
        );

        formulario.add(
                error,
                1,
                11
        );

        formulario.add(
                disponibilidad,
                1,
                12
        );

        GridPane.setHgrow(
                titulo,
                Priority.ALWAYS
        );

        GridPane.setHgrow(
                materia,
                Priority.ALWAYS
        );

        GridPane.setHgrow(
                contenido,
                Priority.ALWAYS
        );

        dialogo.getDialogPane().setContent(
                formulario
        );

        Button botonOk =
                (Button) dialogo.getDialogPane()
                        .lookupButton(
                                javafx.scene.control.ButtonType.OK
                        );

        botonOk.setText(
                editando
                        ? "Guardar cambios"
                        : "Guardar"
        );

        // =====================================================
        // GUARDAR
        // =====================================================

        botonOk.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                evento -> {

                    error.setText("");
                    disponibilidad.setText("");

                    if (titulo.getText().isBlank()) {

                        error.setText(
                                "El título es obligatorio."
                        );

                        evento.consume();
                        return;
                    }

                    if (tipo.getValue() == null) {

                        error.setText(
                                "Selecciona un tipo de actividad."
                        );

                        evento.consume();
                        return;
                    }

                    if (inicio.getValue() == null) {

                        error.setText(
                                "La fecha de inicio es obligatoria."
                        );

                        evento.consume();
                        return;
                    }

                    if (vencimiento.getValue() != null
                            && vencimiento.getValue()
                            .isBefore(inicio.getValue())) {

                        error.setText(
                                "La fecha de vencimiento no puede ser anterior a la fecha de inicio."
                        );

                        evento.consume();
                        return;
                    }

                    LocalTime horaValor = null;

                    if (!hora.getText().isBlank()) {

                        try {

                            horaValor =
                                    LocalTime.parse(
                                            hora.getText().trim()
                                    );

                        } catch (DateTimeParseException e) {

                            error.setText(
                                    "La hora debe tener formato HH:mm."
                            );

                            evento.consume();
                            return;
                        }
                    }

                    int diaValor = 0;

                    if (recurrente.isSelected()) {

                        if (diaSemana.getValue() == null) {

                            error.setText(
                                    "Selecciona el día de la recurrencia."
                            );

                            evento.consume();
                            return;
                        }

                        diaValor =
                                diaSemana.getSelectionModel()
                                        .getSelectedIndex()
                                        + 1;
                    }

                    NuevaActividad actividad =
                            new NuevaActividad(
                                    titulo.getText().trim(),
                                    materia.getText().trim(),
                                    contenido.getText(),
                                    minutos.getValue(),
                                    tipo.getValue(),
                                    inicio.getValue(),
                                    vencimiento.getValue(),
                                    horaValor,
                                    recurrente.isSelected(),
                                    diaValor,
                                    anticipacion.getValue()
                            );

                    // =========================================
                    // COMPROBAR CONFLICTO
                    // =========================================

                    try {

                        boolean conflicto =
                                existeConflictoHorario.test(
                                        actividad
                                );

                        if (conflicto) {

                            error.setText(
                                    "Existe un conflicto de horario con otra actividad."
                            );

                            evento.consume();
                            return;
                        }

                    } catch (Exception excepcion) {

                        error.setText(
                                "No se pudo comprobar el horario."
                        );

                        evento.consume();
                        return;
                    }

                    // =========================================
                    // EJECUTAR OPERACIÓN
                    // =========================================

                   if (editando) {

    alModificar.accept(
            actividadEditar.id(),
            actividad
                    );

                    } else {

             alGuardar.accept(
            actividad
                    );
                  }
                }
            );

        dialogo.showAndWait();
       }
    }
}
