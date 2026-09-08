-- ============================================================
-- THIARAS OS
-- RESUMEN DE NOTIFICACIONES
-- ============================================================

SELECT
    COUNT(*) AS total,

    COUNT(*) FILTER (
        WHERE leida = FALSE
    ) AS pendientes,

    COUNT(*) FILTER (
        WHERE leida = TRUE
    ) AS leidas

FROM notificacion;