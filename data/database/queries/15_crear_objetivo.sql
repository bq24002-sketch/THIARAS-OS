BEGIN;

INSERT INTO objetivo (
    nombre,
    descripcion,
    tipo_id,
    metrica,
    meta,
    periodo,
    fecha_inicio,
    fecha_fin
)
SELECT
    'Estudiar 120 minutos esta semana',
    'Objetivo semanal de estudio.',
    id,
    'MINUTOS',
    120,
    'SEMANAL',
    CURRENT_DATE,
    CURRENT_DATE + 6
FROM tipo_actividad
WHERE nombre = 'ESTUDIO';

COMMIT;


SELECT
    o.id,
    o.nombre,
    ta.nombre AS tipo,
    o.metrica,
    o.meta,
    o.periodo,
    o.fecha_inicio,
    o.fecha_fin,
    o.activo
FROM objetivo o
LEFT JOIN tipo_actividad ta
    ON ta.id = o.tipo_id
ORDER BY o.id DESC
LIMIT 1;