-- ============================================================
-- THIARAS OS
-- GENERAR NOTIFICACIONES DE LOGROS
-- ============================================================

BEGIN;

INSERT INTO notificacion (
    tipo,
    titulo,
    mensaje,
    logro_id
)
SELECT
    'LOGRO',
    '🎉 ' || l.nombre,
    '¡Felicidades! Has desbloqueado el logro "' ||
        l.nombre ||
        '".',

    lo.logro_id

FROM logro_obtenido lo

JOIN logro l
    ON l.id = lo.logro_id

WHERE NOT EXISTS (
    SELECT 1
    FROM notificacion n
    WHERE n.tipo = 'LOGRO'
      AND n.logro_id = lo.logro_id
);

COMMIT;


-- ============================================================
-- VERIFICACIÓN
-- ============================================================

SELECT
    n.id,
    n.tipo,
    n.titulo,
    n.mensaje,
    n.logro_id,
    n.creada_en,
    n.leida
FROM notificacion n
WHERE n.tipo = 'LOGRO'
ORDER BY n.id;