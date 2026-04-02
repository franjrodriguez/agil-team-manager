# AgilTeam Manager

<div align="center">

![Java](https://img.shields.io/badge/Java-21.0.10_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.4-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.2-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**Sistema de Gestión Integral para Equipos de Desarrollo Ágil**

*Optimiza la asignación de tareas basándose en competencias técnicas, disponibilidad y carga de trabajo real*

[🚀 Características](#-características-principales) • [📋 Requisitos](#-requisitos-previos) • [⚙️ Instalación](#️-instalación-local) • [🐳 Docker](#-ejecución-con-docker) • [📖 Documentación](#-documentación-adicional)

</div>

---

## 📑 Tabla de Contenidos

- [🎯 Sobre el Proyecto](#-sobre-el-proyecto)
- [⚡ Características Principales](#-características-principales)
- [🛠️ Stack Tecnológico](#️-stack-tecnológico)
- [🏗️ Arquitectura](#️-arquitectura)
- [📋 Requisitos Previos](#-requisitos-previos)
- [⚙️ Instalación Local](#️-instalación-local)
- [🐳 Ejecución con Docker](#-ejecución-con-docker)
- [🗂️ Estructura del Proyecto](#️-estructura-del-proyecto)
- [🤖 Motor de Recomendación](#-motor-de-recomendación)
- [🗺️ Roadmap](#️-roadmap)
- [👨‍💻 Autor](#-autor)

---

## 🎯 Sobre el Proyecto

**AgilTeam Manager** es una aplicación de escritorio multiplataforma (JavaFX + Spring Boot + PostgreSQL) diseñada para PYMEs y startups con equipos ágiles de 5 a 25 personas. Facilita la gestión integral de proyectos mediante un **motor de recomendación** que asigna personas a tareas basándose en:

- ✅ **Competencias técnicas reales** de cada miembro del equipo
- ✅ **Carga de trabajo actual** y disponibilidad
- ✅ **Prioridad de la tarea** como factor de ajuste
- ✅ **Histórico de evolución** de habilidades

> **Proyecto Final del Ciclo Formativo de Grado Superior en DAM — Distancia**
> **Autor:** Francisco José Rodríguez Ruiz | **Tutor:** Rafael Pablo Gómez Moral | **Centro:** IES Aguadulce

---

## ⚡ Características Principales

### 🔐 Seguridad y Control de Acceso
- **3 roles de sistema:** Administrador, Jefe de Proyecto, Usuario
- **Autenticación segura** con contraseñas encriptadas (BCrypt)
- **Sistema de permisos granular** por funcionalidad

### 👥 Gestión de Equipos y Competencias
- **6 tipos de competencias:** Lenguaje, Framework, BD, DevOps, Testing, Cloud
- **Niveles 0–100** por persona y competencia
- **Gestión de disponibilidad** y carga de trabajo en tiempo real
- **Puestos de trabajo** y roles profesionales personalizables

### 📊 Proyectos y Tareas
- **CRUD completo** de proyectos, sprints y tareas
- **Requisitos de competencias por tarea** con pesos relativos
- **Estados del ciclo de vida** configurables
- **Estimación de horas** y priorización

### 🤖 Motor de Recomendación (CORE)
- **Cálculo automático de idoneidad** (score base + ajuste)
- **Ranking ordenado** de candidatos con justificación
- Considera competencias, carga actual y prioridad de tarea

### 📈 Dashboard y Métricas
- **KPIs en tiempo real** (personas, proyectos, tareas, competencias)
- Distribución de carga del equipo
- Evolución de competencias por persona

---

## 🛠️ Stack Tecnológico

<div align="center">

### Backend & Lógica de Negocio
| Tecnología | Versión | Propósito |
|---|---|---|
| ![Java](https://img.shields.io/badge/-Java-ED8B00?logo=openjdk&logoColor=white) | 21.0.10 LTS (Temurin) | Lenguaje base |
| ![Spring Boot](https://img.shields.io/badge/-Spring_Boot-6DB33F?logo=spring&logoColor=white) | 3.4.2 | Framework backend |
| ![JPA](https://img.shields.io/badge/-JPA/Hibernate-59666C?logo=hibernate&logoColor=white) | (incluido en Spring) | ORM y persistencia |

### Frontend & UI
| Tecnología | Versión | Propósito |
|---|---|---|
| ![JavaFX](https://img.shields.io/badge/-JavaFX-007396?logo=java&logoColor=white) | 21.0.4 | Interfaz gráfica de escritorio |
| ![FXML](https://img.shields.io/badge/-FXML-5382A1?logoColor=white) | — | Diseño declarativo de vistas |
| ![AtlantaFX](https://img.shields.io/badge/-AtlantaFX-6C63FF?logoColor=white) | — | Tema visual moderno |

### Base de Datos
| Tecnología | Versión | Propósito |
|---|---|---|
| ![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-316192?logo=postgresql&logoColor=white) | 16 | BBDD relacional principal |

### Herramientas de Desarrollo
| Tecnología | Versión | Propósito |
|---|---|---|
| ![Maven](https://img.shields.io/badge/-Maven-C71A36?logo=apache-maven&logoColor=white) | 3.9+ | Gestión de dependencias |
| ![Git](https://img.shields.io/badge/-Git-F05032?logo=git&logoColor=white) | — | Control de versiones |
| ![Docker](https://img.shields.io/badge/-Docker-2496ED?logo=docker&logoColor=white) | — | Contenerización |
| ![IntelliJ IDEA](https://img.shields.io/badge/-IntelliJ_IDEA-000000?logo=intellij-idea&logoColor=white) | Community | IDE principal |
| ![Windsurf](https://img.shields.io/badge/-Windsurf-00BFA6?logoColor=white) | — | Editor SQL y configuración |

</div>

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                     CAPA PRESENTACIÓN                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           JavaFX (FXML + Controllers UI)             │  │
│  │  • Login  • Dashboard  • CRUD  • Asignaciones        │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────┘
                        │ SpringContext.getBean()
┌───────────────────────▼─────────────────────────────────────┐
│                   CAPA LÓGICA DE NEGOCIO                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Spring Boot Services                    │  │
│  │  • PersonaService    • TareaService                  │  │
│  │  • MotorAsignacionService  • DashboardService        │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────┘
                        │ Spring Data JPA
┌───────────────────────▼─────────────────────────────────────┐
│                    CAPA PERSISTENCIA                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Repositories (Spring Data JPA)               │  │
│  │  • PersonaRepository   • TareaRepository             │  │
│  │  • CompetenciaRepository • AsignacionRepository      │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│                    BASE DE DATOS                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │               PostgreSQL 18.2                        │  │
│  │   14 tablas normalizadas (3FN) + índices             │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

**Patrones de diseño aplicados:**
- 🎯 **MVC** — Separación presentación / lógica / datos
- 🗄️ **Repository** — Acceso a datos desacoplado
- 🏭 **Service Layer** — Lógica de negocio centralizada
- 💉 **Dependency Injection** — Spring IoC Container

---

## 📋 Requisitos Previos

| Opción | Qué instalar |
|---|---|
| 🖥️ **Instalación local** | Java 21 LTS + PostgreSQL 18 + Git |
| 🐳 **Docker** *(recomendado para evaluadores)* | Solo Docker Desktop |

**Plataformas soportadas:**
- ✅ Windows 10/11 (64 bits)
- ✅ macOS 12 o superior
- ✅ Linux (Debian/Ubuntu)

---

## ⚙️ Instalación Local

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/franjrodriguez/agil-team-manager.git
cd agil-team-manager
```

### 2️⃣ Crear usuario y base de datos

Ejecuta como superusuario de PostgreSQL:

```bash
psql -U postgres -f scripts/CREAR_USUARIO_CON_PERMISOS.sql
```

> Crea el usuario `franrodriguez` con contraseña `1234` y la base de datos `agilteamdb`.

### 3️⃣ Crear el modelo de base de datos

```bash
psql -U franrodriguez -d agilteamdb -f scripts/CREAR_MODELO_FISICO_DB.sql
```

### 4️⃣ Cargar datos de prueba *(opcional)*

```bash
psql -U franrodriguez -d agilteamdb -f scripts/CARGAR_DATOS_PRUEBA.sql
```

### 5️⃣ Configurar application.properties

Edita `src/main/resources/application.properties`:

```properties
# Database (Conexión Local)
spring.datasource.url=jdbc:postgresql://localhost:5432/agilteamdb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=franrodriguez
spring.datasource.password=1234
```

### 6️⃣ Compilar y ejecutar

```bash
mvn clean javafx:run
```

### 👤 Acceso inicial

| Campo | Valor |
|---|---|
| Usuario | `admin` |
| Contraseña | `1234` |

---

## 🐳 Ejecución con Docker

La opción más rápida para **tutores y evaluadores** — sin instalar Java ni PostgreSQL.

### Requisitos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/franjrodriguez/agil-team-manager.git
cd agil-team-manager
```

### 2️⃣ Levantar los contenedores

```bash
docker compose up -d
```

Se levantará automáticamente:
- 🐘 Contenedor **PostgreSQL 16** con `agilteamdb`, esquema y datos de prueba cargados

> **Nota:** La aplicación JavaFX se ejecuta directamente en el equipo del usuario (no en Docker), ya que JavaFX requiere entorno gráfico. Consulta el apartado [⚙️ Instalación Local](#️-instalación-local) para ejecutar el JAR una vez levantada la base de datos.

### 3️⃣ Detener

```bash
# Conserva los datos
docker compose down

# Borra también los datos
docker compose down -v
```

---

## 🗂️ Estructura del Proyecto

```
agilteammanager/
│
├── scripts/                              ← 📂 Scripts SQL
│   ├── CREAR_USUARIO_CON_PERMISOS.sql   ← Paso 1: usuario + BD
│   ├── CREAR_MODELO_FISICO_DB.sql       ← Paso 2: tablas + índices
│   └── CARGAR_DATOS_PRUEBA.sql          ← Paso 3: datos de ejemplo
│
└── src/main/
    ├── java/com/iesaguadulce/agilteammanager/
    │   ├── config/              ← Configuración Spring
    │   ├── model/
    │   │   ├── personas/        ← Persona, Competencia, Puesto
    │   │   ├── proyectos/       ← Proyecto, Sprint, Tarea
    │   │   ├── asignaciones/    ← Asignacion, AsignacionSugerida
    │   │   └── seguridad/       ← RolSistema, Permiso
    │   ├── repository/          ← Spring Data JPA
    │   ├── service/
    │   │   ├── personas/
    │   │   ├── proyectos/
    │   │   ├── asignaciones/    ← MotorAsignacionService ⭐
    │   │   ├── dashboard/
    │   │   └── seguridad/
    │   └── ui/                  ← Controladores JavaFX
    │       ├── login/
    │       ├── dashboard/
    │       ├── personas/
    │       ├── proyectos/
    │       ├── asignaciones/
    │       └── seguridad/
    │
    └── resources/
        ├── fxml/                ← Vistas JavaFX
        ├── css/                 ← Estilos AtlantaFX
        └── application.properties
```

---

## 🤖 Motor de Recomendación

### Algoritmo en 2 fases

#### Fase 1 — Score Base (aptitud técnica)

```
score_base(P,T) = Σ [ nivel_actual(P,Ci) × peso(T,Ci) ]

  nivel_actual  ∈ [0, 100]
  peso          ∈ [0, 1]   con Σ peso = 1
```

**Ejemplo:**
```
Tarea: Implementar API REST
  Java (peso 0.5) + Spring Boot (peso 0.3) + SQL (peso 0.2)

Luis:  Java=80, Spring=70, SQL=50
  → score_base = (80×0.5) + (70×0.3) + (50×0.2) = 71
```

#### Fase 2 — Score Ajustado (idoneidad final)

```
score_ajustado = (score_base / 100) × (1 - carga) × prioridad

  carga      ∈ [0, 1]   (0=libre, 1=totalmente ocupado)
  prioridad  ∈ [0, 1]   (1=tarea crítica)
```

**Ejemplo:**
```
Luis: score_base=71, carga=0.4
Tarea: prioridad=0.8

  → score_ajustado = (71/100) × (1-0.4) × 0.8 = 0.34
```

---

## 🗺️ Roadmap

### ✅ MVP — Estado actual
- [x] Aplicación de escritorio JavaFX
- [x] Backend Spring Boot + JPA + PostgreSQL
- [x] Autenticación BCrypt + roles y permisos
- [x] Dashboard con KPIs
- [x] CRUD Puestos de Trabajo
- [x] CRUD Competencias (con filtro por tipo)
- [x] CRUD Personas (con gestión de disponibilidad)
- [x] CRUD Proyectos / Sprints / Tareas
- [x] Motor de asignación (MotorAsignacionService)
- [x] Docker para base de datos (PostgreSQL)

### 🔄 Versión 1.5 (futuro)
- [ ] API REST con documentación Swagger
- [ ] Integración con Jira / Trello / Asana
- [ ] Exportación PDF/CSV

---

## 🤝 Contribución

Proyecto académico — no se aceptan contribuciones externas actualmente. Puedes:

1. 🌟 Dar una estrella si te resulta útil
2. 🐛 Reportar bugs abriendo un issue
3. 📚 Usarlo como referencia (licencia MIT)

---

## 📄 Licencia

**MIT License** — ver [LICENSE](LICENSE.txt) para más detalles.

---

## 👨‍💻 Autor

**Francisco José Rodríguez Ruiz**

- 🎓 Estudiante DAM — Desarrollo de Aplicaciones Multiplataforma
- 📧 [franj.rodriguezruiz@gmail.com](mailto:franj.rodriguezruiz@gmail.com)
- 🐙 GitHub: [@franjrodriguez](https://github.com/franjrodriguez)

**Tutor Académico:** Rafael Pablo Gómez Moral — IES Aguadulce

---

## 📚 Documentación Adicional

- 🎯 [Memoria Técnica](docs/MEMORIA_TECNICA_V2.0_AGILETEAM_MANAGER.md)
- 📊 [Análisis de Especificaciones](docs/Analisis_de_Especificaciones.pdf)

---

<div align="center">

**Desarrollado con ❤️ como Proyecto Final de DAM**

[![GitHub stars](https://img.shields.io/github/stars/franjrodriguez/agilteammanager?style=social)](https://github.com/franjrodriguez/agilteammanager/stargazers)

</div>