package com.thiarasos;

import com.thiarasos.agenda.Agenda;
import com.thiarasos.agenda.CargadorAgenda;
import com.thiarasos.agenda.MotorRecordatorios;
import com.thiarasos.correo.ServicioCorreo;
import com.thiarasos.config.Configuracion;
import com.thiarasos.registro.RegistroEjecuciones;
import com.thiarasos.estado.GestorPendientes;

public class Main {

    public static void main(String[] args) {

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                    THIARAS OS"
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

        CargadorAgenda cargador =
                new CargadorAgenda();

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
// GESTION DE PENDIENTES
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
                agenda,
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