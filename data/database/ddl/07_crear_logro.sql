-- ============================================================
-- THIARAS OS
-- SISTEMA DE LOGROS
-- ============================================================


-- ============================================================
-- 1. DEFINICIÓN DE LOS LOGROS
-- ============================================================

CREATE TABLE logro (

    id BIGINT GENERATED ALWAYS AS IDENTITY,

    codigo VARCHAR(50) NOT NULL,

    nombre VARCHAR(150) NOT NULL,

    descripcion TEXT,

    metrica VARCHAR(30) NOT NULL,

    meta INTEGER NOT NULL,

    activo BOOLEAN NOT NULL DEFAULT TRUE,

    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_logro
        PRIMARY KEY (id),

    CONSTRAINT uq_logro_codigo
        UNIQUE (codigo),

    CONSTRAINT chk_logro_metrica
        CHECK (
            metrica IN (
                'ACTIVIDADES',
                'MINUTOS'
            )
        ),

    CONSTRAINT chk_logro_meta
        CHECK (
            meta > 0
        )
);


CREATE INDEX idx_logro_activo
    ON logro(activo);


-- ============================================================
-- 2. LOGROS OBTENIDOS
-- ============================================================

CREATE TABLE logro_obtenido (

    id BIGINT GENERATED ALWAYS AS IDENTITY,

    logro_id BIGINT NOT NULL,

    fecha_obtenido DATE NOT NULL,

    momento_obtenido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    valor_alcanzado INTEGER,

    CONSTRAINT pk_logro_obtenido
        PRIMARY KEY (id),

    CONSTRAINT fk_logro_obtenido_logro
        FOREIGN KEY (logro_id)
        REFERENCES logro(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_logro_obtenido
        UNIQUE (logro_id),

    CONSTRAINT chk_logro_valor
        CHECK (
            valor_alcanzado IS NULL
            OR valor_alcanzado > 0
        )
);


CREATE INDEX idx_logro_obtenido_fecha
    ON logro_obtenido(fecha_obtenido);

CREATE INDEX idx_logro_obtenido_logro
    ON logro_obtenido(logro_id);