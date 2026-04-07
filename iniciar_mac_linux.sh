#!/bin/bash

echo
echo "+---------------------------------------------------+"
echo "+       Agil Team Manager - Script de Inicio        +"
echo "+---------------------------------------------------+"
echo

ERRORES=0
JAR="target/Agilteammanager-0.0.1-SNAPSHOT.jar"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# =============================================
# 1. VERIFICAR JAVA 21
# =============================================
echo "[1/3] Verificando Java 21..."

if ! command -v java &> /dev/null; then
    echo "   [ERROR] Java no encontrado en el PATH."
    echo "   Por favor, instala Java 21 desde: https://adoptium.net"
    ERRORES=1
else
    JAVA_VERSION=$(java -version 2>&1 | grep -i version | awk -F'"' '{print $2}' | cut -d'.' -f1)
    echo "   [OK] Java detectado: $(java -version 2>&1 | head -n 1)"
    
    if [ "$JAVA_VERSION" -lt 21 ]; then
        echo "   [ERROR] Se requiere Java 21 o superior. Versión detectada: $JAVA_VERSION"
        echo "   Descarga Java 21 desde: https://adoptium.net"
        ERRORES=1
    else
        echo "   [OK] Versión correcta (Java $JAVA_VERSION)"
    fi
fi

# =============================================
# 2. VERIFICAR DOCKER
# =============================================
echo
echo "[2/3] Verificando Docker Desktop..."

if ! command -v docker &> /dev/null; then
    echo "   [ERROR] Docker no encontrado."
    echo "   Descarga e instala Docker Desktop desde: https://www.docker.com/products/docker-desktop"
    ERRORES=1
elif ! docker info &> /dev/null; then
    echo "   [ERROR] Docker está instalado pero no está en ejecución."
    echo "   Abre Docker Desktop y espera a que el daemon esté activo."
    ERRORES=1
else
    echo "   [OK] Docker detectado y en ejecución."
fi

# =============================================
# 3. COMPILAR SI NO EXISTE EL JAR
# =============================================
echo
echo "[3/3] Verificando aplicación compilada..."

if [ -f "$JAR" ]; then
    echo "   [OK] JAR encontrado. No es necesario compilar."
else
    echo "   [!] JAR no encontrado. Procediendo a compilar con Maven..."

    # Intentar primero con Maven Wrapper (recomendado)
    if [ -f "./mvnw" ]; then
        echo "   [OK] Usando Maven Wrapper (./mvnw)"
        ./mvnw clean package -DskipTests
    elif command -v mvn &> /dev/null; then
        echo "   [OK] Maven detectado en el sistema."
        mvn clean package -DskipTests
    else
        echo "   [ERROR] Maven ni Maven Wrapper encontrados."
        echo "   Opciones:"
        echo "     1. Instala Maven manualmente"
        echo "     2. Asegúrate de que el Maven Wrapper (.mvn/) esté presente"
        ERRORES=1
    fi

    if [ $? -ne 0 ]; then
        echo "   [ERROR] La compilación ha fallado. Revisa los errores anteriores."
        ERRORES=1
    else
        echo "   [OK] Compilación completada correctamente."
    fi
fi

# =============================================
# RESULTADO DE VERIFICACIONES
# =============================================
if [ $ERRORES -ne 0 ]; then
    echo
    echo "+---------------------------------------------------+"
    echo "+  Se detectaron errores. Resuélvelos y vuelve a   +"
    echo "+  ejecutar este script.                            +"
    echo "+---------------------------------------------------+"
    echo
    exit 1
fi

echo
echo "+---------------------------------------------------+"
echo "+  Todo correcto. Iniciando Agil Team Manager...   +"
echo "+---------------------------------------------------+"
echo

# =============================================
# 4. INICIAR BASE DE DATOS
# =============================================
echo "Iniciando PostgreSQL con Docker..."
docker compose up -d

if [ $? -ne 0 ]; then
    echo "   [ERROR] No se pudo iniciar Docker Compose."
    echo "   Verifica que el archivo docker-compose.yml existe en esta carpeta."
    exit 1
fi

echo "   [OK] Base de datos PostgreSQL iniciada."
echo "   Esperando 5 segundos para que esté lista..."
sleep 5

# =============================================
# 5. LANZAR LA APLICACIÓN
# =============================================
echo
echo "Lanzando Agil Team Manager..."
echo
echo "   Usuario de prueba recomendado:"
echo "     Usuario:     admin"
echo "     Contraseña:  1234"
echo
echo "(La ventana de la aplicación aparecerá en unos segundos)"
echo

java -Djava.awt.headless=false -jar "$JAR"

echo
echo "Aplicación finalizada."