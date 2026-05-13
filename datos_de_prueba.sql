-- =========================================================
-- SCRIPT SQL: DATOS DE PRUEBA EXCLUSIVOS PARA COMPATIBILIDAD
-- Ejecuta esto en phpMyAdmin sobre la BD: db_compatibilidad
-- =========================================================

USE db_compatibilidad;

-- ---------------------------------------------------------
-- DICCIONARIO DE REGLAS
-- ---------------------------------------------------------

-- Reglas de Sockets compatibles
INSERT INTO reglas_socket (tipo_componente, nombre_socket, generacion_soportada) VALUES
('CPU', 'AM4', 'Ryzen Serie 5000'),
('PLACA_MADRE', 'AM4', 'Ryzen Serie 5000'),
('CPU', 'LGA1700', 'Intel Core 12va y 13va Gen'),
('PLACA_MADRE', 'LGA1700', 'Intel Core 12va y 13va Gen');

-- Reglas de Fuentes de Poder recomendadas
INSERT INTO reglas_energia (consumo_watts_min, consumo_watts_max, fuente_recomendada_watts) VALUES
(0, 300, 500),
(301, 500, 750),
(501, 1000, 1000),
(1001, 2000, 1600);
