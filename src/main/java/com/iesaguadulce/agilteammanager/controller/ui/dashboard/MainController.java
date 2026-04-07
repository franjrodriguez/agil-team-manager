package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.util.FotoUtil;
import com.iesaguadulce.agilteammanager.util.HelpViewer;
import com.iesaguadulce.agilteammanager.controller.ui.login.LoginController;
import com.iesaguadulce.agilteammanager.controller.ui.perfil.MisTareasController;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.service.seguridad.PermisoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MainController — Controlador principal de la ventana de la aplicación.
 *
 * <p>Gestiona la navegación entre vistas, permisos de menú, cabecera de usuario
 * y comunicación entre componentes. Punto central de coordinación UI.</p>
 *
 *  * Responsabilidades:
 *  - Inicializar la cabecera (usuario, rol, fecha, estado BD)
 *  - Gestionar la navegación lateral cargando FXMLs en el AnchorPane central
 *  - Controlar los iconos de la cabecera (About, Salir)
 *  - Actualizar el pie de página con estado de conexión y hora de sesión
 *  - Aplicar visibilidad de opciones del menú según permisos del rol
 *
 * @see PermisoService
 */

@Component
public class MainController {

    // ─────────────────────────────────────────────────────────
    // CABECERA
    // ─────────────────────────────────────────────────────────

    @FXML private Label    lbl_path_in_app;
    @FXML private ImageView img_user;
    @FXML private Label    lbl_user;
    @FXML private Label    lbl_system_group;
    @FXML private Label    lbl_date;
    @FXML private ImageView  img_db_status;   // ya no está en FXML → será null (manejado con null-check)
    @FXML private MenuButton btn_ayuda;
    @FXML private ImageView  img_about;
    @FXML private ImageView  img_exit;

    // ─────────────────────────────────────────────────────────
    // ÁREA CENTRAL
    // ─────────────────────────────────────────────────────────

    @FXML private AnchorPane contentArea;

    // ─────────────────────────────────────────────────────────
    // PIE DE PÁGINA
    // ─────────────────────────────────────────────────────────

    @FXML private ImageView img_footer_db_status;
    @FXML private Label     lbl_status_conection;
    @FXML private Label     lbl_status_conected;

    // ─────────────────────────────────────────────────────────
    // BOTONES DEL MENÚ LATERAL (para control de permisos)
    // ─────────────────────────────────────────────────────────

    @FXML private Button btn_inicio;
    @FXML private Button btn_roles;
    @FXML private Button btn_competencias;
    @FXML private Button btn_profesionales;
    @FXML private Button btn_proyectos;
    @FXML private Button btn_proy_motor;
    @FXML private Button btn_my_tareas;
    @FXML private Button btn_admin_user;
    @FXML private Button btn_admin_roles;
    @FXML private Button btn_configuration;

    // ─────────────────────────────────────────────────────────
    // DATOS DE SESIÓN
    // ─────────────────────────────────────────────────────────

    /** Objeto Persona completo del usuario logueado (necesario para MisTareas y permisos) */
    private Persona personaEnSesion;

    private String  nombreUsuario = "Usuario";
    private String  rolUsuario    = "ROL";
    private boolean bdConectada   = false;
    private boolean esAdmin       = false;

    // ─────────────────────────────────────────────────────────
    // SERVICIOS (cargados manualmente desde Spring)
    // ─────────────────────────────────────────────────────────

    private PermisoService permisoService;

    // ─────────────────────────────────────────────────────────
    // RUTAS FXML
    // ─────────────────────────────────────────────────────────

    private static final String FXML_DASHBOARD_ADMIN = "/views/dashboard/DashboardAdminView.fxml";
    private static final String FXML_DASHBOARD_USER  = "/views/dashboard/DashboardView.fxml";
    private static final String FXML_ABOUT           = "/views/aboutView.fxml";

    private static final String FXML_ROLES           = "/views/profesionales/RolesProfesionalesView.fxml";
    private static final String FXML_COMPETENCIAS    = "/views/profesionales/CompetenciasView.fxml";
    private static final String FXML_PROFESIONALES   = "/views/profesionales/PersonasView.fxml";

    private static final String FXML_PROYECTOS       = "/views/proyectos/ProyectosTareasView.fxml";

    private static final String FXML_MOTOR           = "/views/asignaciones/MotorAsignacionView.fxml";

    private static final String FXML_MIS_TAREAS      = "/views/perfil/MisTareasView.fxml";

    private static final String FXML_USUARIOS        = "/views/seguridad/UsuariosView.fxml";
    private static final String FXML_ROLES_SISTEMA   = "/views/seguridad/RolesPermisosView.fxml";

    private static final String FXML_CONFIGURACION   = "/views/configuracion/ConfiguracionView.fxml";

    private static final String FXML_LOGIN           = "/views/login/LoginView.fxml";
    private static final String FXML_CHANGE_SESSION  = "/views/sesion/ChangeSessionView.fxml";

    /** Inicializa tooltips y carga servicios. */
    @FXML
    public void initialize() {
        // Cargamos el servicio de permisos desde Spring
        permisoService = SpringContext.getBean(PermisoService.class);

        Tooltip.install(img_about,  new Tooltip("Acerca de AgilTeam Manager"));
        Tooltip.install(img_exit,   new Tooltip("Cerrar sesión"));
        if (btn_ayuda != null) btn_ayuda.setTooltip(new Tooltip("Centro de Ayuda y documentación"));

        actualizarCabecera();
        actualizarFooter();
    }

    // ─────────────────────────────────────────────────────────
    // MÉTODO PÚBLICO llamado por LoginController tras autenticar
    // ─────────────────────────────────────────────────────────

    /**
     * LoginController llama a este método justo después de cargar MainView.fxml.
     * Ahora recibe el objeto Persona completo para poder:
     *   1. Mostrar nombre y rol en la cabecera
     *   2. Cargar los permisos y ajustar la visibilidad del menú
     *   3. Pasarle el objeto al controlador de "Mis Tareas"
     *
     * @param persona  Objeto Persona autenticado
     * @param bdOk     true si la BD está conectada
     * @param esAdmin  true si el usuario tiene rol ADMIN
     */
    public void iniciarSesion(Persona persona, boolean bdOk, boolean esAdmin) {
        this.personaEnSesion = persona;
        this.nombreUsuario   = persona.getNombre();
        this.rolUsuario      = persona.getRol().getNombre();
        this.bdConectada     = bdOk;
        this.esAdmin         = esAdmin;

        actualizarCabecera();
        actualizarFooter();

        // Ajustamos qué botones del menú están activos según los permisos del rol
        aplicarPermisosMenu();

        // Cargamos el dashboard inicial según el rol
        if (esAdmin) {
            cargarVista(FXML_DASHBOARD_ADMIN, "> INICIO");
        } else {
            cargarVista(FXML_DASHBOARD_USER, "> INICIO");
        }
    }

    // ─────────────────────────────────────────────────────────
    // NAVEGACIÓN (o un intento de hacerlo bien... no me gusta)
    // ─────────────────────────────────────────────────────────

    @FXML
    public void mostrarInicio() {
        if (esAdmin) {
            cargarVista(FXML_DASHBOARD_ADMIN, "> INICIO");
        } else {
            cargarVista(FXML_DASHBOARD_USER, "> INICIO");
        }
    }

    @FXML
    public void mostrarRoles() {
        cargarVista(FXML_ROLES, "> PROFESIONALES > Roles");
    }

    @FXML
    public void mostrarCompetencias() {
        cargarVista(FXML_COMPETENCIAS, "> PROFESIONALES > Competencias");
    }

    @FXML
    public void mostrarProfesionales() {
        cargarVista(FXML_PROFESIONALES, "> PROFESIONALES > Profesionales");
    }

    @FXML
    public void mostrarProyectos() {
        cargarVista(FXML_PROYECTOS, "> PROYECTOS > Proyectos y Tareas");
    }

    @FXML
    public void mostrarMotorAsignacion() {
        cargarVista(FXML_MOTOR, "> PROYECTOS > Motor de Asignación");
    }

    /**
     * Muestra las tareas del usuario actual.
     * Inyecta la persona en sesión al controller correspondiente.
     */
    @FXML
    public void mostrarMisTareas() {
        FXMLLoader loader = cargarVista(FXML_MIS_TAREAS, "> MI PERFIL > Tareas Personales");

        if (loader != null && personaEnSesion != null) {
            MisTareasController ctrl = loader.getController();
            ctrl.setPersona(personaEnSesion);
        }
    }

    @FXML
    public void mostrarUsuarios() {
        cargarVista(FXML_USUARIOS, "> ADMINISTRACIÓN > Usuarios");
    }

    @FXML
    public void mostrarRolesPermisos() {
        cargarVista(FXML_ROLES_SISTEMA, "> ADMINISTRACIÓN > Roles y Permisos");
    }

    @FXML
    public void mostrarConfiguracion() {
        cargarVista(FXML_CONFIGURACION, "> ADMINISTRACIÓN > Configuración");
    }

    // ─────────────────────────────────────────────────────────
    // ICONOS DE CABECERA
    // ─────────────────────────────────────────────────────────

    /** Muestra el diálogo "Acerca de". */
    @FXML
    public void abrirAbout(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_ABOUT));
            Scene scene = new Scene(loader.load());

            Stage aboutStage = new Stage();
            aboutStage.setTitle("Acerca de AgilTeam Manager");
            aboutStage.setScene(scene);
            aboutStage.setResizable(false);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) contentArea.getScene().getWindow();
            aboutStage.initOwner(owner);
            aboutStage.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir la ventana 'Acerca de'.\n" + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    // AYUDA EN LÍNEA
    // ─────────────────────────────────────────────────────────

    /**
     * Abre el Centro de Ayuda (HTML) en el navegador predeterminado del sistema.
     * El archivo se encuentra en src/main/resources/ayuda/centro-ayuda.html
     */
    @FXML
    public void abrirCentroAyuda() {
        // abrirRecursoEnNavegador("/ayuda/centro-ayuda.html");
        try {
            new HelpViewer().mostrarAyuda();
        } catch (IOException e) {
            mostrarError("No se pudo abrir el centro de ayuda.\n" + e.getMessage());
        }
    }

    /**
     * Abre la página de vídeo demostración en el navegador predeterminado.
     * El archivo se encuentra en src/main/resources/ayuda/video-demo.html
     */
    @FXML
    public void abrirVideoDemo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vídeo Demostración");
        alert.setHeaderText("AgilTeam Manager — Vídeo demostración");
        alert.setContentText("El vídeo de demostración estará disponible próximamente en el repositorio del proyecto.");

        ButtonType btnAnticipo = new ButtonType("Ver anticipo");
        ButtonType btnSeguir   = new ButtonType("Seguir", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnAnticipo, btnSeguir);

        alert.showAndWait().ifPresent(respuesta -> {
            if (respuesta == btnAnticipo) {
                try {
                    new ProcessBuilder("cmd", "/c", "start",
                            "https://youtu.be/6Ryfq6fbziA")
                            .start();
                } catch (IOException e) {
                    mostrarError("No se pudo abrir el navegador.\n" + e.getMessage());
                }
            }
        });    }

    /**
     * Abre un recurso del classpath en el navegador predeterminado del sistema.
     * Si la app corre desde un JAR, extrae toda la carpeta /ayuda/ a un directorio
     * temporal manteniendo la estructura relativa (HTML + imágenes + CSS).
     *
     * @param rutaRecurso  Ruta dentro del classpath (ej. "/ayuda/centro-ayuda.html")
     */
    private void abrirRecursoEnNavegador(String rutaRecurso) {
        try {
            java.net.URL url = getClass().getResource(rutaRecurso);
            if (url == null) {
                mostrarError("No se encontró el archivo de ayuda:\n" + rutaRecurso);
                return;
            }

            java.nio.file.Path archivoAAbrir;

            if ("file".equals(url.getProtocol())) {
                // Desarrollo (IntelliJ): archivo normal en disco, abrir directamente
                archivoAAbrir = java.nio.file.Path.of(url.toURI());
            } else {
                // Producción (JAR): extraer toda la carpeta /ayuda/ a un directorio temporal
                // para que el navegador pueda resolver imágenes y CSS con rutas relativas
                java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory("agilteam_ayuda_");
                extraerCarpetaDelJar("/ayuda/", tempDir);
                String nombreArchivo = rutaRecurso.substring("/ayuda/".length());
                archivoAAbrir = tempDir.resolve(nombreArchivo);
            }

            new ProcessBuilder("cmd", "/c", "start", archivoAAbrir.toUri().toString()).start();

        } catch (Exception e) {
            mostrarError("No se pudo abrir el navegador.\n" + e.getMessage());
        }
    }

    /**
     * Extrae recursivamente una carpeta del JAR al directorio de destino,
     * preservando la estructura de subdirectorios para que las rutas relativas
     * (imágenes, CSS) funcionen correctamente en el navegador.
     * Usa JarFile directamente para evitar conflictos con el FileSystem ya abierto por la JVM.
     *
     * @param carpetaRecurso  Ruta de la carpeta en el classpath (ej. "/ayuda/")
     * @param destino         Directorio de destino en el sistema de archivos
     */
    private void extraerCarpetaDelJar(String carpetaRecurso, java.nio.file.Path destino) throws Exception {
        java.io.File jarFile = new java.io.File(
                getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

        // Normalizar prefijo: "/ayuda/" → "ayuda/"
        String prefijo = carpetaRecurso.startsWith("/") ? carpetaRecurso.substring(1) : carpetaRecurso;

        try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
            java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                java.util.jar.JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(prefijo) && !entry.isDirectory()) {
                    // Ruta relativa dentro de la carpeta (ej. "images/captura01.png")
                    String rutaRelativa = entry.getName().substring(prefijo.length());
                    java.nio.file.Path dest = destino.resolve(rutaRelativa);
                    java.nio.file.Files.createDirectories(dest.getParent());
                    try (java.io.InputStream is = jar.getInputStream(entry)) {
                        java.nio.file.Files.copy(is, dest,
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }

    /** Cierra la sesión actual y muestra el login para que otro usuario se autentique. */
    @FXML
    public void cerrarSesion(MouseEvent event) {
        // Limpiar estado de sesión
        personaEnSesion = null;
        nombreUsuario   = "—";
        rolUsuario      = "—";
        bdConectada     = false;
        esAdmin         = false;

        // Resetear UI de cabecera y pie
        actualizarCabecera();
        actualizarFooter();
        if (lbl_status_conected != null) lbl_status_conected.setText("Sesión iniciada a las: --:--:--");

        // Deshabilitar todo el menú lateral
        deshabilitarMenuCompleto();

        // Mostrar pantalla de espera en el área central
        cargarVista(FXML_CHANGE_SESSION, "> SESIÓN CERRADA");

        // Abrir el login como modal; si el usuario lo cierra sin logarse, cerrar la app
        abrirLoginModal();
    }

    private void abrirLoginModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_LOGIN));
            Scene scene = new Scene(loader.load());

            LoginController loginCtrl = loader.getController();
            loginCtrl.setMainController(this);

            Stage loginStage = new Stage();
            loginStage.setTitle("Iniciar Sesión — AgilTeam Manager");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initOwner((Stage) contentArea.getScene().getWindow());
            loginStage.showAndWait();

            // Si el modal se cierra sin que nadie se autentique → cerrar la app
            if (personaEnSesion == null) {
                Platform.exit();
                System.exit(0);
            }

        } catch (IOException e) {
            mostrarError("No se pudo abrir el login.\n" + e.getMessage());
        }
    }

    private void deshabilitarMenuCompleto() {
        List.of(btn_roles, btn_competencias, btn_profesionales, btn_proyectos,
                btn_proy_motor, btn_my_tareas, btn_admin_user, btn_admin_roles,
                btn_configuration)
            .forEach(b -> { if (b != null) { b.setDisable(true); b.setOpacity(0.4); } });
    }

    // ─────────────────────────────────────────────────────────
    // LÓGICA DE PERMISOS DEL MENÚ
    // ─────────────────────────────────────────────────────────

    /**
     * Lee los permisos del rol del usuario en sesión y activa/desactiva
     * cada botón del menú lateral según si tiene acceso o no.
     *
     * Un botón desactivado aparece semitransparente y no responde al clic.
     */
    private void aplicarPermisosMenu() {
        if (personaEnSesion == null || personaEnSesion.getRol() == null) return;

        Long rolId = personaEnSesion.getRol().getId();

        // Cargamos los códigos de permiso del rol desde la BD
        Set<String> codigos = permisoService.obtenerPermisosPorRol(rolId)
                .stream()
                .map(p -> p.getCodigo())
                .collect(Collectors.toSet());

        // Mapeamos cada botón con su permiso correspondiente
        aplicarEstadoBoton(btn_roles,          codigos, "CRUD_ROLES_PROFESIONALES");
        aplicarEstadoBoton(btn_competencias,   codigos, "CRUD_COMPETENCIAS");
        aplicarEstadoBoton(btn_profesionales,  codigos, "CRUD_PERSONAS");
        aplicarEstadoBoton(btn_proyectos,      codigos, "CRUD_PROYECTOS");
        aplicarEstadoBoton(btn_proy_motor,     codigos, "CALCULAR_ASIGNACION");
        aplicarEstadoBoton(btn_admin_user,     codigos, "CRUD_USUARIOS_SISTEMA");
        aplicarEstadoBoton(btn_admin_roles,    codigos, "CRUD_ROLES_SISTEMA");
        aplicarEstadoBoton(btn_configuration,  codigos, "CRUD_CONFIGURACION");

        // Tareas Personales: OFF para ADMIN (rol de gestión del sistema, sin tareas asignadas)
        if (esAdmin) {
            aplicarEstadoBoton(btn_my_tareas, codigos, "");   // permiso inexistente → siempre OFF
        } else {
            aplicarEstadoBoton(btn_my_tareas, codigos, "VER_TAREAS_PROPIAS");
        }
        // btn_inicio siempre habilitado (VER_DASHBOARD lo tienen todos)
    }

    /**
     * Activa o desactiva un botón del menú según si el conjunto de permisos
     * contiene el código requerido.
     *
     * @param boton    Botón del menú lateral
     * @param codigos  Set de códigos de permiso del usuario en sesión
     * @param permiso  Código de permiso requerido para este botón
     */
    private void aplicarEstadoBoton(Button boton, Set<String> codigos, String permiso) {
        if (boton == null) return;

        boolean tieneAcceso = codigos.contains(permiso);
        boton.setDisable(!tieneAcceso);
        // Reducimos la opacidad para dar feedback visual de que está bloqueado
        boton.setOpacity(tieneAcceso ? 1.0 : 0.4);
    }

    /**
     * Carga un FXML en el AnchorPane central y devuelve el FXMLLoader
     * para que el llamador pueda acceder al controlador si lo necesita.
     *
     * @param rutaFxml  Ruta del FXML dentro del classpath
     * @param pasillo   Texto de navegación para la cabecera
     * @return FXMLLoader con el controlador ya inicializado, o null si falla
     */
    private FXMLLoader cargarVista(String rutaFxml, String pasillo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            loader.setControllerFactory(SpringContext::getBean);
            Node vista = loader.load();

            AnchorPane.setTopAnchor(vista,    0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista,   0.0);
            AnchorPane.setRightAnchor(vista,  0.0);

            contentArea.getChildren().setAll(vista);
            lbl_path_in_app.setText(pasillo);

            return loader;

        } catch (IOException e) {
            mostrarPlaceholder(pasillo, rutaFxml);
            lbl_path_in_app.setText(pasillo);
            return null;
        }
    }

    private void mostrarPlaceholder(String titulo, String rutaFxml) {
        Label placeholder = new Label("🚧  Vista en construcción\n\n" + titulo
                + "\n\nFXML: " + rutaFxml);
        placeholder.setStyle("-fx-font-size: 18px; -fx-text-fill: #888; "
                + "-fx-alignment: center; -fx-text-alignment: center;");

        AnchorPane.setTopAnchor(placeholder,    0.0);
        AnchorPane.setBottomAnchor(placeholder, 0.0);
        AnchorPane.setLeftAnchor(placeholder,   0.0);
        AnchorPane.setRightAnchor(placeholder,  0.0);

        contentArea.getChildren().setAll(placeholder);
    }

    // ─────────────────────────────────────────────────────────
    // MÉTODOS PRIVADOS — cabecera / footer
    // ─────────────────────────────────────────────────────────

    private void actualizarCabecera() {
        if (lbl_user != null)         lbl_user.setText(nombreUsuario);
        if (lbl_system_group != null) lbl_system_group.setText(rolUsuario);

        if (lbl_date != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.of("es", "ES"));
            lbl_date.setText(LocalDate.now().format(fmt));
        }

        actualizarIconoBD(img_db_status);
        cargarAvatarUsuario();
    }

    private void cargarAvatarUsuario() {
        if (img_user == null) return;

        // Intentamos cargar el avatar desde ~/.agilteammanager/avatars/
        if (personaEnSesion != null) {
            Image imagen = FotoUtil.cargarImagen(personaEnSesion.getFotoPath());
            if (imagen != null) {
                img_user.setImage(imagen);
                return;
            }
        }

        // Fallback: icono genérico según sexo
        Character sexo = personaEnSesion != null ? personaEnSesion.getSexo() : null;
        String ruta = Character.valueOf('F').equals(sexo)
                ? "/icons/user-female.png"
                : "/icons/user-male.png";
        try {
            img_user.setImage(new Image(getClass().getResourceAsStream(ruta)));
        } catch (Exception ignored) { }
    }

    private void actualizarFooter() {
        actualizarIconoBD(img_footer_db_status);

        if (lbl_status_conection != null) {
            lbl_status_conection.setText("Estado BD: " + (bdConectada ? "✔ Conectada" : "✘ Desconectada"));
        }

        if (lbl_status_conected != null) {
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            lbl_status_conected.setText("Sesión iniciada a las: " + hora);
        }
    }

    private void actualizarIconoBD(ImageView iconView) {
        if (iconView == null) return;
        String ruta = bdConectada
                ? "/icons/menu/database-verde.png"
                : "/icons/menu/database-rojo.png";
        try {
            iconView.setImage(new Image(getClass().getResourceAsStream(ruta)));
        } catch (Exception e) {
            // Si el icono no existe, no falla la aplicación
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
