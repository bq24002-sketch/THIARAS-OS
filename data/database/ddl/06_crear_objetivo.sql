-- ============================================================
-- THIARAS OS
-- OBJETIVOS
-- ============================================================

CREATE TABLE objetivo (

    id BIGINT GENERATED ALWAYS AS IDENTITY,

    nombre VARCHAR(150) NOT NULL,

    descripcion TEXT,

    tipo_id INTEGER,

    metrica VARCHAR(30) NOT NULL,

    meta INTEGER NOT NULL,

    periodo VARCHAR(30) NOT NULL,

    fecha_inicio DATE NOT NULL,

    fecha_fin DATE,

    activo BOOLEAN NOT NULL DEFAULT TRUE,

    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_objetivo
        PRIMARY KEY (id),

    CONSTRAINT fk_objetivo_tipo
        FOREIGN KEY (tipo_id)
        REFERENCES tipo_actividad(id),

    CONSTRAINT chk_objetivo_metrica
        CHECK (
            metrica IN (
                'MINUTOS',
                'ACTIVIDADES'
            )
        ),

    CONSTRAINT chk_objetivo_periodo
        CHECK (
            periodo IN (
                'DIARIO',
                'SEMANAL',
                'MENSUAL',
                'PERSONALIZADO'
            )
        ),

    CONSTRAINT chk_objetivo_meta
        CHECK (
            meta > 0
        ),

    CONSTRAINT chk_objetivo_fechas
        CHECK (
            fecha_fin IS NULL
            OR fecha_fin >= fecha_inicio
        )
);


CREATE INDEX idx_objetivo_tipo
    ON objetivo(tipo_id);

CREATE INDEX idx_objetivo_fecha
    ON objetivo(fecha_inicio, fecha_fin);

CREATE INDEX idx_objetivo_activo
    ON objetivo(activo);