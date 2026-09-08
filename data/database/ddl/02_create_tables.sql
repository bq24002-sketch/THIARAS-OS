-- ============================================================
-- THIARAS OS
-- DDL - CREACIÓN DE TABLAS
-- ============================================================


-- ============================================================
-- 1. TIPOS DE ACTIVIDAD
-- ============================================================

CREATE TABLE tipo_actividad (

    id              INTEGER GENERATED ALWAYS AS IDENTITY,

    nombre          VARCHAR(50) NOT NULL,

    descripcion     TEXT,

    CONSTRAINT pk_tipo_actividad
        PRIMARY KEY (id),

    CONSTRAINT uq_tipo_actividad_nombre
        UNIQUE (nombre)
);


-- ============================================================
-- 2. ACTIVIDADES
-- ============================================================

CREATE TABLE actividad (

    id                  BIGINT GENERATED ALWAYS AS IDENTITY,

    titulo              VARCHAR(150) NOT NULL,

    materia             VARCHAR(150),

    contenido           TEXT,

    duracion_minutos    INTEGER,

    tipo_id             INTEGER NOT NULL,

    fecha_inicio        DATE NOT NULL,

    fecha_vencimiento   DATE,

    hora                TIME,

    recurrente          BOOLEAN NOT NULL DEFAULT FALSE,

    activa              BOOLEAN NOT NULL DEFAULT TRUE,

    creada_en           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_actividad
        PRIMARY KEY (id),

    CONSTRAINT fk_actividad_tipo
        FOREIGN KEY (tipo_id)
        REFERENCES tipo_actividad (id),

    CONSTRAINT chk_actividad_duracion
        CHECK (
            duracion_minutos IS NULL
            OR duracion_minutos > 0
        ),

    CONSTRAINT chk_actividad_fechas
        CHECK (
            fecha_vencimiento IS NULL
            OR fecha_vencimiento >= fecha_inicio
        )
);


-- ============================================================
-- 3. RECURRENCIA
-- ============================================================

CREATE TABLE recurrencia (

    id                  BIGINT GENERATED ALWAYS AS IDENTITY,

    actividad_id        BIGINT NOT NULL,

    tipo                VARCHAR(30) NOT NULL,

    intervalo           INTEGER NOT NULL DEFAULT 1,

    dia_semana          INTEGER,

    fecha_fin           DATE,

    CONSTRAINT pk_recurrencia
        PRIMARY KEY (id),

    CONSTRAINT fk_recurrencia_actividad
        FOREIGN KEY (actividad_id)
        REFERENCES actividad (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_recurrencia_intervalo
        CHECK (intervalo > 0),

    CONSTRAINT chk_recurrencia_dia
        CHECK (
            dia_semana IS NULL
            OR dia_semana BETWEEN 1 AND 7
        )
  
);


-- ============================================================
-- 4. RECORDATORIOS
-- ============================================================

CREATE TABLE recordatorio (

    id                  BIGINT GENERATED ALWAYS AS IDENTITY,

    actividad_id        BIGINT NOT NULL,

    minutos_anticipacion INTEGER NOT NULL DEFAULT 0,

    activo              BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_recordatorio
        PRIMARY KEY (id),

    CONSTRAINT fk_recordatorio_actividad
        FOREIGN KEY (actividad_id)
        REFERENCES actividad (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_recordatorio_anticipacion
        CHECK (minutos_anticipacion >= 0)
);


-- ============================================================
-- 5. EJECUCIONES
-- ============================================================

CREATE TABLE ejecucion (

    id                  BIGINT GENERATED ALWAYS AS IDENTITY,

    actividad_id        BIGINT NOT NULL,

    momento_programado  TIMESTAMP NOT NULL,

    momento_ejecucion   TIMESTAMP,

    tipo                VARCHAR(30) NOT NULL DEFAULT 'NORMAL',

    estado              VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',

    error               TEXT,

    CONSTRAINT pk_ejecucion
        PRIMARY KEY (id),

    CONSTRAINT fk_ejecucion_actividad
        FOREIGN KEY (actividad_id)
        REFERENCES actividad (id)
        ON DELETE CASCADE
);
