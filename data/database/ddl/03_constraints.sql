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


-- ============================================================
-- 2. RESTRICCIONES DE EJECUCIÓN
-- ============================================================

ALTER TABLE ejecucion

ADD CONSTRAINT chk_ejecucion_tipo
CHECK (
    tipo IN (
        'NORMAL',
        'ATRASADO',
        'RECUPERADO'
    )
);


ALTER TABLE ejecucion

ADD CONSTRAINT chk_ejecucion_estado
CHECK (
    estado IN (
        'PENDIENTE',
        'ENVIADO',
        'ERROR',
        'RECUPERADO',
        'CANCELADO'
    )
);


-- ============================================================
-- 3. COHERENCIA TEMPORAL DE EJECUCIONES
-- ============================================================

ALTER TABLE ejecucion

ADD CONSTRAINT chk_ejecucion_momento
CHECK (
    momento_ejecucion IS NULL
    OR momento_ejecucion >= momento_programado
);