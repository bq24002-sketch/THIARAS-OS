-- ============================================================
-- THIARAS OS
-- INSTALADOR DEL ESQUEMA
-- ============================================================

\encoding UTF8

\echo '============================================'
\echo ' INSTALANDO ESQUEMA THIARAS OS'
\echo '============================================'

\echo '1. Creando tablas...'
\i 'ddl/02_create_tables.sql'

\echo '2. Aplicando restricciones...'
\i 'ddl/03_constraints.sql'

\echo '3. Creando índices...'
\i 'ddl/04_indexes.sql'

\echo '4. Creando tabla de cumplimiento...'
\i 'ddl/05_crear_cumplimiento.sql'

\echo '5. Creando objetivos...'
\i 'ddl/06_crear_objetivo.sql'

\echo '6. Creando logros...'
\i 'ddl/07_crear_logro.sql'

\echo '7. Creando notificaciones...'
\i 'ddl/08_crear_notificacion.sql'

\echo '8. Creando funciones...'
\i 'ddl/09_crear_funcion_procesar_cumplimiento.sql'
\i 'ddl/10_crear_funcion_iniciar_actividad.sql'
\i 'ddl/11_crear_funcion_completar_actividad.sql'
\i 'ddl/12_crear_funcion_marcar_notificacion_leida.sql'
\i 'ddl/13_crear_funcion_estado_actividad.sql'
\i 'ddl/14_crear_funcion_existe_conflicto.sql'
\echo 'Creando diario...'
\i 'ddl/15_crear_diario.sql'

\echo '============================================'
\echo ' INSTALACION COMPLETADA'
\echo '============================================'