package com.thiarasos.desktop;

import com.thiarasos.persistencia.ActividadEditable;
import com.thiarasos.persistencia.NuevaActividad;
import com.thiarasos.persistencia.TipoActividadBaseDatos;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class FormularioActividad {

    private final List<TipoActividadBaseDatos> tipos;
    private final Predicate<NuevaActividad> existeConflictoHorario;

    private final Consumer<NuevaActividad> alGuardar;
    private final BiConsumer<Long, NuevaActividad> alModificar;

    private final ActividadEditable actividadEditar;

    // =========================================================
    // CONSTRUCTOR PARA NUEVA ACTIVIDAD
    // =========================================================

    FormularioActividad(
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

    FormularioActividad(
            List<TipoActividadBaseDatos> tipos,
            Predicate<NuevaActividad> existeConflictoHorario,
            ActividadEditable actividadEditar,
            BiConsumer<Long, NuevaActividad> alModificar
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

    void mostrar() {

        Dialog<Void> dialogo = new Dialog<>();

        boolean editando = actividadEditar != null;

        dialogo.setTitle(
                editando
                        ? "Editar actividad"
                        : "Nueva actividad"
        );

        dialogo.getDialogPane().getButtonTypes().addAll(
                ButtonType.CANCEL,
                ButtonType.OK
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

                diaSemana.getSelectionModel().select(
                        dia - 1
                );
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
                        .lookupButton(ButtonType.OK);

        botonOk.setText(
                editando
                        ? "Guardar cambios"
                        : "Guardar"
        );

        // =====================================================
        // GUARDAR
        // =====================================================

        botonOk.addEventFilter(
                ActionEvent.ACTION,
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

                        } catch (DateTimeParseException excepcion) {

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

                    // =================================================
                    // COMPROBAR CONFLICTO
                    // =================================================

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

                    // =================================================
                    // EJECUTAR OPERACIÓN
                    // =================================================

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