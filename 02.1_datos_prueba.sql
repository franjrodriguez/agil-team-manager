INSERT INTO puestos (nombre, descripcion) VALUES
('Gestor BD', 'Responsable de base de datos'),
('Backend Developer', 'Desarrollador backend');

INSERT INTO personas (usuario, password, nombre, email, puesto_id, rol_id) VALUES
('dbadmin', 'hash_dbadmin', 'Ana BaseDatos', 'ana@empresa.com', 1, 1),
('backend1', 'hash_backend1', 'Luis Backend', 'luis@empresa.com', 2, 3),
('backend2', 'hash_backend2', 'Marta Backend', 'marta@empresa.com', 2, 3);

INSERT INTO competencias (nombre, descripcion, tipo) VALUES
('PostgreSQL', 'Base de datos PostgreSQL', 'tecnica'),
('Java', 'Desarrollo backend en Java', 'tecnica'),
('Spring Boot', 'Framework backend', 'tecnica'),
('SQL', 'Consultas SQL', 'tecnica');

INSERT INTO personas_competencias (persona_id, competencia_id, nivel_actual) VALUES
(1, 1, 90),
(1, 4, 85),
(3, 2, 80),
(3, 3, 75),
(4, 2, 70),
(4, 3, 65);

INSERT INTO proyectos (nombre, descripcion, estado) VALUES
('AgilTeam Manager', 'Sistema de gestión de equipos ágiles', 'activo');

INSERT INTO sprints (proyecto_id, numero, estado, objetivo) VALUES
(1, 1, 'activo', 'Configuración inicial del sistema'),
(1, 2, 'planificacion', 'Gestión de tareas y asignaciones');

INSERT INTO tareas (proyecto_id, sprint_id, titulo, descripcion, estado, estimacion_horas) VALUES
(1, 1, 'Diseño BD', 'Modelo relacional y normalización', 'en_progreso', 10),
(1, 1, 'Configuración PostgreSQL', 'Instalación y configuración', 'pendiente', 6),
(1, 2, 'API Tareas', 'Endpoints CRUD de tareas', 'pendiente', 12),
(1, 2, 'API Asignaciones', 'Lógica de asignación automática', 'pendiente', 14);

INSERT INTO tareas_competencias (tarea_id, competencia_id, peso) VALUES
(1, 1, 0.6),
(1, 4, 0.4),
(3, 2, 0.7),
(3, 3, 0.3);

INSERT INTO asignaciones (tarea_id, persona_id, observaciones) VALUES
(1, 1, 'Asignado al gestor de BD'),
(3, 3, 'Backend principal');

INSERT INTO asignaciones_sugeridas (tarea_id, persona_id, score_base, score_ajustado) VALUES
(4, 3, 78.5, 82.0);

INSERT INTO disponibilidad (persona_id, carga) VALUES
(1, 0.8),
(3, 0.6),
(4, 0.4);