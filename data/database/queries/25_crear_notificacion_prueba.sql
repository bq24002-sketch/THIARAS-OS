-- ============================================================
-- THIARAS OS
-- NOTIFICACIÓN DE PRUEBA
-- ============================================================

BEGIN;

INSERT INTO notificacion (
    tipo,
    titulo,
    mensaje
)
VALUES (
    'SISTEMA',
    'Prueba de THIARAS OS',
    'Esta es una notificación de prueba para validar el sistema.'
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
    creada_en,
    leida
FROM notificacion
ORDER BY id DESC
LIMIT 1;