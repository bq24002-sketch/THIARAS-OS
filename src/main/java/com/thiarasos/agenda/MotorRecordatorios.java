package com.thiarasos.agenda;

import com.thiarasos.registro.RegistroEjecuciones;
import com.thiarasos.correo.ServicioCorreo;

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class MotorRecordatorios {

    /*
     * Tiempo máximo que permitimos recuperar automáticamente
     * un recordatorio atrasado.
     *
     * Ejemplo:
     *
     * 14:30 → recordatorio
     * 16:00 → THIARAS OS arranca
     *
     * Se recupera porque han pasado menos de 24 horas.
     */
    private static final long HORAS_MAXIMAS_RECUPERACION = 24;

    private final ProveedorAgenda proveedorAgenda;
    private final ServicioCorreo servicioCorreo;
    private final RegistroEjecuciones registro;

    /*
     * Guarda los recordatorios procesados durante
     * la ejecución actual.
     */
    private final Set<String> recordatoriosProcesados;


    public MotorRecordatorios(
            Agenda agenda,
            ServicioCorreo servicioCorreo,
            RegistroEjecuciones registro
    ) {

        this(() -> agenda, servicioCorreo, registro);
    }

    public MotorRecordatorios(
            ProveedorAgenda proveedorAgenda,
            ServicioCorreo servicioCorreo,
            RegistroEjecuciones registro
    ) {

        this.proveedorAgenda = proveedorAgenda;
        this.servicioCorreo = servicioCorreo;
        this.registro = registro;

        this.recordatoriosProcesados =
                new HashSet<>();
    }


    /*
     * Revisa la agenda y determina qué recordatorios
     * deben ejecutarse o recuperarse.
     */
    public void ejecutar() {

        Agenda agenda = proveedorAgenda.cargar();

        LocalDateTime ahora =
                LocalDateTime.now();

        LocalDate fecha =
                ahora.toLocalDate();

        DayOfWeek dia =
                ahora.getDayOfWeek();


        for (
                Actividad actividad :
                agenda.actividadesDelDia(dia)
        ) {

            LocalTime horaRecordatorio =
                    actividad.getHoraRecordatorio();


            LocalDateTime momentoRecordatorio =
                    LocalDateTime.of(
                            fecha,
                            horaRecordatorio
                    );


            String identificador =
                    construirIdentificador(
                            actividad,
                            momentoRecordatorio
                    );


            /*
             * Primero comprobamos si este recordatorio
             * ya fue procesado anteriormente.
             *
             * Esto consulta el archivo persistente.
             */
            if (
                    registro.yaFueProcesado(
                            identificador
                    )
            ) {

                recordatoriosProcesados.add(
                        identificador
                );

                continue;
            }


            /*
             * También evitamos procesarlo dos veces
             * durante la misma ejecución.
             */
            if (
                    recordatoriosProcesados.contains(
                            identificador
                    )
            ) {

                continue;
            }


            /*
             * -------------------------------------------------
             * RECORDATORIO NORMAL
             * -------------------------------------------------
             *
             * Si estamos exactamente en el minuto programado.
             */
            if (
                    ahora.getHour()
                            == momentoRecordatorio.getHour()

                    &&

                    ahora.getMinute()
                            == momentoRecordatorio.getMinute()
            ) {

                enviarNormal(
                        actividad,
                        momentoRecordatorio,
                        identificador
                );

                continue;
            }


            /*
             * -------------------------------------------------
             * RECORDATORIO ATRASADO
             * -------------------------------------------------
             *
             * Si la hora programada ya pasó.
             */
            if (
                    ahora.isAfter(
                            momentoRecordatorio
                    )
            ) {

                Duration atraso =
                        Duration.between(
                                momentoRecordatorio,
                                ahora
                        );


                long horasAtraso =
                        atraso.toHours();


                /*
                 * Solamente recuperamos recordatorios
                 * dentro de la ventana permitida.
                 */
                if (
                        horasAtraso
                                <= HORAS_MAXIMAS_RECUPERACION
                ) {

                    enviarAtrasado(
                            actividad,
                            momentoRecordatorio,
                            identificador,
                            atraso
                    );
                }
            }
        }
    }


    /*
     * ---------------------------------------------------------
     * ENVÍO NORMAL
     * ---------------------------------------------------------
     */
    private void enviarNormal(
            Actividad actividad,
            LocalDateTime momentoRecordatorio,
            String identificador
    ) {

        System.out.println();
        System.out.println(
                "★ RECORDATORIO DETECTADO"
        );

        System.out.println(
                "  → "
                        + actividad.getTitulo()
        );

        System.out.println(
                "  → Momento: "
                        + momentoRecordatorio
        );


        try {

            boolean enviado =
                    servicioCorreo.enviar(
                            actividad.getTitulo(),
                            actividad.getMateria(),
                            actividad.getContenido(),
                            actividad.getDuracion()
                    );


            if (enviado) {

                recordatoriosProcesados.add(
                        identificador
                );

                registro.registrar(
                        identificador,
                        "ENVIADO"
                );

                System.out.println(
                        "✔ Recordatorio procesado correctamente."
                );

            } else {

                registro.registrar(
                        identificador,
                        "ERROR"
                );

                System.out.println(
                        "⚠ El correo no fue enviado."
                );

                System.out.println(
                        "  El recordatorio NO se marcará como procesado."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "❌ No se pudo procesar el recordatorio."
            );

            System.out.println(
                    "   Motivo: "
                            + e.getMessage()
            );
        }
    }


    /*
     * ---------------------------------------------------------
     * ENVÍO DE RECORDATORIO ATRASADO
     * ---------------------------------------------------------
     */
    private void enviarAtrasado(
            Actividad actividad,
            LocalDateTime momentoRecordatorio,
            String identificador,
            Duration atraso
    ) {

        System.out.println();
        System.out.println(
                "⚠ RECORDATORIO ATRASADO"
        );

        System.out.println(
                "  → "
                        + actividad.getTitulo()
        );

        System.out.println(
                "  → Hora programada: "
                        + momentoRecordatorio
        );

        System.out.println(
                "  → Atraso: "
                        + formatearDuracion(atraso)
        );

        System.out.println(
                "  → Reenviando..."
        );


        try {

            boolean enviado =
                    servicioCorreo.enviarAtrasado(
                            actividad.getTitulo(),
                            actividad.getMateria(),
                            actividad.getContenido(),
                            actividad.getDuracion(),
                            momentoRecordatorio,
                            atraso
                    );


            if (enviado) {

                recordatoriosProcesados.add(
                        identificador
                );

                registro.registrar(
                        identificador,
                        "ATRASADO_ENVIADO"
                );

                System.out.println(
                        "✔ Recordatorio atrasado recuperado correctamente."
                );

            } else {

                registro.registrar(
                        identificador,
                        "ERROR"
                );

                System.out.println(
                        "⚠ El correo atrasado no fue enviado."
                );

                System.out.println(
                        "  El recordatorio permanecerá pendiente."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "❌ No se pudo recuperar el recordatorio atrasado."
            );

            System.out.println(
                    "   Motivo: "
                            + e.getMessage()
            );
        }
    }


    /*
     * Convierte una duración a un texto legible.
     *
     * Ejemplos:
     *
     * 25 minutos
     * 2 h 30 min
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


    /*
     * Genera un identificador único para cada
     * ejecución de cada actividad.
     */
    private String construirIdentificador(
            Actividad actividad,
            LocalDateTime momentoRecordatorio
    ) {

        return momentoRecordatorio.toLocalDate()
                + "|"
                + actividad.getId()
                + "|"
                + actividad.getDia()
                + "|"
                + actividad.getHora()
                + "|"
                + actividad.getTitulo();
    }
}
