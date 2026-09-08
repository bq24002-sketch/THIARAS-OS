-- ============================================================
-- THIARAS OS
-- CRUD - MODIFICAR ACTIVIDAD
-- ============================================================
--
-- Modifica los datos principales de una actividad.
-- Los recordatorios NO se modifican en esta operación.
-- ============================================================


BEGIN;


-- ------------------------------------------------------------
-- 1. MODIFICAR ACTIVIDAD
-- ------------------------------------------------------------

UPDATE actividad
SET
    titulo = 'Preparación intensiva examen PostgreSQL',
    contenido = 'Repasar DDL, DML, claves, relaciones, transacciones y consultas.',
    duracion_minutos = 120,
    fecha_vencimiento = DATE '2026-09-10',
    hora = TIME '20:00'
WHERE id = 10;


-- ------------------------------------------------------------
-- 2. VERIFICAR CAMBIOS
-- ------------------------------------------------------------

SELECT
    id,
    titulo,
    contenido,
    duracion_minutos,
    fecha_inicio,
    fecha_vencimiento,
    hora,
    recurrente,
    activa
FROM actividad
WHERE id = 10;


-- ------------------------------------------------------------
-- 3. CONFIRMAR
-- ------------------------------------------------------------

COMMIT;