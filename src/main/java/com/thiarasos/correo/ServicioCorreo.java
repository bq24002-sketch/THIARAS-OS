package com.thiarasos.correo;

import com.thiarasos.config.Configuracion;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.time.Duration;
import java.time.LocalDateTime;

public class ServicioCorreo {

    private final Configuracion configuracion;

    private final String emailOrigen;
    private final String passwordApp;
    private final String emailDestino;


    public ServicioCorreo(Configuracion configuracion) {

        this.configuracion = configuracion;

        this.emailOrigen =
                System.getenv("EMAIL_ORIGEN");

        this.passwordApp =
                System.getenv("PASSWORD_APP");

        this.emailDestino =
                System.getenv("EMAIL_DESTINO");
    }


    public boolean enviar(
            String asunto,
            String materia,
            String contenido,
            String duracion
    ) {

        try {

            verificarCredenciales();

            String mensaje =
                    construirMensaje(
                            materia,
                            contenido,
                            duracion
                    );

            Properties propiedades =
                    configurarSMTP();

            Session sesion =
                    crearSesion(propiedades);

            Message correo =
                    construirCorreo(
                            sesion,
                            asunto,
                            mensaje
                    );

            Transport.send(correo);

            System.out.println(
                    "✔ Correo enviado: " + asunto
            );

            return true;

        } catch (MessagingException e) {

            System.out.println(
                    "❌ Error enviando correo: "
                            + e.getMessage()
            );

            return false;

        } catch (IllegalStateException e) {

            System.out.println(
                    "❌ Configuración de correo incorrecta: "
                            + e.getMessage()
            );

            return false;
        }
    }


    public boolean enviarAtrasado(
            String asunto,
            String materia,
            String contenido,
            String duracion,
            LocalDateTime momentoOriginal,
            Duration atraso
    ) {

        try {

            verificarCredenciales();

            String mensaje =
                    construirMensajeAtrasado(
                            materia,
                            contenido,
                            duracion,
                            momentoOriginal,
                            atraso
                    );

            Properties propiedades =
                    configurarSMTP();

            Session sesion =
                    crearSesion(propiedades);

            Message correo =
                    construirCorreo(
                            sesion,
                            "⚠️ ATRASADO — " + asunto,
                            mensaje
                    );

            Transport.send(correo);

            System.out.println(
                    "✔ Correo atrasado enviado: "
                            + asunto
            );

            return true;

        } catch (MessagingException e) {

            System.out.println(
                    "❌ Error enviando correo atrasado: "
                            + e.getMessage()
            );

            return false;

        } catch (IllegalStateException e) {

            System.out.println(
                    "❌ Configuración de correo incorrecta: "
                            + e.getMessage()
            );

            return false;
        }
    }


    private void verificarCredenciales() {

        if (
                emailOrigen == null ||
                passwordApp == null ||
                emailDestino == null
        ) {

            throw new IllegalStateException(
                    "Variables de entorno no configuradas."
            );
        }
    }


    private String construirMensaje(
            String materia,
            String contenido,
            String duracion
    ) {

        return """
                
                ══════════════════════════════════
                
                📚 %s
                ⏱ Duración: %s
                
                %s
                
                ══════════════════════════════════
                """.formatted(
                        materia,
                        duracion,
                        contenido
                );
    }


    private String construirMensajeAtrasado(
            String materia,
            String contenido,
            String duracion,
            LocalDateTime momentoOriginal,
            Duration atraso
    ) {

        long minutos =
                atraso.toMinutes();

        long horas =
                minutos / 60;

        long minutosRestantes =
                minutos % 60;

        String tiempoAtraso;

        if (horas > 0) {

            tiempoAtraso =
                    horas
                            + " h "
                            + minutosRestantes
                            + " min";

        } else {

            tiempoAtraso =
                    minutos
                            + " minutos";
        }


        return """

                ╔══════════════════════════════════╗
                ║     ⚠️ RECORDATORIO ATRASADO     ║
                ╚══════════════════════════════════╝

                📚 %s

                🕐 Hora programada:
                %s

                ⏳ Tiempo de atraso:
                %s

                Este recordatorio no pudo enviarse
                a su hora programada porque THIARAS OS
                no estaba ejecutándose.

                ──────────────────────────────────

                ⏱ Duración: %s

                %s

                ══════════════════════════════════
                """.formatted(
                        materia,
                        momentoOriginal,
                        tiempoAtraso,
                        duracion,
                        contenido
                );
    }


    private Properties configurarSMTP() {

        Properties propiedades =
                new Properties();

        propiedades.put(
                "mail.smtp.auth",
                "true"
        );

        propiedades.put(
                "mail.smtp.starttls.enable",
                "true"
        );

        propiedades.put(
                "mail.smtp.host",
                configuracion.obtener(
                        "SMTP_SERVIDOR"
                )
        );

        propiedades.put(
                "mail.smtp.port",
                configuracion.obtener(
                        "SMTP_PUERTO"
                )
        );

        return propiedades;
    }


    private Session crearSesion(
            Properties propiedades
    ) {

        return Session.getInstance(
                propiedades,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                emailOrigen,
                                passwordApp
                        );
                    }
                }
        );
    }


    private Message construirCorreo(
            Session sesion,
            String asunto,
            String mensaje
    ) throws MessagingException {

        Message correo =
                new MimeMessage(sesion);

        correo.setFrom(
                new InternetAddress(emailOrigen)
        );

        correo.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(
                        emailDestino
                )
        );

        correo.setSubject(asunto);
        correo.setText(mensaje);

        return correo;
    }
}