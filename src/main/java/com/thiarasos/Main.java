package com.thiarasos;

import com.thiarasos.consola.ConsolaBaseDatos;
import com.thiarasos.desktop.AplicacionThiaras;
import com.thiarasos.config.Configuracion;
import com.thiarasos.servicio.ServiciosPersistencia;
import com.thiarasos.recordatorios.RecordatorioMain;

public class Main {

    public static void main(String[] args) {

        if (args.length > 0 && "--db".equals(args[0])) {
            Configuracion configuracion = new Configuracion();

            String[] argumentosBaseDatos =
                    java.util.Arrays.copyOfRange(args, 1, args.length);

            new ConsolaBaseDatos(
                    ServiciosPersistencia.crearServicioActividad(configuracion)
            ).ejecutar(argumentosBaseDatos);

            return;
        }

        if (args.length == 0 || "--desktop".equals(args[0])) {
            AplicacionThiaras.main(args);
            return;
        }

        if ("--recordatorios".equals(args[0])) {
            RecordatorioMain.main(
                    java.util.Arrays.copyOfRange(args, 1, args.length)
            );
            return;
        }

        System.out.println("Uso: --desktop | --recordatorios | --db");
    }
}