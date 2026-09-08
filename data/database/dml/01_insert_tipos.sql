-- ============================================================
-- THIARAS OS
-- DML - TIPOS DE ACTIVIDAD
-- ============================================================

INSERT INTO tipo_actividad (
    nombre,
    descripcion
)
VALUES
    ('SINCRONA',
     'Actividad que ocurre en un horario compartido.'),

    ('ASINCRONA',
     'Actividad que puede realizarse independientemente.'),

    ('ESTUDIO',
     'Actividad de estudio y aprendizaje.'),

    ('THIARAS',
     'Actividad relacionada con el desarrollo de THIARAS.'),

    ('MEDITACION',
     'Actividad de meditación o atención.'),

    ('EJERCICIO',
     'Actividad física.'),

    ('ADMINISTRACION',
     'Actividad administrativa.'),

    ('PROYECTO',
     'Actividad relacionada con un proyecto.'),

    ('NEGOCIO',
     'Actividad relacionada con negocios.'),

    ('PERSONAL',
     'Actividad personal.'),

    ('ENTREGA',
     'Entrega de trabajo o proyecto.'),

    ('EXAMEN',
     'Examen o evaluación.'),

    ('REUNION',
     'Reunión de grupo o trabajo.');