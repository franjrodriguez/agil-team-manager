package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.controller.ui.perfil.MisTareasController;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.service.seguridad.PermisoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MainController — Controlador principal de la ventana de la aplicación.
 *
 * Responsabilidades:
 *  - Inicializar la cabecera (usuario, rol, fecha, estado BD)
 *  - Gestionar la navegación lateral cargando FXMLs en el AnchorPane central
 *  - Controlar los iconos de la cabecera (About, Salir)
 *  - Actualizar el pie de página con estado de conexión y hora de sesión
 *  - Aplicar visibilidad de opciones del menú según permisos del rol
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
    @FXML private ImageView img_db_status;
    @FXML private ImageView img_about;
    @FXML private ImageView img_exit;

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
    private static final String FXML_MOTOR           = "/views/proyectos/MotorAsignacionView.fxml";

    private static final String FXML_MIS_TAREAS      = "/views/perfil/MisTareasView.fxml";

    private static final String FXML_USUARIOS        = "/views/seguridad/UsuariosView.fxml";
    private static final String FXML_ROLES_SISTEMA   = "/views/seguridad/RolesPermisosView.fxml";

    private static final String FXML_CONFIGURACION   = "/views/configuracion/ConfiguracionView.fxml";

    // ─────────────────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Cargamos el servicio de permisos desde Spring
        permisoService = SpringContext.getBean(PermisoService.class);

        Tooltip.install(img_db_status, new Tooltip("Estado de la conexión a la BD"));
        Tooltip.install(img_about,     new Tooltip("Acerca de AgilTeam Manager"));
        Tooltip.install(img_exit,      new Tooltip("Cerrar sesión y salir"));

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
    // NAVEGACIÓN
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
     * Carga Mis Tareas y le inyecta la Persona en sesión al controlador.
     * Es el único caso donde necesitamos el controlador tras la carga.
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

    @FXML
    public void salirAplicacion(MouseEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Salir de AgilTeam Manager");
        confirmacion.setHeaderText("¿Deseas cerrar la sesión y salir?");
        confirmacion.setContentText("Se cerrarán todas las ventanas abiertas.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
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
        aplicarEstadoBoton(btn_my_tareas,      codigos, "VER_TAREAS_PROPIAS");
        aplicarEstadoBoton(btn_admin_user,     codigos, "CRUD_USUARIOS_SISTEMA");
        aplicarEstadoBoton(btn_admin_roles,    codigos, "CRUD_ROLES_SISTEMA");
        aplicarEstadoBoton(btn_configuration,  codigos, "CRUD_ROLES_SISTEMA");
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

    // ─────────────────────────────────────────────────────────
    // MÉTODOS PRIVADOS — carga de vistas
    // ─────────────────────────────────────────────────────────

    /**
     * Carga un FXML en el AnchorPane central y devuelve el FXMLLoader
     * para que el llamador pueda acceder al controlador si lo necesita.
     *
     * @param rutaFxml  Ruta del FXML dentro del classpath
     * @param pasillo   Texto de navegación para la cabecera
     * @return FXMLLoader con el controlador ya inicializado, o null si falló
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
