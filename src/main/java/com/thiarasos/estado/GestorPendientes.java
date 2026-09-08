package com.thiarasos.estado;

import com.thiarasos.agenda.Actividad;
import com.thiarasos.agenda.Agenda;
import com.thiarasos.registro.RegistroEjecuciones;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class GestorPendientes {

    private final Agenda agenda;
    private final RegistroEjecuciones registro;


    public GestorPendientes(
            Agenda agenda,
            RegistroEjecuciones registro
    ) {

        this.agenda = agenda;
        this.registro = registro;
    }


    /*
     * Muestra el estado actual de la agenda.
     */
    public void mostrarEstado() {

        LocalDateTime ahora =
                LocalDateTime.now();

        System.out.println();
        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                 ESTADO DE LA AGENDA"
        );

        System.out.println(
                "============================================================"
        );


        mostrarAtrasados(ahora);

        mostrarPendientesDeHoy(ahora);

        mostrarProximos(ahora);


        System.out.println(
                "============================================================"
        );

        System.out.println();
    }


    /*
     * ---------------------------------------------------------
     * ATRASADOS
     * ---------------------------------------------------------
     */
    private void mostrarAtrasados(
            LocalDateTime ahora
    ) {

        boolean encontrados = false;

        System.out.println();
        System.out.println(
                "⚠ ATRASADOS"
        );

        System.out.println(
                "------------------------------------------------------------"
        );


        /*
         * Revisamos solamente las actividades del día actual.
         */
        for (
                Actividad actividad :
                agenda.actividadesDelDia(
                        ahora.getDayOfWeek()
                )
        ) {

            LocalTime hora =
                    actividad.getHoraRecordatorio();

            LocalDateTime momento =
                    LocalDateTime.of(
                            ahora.toLocalDate(),
                            hora
                    );


            if (
                    ahora.isAfter(momento)
                    &&
                    !registro.yaFueProcesado(
                            construirIdentificador(
                                    actividad,
                                    momento
                            )
                    )
            ) {

                Duration atraso =
                        Duration.between(
                                momento,
                                ahora
                        );

                System.out.println(
                        "  "
                                + hora
                                + " | "
                                + actividad.getTitulo()
                );

                System.out.println(
                        "       Atrasado: "
                                + formatearDuracion(
                                        atraso
                                )
                );

                encontrados = true;
            }
        }


        if (!encontrados) {

            System.out.println(
                    "  Ninguno."
            );
        }
    }


    /*
     * ---------------------------------------------------------
     * PENDIENTES DE HOY
     * ---------------------------------------------------------
     */
    private void mostrarPendientesDeHoy(
            LocalDateTime ahora
    ) {

        boolean encontrados = false;

        System.out.println();
        System.out.println(
                "📋 PENDIENTES DE HOY"
        );

        System.out.println(
                "------------------------------------------------------------"
        );


        for (
                Actividad actividad :
                agenda.actividadesDelDia(
                        ahora.getDayOfWeek()
                )
        ) {

            LocalTime hora =
                    actividad.getHoraRecordatorio();

            LocalDateTime momento =
                    LocalDateTime.of(
                            ahora.toLocalDate(),
                            hora
                    );


            /*
             * Solo mostramos actividades futuras
             * que todavía no han sido procesadas.
             */
            if (
                    ahora.isBefore(momento)
                    &&
                    !registro.yaFueProcesado(
                            construirIdentificador(
                                    actividad,
                                    momento
                            )
                    )
            ) {

                System.out.println(
                        "  "
                                + hora
                                + " | "
                                + actividad.getTitulo()
                );

                encontrados = true;
            }
        }


        if (!encontrados) {

            System.out.println(
                    "  Ninguno."
            );
        }
    }


    /*
     * ---------------------------------------------------------
     * PRÓXIMOS
     * ---------------------------------------------------------
     *
     * Muestra actividades de los próximos días.
     */
 private void mostrarProximos(
        LocalDateTime ahora
) {

    boolean encontrados = false;

    System.out.println();
    System.out.println(
            "📅 PRÓXIMOS"
    );

    System.out.println(
            "------------------------------------------------------------"
    );

    LocalDate fecha =
            ahora.toLocalDate();

    /*
     * Revisamos los próximos 7 días.
     */
    for (int i = 1; i <= 7; i++) {

        LocalDate fechaProxima =
                fecha.plusDays(i);

        DayOfWeek dia =
                fechaProxima.getDayOfWeek();

        List<Actividad> actividades =
                agenda.actividadesDelDia(dia);

        for (
                Actividad actividad :
                actividades
        ) {

            LocalDateTime momento =
                    LocalDateTime.of(
                            fechaProxima,
                            actividad.getHoraRecordatorio()
                    );

            String identificador =
                    construirIdentificador(
                            actividad,
                            momento
                    );

            if (
                    !registro.yaFueProcesado(
                            identificador
                    )
            ) {

                System.out.println(
                        "  "
                                + dia
                                + " "
                                + fechaProxima
                                + " "
                                + actividad.getHoraRecordatorio()
                                + " | "
                                + actividad.getTitulo()
                );

                encontrados = true;
            }
        }
    }

    if (!encontrados) {

        System.out.println(
                "  Ninguno."
        );
    }
}

    /*
     * ---------------------------------------------------------
     * IDENTIFICADOR
     * ---------------------------------------------------------
     */
    private String construirIdentificador(
            Actividad actividad,
            LocalDateTime momento
    ) {

        return momento.toLocalDate()
                + "|"
                + actividad.getDia()
                + "|"
                + actividad.getHora()
                + "|"
                + actividad.getTitulo();
    }


    /*
     * ---------------------------------------------------------
     * FORMATEAR DURACIÓN
     * ---------------------------------------------------------
     */
    private String formatearDuracion(
            Duration duracion
    ) {

        long minutos =
                duracion.toMinutes();

        long horas =
                minutos / 60;

        long minutosRestantes =
                minutos % 60;


        if (horas > 0) {

            return horas
                    + " h "
                    + minutosRestantes
                    + " min";
        }


        return minutos
                + " minutos";
    }
}