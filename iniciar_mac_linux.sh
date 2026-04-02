#!/bin/bash

# ═══════════════════════════════════════════════════
#  AgilTeam Manager — Verificación y Lanzamiento
#  Compatible con macOS y Linux
# ═══════════════════════════════════════════════════

JAR="target/Agilteammanager-0.0.1-SNAPSHOT.jar"
ERRORES=0

# Colores
VERDE="\033[0;32m"
ROJO="\033[0;31m"
AMARILLO="\033[1;33m"
RESET="\033[0m"

ok()   { echo -e "  ${VERDE}✓${RESET} $1"; }
fail() { echo -e "  ${ROJO}✗${RESET} $1"; ERRORES=$((ERRORES + 1)); }
info() { echo -e "  ${AMARILLO}→${RESET} $1"; }

echo ""
echo "╔══════════════════════════════════════════════╗"
echo "║      AgilTeam Manager — Inicio guiado        ║"
echo "╚══════════════════════════════════════════════╝"
echo ""

# ─────────────────────────────────────────────
# 1. VERIFICAR JAVA
# ─────────────────────────────────────────────
echo "[1/3] Verificando Java..."

if ! command -v java &>/dev/null; then
    fail "Java no encontrado."
    info "Descarga Java 21 en: https://adoptium.net/temurin/releases/?version=21"
else
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    JAVA_MAJOR=$(java -version 2>&1 | head -1 | grep -oE '[0-9]+\.[0-9]+\.[0-9]+|[0-9]+' | head -1)

    # Java 9+ devuelve "21.x.x", Java 8 devuelve "1.8.x"
    if [[ "$JAVA_MAJOR" == "1" ]]; then
        JAVA_MAJOR=$(java -version 2>&1 | head -1 | grep -oE '1\.[0-9]+' | cut -d. -f2)
    fi

    if [[ "$JAVA_MAJOR" -lt 21 ]]; then
        fail "Se requiere Java 21 o superior (detectado: $JAVA_MAJOR)"
        info "Descarga Java 21 en: https://adoptium.net/temurin/releases/?version=21"
    else
        ok "Java detectado: $JAVA_VERSION"
        ok "Versión correcta (Java $JAVA_MAJOR)"
    fi
fi

# ─────────────────────────────────────────────
# 2. VERIFICAR DOCKER
# ─────────────────────────────────────────────
echo ""
echo "[2/3] Verificando Docker..."

if ! command -v docker &>/dev/null; then
    fail "Docker no encontrado."
    info "Descarga Docker Desktop en: https://www.docker.com/products/docker-desktop"
else
    if ! docker info &>/dev/null; then
        fail "Docker está instalado pero no está en ejecución."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            info "Abre Docker Desktop desde Aplicaciones y espera a que el icono"
            info "de la ballena aparezca en la barra de menú superior."
        else
            info "Arranca el servicio Docker:"
            info "  sudo systemctl start docker"
        fi
    else
        ok "$(docker --version)"
        ok "Docker daemon activo"
    fi
fi

# ─────────────────────────────────────────────
# 3. VERIFICAR JAR
# ─────────────────────────────────────────────
echo ""
echo "[3/3] Verificando ejecutable de la aplicación..."

if [[ -f "$JAR" ]]; then
    ok "JAR encontrado: $JAR"
else
    info "JAR no encontrado en $JAR"
    info "Intentando compilar con Maven..."

    if ! command -v mvn &>/dev/null; then
        fail "Maven tampoco está instalado."
        info "Descarga Maven en: https://maven.apache.org/download.cgi"
        info "O descarga el JAR precompilado desde el repositorio GitHub."
    else
        mvn clean package -DskipTests -q
        if [[ $? -ne 0 ]]; then
            fail "La compilación ha fallado. Revisa los errores de Maven."
        else
            ok "Compilación correcta"
        fi
    fi
fi

# ─────────────────────────────────────────────
# RESULTADO DE LA VERIFICACIÓN
# ─────────────────────────────────────────────
echo ""
if [[ $ERRORES -ne 0 ]]; then
    echo "╔══════════════════════════════════════════════╗"
    echo "║  Se encontraron problemas. Resuélvelos y     ║"
    echo "║  vuelve a ejecutar este script.              ║"
    echo "╚══════════════════════════════════════════════╝"
    echo ""
    exit 1
fi

echo "╔══════════════════════════════════════════════╗"
echo "║  Todo correcto. Iniciando AgilTeam Manager   ║"
echo "╚══════════════════════════════════════════════╝"

# ─────────────────────────────────────────────
# ARRANCAR BASE DE DATOS
# ─────────────────────────────────────────────
echo ""
info "Iniciando base de datos PostgreSQL con Docker..."
docker compose up -d

if [[ $? -ne 0 ]]; then
    fail "Error al arrancar el contenedor Docker."
    info "Revisa que docker-compose.yml está en esta carpeta."
    exit 1
fi

ok "Base de datos iniciada"
info "Esperando 3 segundos para que PostgreSQL esté listo..."
sleep 3

# ─────────────────────────────────────────────
# LANZAR APLICACIÓN
# ─────────────────────────────────────────────
echo ""
info "Lanzando AgilTeam Manager..."
echo "  (La ventana de login aparecerá en unos segundos)"
echo ""
echo "  Usuario:    admin"
echo "  Contraseña: 1234"
echo ""
java -jar "$JAR"