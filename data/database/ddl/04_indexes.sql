-- ============================================================
-- THIARAS OS
-- DDL - ÍNDICES
-- ============================================================


-- Actividades por fecha de inicio
CREATE INDEX idx_actividad_fecha_inicio
ON actividad (fecha_inicio);


-- Actividades por fecha de vencimiento
CREATE INDEX idx_actividad_fecha_vencimiento
ON actividad (fecha_vencimiento);


-- Actividades por tipo
CREATE INDEX idx_actividad_tipo
ON actividad (tipo_id);


-- Recurrencias asociadas a una actividad
CREATE INDEX idx_recurrencia_actividad
ON recurrencia (actividad_id);


-- Recordatorios asociados a una actividad
CREATE INDEX idx_recordatorio_actividad
ON recordatorio (actividad_id);


-- Búsqueda de ejecuciones pendientes/programadas
CREATE INDEX idx_ejecucion_momento_programado
ON ejecucion (momento_programado);


-- Búsqueda de ejecuciones por estado
CREATE INDEX idx_ejecucion_estado
ON ejecucion (estado);