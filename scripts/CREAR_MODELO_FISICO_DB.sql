-- ============================================================
-- AGILTEAM MANAGER
-- Author: Francisco José Rodríguez Ruiz
-- Modelo relacional derivado de ER (PostgreSQL)
-- ============================================================

-- ============================================================
-- TABLAS BASE (SIN DEPENDENCIAS)
-- ============================================================

-- Roles del sistema
CREATE TABLE roles_sistema (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) UNIQUE NOT NULL,
  descripcion VARCHAR(255)
);

-- Permisos del sistema
CREATE TABLE permisos (
  id SERIAL PRIMARY KEY,
  codigo VARCHAR(100) UNIQUE NOT NULL,
  descripcion VARCHAR(255)
);

-- Competencias técnicas
CREATE TABLE competencias (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  descripcion VARCHAR(255),
  tipo VARCHAR(50)
);

-- Proyectos
CREATE TABLE proyectos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(200) NOT NULL,
  descripcion TEXT,
  fecha_inicio DATE,
  fecha_fin DATE,
  estado VARCHAR(50) DEFAULT 'planificacion' -- Estados posibles: planificacion, activo, completado, cancelado
);

-- Puestos de trabajo
CREATE TABLE puestos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255)
);

-- ============================================================
-- TABLAS 1:N
-- ============================================================

-- Personas del equipo (una persona → un rol)
CREATE TABLE personas (
  id SERIAL PRIMARY KEY,
  usuario VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  nombre VARCHAR(150) NOT NULL,
  email VARCHAR(150) UNIQUE,
  foto_path TEXT,
  sexo CHAR(1) DEFAULT 'O', -- M: masculino, F: femenino, O: otro
  estado VARCHAR(50) DEFAULT 'activo', -- Estados posibles: activo, inactivo, baja
  fecha_alta DATE DEFAULT CURRENT_DATE,
  puesto_id INT NOT NULL REFERENCES puestos(id),
  rol_id INT NOT NULL REFERENCES roles_sistema(id)
);

-- Sprints de un proyecto
CREATE TABLE sprints (
  id SERIAL PRIMARY KEY,
  proyecto_id INT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
  numero INT,
  estado VARCHAR(50) DEFAULT 'planificacion', -- Estados posibles: planificacion, activo, completado, cancelado
  objetivo TEXT,
  fecha_inicio DATE,
  fecha_fin DATE
);

-- Tareas del proyecto
CREATE TABLE tareas (
  id SERIAL PRIMARY KEY,
  proyecto_id INT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
  sprint_id INT REFERENCES sprints(id) ON DELETE SET NULL,
  titulo VARCHAR(200) NOT NULL,
  descripcion TEXT,
  estado VARCHAR(50) DEFAULT 'pendiente', -- Estados posibles: pendiente, en_progreso, revision, completada, bloqueada
  prioridad NUMERIC(3,2) CHECK (prioridad BETWEEN 0 AND 1) DEFAULT 0.5,
  estimacion_horas INT,
  fecha_creacion TIMESTAMP DEFAULT now()
);


-- ============================================================
-- TABLAS N:N (CLAVE PRIMARIA COMPUESTA)
-- ============================================================

-- Relación Roles ↔ Permisos
CREATE TABLE roles_permisos (
  rol_id INT REFERENCES roles_sistema(id) ON DELETE CASCADE,
  permiso_id INT REFERENCES permisos(id) ON DELETE CASCADE,
  PRIMARY KEY (rol_id, permiso_id)
);

-- Relación Personas ↔ Competencias (con nivel)
CREATE TABLE personas_competencias (
  persona_id INT REFERENCES personas(id) ON DELETE CASCADE,
  competencia_id INT REFERENCES competencias(id) ON DELETE CASCADE,
  nivel_actual INT CHECK (nivel_actual BETWEEN 0 AND 100),
  fecha_actualizado TIMESTAMP DEFAULT now(),
  PRIMARY KEY (persona_id, competencia_id)
);

-- Relación Tareas ↔ Competencias (con peso)
CREATE TABLE tareas_competencias (
  tarea_id INT REFERENCES tareas(id) ON DELETE CASCADE,
  competencia_id INT REFERENCES competencias(id) ON DELETE CASCADE,
  peso NUMERIC(3,2) CHECK (peso BETWEEN 0 AND 1),
  PRIMARY KEY (tarea_id, competencia_id)
);

-- ============================================================
-- TABLAS DE ASIGNACIÓN (ENTIDADES CON SIGNIFICADO PROPIO)
-- ============================================================

-- Asignaciones reales de tareas
CREATE TABLE asignaciones (
  id SERIAL PRIMARY KEY,
  tarea_id INT REFERENCES tareas(id) ON DELETE CASCADE,
  persona_id INT REFERENCES personas(id) ON DELETE CASCADE,
  fecha_asignacion TIMESTAMP DEFAULT now(),
  fecha_completada TIMESTAMP,
  observaciones TEXT,
  valoracion_final NUMERIC(3,2)
);

-- Asignaciones sugeridas por el motor
CREATE TABLE asignaciones_sugeridas (
  id SERIAL PRIMARY KEY,
  tarea_id INT REFERENCES tareas(id) ON DELETE CASCADE,
  persona_id INT REFERENCES personas(id) ON DELETE CASCADE,
  fecha_calculo TIMESTAMP DEFAULT now(),
  score_base NUMERIC(5,2),
  score_ajustado NUMERIC(5,2)
);


-- ============================================================
-- TABLA DE DISPONIBILIDAD (HISTÓRICO)
-- ============================================================

-- Disponibilidad / carga de trabajo por persona
CREATE TABLE disponibilidad (
  id SERIAL PRIMARY KEY,
  persona_id INT REFERENCES personas(id) ON DELETE CASCADE,
  fecha TIMESTAMP DEFAULT now(),
  carga NUMERIC(3,2) CHECK (carga BETWEEN 0 AND 1)
);


-- ============================================================
-- ÍNDICES BÁSICOS
-- ============================================================

CREATE INDEX idx_persona_rol ON personas(rol_id);
CREATE INDEX idx_tarea_proyecto ON tareas(proyecto_id);
CREATE INDEX idx_asignacion_persona ON asignaciones(persona_id);
CREATE INDEX idx_asignacion_tarea ON asignaciones(tarea_id);
CREATE INDEX idx_tarea_sprint ON tareas(sprint_id);
CREATE INDEX idx_sprint_proyecto ON sprints(proyecto_id);


-- ============================================================
-- INICIALIZACIÓN DE DATOS BÁSICOS
-- ============================================================

-- Asignaciones reales de tareas
INSERT INTO roles_sistema (nombre, descripcion) VALUES
('ADMIN', 'Acceso completo al sistema'),
('JEFE_PROYECTO', 'Gestiona proyectos y equipos'),
('USUARIO', 'Trabaja en las tareas asignadas');

-- Permisos del sistema
INSERT INTO permisos (codigo, descripcion) VALUES
('LOGIN', 'Acceso a login'),
('VER_DASHBOARD', 'Ver panel de inicio'),
('CRUD_ROLES_SISTEMA', 'Gestionar roles del sistema'),
('CRUD_USUARIOS_SISTEMA', 'Gestionar usuarios del sistema'),
('CRUD_ROLES_PROFESIONALES', 'Gestionar roles profesionales'),
('CRUD_COMPETENCIAS', 'Gestionar competencias'),
('CRUD_PERSONAS', 'Gestionar personas'),
('CRUD_PROYECTOS', 'Gestionar proyectos'),
('CRUD_TAREAS', 'Gestionar tareas'),
('CALCULAR_ASIGNACION', 'Ejecutar motor asignación'),
('VER_TAREAS_PROPIAS', 'Ver tareas asignadas propias'),
('ACTUALIZAR_ESTADO_PROPIO', 'Actualizar estado de tareas propias'),
('ACTUALIZAR_CARGA_PROPIA', 'Actualizar carga personal'),
('EXPORTAR_CSV', 'Exportar reportes CSV');

-- Asociació Roles ↔ Permisos
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT 1, id FROM permisos; -- ADMIN tiene todos los permisos

-- JEFE_PROYECTO permisos
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT 2, id FROM permisos
WHERE codigo IN (
  'LOGIN', 'VER_DASHBOARD', 'CRUD_ROLES_PROFESIONALES', 'CRUD_PERSONAS',
  'CRUD_PROYECTOS', 'CRUD_TAREAS', 'CALCULAR_ASIGNACION',
  'VER_TAREAS_PROPIAS', 'ACTUALIZAR_ESTADO_PROPIO',
  'ACTUALIZAR_CARGA_PROPIA', 'EXPORTAR_CSV'
);

-- USUARIO permisos
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT 3, id FROM permisos
WHERE codigo IN (
  'LOGIN', 'VER_DASHBOARD',
  'VER_TAREAS_PROPIAS',
  'ACTUALIZAR_ESTADO_PROPIO',
  'ACTUALIZAR_CARGA_PROPIA'
);

-- Datos mínimos necesarios para crear personas
INSERT INTO puestos (nombre, descripcion)
VALUES ('Administrador', 'Puesto administrador del sistema');

-- Persona ADMIN inicial
INSERT INTO personas (
  usuario,
  password,
  nombre,
  email,
  puesto_id,
  rol_id
) VALUES (
  'admin',
  '$2a$10$ZqC9hZP8fZ8oFhl2D9DcUuTlf0r5nGQy1Dc7P3EwG6zMZ6F3Yl6N6',
  'Usuario Administrador',
  'admin@example.com',
  1,  -- puesto Administrador
  1   -- rol ADMIN
);

