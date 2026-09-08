-- ============================================================
-- THIARAS OS
-- DDL - CUMPLIMIENTO DE ACTIVIDADES
-- ============================================================

CREATE TABLE cumplimiento_actividad (

    id BIGINT GENERATED ALWAYS AS IDENTITY,

    actividad_id BIGINT NOT NULL,

    fecha DATE NOT NULL,

    momento_inicio TIMESTAMP,

    momento_fin TIMESTAMP,

    minutos_realizados INTEGER,

    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',

    observaciones TEXT,


    -- --------------------------------------------------------
    -- CLAVE PRIMARIA
    -- --------------------------------------------------------

    CONSTRAINT pk_cumplimiento_actividad
        PRIMARY KEY (id),


    -- --------------------------------------------------------
    -- RELACIÓN CON ACTIVIDAD
    -- --------------------------------------------------------

    CONSTRAINT fk_cumplimiento_actividad
        FOREIGN KEY (actividad_id)
        REFERENCES actividad(id)
        ON DELETE CASCADE,


    -- --------------------------------------------------------
    -- ESTADOS
    -- --------------------------------------------------------

    CONSTRAINT chk_cumplimiento_estado
        CHECK (
            estado IN (
                'PENDIENTE',
                'EN_PROGRESO',
                'COMPLETADA',
                'OMITIDA'
            )
        ),


    -- --------------------------------------------------------
    -- MINUTOS REALIZADOS
    -- --------------------------------------------------------

    CONSTRAINT chk_cumplimiento_minutos
        CHECK (
            minutos_realizados IS NULL
            OR minutos_realizados > 0
        ),


    -- --------------------------------------------------------
    -- MOMENTOS
    -- --------------------------------------------------------

    CONSTRAINT chk_cumplimiento_momentos
        CHECK (
            momento_fin IS NULL
            OR momento_inicio IS NULL
            OR momento_fin >= momento_inicio
        )
);


-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX idx_cumplimiento_actividad
    ON cumplimiento_actividad(actividad_id);

CREATE INDEX idx_cumplimiento_fecha
    ON cumplimiento_actividad(fecha);

CREATE INDEX idx_cumplimiento_estado
    ON cumplimiento_actividad(estado);

CREATE INDEX idx_cumplimiento_actividad_fecha
    ON cumplimiento_actividad(
        actividad_id,
        fecha
    );