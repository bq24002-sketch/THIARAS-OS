package com.thiarasos.agenda;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class CargadorAgenda {

    private final ObjectMapper mapper;

    public CargadorAgenda() {

        mapper = new ObjectMapper();
    }

    public Agenda cargar() {

        Agenda agenda = new Agenda();

        try (
                InputStream archivo =
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream("horario.json")
        ) {

            if (archivo == null) {

                throw new RuntimeException(
                        "No se encontró horario.json"
                );
            }

            List<ActividadJSON> actividades =
                    mapper.readValue(
                            archivo,
                            new TypeReference<>() {}
                    );

            for (ActividadJSON datos : actividades) {

                Actividad actividad =
                        new Actividad(
                                DayOfWeek.valueOf(
                                        datos.dia
                                ),
                                LocalTime.parse(
                                        datos.hora
                                ),
                                datos.materia,
                                datos.titulo,
                                datos.contenido,
                                datos.duracion,
                                TipoActividad.valueOf(
                                        datos.tipo
                                ),
                                datos.minutosAnticipacion
                        );

                agenda.agregar(actividad);
            }

            return agenda;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error leyendo horario.json",
                    e
            );
        }
    }


    private static class ActividadJSON {

        public String dia;

        public String hora;

        public String materia;

        public String titulo;

        public String contenido;

        public String duracion;

        public String tipo;

        public int minutosAnticipacion;
    }
}