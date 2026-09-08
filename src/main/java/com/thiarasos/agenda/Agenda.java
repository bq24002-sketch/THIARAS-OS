package com.thiarasos.agenda;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Agenda {

    private final List<Actividad> actividades;


    public Agenda() {

        actividades = new ArrayList<>();
    }


    public void agregar(Actividad actividad) {

        actividades.add(actividad);
    }


    public List<Actividad> obtenerTodas() {

        return List.copyOf(actividades);
    }


    public List<Actividad> actividadesDelDia(
            DayOfWeek dia
    ) {

        return actividades.stream()
                .filter(actividad ->
                        actividad.getDia() == dia
                )
                .sorted(
                        Comparator.comparing(
                                Actividad::getHora
                        )
                )
                .toList();
    }


    public List<Actividad> actividadesDeHoy() {

        return actividadesDelDia(
                LocalDate.now().getDayOfWeek()
        );
    }


    public List<Actividad> proximasActividades() {

        DayOfWeek diaActual =
                LocalDate.now().getDayOfWeek();

        LocalTime horaActual =
                LocalTime.now();

        return actividades.stream()

                .filter(actividad -> {

                    if (
                            actividad.getDia()
                                    .getValue()
                            >
                            diaActual.getValue()
                    ) {
                        return true;
                    }

                    return actividad.getDia()
                                    == diaActual
                            &&
                            actividad.getHora()
                                    .isAfter(horaActual);
                })

                .sorted(
                        Comparator
                                .comparing(
                                        (Actividad a) ->
                                                a.getDia()
                                                        .getValue()
                                )
                                .thenComparing(
                                        Actividad::getHora
                                )
                )

                .toList();
    }


    public Actividad siguienteActividad() {

        return proximasActividades()
                .stream()
                .findFirst()
                .orElse(null);
    }


    public boolean estaVacia() {

        return actividades.isEmpty();
    }


    public int cantidad() {

        return actividades.size();
    }
}