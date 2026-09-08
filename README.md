# THIARAS-OS
Sistema de gestión de tareas personales con recordatorios

## Persistencia PostgreSQL

La capa Java de persistencia usa las funciones definidas en `data/database/ddl` para iniciar y completar actividades, consultar su estado y marcar notificaciones como leidas. Las consultas de dashboard, actividades y notificaciones se empaquetan desde `data/database/queries`, sin duplicarlas en Java.

La conexion se configura con `DB_URL` y `DB_USUARIO` en `src/main/resources/config.properties`. La contrasena se lee desde la variable de entorno `THIARAS_DB_PASSWORD` y no debe incluirse en el repositorio.

La futura interfaz puede obtener el servicio listo para usar con `ServiciosPersistencia.crearServicioActividad(configuracion)` y llamar a `iniciarYRefrescar`, `completarYRefrescar` o `marcarNotificacionLeidaYRefrescar`.

Tambien existe un modo de consola para comprobar la integracion: `--db dashboard`, `--db iniciar <actividadId>`, `--db completar <actividadId>`, `--db estado <actividadId>` y `--db leer <notificacionId>`. El modo de recordatorios se inicia de forma explicita con `--recordatorios`.

## Aplicacion de escritorio

La aplicacion inicia en modo escritorio con `mvn javafx:run`. Incluye dashboard, acciones de iniciar y completar, notificaciones pendientes y formulario de nueva actividad. El motor de correos se inicia aparte con `--recordatorios`.
