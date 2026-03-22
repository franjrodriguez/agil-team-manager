-- =====================================================
-- CONFIGURACIÓN DE USUARIO Y PERMISOS
-- =====================================================

-- 1. Crear usuario (si no existe)
DO
$$
BEGIN
IF NOT EXISTS (
SELECT FROM pg_catalog.pg_roles
WHERE rolname = 'franrodriguez'
) THEN
CREATE USER franrodriguez WITH PASSWORD '1234';
END IF;
END
$$;

-- =====================================================
-- 2. Permiso de conexión a la base de datos
-- =====================================================

GRANT CONNECT ON DATABASE agilteamdb TO franrodriguez;

-- =====================================================
-- 3. Permisos sobre esquemas
-- =====================================================

GRANT USAGE ON SCHEMA public TO franrodriguez;
GRANT CREATE ON SCHEMA public TO franrodriguez;

-- =====================================================
-- 4. Permisos sobre todas las tablas existentes
-- =====================================================

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO franrodriguez;

-- =====================================================
-- 5. Permisos sobre secuencias (IDs autoincrementales)
-- =====================================================

GRANT USAGE, SELECT, UPDATE
ON ALL SEQUENCES IN SCHEMA public
TO franrodriguez;

-- =====================================================
-- 6. Permisos sobre funciones
-- =====================================================

GRANT EXECUTE
ON ALL FUNCTIONS IN SCHEMA public
TO franrodriguez;

-- =====================================================
-- 7. Permisos para FUTURAS tablas
-- =====================================================

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO franrodriguez;

-- =====================================================
-- 8. Permisos para FUTURAS secuencias
-- =====================================================

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO franrodriguez;

-- =====================================================
-- 9. Permisos para FUTURAS funciones
-- =====================================================

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT EXECUTE ON FUNCTIONS TO franrodriguez;

