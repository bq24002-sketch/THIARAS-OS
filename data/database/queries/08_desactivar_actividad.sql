-- ============================================================
-- THIARAS OS
-- CRUD - DESACTIVAR ACTIVIDAD
-- ============================================================
--
-- Desactiva una actividad sin eliminarla físicamente.
--
-- Esto permite conservar su información histórica.
-- ============================================================


BEGIN;


-- ------------------------------------------------------------
-- 1. DESACTIVAR ACTIVIDAD
-- ------------------------------------------------------------

UPDATE actividad
SET
    activa = FALSE
WHERE id = 10;


-- ------------------------------------------------------------
-- 2. VERIFICAR ESTADO
-- ------------------------------------------------------------

SELECT
    id,
    titulo,
    activa,
    fecha_inicio,
    fecha_vencimiento,
    recurrente
FROM actividad
WHERE id = 10;


-- ------------------------------------------------------------
-- 3. CONFIRMAR
-- ------------------------------------------------------------

COMMIT;