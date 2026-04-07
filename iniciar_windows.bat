@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo.
echo +---------------------------------------------------+
echo +       Agil Team Manager - Script de Inicio        +
echo +---------------------------------------------------+
echo.

set "ERRORES=0"
set "JAR=target\Agilteammanager-0.0.1-SNAPSHOT.jar"
set "SCRIPT_DIR=%~dp0"

:: =============================================
:: 1. VERIFICAR JAVA 21
:: =============================================
echo [1/3] Verificando Java 21...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo   [ERROR] Java no encontrado en el PATH.
    echo   Por favor, instala Java 21 desde: https://adoptium.net
    echo   Asegúrate de seleccionar "Add to PATH" durante la instalación.
    set ERRORES=1
    goto :fin
)

for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do set "VER_STR=%%~a"
for /f "delims=." %%m in ("!VER_STR!") do set "JAVA_MAJOR=%%m"

echo   [OK] Java detectado: !VER_STR!
if !JAVA_MAJOR! LSS 21 (
    echo   [ERROR] Se requiere Java 21 o superior. Versión detectada: !JAVA_MAJOR!
    echo   Descarga Java 21 desde: https://adoptium.net
    set ERRORES=1
    goto :fin
)

:: =============================================
:: 2. VERIFICAR DOCKER
:: =============================================
echo.
echo [2/3] Verificando Docker Desktop...
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo   [ERROR] Docker no encontrado.
    echo   Descarga e instala Docker Desktop desde: https://www.docker.com/products/docker-desktop
    set ERRORES=1
    goto :fin
)

docker info >nul 2>&1
if !errorlevel! neq 0 (
    echo   [ERROR] Docker está instalado pero no está en ejecución.
    echo   Abre Docker Desktop y espera a que el icono de la ballena aparezca en la bandeja del sistema.
    set ERRORES=1
    goto :fin
)

echo   [OK] Docker Desktop detectado y en ejecución.

:: =============================================
:: 3. COMPILAR SI NO EXISTE EL JAR
:: =============================================
echo.
echo [3/3] Verificando aplicación compilada...
if exist "%JAR%" goto :jar_ok

echo   [!] JAR no encontrado. Procediendo a compilar con Maven...
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo   [!] Maven no encontrado. Usando Maven Wrapper...
    call .\mvnw clean package -DskipTests
) else (
    call mvn clean package -DskipTests
)
if %errorlevel% neq 0 (
    echo   [ERROR] La compilacion ha fallado.
    set ERRORES=1
    goto :fin
)
echo   [OK] Compilacion completada.

:jar_ok
echo   [OK] JAR encontrado. Continuando...

:: =============================================
:: RESULTADO DE VERIFICACIONES
:: =============================================
:fin
if !ERRORES! neq 0 (
    echo.
    echo +---------------------------------------------------+
    echo +  Se detectaron errores. Resuélvelos y vuelve a   +
    echo +  ejecutar este script.                            +
    echo +---------------------------------------------------+
    echo.
    pause
    exit /b 1
)

echo.
echo +---------------------------------------------------+
echo +  Todo correcto. Iniciando Agil Team Manager...   +
echo +---------------------------------------------------+
echo.

:: =============================================
:: 4. INICIAR BASE DE DATOS
:: =============================================
echo Iniciando PostgreSQL con Docker...
docker compose up -d
if !errorlevel! neq 0 (
    echo   [ERROR] No se pudo iniciar Docker Compose.
    echo   Verifica que el archivo docker-compose.yml existe en esta carpeta.
    pause
    exit /b 1
)

echo   [OK] Base de datos PostgreSQL iniciada.
echo   Esperando unos segundos para que esté lista...
timeout /t 5 /nobreak >nul

:: =============================================
:: 5. LANZAR LA APLICACIÓN
:: =============================================
echo.
echo Lanzando Agil Team Manager...
echo.
echo   Usuario de prueba recomendado:
echo     Usuario:     admin
echo     Contraseña:  1234
echo.
echo (La ventana de la aplicación aparecerá en unos segundos)
echo.

java "-Djava.awt.headless=false" -jar "!JAR!"

echo.
echo Aplicación finalizada.
echo.