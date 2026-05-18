-- 2. LIMPIEZA DE DATOS PREVIOS (Evita duplicados si se vuelve a ejecutar)
-- Desactivamos restricciones de llaves foráneas temporalmente para limpiar de forma segura
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reglas_socket;
TRUNCATE TABLE reglas_energia;
SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- 3. INSERCIÓN DE REGLAS DE COMPATIBILIDAD DE SOCKETS (reglas_socket)
-- ====================================================================
-- Registra qué tipos de componentes (CPU y PLACA_MADRE) son compatibles
-- con los distintos sockets del mercado.
-- Nota: LGA1151 se omite intencionalmente para usarlo como caso de prueba incompatible.
INSERT INTO reglas_socket (tipo_componente, nombre_socket, generacion_soportada) VALUES
-- Plataforma AMD AM4 (Muy Común)
('CPU', 'AM4', 'Ryzen Serie 3000 / 5000'),
('PLACA_MADRE', 'AM4', 'Ryzen Serie 3000 / 5000'),

-- Plataforma AMD AM5 (Nueva Generación)
('CPU', 'AM5', 'Ryzen Serie 7000 / 8000 / 9000'),
('PLACA_MADRE', 'AM5', 'Ryzen Serie 7000 / 8000 / 9000'),

-- Plataforma Intel LGA1700 (Híbridos Recientes)
('CPU', 'LGA1700', 'Intel Core 12va, 13va y 14va Gen'),
('PLACA_MADRE', 'LGA1700', 'Intel Core 12va, 13va y 14va Gen'),

-- Plataforma Intel LGA1200 (Generaciones Anteriores)
('CPU', 'LGA1200', 'Intel Core 10ma y 11va Gen'),
('PLACA_MADRE', 'LGA1200', 'Intel Core 10ma y 11va Gen'),

-- Servidores / Workstation AMD sTR5
('CPU', 'sTR5', 'AMD Threadripper 7000'),
('PLACA_MADRE', 'sTR5', 'AMD Threadripper 7000');


-- ====================================================================
-- 4. INSERCIÓN DE REGLAS DE FUENTES DE PODER RECOMENDADAS (reglas_energia)
-- ====================================================================
-- Define rangos de consumo (Watts mínimos y máximos) y el tamaño recomendado
-- de la fuente de poder para asegurar estabilidad del sistema.
-- Nota: El rango máximo es de 2000 W para cumplir con el control de excepciones de la rúbrica.
INSERT INTO reglas_energia (consumo_watts_min, consumo_watts_max, fuente_recomendada_watts) VALUES
-- Rango de 0 a 300 Watts (Recomienda fuente de 500W)
(0, 300, 500),

-- Rango de 301 a 500 Watts (Recomienda fuente de 750W)
(301, 500, 750),

-- Rango de 501 a 1000 Watts (Recomienda fuente de 1000W)
(501, 1000, 1000),

-- Rango de 1001 a 2000 Watts (Recomienda fuente de 1600W)
(1001, 2000, 1600);
