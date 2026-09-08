-- ============================================================
-- THIARAS OS
-- SISTEMA DE NOTIFICACIONES
-- ============================================================

CREATE TABLE notificacion (

    id BIGINT GENERATED ALWAYS AS IDENTITY,

    tipo VARCHAR(30) NOT NULL,

    titulo VARCHAR(150) NOT NULL,

    mensaje TEXT NOT NULL,

    actividad_id BIGINT,

    logro_id BIGINT,

    creada_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    leida BOOLEAN NOT NULL DEFAULT FALSE,

    leida_en TIMESTAMP,

    CONSTRAINT pk_notificacion
        PRIMARY KEY (id),

    CONSTRAINT fk_notificacion_actividad
        FOREIGN KEY (actividad_id)
        REFERENCES actividad(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_notificacion_logro
        FOREIGN KEY (logro_id)
        REFERENCES logro(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_notificacion_tipo
        CHECK (
            tipo IN (
                'RECORDATORIO',
                'LOGRO',
                'OBJETIVO',
                'VENCIMIENTO',
                'RESUMEN',
                'SISTEMA'
            )
        ),

    CONSTRAINT chk_notificacion_leida
        CHECK (
            (leida = FALSE AND leida_en IS NULL)
            OR
            (leida = TRUE AND leida_en IS NOT NULL)
        )
);


CREATE INDEX idx_notificacion_tipo
    ON notificacion(tipo);


CREATE INDEX idx_notificacion_leida
    ON notificacion(leida);


CREATE INDEX idx_notificacion_creada
    ON notificacion(creada_en);


CREATE INDEX idx_notificacion_actividad
    ON notificacion(actividad_id);


CREATE INDEX idx_notificacion_logro
    ON notificacion(logro_id);