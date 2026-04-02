@echo off
chcp 65001 >nul
title AgilTeam Manager — Verificación y Lanzamiento

echo.
echo ╔══════════════════════════════════════════════╗
echo ║      AgilTeam Manager — Inicio guiado        ║
echo ╚══════════════════════════════════════════════╝
echo.

set ERRORES=0
set JAR=target\Agilteammanager-0.0.1-SNAPSHOT.jar

:: ─────────────────────────────────────────────
:: 1. VERIFICAR JAVA
:: ─────────────────────────────────────────────
echo [1/3] Verificando Java...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo   ✗ Java no encontrado.
    echo     → Descarga Java 21 en: https://adoptium.net/temurin/releases/?version=21
    set ERRORES=1
) else (
    for /f "tokens=*" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_INFO=%%v
    echo   ✓ Java detectado: %JAVA_INFO%
    :: Extraer versión major
    for /f "tokens=3 delims= " %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        set VER_STR=%%a
    )
    :: Quitar comillas y obtener major (antes del primer punto)
    set VER_STR=%VER_STR:"=%
    for /f "delims=." %%m in ("%VER_STR%") do set JAVA_MAJOR=%%m
    if %JAVA_MAJOR% LSS 21 (
        echo   ✗ Se requiere Java 21 o superior ^(detectado: %JAVA_MAJOR%^)
        echo     → Descarga Java 21 en: https://adoptium.net/temurin/releases/?version=21
        set ERRORES=1
    ) else (
        echo   ✓ Versión correcta ^(Java %JAVA_MAJOR%^)
    )
)

:: ─────────────────────────────────────────────
:: 2. VERIFICAR DOCKER
:: ─────────────────────────────────────────────
echo.
echo [2/3] Verificando Docker...
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo   ✗ Docker no encontrado.
    echo     → Descarga Docker Desktop en: https://www.docker.com/products/docker-desktop
    set ERRORES=1
) else (
    docker info >nul 2>&1
    if %errorlevel% neq 0 (
        echo   ✗ Docker está instalado pero no está en ejecución.
        echo     → Abre Docker Desktop y espera a que el icono de la ballena
        echo       aparezca en la bandeja del sistema ^(esquina inferior derecha^).
        set ERRORES=1
    ) else (
        for /f "tokens=*" %%d in ('docker --version') do echo   ✓ %%d
        echo   ✓ Docker daemon activo
    )
)

:: ─────────────────────────────────────────────
:: 3. VERIFICAR JAR
:: ─────────────────────────────────────────────
echo.
echo [3/3] Verificando ejecutable de la aplicación...
if exist "%JAR%" (
    echo   ✓ JAR encontrado: %JAR%
) else (
    echo   ! JAR no encontrado en %JAR%
    echo     Intentando compilar con Maven...
    where mvn >nul 2>&1
    if %errorlevel% neq 0 (
        echo   ✗ Maven tampoco está instalado.
        echo     → Descarga Maven en: https://maven.apache.org/download.cgi
        echo     → O descarga el JAR precompilado desde el repositorio GitHub.
        set ERRORES=1
    ) else (
        echo   → Compilando... ^(puede tardar 2-3 minutos la primera vez^)
        call mvn clean package -DskipTests -q
        if %errorlevel% neq 0 (
            echo   ✗ La compilación ha fallado. Revisa los errores de Maven arriba.
            set ERRORES=1
        ) else (
            echo   ✓ Compilación correcta
        )
    )
)

:: ─────────────────────────────────────────────
:: RESULTADO DE LA VERIFICACIÓN
:: ─────────────────────────────────────────────
echo.
if %ERRORES% neq 0 (
    echo ╔══════════════════════════════════════════════╗
    echo ║  Se encontraron problemas. Resuélvelos y     ║
    echo ║  vuelve a ejecutar este script.              ║
    echo ╚══════════════════════════════════════════════╝
    echo.
    pause
    exit /b 1
)

echo ╔══════════════════════════════════════════════╗
echo ║  Todo correcto. Iniciando AgilTeam Manager   ║
echo ╚══════════════════════════════════════════════╝

:: ─────────────────────────────────────────────
:: ARRANCAR BASE DE DATOS
:: ─────────────────────────────────────────────
echo.
echo → Iniciando base de datos PostgreSQL con Docker...
docker compose up -d
if %errorlevel% neq 0 (
    echo   ✗ Error al arrancar el contenedor Docker.
    echo     Revisa que docker-compose.yml está en esta carpeta.
    pause
    exit /b 1
)

echo   ✓ Base de datos iniciada
echo   → Esperando 3 segundos para que PostgreSQL esté listo...
timeout /t 3 /nobreak >nul

:: ─────────────────────────────────────────────
:: LANZAR APLICACIÓN
:: ─────────────────────────────────────────────
echo.
echo → Lanzando AgilTeam Manager...
echo   (La ventana de login aparecerá en unos segundos)
echo.
echo   Usuario: admin
echo   Contraseña: 1234
echo.
java -jar %JAR%