# HISTÓRICO DEL PROYECTO — AgilTeam Manager
> Última actualización: 2026-03-13
> Autor: Francisco José Rodríguez Ruiz
> Uso: Contexto para agentes IA / Claude en nuevas sesiones

---

## 1. IDENTIDAD DEL PROYECTO

**AgilTeam Manager** — Aplicación de escritorio para gestión de equipos ágiles.
Proyecto de **FP DAM a Distancia** (IES Aguadulce).
Stack: JavaFX 21 + Spring Boot 3.4.2 + PostgreSQL 18.
Arquitectura: `UI Controllers → Services → Repositories → PostgreSQL` (sin REST API).

---

## 2. HISTORIAL GIT (commits reales)

| Fecha | Hash | Mensaje |
|---|---|---|
| 2026-02-15 | `5ed38e4` | feat: setup inicial proyecto |
| 2026-02-28 | `4066197` | Actualización 0 errores compilación. Investigando bug login→cierre |
| 2026-03-01 | `1f04764` | Actualización 0 errores compilación. Investigando bug login→cierre |
| 2026-03-01 | `5aae68d` | Actualización 0 errores compilación. Investigando bug login→cierre |

> ⚠️ **Bug activo**: La app abre el Login, el usuario se autentica, pero la aplicación termina sin mostrar el `MainView`. Problema sin resolver a fecha 2026-03-01.

---

## 3. ESTADO REAL DE MÓDULOS (a 2026-03-13)

### ✅ Completo y funcional

| Módulo | Archivos clave |
|---|---|
| Login + BCrypt | `LoginController`, `AutenticacionService`, `LoginView.fxml` |
| Navegación principal | `MainController`, `MainView.fxml` |
| CRUD Puestos | `RolesProfesionalesController`, `PuestoService`, `PuestoRepository` |
| CRUD Competencias | `CompetenciasController`, `CompetenciaService`, `CompetenciaRepository` |
| CRUD Personas | `PersonasController`, `PersonaService`, `PersonaRepository` |
| Roles, permisos y usuarios | `RolesPermisosController`, `UsuariosController`, `RolSistemaService`, `PermisoService`, `UsuarioService` |
| Gestión competencias de persona | `AsignarCompetenciasController`, `PersonaCompetenciaService` |

### 🔄 Estructura presente, funcionalidad parcial o en revisión

| Módulo | Estado real | Archivos clave |
|---|---|---|
| Dashboard Admin | Controller carga KPIs de usuarios/roles y fecha última conexión. Sin gráficas. | `DashboardAdminController`, `DashboardAdminView.fxml` |
| Dashboard usuario normal | Vista FXML presente, controller existe | `DashboardController`, `DashboardView.fxml` |
| Proyectos / Sprints / Tareas | `ProyectosController` completo: lista proyectos, tablas sprints/tareas, drawer animado para crear sprint/tarea, búsqueda, filtros. `ProyectoService` y `SprintService` completos. Compilación OK. Pendiente: verificar flujo en ejecución. | `ProyectosController`, `NuevaTareaController`, `NuevoSprintController`, `ProyectosTareasView.fxml`, `NuevaTareaDrawer.fxml`, `NuevoSprintDrawer.fxml` |
| Motor de Asignación | Servicio CORE implementado con algoritmo de scoring: `score_ajustado = (score_base/100) × (1 - carga) × prioridad`. Genera sugerencias con explicación. Sin UI conectada. | `MotorAsignacionService`, `AsignacionService`, `AsignacionesController`, `CalcularAsignacionController` |
| Métricas | Controller stub, vista FXML presente | `MetricasController`, `MetricasService` |
| Mis Tareas (perfil usuario) | Vista FXML y controller presentes | `MisTareasController`, `MisTareasView.fxml` |
| Disponibilidad | Service y repository presentes, sin UI | `DisponibilidadService`, `DisponibilidadRepository` |

### ❌ No implementado

| Módulo | Notas |
|---|---|
| Configuración (pantalla) | Controller stub vacío (`ConfiguracionController` en dos paquetes) |
| Dockerización | Sin `Dockerfile` ni `docker-compose.yml` |
| Dashboard KPIs completos | Gráficas, tendencias, velocidad de sprint |

---

## 4. ESTRUCTURA DE ARCHIVOS JAVA (src/main/java)

```
com.iesaguadulce.agilteammanager
├── AgilteammanagerApplication.java   ← Entry point Spring Boot
├── JavaFxApplication.java             ← Entry point JavaFX
├── config/
│   ├── AppConfig.java
│   ├── DataSeedConfig.java
│   ├── DatabaseConfig.java
│   └── SpringContext.java             ← getBean() para controllers UI
├── model/
│   ├── personas/     Persona, Competencia, Puesto, PersonaCompetencia(+Id)
│   ├── proyectos/    Proyecto, Sprint, Tarea, TareaCompetencia(+Id)
│   ├── asignaciones/ Asignacion, AsignacionSugerida, Disponibilidad
│   └── seguridad/    RolSistema, Permiso
├── repository/       (un repo por modelo, Spring Data JPA)
├── service/
│   ├── personas/     CompetenciaService, DisponibilidadService,
│   │                 PersonaCompetenciaService, PersonaService,
│   │                 PuestoService, RolProfesionalService
│   ├── proyectos/    ProyectoService, SprintService, TareaService
│   ├── asignaciones/ AsignacionService, MetricasService, MotorAsignacionService
│   ├── dashboard/    DashboardService
│   └── seguridad/    AutenticacionService, PermisoService,
│                     RolSistemaService, UsuarioService
├── controller/ui/
│   ├── login/        LoginController
│   ├── dashboard/    MainController, DashboardAdminController,
│   │                 DashboardController, WidgetCardController
│   ├── personas/     PersonasController, CompetenciasController,
│   │                 RolesProfesionalesController, AsignarCompetenciasController
│   ├── proyectos/    ProyectosController, NuevaTareaController,
│   │                 NuevoSprintController, DrawerChildController
│   ├── asignaciones/ AsignacionesController, CalcularAsignacionController
│   ├── seguridad/    RolesPermisosController, UsuariosController,
│   │                 ConfiguracionController (×2 — revisar duplicado)
│   ├── metricas/     MetricasController
│   ├── perfil/       MisTareasController
│   └── configuracion/ConfiguracionController
├── dto/
│   ├── DashboardKPIs.java
│   ├── PersonaActivaDTO.java
│   ├── RendimientoDiaDTO.java
│   └── TareaPendienteDTO.java
└── util/
    └── GeneradorPassword.java
```

---

## 5. VISTAS FXML (src/main/resources/views)

```
views/
├── login/           LoginView.fxml  (LoginViewOLD.fxml — ignorar)
├── dashboard/       MainView.fxml, DashboardAdminView.fxml,
│                    DashboardView.fxml, WidgetCard.fxml
├── profesionales/   PersonasView.fxml, CompetenciasView.fxml,
│                    RolesProfesionalesView.fxml
├── proyectos/       ProyectosTareasView.fxml (activa),
│                    NuevaTareaDrawer.fxml, NuevoSprintDrawer.fxml,
│                    ProyectosSprintTareasView.fxml (alternativa),
│                    ProyectosTareasView0.fxml (versión anterior — ignorar)
├── seguridad/       RolesPermisosView.fxml, UsuariosView.fxml
├── perfil/          MisTareasView.fxml
├── configuracion/   ConfiguracionView.fxml
└── aboutView.fxml
```

---

## 6. BUG CONOCIDO ACTIVO — Login → Cierre

**Síntoma**: Login funciona (BCrypt OK, persona autenticada), pero tras el login la app cierra sin cargar `MainView`.

**Contexto**: El bug lleva 3 commits consecutivos sin resolver (28 Feb – 1 Mar 2026).
**Área probable**: Transición entre `LoginController` y `MainController`. Revisar el método que carga `MainView.fxml` tras autenticación exitosa, y el flujo del `Stage` en `JavaFxApplication`.

**Patrón de carga de vistas**:
```java
MainController.cargarVista("/views/dashboard/MainView.fxml");
```

---

## 7. REGLAS DE ARQUITECTURA (no negociables)

### Inyección en controllers UI
```java
// En initialize():
this.miService = SpringContext.getBean(MiService.class);
```

### Anotaciones (una sola por clase)
- Controllers UI → `@Component`
- Servicios → `@Service`
- Repositorios → `@Repository`

### LazyInitializationException (solución)
```java
// En el Service, antes de retornar la entidad:
Hibernate.initialize(entidad.getColeccion());
```

### Enums
- `TipoCompetencia`: `LENGUAJE, FRAMEWORK, BD, DEVOPS, TESTING, CLOUD`
- Estados proyecto: `planificacion, activo, completado, cancelado`

### Dimensiones de ventanas
- Principal: `1440 × 900`
- Vistas hijas: `1200 × 806`

---

## 8. BASE DE DATOS

```
Host: localhost | Puerto: 5432 | BD: agilteamdb | Usuario: franrodriguez
```

Scripts: `src/main/resources/sql/`
Autenticación: tabla `personas`, campo `password` con hash BCrypt (sin tabla de usuarios separada).

---

## 9. MAPA MENÚ → VISTA → TABLAS BD

| Menú | Vista FXML | Tablas principales |
|---|---|---|
| Login | LoginView | personas |
| Inicio (Admin) | DashboardAdminView | personas, roles_sistema |
| Inicio (resto) | DashboardView | — |
| Puestos de trabajo | RolesProfesionalesView | puestos |
| Competencias | CompetenciasView | competencias |
| Profesionales | PersonasView | personas |
| Proyectos y Tareas | ProyectosTareasView | proyectos, sprints, tareas |
| Motor Asignación | (sin vista conectada aún) | asignaciones, disponibilidad |
| Mis Tareas | MisTareasView | tareas, asignaciones |
| Usuarios | UsuariosView | personas |
| Roles y Permisos | RolesPermisosView | roles_sistema, permisos, roles_permisos |
| Configuración | ConfiguracionView | — |

---

## 10. RECURSOS UI

- **Estilos CSS**: AtlantaFX (cupertino-dark/light, nord-dark/light, primer-dark/light) + `agilteam-styles.css`
- **Fuente**: Inter (Regular, Medium, SemiBold, Bold)
- **Iconos**: `/resources/icons/` (PNG por categoría, versiones de color por estado)
- **Fotos profesionales**: `/resources/assets/profesionales/` (foto_0X_h.jpg / foto_0X_m.jpg)
- **Tema activo en vistas**: `primer-light.css` + `agilteam-styles.css`

---

## 11. COMANDOS ÚTILES

```bash
./mvnw clean compile          # compilar
./mvnw javafx:run             # ejecutar
./mvnw test                   # todos los tests
./mvnw test -Dtest=ClaseTest  # test específico
```

> ⚠️ No hay Maven global instalado. Usar siempre `./mvnw`.

---

## 12. PRÓXIMOS PASOS SUGERIDOS (pendientes al 2026-03-13)

1. **Resolver bug login→cierre** (prioridad máxima, bloquea todo lo demás)
2. Verificar flujo completo Proyectos/Sprints/Tareas en ejecución
3. Conectar UI del Motor de Asignación (`AsignacionesController`)
4. Completar Dashboard KPIs (gráficas, métricas de sprint)
5. Completar vista de Métricas
6. Revisar controlador duplicado `ConfiguracionController` (en `configuracion/` y `seguridad/`)
7. Dockerización
