-- ============================================================
-- THIARAS OS
-- MARCAR NOTIFICACIÓN COMO LEÍDA
-- ============================================================

BEGIN;

UPDATE notificacion
SET
    leida = TRUE,
    leida_en = CURRENT_TIMESTAMP
WHERE id = (
    SELECT id
    FROM notificacion
    WHERE leida = FALSE
    ORDER BY creada_en ASC
    LIMIT 1
);

COMMIT;


-- ============================================================
-- VERIFICACIÓN
-- ============================================================

SELECT
    id,
    tipo,
    titulo,
    mensaje,
    leida,
    leida_en
FROM notificacion
ORDER BY id DESC
LIMIT 1;