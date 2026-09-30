package com.thiarasos.recordatorios;

import com.thiarasos.agenda.Agenda;
import com.thiarasos.agenda.CargadorAgendaBaseDatos;
import com.thiarasos.agenda.MotorRecordatorios;
import com.thiarasos.correo.ServicioCorreo;
import com.thiarasos.config.Configuracion;
import com.thiarasos.estado.GestorPendientes;
import com.thiarasos.persistencia.ConexionPostgres;
import com.thiarasos.registro.RegistroEjecuciones;

public class RecordatorioMain {

    public static void main(String[] args) {

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                    THIARAS OS"
        );

        System.out.println(
                "              MOTOR DE RECORDATORIOS"
        );

        System.out.println(
                "============================================================"
        );

        System.out.println();

        // =====================================================
        // CONFIGURACIÓN
        // =====================================================

        Configuracion configuracion =
                new Configuracion();

        // =====================================================
        // SERVICIO DE CORREO
        // =====================================================

        ServicioCorreo servicioCorreo =
                new ServicioCorreo(
                        configuracion
                );

        // =====================================================
        // CARGAR AGENDA
        // =====================================================

        CargadorAgendaBaseDatos cargador =
                new CargadorAgendaBaseDatos(
                        new ConexionPostgres(configuracion)
                );

        Agenda agenda =
                cargador.cargar();

        System.out.println(
                "Actividades cargadas: "
                        + agenda.cantidad()
        );

        System.out.println();

        // =====================================================
        // REGISTRO DE EJECUCIONES
        // =====================================================

        RegistroEjecuciones registro =
                new RegistroEjecuciones();

        // =====================================================
        // GESTIÓN DE PENDIENTES
        // =====================================================

        GestorPendientes gestorPendientes =
                new GestorPendientes(
                        agenda,
                        registro
                );

        gestorPendientes.mostrarEstado();

        // =====================================================
        // MOTOR DE RECORDATORIOS
        // =====================================================

        MotorRecordatorios motor =
                new MotorRecordatorios(
                        cargador,
                        servicioCorreo,
                        registro
                );

        System.out.println(
                "Motor de recordatorios iniciado."
        );

        System.out.println(
                "Sistema ejecutándose..."
        );

        System.out.println(
                "Presiona Ctrl + C para detenerlo."
        );

        System.out.println();

        // =====================================================
        // LOOP PRINCIPAL
        // =====================================================

        while (true) {

            try {

                motor.ejecutar();

                Thread.sleep(1000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.out.println(
                        "Sistema interrumpido."
                );

                break;
            }
        }
    }
}