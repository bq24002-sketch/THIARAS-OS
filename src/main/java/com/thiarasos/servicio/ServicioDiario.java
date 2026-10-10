package com.thiarasos.servicio;

import com.thiarasos.persistencia.EntradaDiario;
import com.thiarasos.persistencia.ErrorPersistencia;
import com.thiarasos.persistencia.RepositorioDiario;
import com.thiarasos.persistencia.CumplimientoDiario;
import java.util.List;

import java.time.LocalDate;
import java.util.List;

public class ServicioDiario {

    private final RepositorioDiario repositorio;

    public ServicioDiario(RepositorioDiario repositorio) {
        this.repositorio = repositorio;
    }
    public List<CumplimientoDiario> listarCumplimientos(long actividadId) {
        return repositorio.listarCumplimientos(actividadId);
    }
    
    public EntradaDiario crearEntrada(
            long actividadId,
            Long cumplimientoId,
            LocalDate fecha,
            String contenido
    ) throws ErrorPersistencia {

        if (actividadId <= 0) {
            throw new IllegalArgumentException(
                    "La actividad debe ser válida."
            );
        }

        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException(
                    "El contenido del diario no puede estar vacío."
            );
        }

        String contenidoLimpio = contenido.trim();

        return repositorio.crear(
                actividadId,
                cumplimientoId,
                fecha,
                contenidoLimpio
        );
    }

    public List<EntradaDiario> obtenerPorActividad(
            long actividadId
    ) throws ErrorPersistencia {

        return repositorio.obtenerPorActividad(actividadId);
    }

    public List<EntradaDiario> obtenerTodas()
            throws ErrorPersistencia {

        return repositorio.obtenerTodas();
    }

    public void eliminar(long id)
            throws ErrorPersistencia {

        repositorio.eliminar(id);
    }
}