# AgilTeam Manager

<div align="center">

![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17+-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**Sistema de Gestión Integral para Equipos de Desarrollo Ágil**

*Optimiza la asignación de tareas mediante IA basándose en competencias técnicas, disponibilidad y rendimiento histórico*

[🚀 Características](#-características-principales) • [📋 Requisitos](#-requisitos-previos) • [⚙️ Instalación](#️-instalación) • [📖 Documentación](#-documentación)

</div>

---

## 📑 Tabla de Contenidos

- [Sobre el Proyecto](#-sobre-el-proyecto)
- [Características Principales](#-características-principales)
- [Stack Tecnológico](#-stack-tecnológico)
- [Arquitectura](#-arquitectura)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#️-instalación)
- [Uso](#-uso)
- [Modelo de Datos](#-modelo-de-datos)
- [Motor de Recomendación](#-motor-de-recomendación)
- [Roadmap](#-roadmap)
- [Dockerizacion e Instalación Rápida](#-dockerizacion-e-instalacion-rapida)
- [Contribución](#-contribución)
- [Licencia](#-licencia)
- [Autor](#-autor)

---

## 🎯 Sobre el Proyecto

**AgilTeam Manager** es una aplicación de escritorio multiplataforma diseñada para PYMEs, startups y equipos ágiles (5-25 personas) que optimiza la asignación de tareas a profesionales basándose en:

- ✅ **Competencias técnicas reales** de cada miembro
- ✅ **Disponibilidad y carga de trabajo** actual
- ✅ **Rendimiento histórico** y evolución de habilidades
- ✅ **Motor de recomendación inteligente** con scoring automático

> **Proyecto Final del Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM)**  
> **Autor:** Francisco José Rodríguez Ruiz  
> **Tutor:** Rafael Pablo Gómez Moral  
> **Versión:** 1.0  
> **Fecha:** Noviembre 2025

---

## ⚡ Características Principales

### 🔐 Seguridad y Control de Acceso
- **3 Roles de Usuario:** Administrador, Jefe de Proyecto, Usuario/Desarrollador
- **Autenticación segura** con contraseñas encriptadas (BCrypt)
- **Sistema de permisos granular** por funcionalidad
- **Gestión completa de usuarios** y roles del sistema

### 👥 Gestión de Equipos y Competencias
- **Catálogo extensible** de competencias técnicas (Lenguajes, Frameworks, BD, DevOps, Testing, Cloud)
- **Asignación de niveles** (0-100) con registro histórico automático
- **Visualización de evolución** temporal mediante gráficos de radar
- **Gestión de disponibilidad** y carga de trabajo en tiempo real
- **Roles profesionales** personalizables (Backend, Frontend, QA, DevOps, etc.)

### 📊 Proyectos y Tareas
- **CRUD completo** de proyectos, sprints y tareas
- **Tablero Kanban** con drag-and-drop para gestión visual
- **Definición de competencias requeridas** con pesos relativos por tarea
- **Priorización** y estimación de horas
- **Estados personalizables** del ciclo de vida de tareas

### 🤖 Motor de Recomendación (CORE)
- **Cálculo automático de idoneidad** (score base + ajustes)
- **Ranking ordenado** de candidatos con justificación detallada
- **Consideración de factores múltiples:**
    - Competencias técnicas y niveles
    - Carga de trabajo actual
    - Prioridad de tareas
    - Disponibilidad del equipo

### 📈 Métricas y Reportes
- **Dashboard con KPIs** en tiempo real
- **Gráficos interactivos:**
    - Distribución de carga del equipo
    - Evolución de competencias
    - Burndown charts de sprints
    - Tareas por estado
- **Exportación** a PDF/CSV con marca de agua

---

## 🛠️ Stack Tecnológico

<div align="center">

### Backend & Lógica de Negocio
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| ![Java](https://img.shields.io/badge/-Java-007396?logo=openjdk&logoColor=white) | 21 LTS  | Lenguaje base |
| ![Spring Boot](https://img.shields.io/badge/-Spring_Boot-6DB33F?logo=spring&logoColor=white) | 4.x     | Framework backend |
| ![JPA](https://img.shields.io/badge/-JPA/Hibernate-59666C?logo=hibernate&logoColor=white) | -       | ORM & Persistencia |

### Frontend & UI
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| ![JavaFX](https://img.shields.io/badge/-JavaFX-007396?logo=java&logoColor=white) | 21+      | Interfaz gráfica |
| ![FXML](https://img.shields.io/badge/-FXML-5382A1?logo=xml&logoColor=white) | -       | Diseño de vistas |
| ![Scene Builder](https://img.shields.io/badge/-Scene_Builder-007396?logo=java&logoColor=white) | -       | Diseño visual |

### Base de Datos
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| ![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-316192?logo=postgresql&logoColor=white) | 12+ | BBDD relacional |

### Herramientas de Desarrollo
| Tecnología | Versión   | Propósito |
|------------|-----------|-----------|
| ![Maven](https://img.shields.io/badge/-Maven-C71A36?logo=apache-maven&logoColor=white) | 3.9+      | Gestión de dependencias |
| ![Git](https://img.shields.io/badge/-Git-F05032?logo=git&logoColor=white) | -         | Control de versiones |
| ![Docker](https://img.shields.io/badge/-Docker-2496ED?logo=docker&logoColor=white) | -         | Contenerización |
| ![IntelliJ IDEA](https://img.shields.io/badge/-IntelliJ_IDEA-000000?logo=intellij-idea&logoColor=white) | Community | IDE |

### Documentación & Otros
| Tecnología | Propósito |
|------------|-----------|
| ![Pandoc](https://img.shields.io/badge/-Pandoc-4A8BBC?logo=markdown&logoColor=white) | Generación de docs |
| ![Penpot](https://img.shields.io/badge/-Penpot-FF6B6B?logo=figma&logoColor=white) | Diseño de UI/UX |
| ![Draw.io](https://img.shields.io/badge/-Draw.io-F08705?logo=diagrams.net&logoColor=white) | Diagramas técnicos |

</div>

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                     CAPA PRESENTACIÓN                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           JavaFX (FXML + Controllers)                │  │
│  │  • Login  • Dashboard  • CRUD  • Kanban  • Gráficos  │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│                   CAPA LÓGICA DE NEGOCIO                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Spring Boot Services                    │  │
│  │  • PersonaService  • TareaService  • MotorService    │  │
│  │  • ProyectoService • AsignacionService               │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│                    CAPA PERSISTENCIA                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         JPA/Hibernate Repositories                   │  │
│  │  • PersonaRepository  • TareaRepository              │  │
│  │  • ProyectoRepository • AsignacionRepository         │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│                    BASE DE DATOS                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  PostgreSQL 12+                       │  │
│  │  16 tablas normalizadas (3FN) + índices optimizados │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

**Patrones de Diseño:**
- 🎯 **MVC/MVVM** - Separación de responsabilidades
- 🗄️ **DAO (Repository)** - Acceso a datos
- 📦 **DTO** - Transferencia de objetos
- 🏭 **Service Layer** - Lógica de negocio
- 💉 **Dependency Injection** - Spring IoC

---

## 📋 Requisitos Previos

### Hardware Mínimo
- **Procesador:** Intel Core i3 o equivalente (2 GHz+)
- **RAM:** 4 GB (8 GB recomendado)
- **Disco:** 3 GB libres
- **Pantalla:** 1280x720 px mínimo

### Software Requerido
```bash
☑️ JDK 17+ (LTS obligatorio)
☑️ PostgreSQL 12+
☑️ Maven 3.8+
☑️ Git
```

### Plataformas Soportadas
- ✅ Windows 10/11 (64 bits)
- ✅ macOS 10.14 (Mojave) o superior
- ✅ Linux (Debian/Ubuntu, Fedora, Arch)

---

## ⚙️ Instalación

### 1️⃣ Clonar el Repositorio
```bash
git clone https://github.com/franjrodriguez/agilteam-manager.git
cd agilteam-manager
```

### 2️⃣ Configurar PostgreSQL
```sql
-- Crear base de datos
CREATE DATABASE agilteamdb;

-- Ejecutar script de creación
psql -U tu_usuario -d agilteamdb -f scripts/CREAR_MODELO_FISICO.sql

-- (Opcional) Cargar datos de prueba
psql -U tu_usuario -d agilteamdb -f scripts/CARGAR_DATOS_PRUEBA.sql
```

### 3️⃣ Configurar Variables de Entorno
Crear archivo `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/agilteam_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Application Settings
app.version=1.0
app.env=development
```

### 4️⃣ Compilar y Ejecutar
```bash
# Compilar proyecto
mvnw clean install

# Ejecutar aplicación
mvnw javafx:run
```

### 🐳 Alternativa con Docker
```bash
# Construir imagen
docker build -t agilteam-manager .

# Ejecutar contenedor
docker-compose up -d
```

---

## 🚀 Uso

### Credenciales Iniciales
El sistema incluye 3 usuarios de prueba:

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `1234` | Administrador |
| `laura` | `1234` | Jefe de Proyecto |
| `ana` | `1234` | Desarrollador |

### Flujo de Trabajo Típico

#### 1️⃣ **Configuración Inicial (Admin)**
```
Login → Dashboard → Configuración
  ├─ Gestionar Competencias (Java, React, Docker...)
  ├─ Gestionar Roles Profesionales (Backend, Frontend, QA...)
  └─ Crear Usuarios del Sistema
```

#### 2️⃣ **Gestión de Equipo (Jefe de Proyecto)**
```
Dashboard → Personas
  ├─ Alta de Profesionales
  ├─ Asignar Competencias con Niveles (0-100)
  └─ Actualizar Disponibilidad
```

#### 3️⃣ **Gestión de Proyectos y Tareas**
```
Dashboard → Proyectos
  ├─ Crear Proyecto
  ├─ Definir Sprints (opcional)
  └─ Crear Tareas
      ├─ Definir Competencias Requeridas (con pesos)
      ├─ Asignar Prioridad (0-1)
      └─ Estimar Horas
```

#### 4️⃣ **Asignación Inteligente**
```
Dashboard → Tareas → Seleccionar Tarea → [Calcular Asignación]
  ├─ Motor analiza competencias + disponibilidad
  ├─ Genera ranking ordenado de candidatos
  └─ Muestra justificación detallada
      └─ [Asignar] o [Asignar Manualmente]
```

#### 5️⃣ **Seguimiento (Desarrollador)**
```
Login → Mi Dashboard
  ├─ Ver Mis Tareas Asignadas
  ├─ Actualizar Estado (Kanban)
  ├─ Actualizar Mi Carga de Trabajo
  └─ Ver Mi Evolución de Competencias
```

#### 6️⃣ **Análisis y Reportes (Jefe/Admin)**
```
Dashboard → Métricas
  ├─ Visualizar KPIs y Gráficos
  ├─ Análisis de Carga del Equipo
  ├─ Evolución de Competencias
  └─ [Exportar] → PDF/CSV
```

---

## 🗄️ Modelo de Datos

### Diagrama Entidad-Relación (Simplificado)

```
┌─────────────┐       ┌───────────────┐       ┌──────────────┐
│  USUARIOS   │──1:N──│ USUARIO_ROLES │──N:1──│ ROLES_SISTEMA│
└─────────────┘       └───────────────┘       └──────────────┘
                                                      │ 1:N
                                               ┌──────▼───────┐
                                               │ ROL_PERMISOS │
                                               └──────┬───────┘
                                                      │ N:1
                                               ┌──────▼───────┐
                                               │   PERMISOS   │
                                               └──────────────┘

┌─────────────┐       ┌──────────────────┐       ┌──────────────┐
│  PERSONAS   │──N:M──│PERSONA_COMPETENCIA│──M:1──│ COMPETENCIAS │
└──────┬──────┘       └──────────────────┘       └──────────────┘
       │ 1:N                   │ (histórico)
       │              ┌────────▼───────────┐
       │              │PERSONA_DISPONIBILIDAD│
       │              └────────────────────┘
       │
       │ N:M          ┌──────────────────┐
       └──────────────│  ASIGNACIONES    │
                      └────────┬─────────┘
                               │ N:1
┌──────────────┐       ┌───────▼────┐       ┌──────────────┐
│  PROYECTOS   │──1:N──│   TAREAS   │──N:M──│TAREA_COMPETENCIA│
└──────┬───────┘       └────────────┘       └──────────────┘
       │ 1:N
┌──────▼───────┐
│   SPRINTS    │
└──────────────┘
```

### Tablas Principales (16 totales)

**Módulo Seguridad:**
- `usuarios` - Credenciales de acceso
- `roles_sistema` - ADMIN, JEFE_PROYECTO, USUARIO
- `usuario_roles` - Relación N:M
- `permisos` - Permisos granulares
- `rol_permisos` - Relación N:M

**Módulo Equipos:**
- `personas` - Profesionales del equipo
- `roles` - Roles profesionales (Backend, QA...)
- `competencias` - Catálogo de habilidades técnicas
- `persona_competencia` - Niveles + histórico
- `persona_disponibilidad` - Carga actual (0-1)

**Módulo Proyectos:**
- `proyectos` - Información de proyectos
- `sprints` - Iteraciones ágiles
- `tareas` - Unidades de trabajo
- `tarea_competencia` - Requisitos técnicos

**Módulo Asignaciones:**
- `asignaciones_sugeridas` - Recomendaciones del motor
- `asignaciones` - Asignaciones reales confirmadas

---

## 🤖 Motor de Recomendación

### Algoritmo de Scoring

#### **Fase 1: Score Base (Aptitud Técnica)**
```
score_base(P,T) = Σ [nivel_actual(P,Ci) × peso(T,Ci)]
                  i=1 hasta n

Donde:
  n = número de competencias requeridas por la tarea
  nivel_actual ∈ [0, 100]
  peso ∈ [0, 1] con Σ peso = 1
```

**Ejemplo:**
```
Tarea: Implementar API REST
  Java (peso 0.5) + Spring Boot (peso 0.3) + SQL (peso 0.2)

Ana: Java=80, Spring=70, SQL=50
  → score_base = (80×0.5) + (70×0.3) + (50×0.2) = 71
```

#### **Fase 2: Score Ajustado (Idoneidad Final)**
```
score_ajustado(P,T) = (score_base / 100) × (1 - carga) × prioridad

Donde:
  carga ∈ [0, 1]  (0=libre, 1=ocupado)
  prioridad ∈ [0, 1]  (1=crítica)
```

**Ejemplo:**
```
Ana: score_base=71, carga=0.4 (60% disponible)
Tarea: prioridad=0.8

  → score_ajustado = (71/100) × (1-0.4) × 0.8 = 0.3408
```

#### **Fase 3: Ranking y Justificación**
```
┌─────────────────────────────────────────────────┐
│ 🥇 MARÍA GONZÁLEZ (Score: 43.1%)                │
├─────────────────────────────────────────────────┤
│ Competencias:                                   │
│   • Java: 90/100 (peso 50%) → +45 pts          │
│   • Spring Boot: 80/100 (peso 30%) → +24 pts   │
│   • SQL: 40/100 (peso 20%) → +8 pts            │
│ ──────────────────────────────────────────────  │
│ Score Técnico: 77/100                           │
│ Disponibilidad: 70% (carga: 30%)                │
│ Ajuste prioridad: ×0.8                          │
│                                                 │
│ [ASIGNAR A MARÍA] [Ver Alternativas]            │
└─────────────────────────────────────────────────┘
```

### Características del Motor
- ✅ **Transparencia:** Justificación completa del cálculo
- ✅ **Múltiples factores:** Competencias + disponibilidad + prioridad
- ✅ **Ranking ordenado:** Top 5 candidatos
- ✅ **Advertencias:** Sobrecarga, competencias faltantes
- ✅ **Histórico:** Registro de todas las sugerencias

---

## 🗺️ Roadmap

### ✅ Versión 1.0 (MVP - Actual)
- [x] Aplicación de escritorio multiplataforma (JavaFX)
- [x] Backend Spring Boot + JPA
- [x] Base de datos PostgreSQL con 16 tablas normalizadas
- [x] Motor de cálculo de idoneidad
- [x] Sistema de autenticación y autorización por roles
- [x] Dashboard con métricas básicas y visualizaciones
- [x] Tablero Kanban para gestión de tareas
- [x] Exportación de reportes PDF/CSV
- [x] Carga inicial de datos mediante JSON seed

### 🔄 Versión 1.5 (Q2 2026)
- [ ] Aplicación web (Spring Boot + React)
- [ ] API REST completa con documentación Swagger
- [ ] Integración bidireccional con Jira/Trello/Asana
- [ ] Notificaciones push y por email
- [ ] Chat interno por proyecto
- [ ] Modo multi-tenant (SaaS)

### 🚀 Versión 2.0 (Q4 2026)
- [ ] Machine Learning sobre históricos de rendimiento
- [ ] Optimización global de asignaciones (algoritmo Hungarian)
- [ ] Predicción de entregas y detección de riesgos
- [ ] App móvil (React Native)
- [ ] Dashboard ejecutivo con BI avanzado
- [ ] Integración con herramientas de HR (gestión de vacaciones, ausencias)

---

## 🤝 Contribución

Este es un **proyecto académico** desarrollado como TFG del Ciclo Formativo DAM. Actualmente no se aceptan contribuciones externas, pero puedes:

1. 🌟 **Dar una estrella** al proyecto si te resulta útil
2. 🐛 **Reportar bugs** abriendo un issue
3. 💡 **Sugerir mejoras** en la sección de Discussions
4. 📚 **Usar como referencia** para tus propios proyectos (respetando la licencia)

---

## 📄 Licencia

Este proyecto está licenciado bajo la **Licencia MIT** - ver el archivo [LICENSE](LICENSE.txt) para más detalles.

```
MIT License

Copyright (c) 2025 Francisco José Rodríguez Ruiz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

---

## 👨‍💻 Autor

**Francisco José Rodríguez Ruiz**

- 🎓 Estudiante de DAM (Desarrollo de Aplicaciones Multiplataforma)
- 📧 Email: [franj.rodriguezruiz@gmail.com](mailto:franj.rodriguezruiz@gmail.com)
- 💼 LinkedIn: [tu-perfil](https://linkedin.com/in/tu-perfil)
- 🐙 GitHub: [@franjrodriguez](https://github.com/franjrodriguez)

**Tutor Académico:** Rafael Pablo Gómez Moral

---

<div align="center">

### 🌟 Si este proyecto te ha sido útil, considera darle una estrella

[![GitHub stars](https://img.shields.io/github/stars/franjrodriguez/agilteam-manager?style=social)](https://github.com/franjrodriguez/agilteam-manager/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/franjrodriguez/agilteam-manager?style=social)](https://github.com/franjrodriguez/agilteam-manager/network/members)

**Desarrollado con ❤️ como Proyecto Final de DAM**

</div>

---

## 📚 Documentación Adicional

- 📖 [Manual de Usuario](docs/manual-usuario.pdf)
- 🔧 [Manual de Instalación](docs/manual-instalacion.pdf)
- 💻 [Manual Técnico](docs/manual-tecnico.pdf)
- 📊 [Especificación de Requerimientos](docs/Analisis%20de%20EspecificacionesII.pdf)
- 🎯 [Memoria Técnica Completa](docs/MEMORIA%20TÉCNICA%20V2.0%20-%20AGILETEAM%20MANAGER.md)

---

<div align="center">

**¿Preguntas? ¿Sugerencias?**

Abre un [issue](https://github.com/franjrodriguez/agilteam-manager/issues) o contáctame directamente

</div>