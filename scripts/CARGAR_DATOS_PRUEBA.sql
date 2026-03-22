-- ============================================================
-- CARGAR_DATOS_PRUEBA.sql
-- AgilTeam Manager — Datos de prueba enriquecidos
-- Autor: Francisco José Rodríguez Ruiz
--
-- PREREQUISITO: CREAR_MODELO_FISICO_DB.sql ejecutado primero.
-- Estado BD esperado tras CREAR:
--   puestos  → {1: Administrador}
--   personas → {1: admin}
--   roles    → {1: ADMIN, 2: JEFE_PROYECTO, 3: USUARIO}
--   permisos → {1..14}
--
-- Contraseña de todos los usuarios de prueba: igual que 'admin'
-- ============================================================

-- ============================================================
-- LIMPIEZA PREVIA — borra todos los datos de prueba sin tocar
-- el usuario 'admin' ni el puesto 'Administrador'.
-- ============================================================

-- TRUNCATE borra las tablas que vaciamos completamente y resetea
-- sus secuencias automáticamente (RESTART IDENTITY).
-- CASCADE elimina primero las dependencias en el orden correcto.
TRUNCATE TABLE
    disponibilidad,
    asignaciones_sugeridas,
    asignaciones,
    tareas_competencias,
    personas_competencias,
    tareas,
    sprints,
    proyectos,
    competencias
RESTART IDENTITY CASCADE;

-- Para personas y puestos solo borramos los registros de prueba
-- (el admin y Administrador se mantienen).
DELETE FROM personas WHERE usuario != 'admin';
DELETE FROM puestos  WHERE nombre  != 'Administrador';

-- Ajustamos las secuencias de ambas tablas al max(id) actual,
-- usando pg_get_serial_sequence para no depender del nombre exacto.
SELECT setval(pg_get_serial_sequence('personas', 'id'), COALESCE(MAX(id), 1)) FROM personas;
SELECT setval(pg_get_serial_sequence('puestos',  'id'), COALESCE(MAX(id), 1)) FROM puestos;

-- ============================================================
-- BLOQUE 1: PUESTOS ADICIONALES
-- id=1 (Administrador) ya existe en CREAR.
-- IDs resultantes: 2..9
-- ============================================================
INSERT INTO puestos (nombre, descripcion) VALUES
('Backend Developer',    'Desarrollo de APIs REST y lógica de negocio backend'),
('Frontend Developer',   'Desarrollo de interfaces y experiencia de usuario'),
('Full Stack Developer', 'Desarrollo completo: frontend + backend'),
('DevOps Engineer',      'Infraestructura, CI/CD y gestión de despliegues'),
('QA Engineer',          'Calidad del software, testing manual y automatizado'),
('Scrum Master',         'Facilitador ágil y gestión de equipos Scrum'),
('Data Engineer',        'Ingeniería de datos, ETL y data warehousing'),
('Gestor BD',            'Diseño, administración y optimización de bases de datos');

-- ============================================================
-- BLOQUE 2: COMPETENCIAS
-- Tipos válidos: LENGUAJE | FRAMEWORK | BD | DEVOPS | TESTING | CLOUD
-- IDs resultantes: 1..21
-- ============================================================
INSERT INTO competencias (nombre, descripcion, tipo) VALUES
-- LENGUAJE
('Java',        'Lenguaje de programación orientado a objetos',          'LENGUAJE'),
('Python',      'Lenguaje de programación multiparadigma',               'LENGUAJE'),
('JavaScript',  'Lenguaje de scripting para web',                        'LENGUAJE'),
('TypeScript',  'JavaScript con tipado estático',                        'LENGUAJE'),
('SQL',         'Lenguaje de consultas para bases de datos relacionales', 'LENGUAJE'),
-- FRAMEWORK
('Spring Boot', 'Framework backend Java para microservicios',            'FRAMEWORK'),
('React',       'Librería de UI para aplicaciones web',                  'FRAMEWORK'),
('Angular',     'Framework frontend desarrollado por Google',            'FRAMEWORK'),
('Hibernate',   'ORM para mapeo objeto-relacional en Java',              'FRAMEWORK'),
('Node.js',     'Entorno de ejecución JavaScript en el servidor',        'FRAMEWORK'),
-- BD
('PostgreSQL',  'Base de datos relacional avanzada',                     'BD'),
('MongoDB',     'Base de datos NoSQL orientada a documentos',            'BD'),
('Redis',       'Almacén clave-valor en memoria para caché',             'BD'),
-- DEVOPS
('Docker',      'Contenedores y empaquetado de aplicaciones',            'DEVOPS'),
('Kubernetes',  'Orquestación y escalado de contenedores',               'DEVOPS'),
('CI/CD',       'Integración y despliegue continuo (Jenkins / GitLab)',  'DEVOPS'),
-- TESTING
('JUnit',       'Framework de testing unitario para Java',               'TESTING'),
('Selenium',    'Automatización de pruebas de interfaz web',             'TESTING'),
('Postman',     'Testing y documentación de APIs REST',                  'TESTING'),
-- CLOUD
('AWS',         'Amazon Web Services — plataforma cloud líder',          'CLOUD'),
('Azure',       'Microsoft Azure — plataforma cloud empresarial',        'CLOUD');

-- ============================================================
-- BLOQUE 3: PERSONAS DE PRUEBA
-- Prerrequisito: puestos 2..9 ya insertados.
-- Roles: 1=ADMIN, 2=JEFE_PROYECTO, 3=USUARIO
-- Hash = misma contraseña que el usuario 'admin' del sistema.
-- IDs resultantes: 2..9
-- ============================================================
INSERT INTO personas (usuario, password, nombre, email, sexo, estado, fecha_alta, puesto_id, rol_id) VALUES
('dbadmin',   '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Ana García',      'ana.garcia@empresa.com',      'F', 'activo', '2024-01-10', 9, 2),
('backend1',  '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Luis Martínez',   'luis.martinez@empresa.com',   'M', 'activo', '2024-01-10', 2, 3),
('backend2',  '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Marta Sánchez',   'marta.sanchez@empresa.com',   'F', 'activo', '2024-01-10', 2, 3),
('frontend1', '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Carlos Ruiz',     'carlos.ruiz@empresa.com',     'M', 'activo', '2024-02-01', 3, 3),
('devops1',   '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Elena Torres',    'elena.torres@empresa.com',    'F', 'activo', '2024-02-01', 5, 3),
('qa1',       '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Pedro Jiménez',   'pedro.jimenez@empresa.com',   'M', 'activo', '2024-02-15', 6, 3),
('pm1',       '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Sofía López',     'sofia.lopez@empresa.com',     'F', 'activo', '2024-01-10', 7, 2),
('data1',     '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6', 'Marco Fernández', 'marco.fernandez@empresa.com', 'M', 'activo', '2024-03-01', 8, 3);

-- ============================================================
-- BLOQUE 4: COMPETENCIAS POR PERSONA
-- persona_id:  2=Ana  3=Luis  4=Marta  5=Carlos  6=Elena  7=Pedro  8=Sofía  9=Marco
-- competencia: 1=Java 2=Python 3=JS 4=TS 5=SQL 6=SpringBoot 7=React 8=Angular
--              9=Hibernate 10=Node 11=PostgreSQL 12=MongoDB 13=Redis
--              14=Docker 15=K8s 16=CI/CD 17=JUnit 18=Selenium 19=Postman
--              20=AWS 21=Azure
-- ============================================================
INSERT INTO personas_competencias (persona_id, competencia_id, nivel_actual) VALUES
-- Ana García (id=2) — Gestor BD
(2,  5, 92),  -- SQL
(2, 11, 95),  -- PostgreSQL
(2,  2, 72),  -- Python
(2, 12, 68),  -- MongoDB
(2, 13, 65),  -- Redis
-- Luis Martínez (id=3) — Backend Developer
(3,  1, 88),  -- Java
(3,  6, 85),  -- Spring Boot
(3,  9, 78),  -- Hibernate
(3,  5, 72),  -- SQL
(3, 17, 68),  -- JUnit
(3, 11, 70),  -- PostgreSQL
-- Marta Sánchez (id=4) — Backend Developer
(4,  1, 84),  -- Java
(4,  6, 82),  -- Spring Boot
(4,  9, 75),  -- Hibernate
(4,  2, 70),  -- Python
(4, 14, 62),  -- Docker
(4, 17, 73),  -- JUnit
-- Carlos Ruiz (id=5) — Frontend Developer
(5,  3, 92),  -- JavaScript
(5,  4, 87),  -- TypeScript
(5,  7, 90),  -- React
(5,  8, 74),  -- Angular
(5, 10, 68),  -- Node.js
(5, 18, 60),  -- Selenium
-- Elena Torres (id=6) — DevOps Engineer
(6, 14, 94),  -- Docker
(6, 15, 87),  -- Kubernetes
(6, 16, 90),  -- CI/CD
(6, 20, 80),  -- AWS
(6, 21, 72),  -- Azure
(6,  2, 70),  -- Python
-- Pedro Jiménez (id=7) — QA Engineer
(7, 17, 88),  -- JUnit
(7, 18, 87),  -- Selenium
(7, 19, 92),  -- Postman
(7,  2, 65),  -- Python
(7,  5, 72),  -- SQL
-- Sofía López (id=8) — Scrum Master
(8,  1, 62),  -- Java
(8,  5, 58),  -- SQL
(8, 19, 70),  -- Postman
(8, 20, 64),  -- AWS
-- Marco Fernández (id=9) — Data Engineer
(9,  2, 92),  -- Python
(9,  5, 89),  -- SQL
(9, 11, 85),  -- PostgreSQL
(9, 12, 80),  -- MongoDB
(9, 13, 74),  -- Redis
(9, 20, 76);  -- AWS

-- ============================================================
-- BLOQUE 5: PROYECTOS
-- IDs: 1=AgilTeam  2=ECommerce  3=DataWarehouse  4=PortalRRHH
-- ============================================================
INSERT INTO proyectos (nombre, descripcion, fecha_inicio, fecha_fin, estado) VALUES
('AgilTeam Manager',
 'Sistema de gestión de equipos ágiles con motor de asignación automática basado en scoring',
 '2024-01-15', '2024-12-31', 'activo'),

('E-Commerce Platform',
 'Plataforma de comercio electrónico con catálogo, carrito y pasarela de pago integrada',
 '2024-03-01', '2024-09-30', 'activo'),

('DataWarehouse Migration',
 'Migración del almacén de datos legacy a arquitectura moderna en cloud (AWS Redshift)',
 '2024-06-01', '2025-03-31', 'planificacion'),

('Portal RR.HH. Interno',
 'Portal interno para gestión de empleados, vacaciones y reportes de asistencia',
 '2023-06-01', '2024-02-28', 'completado');

-- ============================================================
-- BLOQUE 6: SPRINTS
-- IDs resultantes (en orden de inserción): 1..9
-- ============================================================
INSERT INTO sprints (proyecto_id, numero, estado, objetivo, fecha_inicio, fecha_fin) VALUES
-- Proyecto 1 — AgilTeam Manager
(1, 1, 'completado',    'Configuración inicial: BD, autenticación y scaffolding del proyecto',    '2024-01-15', '2024-02-15'),
(1, 2, 'activo',        'Gestión de personas, puestos y competencias (CRUD completo)',            '2024-02-16', '2024-03-31'),
(1, 3, 'planificacion', 'Motor de asignación automática persona-tarea basado en scoring',         '2024-04-01', '2024-04-30'),
-- Proyecto 2 — E-Commerce Platform
(2, 1, 'completado',    'Análisis de requisitos y diseño de arquitectura del sistema',            '2024-03-01', '2024-03-31'),
(2, 2, 'activo',        'Desarrollo backend: catálogo de productos, carrito y autenticación JWT', '2024-04-01', '2024-04-30'),
(2, 3, 'planificacion', 'Desarrollo frontend React: catálogo, carrito y checkout',               '2024-05-01', '2024-05-31'),
-- Proyecto 3 — DataWarehouse Migration
(3, 1, 'planificacion', 'Análisis de fuentes de datos y diseño del esquema del Data Warehouse',  '2024-06-15', '2024-07-15'),
-- Proyecto 4 — Portal RR.HH. (completado)
(4, 1, 'completado',    'Módulo de gestión de empleados y departamentos',                        '2023-06-01', '2023-09-30'),
(4, 2, 'completado',    'Módulo de reportes, exportación CSV y dashboard de indicadores',        '2023-10-01', '2024-02-28');

-- ============================================================
-- BLOQUE 7: TAREAS
-- proyecto_id / sprint_id según bloques 5 y 6.
-- estados: pendiente | en_progreso | revision | completada | bloqueada
-- prioridad: NUMERIC(3,2) entre 0.00 y 1.00
-- ============================================================
INSERT INTO tareas (proyecto_id, sprint_id, titulo, descripcion, estado, prioridad, estimacion_horas) VALUES
-- ── Proyecto 1, Sprint 1 (completado) ─────────────────────────────────────────
(1, 1, 'Diseño del modelo de base de datos',
 'Modelo ER, normalización y generación del esquema físico PostgreSQL',
 'completada', 0.90, 12),

(1, 1, 'Configuración entorno PostgreSQL',
 'Instalación, configuración del servidor y creación de la base de datos agilteamdb',
 'completada', 0.80, 8),

(1, 1, 'Setup del proyecto Spring Boot + JavaFX',
 'Scaffolding inicial, dependencias Maven y estructura de paquetes por capas',
 'completada', 0.80, 8),

(1, 1, 'Login y autenticación BCrypt',
 'Implementación del sistema de login con validación BCrypt y gestión de sesión',
 'completada', 1.00, 14),

-- ── Proyecto 1, Sprint 2 (activo) ─────────────────────────────────────────────
(1, 2, 'CRUD Puestos de trabajo',
 'Formulario, listado, alta, edición y baja lógica de puestos de trabajo',
 'completada', 0.70, 8),

(1, 2, 'CRUD Competencias',
 'Gestión completa de competencias con 6 tipos y filtro por ComboBox',
 'completada', 0.70, 10),

(1, 2, 'CRUD Personas',
 'Formulario de persona con asignación de puesto, rol y competencias',
 'en_progreso', 0.85, 14),

(1, 2, 'Vista Dashboard con KPIs',
 'Panel principal con métricas: personas activas, proyectos, tareas en progreso y carga media',
 'en_progreso', 0.75, 12),

(1, 2, 'Tests unitarios módulo login',
 'Suite de tests JUnit para el servicio de autenticación y validaciones de entrada',
 'pendiente', 0.50, 6),

-- ── Proyecto 1, Sprint 3 (planificacion) ──────────────────────────────────────
(1, 3, 'Motor de asignación — lógica base',
 'Implementación del algoritmo: score_ajustado = score_base × (1-carga) × prioridad',
 'pendiente', 0.95, 20),

(1, 3, 'Algoritmo de scoring persona-tarea',
 'Cálculo del score_base según coincidencia de competencias y niveles requeridos vs disponibles',
 'pendiente', 0.90, 16),

(1, 3, 'Vista de asignaciones y sugerencias',
 'Interfaz para revisar sugerencias del motor y confirmar asignaciones manualmente',
 'pendiente', 0.75, 12),

-- ── Proyecto 2, Sprint 1 (completado) ─────────────────────────────────────────
(2, 4, 'Análisis de requisitos e-commerce',
 'Levantamiento de requisitos funcionales y no funcionales con el cliente',
 'completada', 0.90, 10),

(2, 4, 'Diseño de arquitectura del sistema',
 'Definición de capas, tecnologías y estructura de la solución backend + frontend',
 'completada', 0.85, 14),

(2, 4, 'Setup base del proyecto backend',
 'Scaffolding Spring Boot, configuración BD y pipeline CI/CD básico con GitHub Actions',
 'completada', 0.80, 8),

-- ── Proyecto 2, Sprint 2 (activo) ─────────────────────────────────────────────
(2, 5, 'API REST catálogo de productos',
 'Endpoints CRUD para productos y categorías con búsqueda y filtros paginados',
 'en_progreso', 0.90, 16),

(2, 5, 'Modelo de datos de productos',
 'Entidades JPA: Producto, Categoría e Inventario con relaciones y validaciones',
 'completada', 0.80, 8),

(2, 5, 'API REST carrito de compra',
 'Gestión del carrito: añadir, modificar y eliminar ítems con cálculo de totales y Redis',
 'pendiente', 0.85, 14),

(2, 5, 'Autenticación JWT para la API',
 'Login y registro con access token + refresh token mediante Spring Security',
 'en_progreso', 0.90, 12),

(2, 5, 'Tests de integración API catálogo',
 'Tests con JUnit + Mockito para todos los endpoints del catálogo de productos',
 'pendiente', 0.60, 8),

-- ── Proyecto 2, Sprint 3 (planificacion) ──────────────────────────────────────
(2, 6, 'Componente React: listado de productos',
 'Vista de catálogo con grid de productos, filtros laterales y paginación',
 'pendiente', 0.80, 14),

(2, 6, 'Componente React: carrito de compra',
 'Vista del carrito con resumen de ítems, cupones de descuento y cálculo de precios',
 'pendiente', 0.80, 12),

(2, 6, 'Integración pasarela de pago Stripe',
 'Integración con Stripe Checkout para procesamiento seguro de pagos en línea',
 'pendiente', 1.00, 20),

-- ── Proyecto 3, Sprint 1 (planificacion) ──────────────────────────────────────
(3, 7, 'Inventario y análisis de fuentes de datos',
 'Catalogar todas las fuentes existentes y analizar calidad, estructura y volumen',
 'pendiente', 0.85, 10),

(3, 7, 'Diseño del esquema del Data Warehouse',
 'Diseño en estrella para el DW destino: definición de dimensiones y tabla de hechos',
 'pendiente', 0.90, 16),

(3, 7, 'Plan de migración y roadmap técnico',
 'Definir fases de migración, procesos ETL y criterios de validación de datos',
 'pendiente', 0.80, 10),

-- ── Proyecto 4, Sprint 1 (completado) ─────────────────────────────────────────
(4, 8, 'Módulo ficha de empleado',
 'CRUD completo de empleados con datos personales, tipo de contrato y departamento',
 'completada', 0.85, 14),

(4, 8, 'Gestión de departamentos',
 'CRUD de departamentos con asignación de empleados y responsables de área',
 'completada', 0.70, 8),

(4, 8, 'Sistema de gestión de vacaciones',
 'Solicitud, aprobación y calendario de vacaciones por empleado y departamento',
 'completada', 0.75, 12),

-- ── Proyecto 4, Sprint 2 (completado) ─────────────────────────────────────────
(4, 9, 'Reporte de asistencia mensual',
 'Generación de informes de asistencia por empleado y departamento con filtros',
 'completada', 0.70, 10),

(4, 9, 'Exportación de datos a CSV',
 'Función de exportación de cualquier listado del sistema a formato CSV descargable',
 'completada', 0.60, 8),

(4, 9, 'Dashboard de indicadores RR.HH.',
 'Panel con KPIs de RRHH: rotación, absentismo y distribución por departamento',
 'completada', 0.75, 14);

-- ============================================================
-- BLOQUE 8: COMPETENCIAS REQUERIDAS POR TAREA
-- tarea_id: 1..32 (en el mismo orden del bloque 7)
-- peso: contribución de esa competencia al perfil requerido (suma ≈ 1.0 por tarea)
-- ============================================================
INSERT INTO tareas_competencias (tarea_id, competencia_id, peso) VALUES
-- T1: Diseño BD
(1,  5, 0.60), (1, 11, 0.40),
-- T2: Config PostgreSQL
(2, 11, 0.80), (2,  5, 0.20),
-- T3: Setup Spring Boot
(3,  1, 0.50), (3,  6, 0.50),
-- T4: Login BCrypt
(4,  1, 0.40), (4,  6, 0.40), (4,  5, 0.20),
-- T5: CRUD Puestos
(5,  1, 0.50), (5,  6, 0.50),
-- T6: CRUD Competencias
(6,  1, 0.50), (6,  6, 0.50),
-- T7: CRUD Personas
(7,  1, 0.40), (7,  6, 0.35), (7,  5, 0.25),
-- T8: Dashboard KPIs
(8,  1, 0.35), (8,  6, 0.35), (8,  5, 0.30),
-- T9: Tests unitarios login
(9, 17, 0.70), (9,  1, 0.30),
-- T10: Motor asignación base
(10, 1, 0.50), (10, 6, 0.30), (10, 5, 0.20),
-- T11: Algoritmo scoring
(11, 1, 0.60), (11, 5, 0.40),
-- T12: Vista asignaciones
(12, 1, 0.40), (12, 3, 0.60),
-- T13: Análisis requisitos
(13, 19, 0.60), (13, 5, 0.40),
-- T14: Diseño arquitectura
(14, 1, 0.40), (14, 6, 0.40), (14, 16, 0.20),
-- T15: Setup proyecto backend
(15, 1, 0.40), (15, 6, 0.40), (15, 16, 0.20),
-- T16: API catálogo
(16, 1, 0.40), (16, 6, 0.40), (16, 5, 0.20),
-- T17: Modelo datos productos
(17, 1, 0.40), (17, 9, 0.35), (17, 5, 0.25),
-- T18: API carrito (usa Redis)
(18, 1, 0.40), (18, 6, 0.35), (18, 13, 0.25),
-- T19: Auth JWT
(19, 1, 0.50), (19, 6, 0.50),
-- T20: Tests integración catálogo
(20, 17, 0.50), (20, 19, 0.50),
-- T21: Lista productos React
(21, 3, 0.30), (21, 4, 0.30), (21, 7, 0.40),
-- T22: Carrito UI React
(22, 3, 0.25), (22, 4, 0.35), (22, 7, 0.40),
-- T23: Pasarela pago Stripe
(23, 3, 0.30), (23, 4, 0.30), (23, 10, 0.40),
-- T24: Inventario fuentes
(24, 5, 0.50), (24, 2, 0.50),
-- T25: Esquema DW
(25, 5, 0.50), (25, 11, 0.30), (25, 2, 0.20),
-- T26: Plan migración
(26, 5, 0.40), (26, 2, 0.40), (26, 11, 0.20),
-- T27: Ficha empleado
(27, 1, 0.50), (27, 6, 0.50),
-- T28: Departamentos
(28, 1, 0.50), (28, 5, 0.50),
-- T29: Vacaciones
(29, 1, 0.45), (29, 6, 0.35), (29, 5, 0.20),
-- T30: Reporte asistencia
(30, 2, 0.50), (30, 5, 0.50),
-- T31: Exportación CSV
(31, 2, 0.50), (31, 5, 0.50),
-- T32: Dashboard RRHH
(32, 1, 0.40), (32, 3, 0.35), (32, 5, 0.25);

-- ============================================================
-- BLOQUE 9: ASIGNACIONES REALES
-- valoracion_final: NUMERIC(3,2) — escala 0.00 a 9.99
-- ============================================================
INSERT INTO asignaciones (tarea_id, persona_id, fecha_asignacion, fecha_completada, observaciones, valoracion_final) VALUES
-- Sprint 1 Proyecto 1 (todas completadas)
(1,  2, '2024-01-15', '2024-01-28', 'Ana lideró el diseño del modelo relacional',             4.80),
(2,  2, '2024-01-15', '2024-01-22', 'Configuración rápida y sin incidencias',                 4.90),
(3,  3, '2024-01-18', '2024-01-31', 'Setup completado con estructura de paquetes definida',   4.70),
(4,  3, '2024-01-22', '2024-02-08', 'Login con BCrypt operativo y testeado manualmente',      4.60),
(4,  4, '2024-01-22', '2024-02-08', 'Apoyo en implementación del módulo de autenticación',    4.50),
-- Sprint 2 Proyecto 1 (activo — mezcla de estados)
(5,  3, '2024-02-16', '2024-02-26', 'CRUD de puestos operativo con validaciones de negocio',  4.70),
(6,  4, '2024-02-16', '2024-02-28', 'Competencias con 6 tipos y filtro por ComboBox',         4.80),
(7,  4, '2024-02-28', NULL,         'En desarrollo — avance estimado al 60%',                  NULL),
(8,  5, '2024-03-01', NULL,         'Maquetado inicial listo, pendiente conectar datos reales',NULL),
-- Sprint 1 Proyecto 2 (completadas)
(13, 8, '2024-03-01', '2024-03-12', 'Sofía coordinó el levantamiento de requisitos',          4.90),
(14, 3, '2024-03-10', '2024-03-28', 'Arquitectura bien definida y aprobada por el cliente',   4.80),
(15, 4, '2024-03-20', '2024-03-31', 'Setup completo con pipeline básico funcionando',         4.70),
-- Sprint 2 Proyecto 2 (activo)
(16, 3, '2024-04-01', NULL,         'Endpoints de catálogo al 70%, falta búsqueda avanzada',  NULL),
(17, 3, '2024-04-08', '2024-04-16', 'Entidades JPA con relaciones y validaciones completas',  4.60),
(19, 4, '2024-04-01', NULL,         'JWT implementado, pendiente lógica de refresh token',    NULL),
-- Proyecto 4 (todos completados)
(27, 3, '2023-06-01', '2023-07-15', 'Módulo de empleados completo y con tests de regresión',  4.50),
(28, 7, '2023-06-15', '2023-07-31', 'Pedro implementó la gestión de departamentos',           4.40),
(29, 4, '2023-07-01', '2023-09-15', 'Sistema de vacaciones con calendario visual operativo',  4.60),
(30, 9, '2023-10-01', '2023-11-30', 'Reportes con filtros por mes, empleado y departamento',  4.70),
(31, 9, '2023-11-15', '2023-12-31', 'Exportación CSV disponible en todos los listados',       4.50),
(32, 5, '2024-01-01', '2024-02-28', 'Dashboard con 6 KPIs de RRHH finalizados',               4.80);

-- ============================================================
-- BLOQUE 10: ASIGNACIONES SUGERIDAS POR EL MOTOR
-- Fórmula: score_ajustado = score_base × (1 - carga) × prioridad
-- Carga por persona: 2=0.70, 3=0.85, 4=0.75, 5=0.50, 6=0.60, 7=0.40, 8=0.65, 9=0.30
-- ============================================================
INSERT INTO asignaciones_sugeridas (tarea_id, persona_id, score_base, score_ajustado) VALUES
-- T9: Tests login (prio 0.50) → Pedro, carga 0.40
(9,  7, 85.00, 25.50),
-- T10: Motor asignación (prio 0.95) → Luis, carga 0.85
(10, 3, 82.00, 11.69),
-- T11: Algoritmo scoring (prio 0.90) → Marta, carga 0.75
(11, 4, 79.50, 17.89),
-- T12: Vista asignaciones (prio 0.75) → Carlos, carga 0.50
(12, 5, 88.00, 33.00),
-- T18: API carrito (prio 0.85) → Luis, carga 0.85 (segunda: Marta 0.75)
(18, 3, 78.00, 17.55),
(18, 4, 75.00, 28.13),
-- T20: Tests catálogo (prio 0.60) → Pedro, carga 0.40
(20, 7, 90.00, 32.40),
-- T21: Lista productos React (prio 0.80) → Carlos, carga 0.50
(21, 5, 92.00, 36.80),
-- T22: Carrito UI (prio 0.80) → Carlos, carga 0.50
(22, 5, 89.00, 35.60),
-- T23: Pasarela pago (prio 1.00) → Carlos, carga 0.50
(23, 5, 76.00, 38.00),
-- T24: Inventario fuentes (prio 0.85) → Marco, carga 0.30
(24, 9, 91.00, 54.09),
-- T25: Esquema DW (prio 0.90) → Marco (1ª), Ana (2ª con penalización por carga 0.70)
(25, 9, 88.00, 55.44),
(25, 2, 85.00, 22.95),
-- T26: Plan migración (prio 0.80) → Marco, carga 0.30
(26, 9, 86.00, 48.16);

-- ============================================================
-- BLOQUE 11: DISPONIBILIDAD / CARGA ACTUAL
-- carga: 0.0 = libre total | 1.0 = completamente ocupado
-- ============================================================
INSERT INTO disponibilidad (persona_id, carga) VALUES
(2, 0.70),  -- Ana García       — alta carga (coordina varios proyectos)
(3, 0.85),  -- Luis Martínez    — carga muy alta (sprints activos en dos proyectos)
(4, 0.75),  -- Marta Sánchez    — carga alta
(5, 0.50),  -- Carlos Ruiz      — carga media (sprint futuro en planificación)
(6, 0.60),  -- Elena Torres     — carga media-alta
(7, 0.40),  -- Pedro Jiménez    — carga media-baja (tareas pendientes)
(8, 0.65),  -- Sofía López      — carga media-alta (coordinación multi-proyecto)
(9, 0.30);  -- Marco Fernández  — carga baja (proyecto en planificación)

-- ============================================================
-- BLOQUE 12: ACTUALIZACION DE TODAS LAS CONTRASEÑAS A VALOR 1234 PARA PRUEBAS
-- ============================================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;
UPDATE personas SET password = crypt('1234', gen_salt('bf', 10));

-- ============================================================
-- BLOQUE 13: SE ACTUALIZAN LAS FECHAS DE FINALIZACION DE TAREAS COMPLETADAS
-- ============================================================
-- Asigna fecha_terminacion a las tareas con estado 'completada'
-- distribuyendo las 15 tareas entre los últimos 7 días (2-3 por día)
-- Ejecutar UNA SOLA VEZ en agilteamdb

WITH ranked AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM tareas
    WHERE estado = 'completada'
)
UPDATE tareas
SET fecha_terminacion = CASE
    WHEN id IN (SELECT id FROM ranked WHERE rn IN (1,  2))       THEN '2026-03-11 10:00:00'::TIMESTAMP
    WHEN id IN (SELECT id FROM ranked WHERE rn IN (3,  4))       THEN '2026-03-12 11:00:00'::TIMESTAMP
    WHEN id IN (SELECT id FROM ranked WHERE rn IN (5,  6))       THEN '2026-03-13 09:30:00'::TIMESTAMP
    WHEN id IN (SELECT id FROM ranked WHERE rn IN (7,  8))       THEN '2026-03-14 14:00:00'::TIMESTAMP
    WHEN id IN (SELECT id FROM ranked WHERE rn IN (9,  10))      THEN '2026-03-15 16:00:00'::TIMESTAMP
    WHEN id IN (SELECT id FROM ranked WHERE rn IN (11, 12))      THEN '2026-03-16 10:30:00'::TIMESTAMP
    WHEN id IN (SELECT id FROM ranked WHERE rn IN (13, 14, 15))  THEN '2026-03-17 08:00:00'::TIMESTAMP
END
WHERE estado = 'completada';
