package com.thiarasos.agenda;

import com.thiarasos.persistencia.Recordatorio;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class Actividad {

    private final long id;

    private final DayOfWeek dia;

    private final LocalTime hora;

    private final String materia;

    private final String titulo;

    private final String contenido;

    private final String duracion;

    private final TipoActividad tipo;

    private final List<Recordatorio> recordatorios;


    public Actividad(
            long id,
            DayOfWeek dia,
            LocalTime hora,
            String materia,
            String titulo,
            String contenido,
            String duracion,
            TipoActividad tipo,
            List<Recordatorio> recordatorios
    ) {

        this.id = id;
        this.dia = dia;
        this.hora = hora;
        this.materia = materia;
        this.titulo = titulo;
        this.contenido = contenido;
        this.duracion = duracion;
        this.tipo = tipo;
        this.recordatorios = recordatorios == null
                ? List.of()
                : List.copyOf(recordatorios);
    }


    public Actividad(
            DayOfWeek dia,
            LocalTime hora,
            String materia,
            String titulo,
            String contenido,
            String duracion,
            TipoActividad tipo,
            List<Recordatorio> recordatorios
    ) {
        this(
                0,
                dia,
                hora,
                materia,
                titulo,
                contenido,
                duracion,
                tipo,
                recordatorios
        );
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


    public List<Recordatorio> getRecordatorios() {
        return recordatorios;
    }


    public List<LocalTime> getHorasRecordatorio() {

        if (hora == null) {
            return List.of();
        }

        return recordatorios.stream()
                .filter(Recordatorio::activo)
                .map(recordatorio ->
                        hora.minusMinutes(recordatorio.minutosAnticipacion())
                )
                .toList();
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