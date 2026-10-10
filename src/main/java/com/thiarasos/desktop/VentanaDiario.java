package com.thiarasos.desktop;

import com.thiarasos.persistencia.EntradaDiario;
import com.thiarasos.persistencia.ErrorPersistencia;
import com.thiarasos.servicio.ServicioDiario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.thiarasos.persistencia.CumplimientoDiario;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaDiario {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final ServicioDiario servicioDiario;

    private final VBox contenedorEntradas = new VBox(12);

    private final long actividadId;

    public VentanaDiario(
            ServicioDiario servicioDiario,
            long actividadId
    ) {
        this.servicioDiario = servicioDiario;
        this.actividadId = actividadId;
    }

    public void mostrar() {

        Stage ventana = new Stage();

        ventana.setTitle("📖 Diario");

        ventana.initModality(Modality.APPLICATION_MODAL);

        BorderPane raiz = new BorderPane();

        raiz.setPadding(new Insets(20));

        // ============================================================
        // ENCABEZADO
        // ============================================================

        Label titulo = new Label("📖 Mi diario");

        titulo.setStyle("""
            -fx-font-size: 24px;
            -fx-font-weight: bold;
        """);

        Label subtitulo = new Label(
                "Reflexiones y notas de esta actividad"
        );

        subtitulo.setStyle("""
            -fx-text-fill: #666666;
            -fx-font-size: 13px;
        """);

        VBox encabezado = new VBox(
                5,
                titulo,
                subtitulo
        );

        // ============================================================
        // ENTRADAS
        // ============================================================

        contenedorEntradas.setPadding(
                new Insets(10, 5, 10, 5)
        );

        ScrollPane desplazamiento =
                new ScrollPane(contenedorEntradas);

        desplazamiento.setFitToWidth(true);

        desplazamiento.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        desplazamiento.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        // ============================================================
        // BOTÓN NUEVA ENTRADA
        // ============================================================

        Button nuevaEntrada =
                new Button("＋ Nueva entrada");

        nuevaEntrada.setOnAction(
                evento -> mostrarFormularioNuevaEntrada(ventana)
        );

        HBox barraInferior = new HBox(
                nuevaEntrada
        );

        barraInferior.setAlignment(
                Pos.CENTER_RIGHT
        );

        barraInferior.setPadding(
                new Insets(15, 0, 0, 0)
        );

        raiz.setTop(encabezado);
        raiz.setCenter(desplazamiento);
        raiz.setBottom(barraInferior);

        Scene escena = new Scene(
                raiz,
                650,
                600
        );

        ventana.setScene(escena);

        cargarEntradas();

        ventana.showAndWait();
    }

    private void cargarEntradas() {

        contenedorEntradas.getChildren().clear();

        try {

            List<EntradaDiario> entradas =
                    servicioDiario.obtenerPorActividad(
                            actividadId
                    );

            if (entradas.isEmpty()) {

                Label vacio = new Label(
                        "Todavía no hay entradas para esta actividad."
                );

                vacio.setStyle("""
                    -fx-text-fill: #777777;
                    -fx-font-size: 14px;
                """);

                vacio.setPadding(
                        new Insets(30)
                );

                contenedorEntradas.getChildren().add(
                        vacio
                );

                return;
            }

            for (EntradaDiario entrada : entradas) {

                contenedorEntradas.getChildren().add(
                        crearTarjetaEntrada(entrada)
                );
            }

        } catch (ErrorPersistencia e) {

            mostrarError(
                    "No se pudo cargar el diario.",
                    e.getMessage()
            );
        }
    }

    private VBox crearTarjetaEntrada(
            EntradaDiario entrada
    ) {

        VBox tarjeta = new VBox(8);

        tarjeta.setPadding(
                new Insets(15)
        );

        tarjeta.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #dddddd;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
        """);

        // ============================================================
        // FECHA
        // ============================================================

        Label fecha = new Label(
                entrada.fecha().format(FORMATO_FECHA)
        );

        fecha.setStyle("""
            -fx-font-size: 15px;
            -fx-font-weight: bold;
        """);

        // ============================================================
        // HORA
        // ============================================================

        String horaTexto =
                obtenerHoraTexto(entrada.creadoEn());

        Label hora = new Label(
                horaTexto
        );

        hora.setStyle("""
            -fx-text-fill: #777777;
            -fx-font-size: 12px;
        """);

        // ============================================================
        // CONTENIDO
        // ============================================================

        Label contenido = new Label(
                entrada.contenido()
        );

        contenido.setWrapText(true);

        contenido.setMaxWidth(
                Double.MAX_VALUE
        );

        contenido.setStyle("""
            -fx-font-size: 14px;
            -fx-line-spacing: 3px;
        """);

        // ============================================================
        // ELIMINAR
        // ============================================================

        Button eliminar =
                new Button("Eliminar");

        eliminar.setOnAction(
                evento -> eliminarEntrada(entrada)
        );

        HBox informacion = new HBox(
                10,
                fecha,
                hora
        );

        informacion.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox acciones = new HBox(
                eliminar
        );

        acciones.setAlignment(
                Pos.CENTER_RIGHT
        );

        tarjeta.getChildren().addAll(
                informacion,
                contenido,
                acciones
        );

        return tarjeta;
    }

    private void mostrarFormularioNuevaEntrada(
            Stage ventanaPadre
    ) {

        Stage formulario = new Stage();

        formulario.setTitle(
                "Nueva entrada"
        );

        formulario.initOwner(
                ventanaPadre
        );

        formulario.initModality(
                Modality.APPLICATION_MODAL
        );

        VBox raiz = new VBox(12);

        raiz.setPadding(
                new Insets(20)
        );

        Label titulo = new Label(
                "Nueva entrada del diario"
        );

        titulo.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
        """);

        Label etiquetaFecha = new Label(
                "Fecha:"
        );

        DatePicker fecha =
                new DatePicker(LocalDate.now());

        Label etiquetaCumplimiento =
                new Label("Realización asociada:");

        ComboBox<CumplimientoDiario> selectorCumplimiento =
                new ComboBox<>();

        selectorCumplimiento.getItems().addAll(
                servicioDiario.listarCumplimientos(actividadId)
        );

        selectorCumplimiento.getSelectionModel().clearSelection();
        selectorCumplimiento.setPromptText("Sin asociar");    

        Label etiquetaContenido = new Label(
                "¿Qué quieres escribir?"
        );

        TextArea contenido =
                new TextArea();

        contenido.setPromptText(
                "Escribe aquí tus pensamientos, "
                + "reflexiones o notas..."
        );

        contenido.setWrapText(true);

        contenido.setPrefRowCount(8);

        Button guardar =
                new Button("Guardar");

        Button cancelar =
                new Button("Cancelar");

        guardar.setOnAction(
                evento -> {

                    String texto =
                            contenido.getText();

                    if (texto == null
                            || texto.isBlank()) {

                        mostrarAdvertencia(
                                "Escribe algo antes de guardar."
                        );

                        return;
                    }

                    try {

                        servicioDiario.crearEntrada(
                                actividadId,
                                null,
                                fecha.getValue(),
                                texto
                        );

                        formulario.close();

                        cargarEntradas();

                    } catch (ErrorPersistencia e) {

                        mostrarError(
                                "No se pudo guardar la entrada.",
                                e.getMessage()
                        );
                    }
                }
        );

        cancelar.setOnAction(
                evento -> formulario.close()
        );

        HBox botones = new HBox(
                10,
                cancelar,
                guardar
        );

        botones.setAlignment(
                Pos.CENTER_RIGHT
        );

        raiz.getChildren().addAll(
                titulo,
                etiquetaFecha,
                fecha,
                etiquetaContenido,
                contenido,
                botones
        );

        Scene escena = new Scene(
                raiz,
                500,
                450
        );

        formulario.setScene(
                escena
        );

        formulario.showAndWait();
    }

    private void eliminarEntrada(
            EntradaDiario entrada
    ) {

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle(
                "Eliminar entrada"
        );

        confirmacion.setHeaderText(
                "¿Eliminar esta entrada?"
        );

        confirmacion.setContentText(
                "Esta acción no se puede deshacer."
        );

        confirmacion.showAndWait()
                .ifPresent(respuesta -> {

                    if (respuesta == javafx.scene.control.ButtonType.OK) {

                        try {

                            servicioDiario.eliminar(
                                    entrada.id()
                            );

                            cargarEntradas();

                        } catch (ErrorPersistencia e) {

                            mostrarError(
                                    "No se pudo eliminar la entrada.",
                                    e.getMessage()
                            );
                        }
                    }
                });
    }

    private String obtenerHoraTexto(
            LocalDateTime fechaHora
    ) {

        if (fechaHora == null) {
            return "";
        }

        return fechaHora
                .toLocalTime()
                .format(FORMATO_HORA);
    }

    private void mostrarAdvertencia(
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alerta.setTitle("Diario");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void mostrarError(
            String titulo,
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alerta.setTitle("Diario");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}