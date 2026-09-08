package com.thiarasos.agenda;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Actividad {

    private final long id;

    private final DayOfWeek dia;

    private final LocalTime hora;

    private final String materia;

    private final String titulo;

    private final String contenido;

    private final String duracion;

    private final TipoActividad tipo;

    private final int minutosAnticipacion;


    public Actividad(
            long id,
            DayOfWeek dia,
            LocalTime hora,
            String materia,
            String titulo,
            String contenido,
            String duracion,
            TipoActividad tipo,
            int minutosAnticipacion
    ) {

        this.id = id;
        this.dia = dia;
        this.hora = hora;
        this.materia = materia;
        this.titulo = titulo;
        this.contenido = contenido;
        this.duracion = duracion;
        this.tipo = tipo;
        this.minutosAnticipacion = minutosAnticipacion;
    }

    public Actividad(DayOfWeek dia, LocalTime hora, String materia, String titulo,
                     String contenido, String duracion, TipoActividad tipo, int minutosAnticipacion) {
        this(0, dia, hora, materia, titulo, contenido, duracion, tipo, minutosAnticipacion);
    }

    public long getId() {
        return id;
    }


    public DayOfWeek getDia() {
        return dia;
    }


    public LocalTime getHora() {
        return hora;
    }


    public String getMateria() {
        return materia;
    }


    public String getTitulo() {
        return titulo;
    }


    public String getContenido() {
        return contenido;
    }


    public String getDuracion() {
        return duracion;
    }


    public TipoActividad getTipo() {
        return tipo;
    }


    public int getMinutosAnticipacion() {
        return minutosAnticipacion;
    }


    public LocalTime getHoraRecordatorio() {

        return hora.minusMinutes(
                minutosAnticipacion
        );
    }


    @Override
    public String toString() {

        return String.format(
                "%s | %s | %s | %s",
                dia,
                hora,
                materia,
                titulo
        );
    }
}
