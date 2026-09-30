-- ============================================================
-- THIARAS OS
-- DDL - DIARIO PERSONAL
-- ============================================================

CREATE TABLE diario (

    id BIGINT GENERATED ALWAYS AS IDENTITY,

    actividad_id BIGINT NOT NULL,

    cumplimiento_id BIGINT,

    fecha DATE NOT NULL DEFAULT CURRENT_DATE,

    contenido TEXT NOT NULL,

    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_diario
        PRIMARY KEY (id),

    CONSTRAINT fk_diario_actividad
        FOREIGN KEY (actividad_id)
        REFERENCES actividad(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_diario_cumplimiento
        FOREIGN KEY (cumplimiento_id)
        REFERENCES cumplimiento_actividad(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_diario_contenido
        CHECK (length(trim(contenido)) > 0)
);


-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX idx_diario_actividad
    ON diario(actividad_id);

CREATE INDEX idx_diario_fecha
    ON diario(fecha);

CREATE INDEX idx_diario_creado_en
    ON diario(creado_en);