# CLAUDE.md

## Proyecto

**AgilTeam Manager** — Aplicación de escritorio JavaFX + Spring Boot + PostgreSQL.  
FP DAM Distancia. Autor: Francisco José Rodríguez Ruiz.

---

## Stack

| Componente | Versión |
|---|---|
| Java (Eclipse Temurin) | 21.0.10 LTS |
| JavaFX | 21.0.4 |
| Spring Boot | 3.4.2 |
| PostgreSQL | 18.2 |
| Maven | wrapper `./mvnw` (NO hay Maven global) |
| SO | Windows 11 |

---

## Comandos

```bash
./mvnw clean compile
./mvnw test
./mvnw javafx:run
./mvnw test -Dtest=NombreDelaClaseTest
```

---

## Base de datos

```
Host: localhost  Puerto: 5432  BD: agilteamdb  Usuario: franrodriguez
```

Scripts SQL: `src/main/resources/sql/`

---

## Arquitectura — NO modificar el patrón

```
UI Controllers (JavaFX) → Services (Spring) → Repositories (JPA) → PostgreSQL
```

- Aplicación **escritorio puro**: `spring.main.web-application-type=none`. **No hay REST API.**
- Toda la lógica de negocio va en `@Service`. Los controllers UI no tienen lógica.
- Repositorios: `@Repository` + Spring Data JPA. Sin SQL nativo salvo casos excepcionales.

### Inyección en UI Controllers (patrón obligatorio)

```java
@Autowired
private CompetenciaService competenciaService;

// En initialize():
this.competenciaService = SpringContext.getBean(CompetenciaService.class);
```

### Anotaciones

- Controllers UI → `@Component` (NUNCA `@Controller`, eso es REST)
- Servicios → `@Service`
- Repositorios → `@Repository`
- Una clase nunca tiene dos anotaciones de rol a la vez.

### Carga de vistas

```java
MainController.cargarVista("/fxml/NombreVista.fxml");
```

### Dimensiones

- Ventana principal: `1440 × 900`
- Vistas hijas: `1200 × 806`

---

## Estructura de paquetes

```
com.iesaguadulce.agilteammanager
├── config/          → AppConfig, DataSeedConfig, DatabaseConfig, SpringContext
├── model/
│   ├── personas/    → Persona, Competencia, Puesto, PersonaCompetencia
│   ├── proyectos/   → Proyecto, Sprint, Tarea, TareaCompetencia
│   ├── asignaciones/→ Asignacion, AsignacionSugerida, Disponibilidad
│   └── seguridad/   → RolSistema, Permiso
├── repository/      → personas/ proyectos/ asignaciones/ seguridad/
├── service/         → personas/ proyectos/ asignaciones/ seguridad/ dashboard/
├── controller/ui/   → login/ dashboard/ personas/ proyectos/ asignaciones/
│                      seguridad/ configuracion/ metricas/ perfil/
├── dto/             → DTOs para dashboard y métricas
└── util/            → GeneradorPassword y utilidades
```

---

## Convenciones de código

- **Lombok**: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **FXML**: snake_case en `/resources/fxml/` → ej: `vista_personas.fxml`
- **Controllers UI**: PascalCase + sufijo `Controller` → ej: `PersonasController.java`
- **Styling**: AtlantaFX + CSS en `/resources/css/`
- **pom.xml**: no modificar sin explicar la necesidad. No inventar librerías.

---

## Autenticación

- Login directo contra tabla `personas` (campo `password` con hash BCrypt).
- No hay tabla separada de usuarios.
- Clase: `AutenticacionService.java`

---

## Enum TipoCompetencia

```
LENGUAJE, FRAMEWORK, BD, DEVOPS, TESTING, CLOUD
```

---

## Errores conocidos

**LazyInitializationException**
```java
Hibernate.initialize(entidad.getColeccion()); // en el Service, antes de retornar
```

**Beans duplicados**: verificar que cada clase tenga solo una anotación de rol (`@Component`, `@Service` o `@Repository`).

---

## Mapa vistas ↔ tablas BD

| Opción menú | Vista FXML | Tablas BD |
|---|---|---|
| Inicio (Admin) | MainView → DashboardAdminView | — |
| Inicio (resto) | MainView → DashboardView | — |
| Login | LoginView | personas |
| Puestos de trabajo | RolesProfesionalesView | puestos |
| Competencias | CompetenciasView | competencias |
| Profesionales | PersonasView | personas |
| Proyectos y Tareas | ProyectosTareasView, NuevaTareaDrawer, NuevoSprintDrawer | proyectos, sprints, tareas |
| Motor Asignación | — | — |
| Tareas Personales | MisTareasView | — |
| Usuarios | UsuariosView | personas |
| Roles y Permisos | RolesPermisosView | roles_sistema, permisos, roles_permisos |
| Configuración | — | — |

⚠️ Al crear controladores, revisar también tablas relacionadas no listadas aquí.

---

## Estado de módulos

| Módulo | Estado |
|---|---|
| Login + autenticación BCrypt | ✅ |
| MainView + navegación | ✅ |
| Dashboard KPIs | 🔄 Pendiente |
| CRUD Puestos | ✅ |
| CRUD Competencias | ✅ |
| CRUD Personas | ✅ |
| CRUD Proyectos / Sprints / Tareas | 🔄 Pendiente |
| Asignaciones + Motor | 🔄 Pendiente |
| Roles, permisos y usuarios | ✅ |
| Métricas y perfil | 🔄 Pendiente |
| Dockerización | 🔄 Pendiente |

🧠 Notas TDAH-Friendly
Paso a paso: No generes varios archivos si no se solicita.

Visual: Si explicas un flujo, usa esquemas simples.

Fiel al patrón: Si algo ya existe en el proyecto, cópialo, no inventes una solución nueva.