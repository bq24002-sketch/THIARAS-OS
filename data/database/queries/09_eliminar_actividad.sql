-- ============================================================
-- THIARAS OS
-- CRUD - ELIMINAR ACTIVIDAD
-- ============================================================
--
-- Eliminación física de una actividad.
--
-- IMPORTANTE:
-- Esta operación elimina definitivamente la actividad
-- y sus recordatorios asociados.
--
-- Se utiliza una transacción para garantizar atomicidad.
-- ============================================================


BEGIN;


-- ------------------------------------------------------------
-- 1. ELIMINAR RECORDATORIOS ASOCIADOS
-- ------------------------------------------------------------

DELETE FROM recordatorio
WHERE actividad_id = 10;


-- ------------------------------------------------------------
-- 2. ELIMINAR ACTIVIDAD
-- ------------------------------------------------------------

DELETE FROM actividad
WHERE id = 10;


-- ------------------------------------------------------------
-- 3. VERIFICAR QUE FUE ELIMINADA
-- ------------------------------------------------------------

SELECT
    id,
    titulo,
    activa
FROM actividad
WHERE id = 10;


-- ------------------------------------------------------------
-- 4. CONFIRMAR TRANSACCIÓN
-- ------------------------------------------------------------

COMMIT;