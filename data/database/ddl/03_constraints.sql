-- ============================================================
-- THIARAS OS
-- DDL - RESTRICCIONES ADICIONALES
-- ============================================================


-- ============================================================
-- 1. RESTRICCIONES DE RECURRENCIA
-- ============================================================

ALTER TABLE recurrencia

ADD CONSTRAINT chk_recurrencia_tipo

CHECK (

    tipo IN (

        'DIARIA',

        'SEMANAL',

        'MENSUAL'

    )

);