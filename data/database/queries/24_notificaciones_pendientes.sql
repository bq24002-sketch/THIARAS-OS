-- ============================================================
-- THIARAS OS
-- NOTIFICACIONES PENDIENTES
-- ============================================================

SELECT
    n.id,
    n.tipo,
    n.titulo,
    n.mensaje,
    n.actividad_id,
    n.logro_id,
    n.creada_en
FROM notificacion n
WHERE n.leida = FALSE
ORDER BY n.creada_en DESC;