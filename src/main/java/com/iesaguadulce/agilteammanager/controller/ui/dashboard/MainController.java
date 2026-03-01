package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import java.util.Locale;
import java.util.Optional;

/**
 * MainController — Controlador principal de la ventana de la aplicación.
 *
 * Responsabilidades:
 *  - Inicializar la cabecera (usuario, rol, fecha, estado BD)
 *  - Gestionar la navegación lateral cargando FXMLs en el AnchorPane central
 *  - Controlar los iconos de la cabecera (About, Salir)
 *  - Actualizar el pie de página con estado de conexión y hora de sesión
 */
@Component
public class MainController {

    // ─────────────────────────────────────────────────────────
    // CABECERA
    // ─────────────────────────────────────────────────────────

    /** Muestra la ruta de navegación actual: > INICIO > PROFESIONALES, etc. */
    @FXML private Label lbl_path_in_app;

    /** Avatar del usuario logueado */
    @FXML private ImageView img_user;

    /** Nombre del usuario logueado */
    @FXML private Label lbl_user;

    /** Rol profesional del usuario (ADMIN, JEFE_PROYECTO, USUARIO) */
    @FXML private Label lbl_system_group;

    /** Fecha del día formateada */
    @FXML private Label lbl_date;

    /**
     * Icono de estado de la BD en la cabecera.
     * Rojo = sin conexión | Verde = conectado.
     * SOLO INFORMATIVO — no tiene acción de clic.
     */
    @FXML private ImageView img_db_status;

    /** Icono de información (abre AboutView) */
    @FXML private ImageView img_about;

    /** Icono de salida (cierra la aplicación) */
    @FXML private ImageView img_exit;

    // ─────────────────────────────────────────────────────────
    // ÁREA CENTRAL
    // ─────────────────────────────────────────────────────────

    /** Contenedor central donde se cargan dinámicamente las vistas FXML */
    @FXML private AnchorPane contentArea;

    // ─────────────────────────────────────────────────────────
    // PIE DE PÁGINA
    // ─────────────────────────────────────────────────────────

    /** Icono de estado de BD en el footer */
    @FXML private ImageView img_footer_db_status;

    /** Texto de estado de conexión a la BD */
    @FXML private Label lbl_status_conection;

    /** Texto con la hora en que el usuario inició sesión */
    @FXML private Label lbl_status_conected;

    // ─────────────────────────────────────────────────────────
    // DATOS DE SESIÓN (se setean desde el controlador de Login)
    // ─────────────────────────────────────────────────────────

    private String  nombreUsuario = "Usuario";
    private String  rolUsuario    = "ROL";
    private boolean bdConectada   = false;
    private boolean esAdmin       = false;

    // ─────────────────────────────────────────────────────────
    // RUTAS FXML DE LAS VISTAS (centralizado para fácil mantenimiento)
    // ─────────────────────────────────────────────────────────

    // Dashboards de inicio (rol-dependientes)
    private static final String FXML_DASHBOARD_ADMIN = "/views/dashboard/DashboardAdminView.fxml";
    private static final String FXML_DASHBOARD_USER  = "/views/dashboard/DashboardView.fxml";
    private static final String FXML_ABOUT           = "/views/aboutView.fxml";

    // Secciones del menú de navegación

    // Submenú PROFESIONALES /views/personas/
    private static final String FXML_ROLES           = "/views/profesionales/RolesProfesionalesView.fxml";
    private static final String FXML_COMPETENCIAS    = "/views/profesionales/CompetenciasView.fxml";
    private static final String FXML_PROFESIONALES   = "/views/profesionales/PersonasView.fxml";

    // Submenú PROYECTOS /views/proyectos/
    private static final String FXML_PROYECTOS       = "/views/proyectos/ProyectosTareasView.fxml";
    private static final String FXML_MOTOR           = "/views/proyectos/MotorAsignacionView.fxml";

    // Submenú PROYECTOS /views/proyectos/
    private static final String FXML_MIS_TAREAS      = "/views/perfil/MisTareasView.fxml";

    // Submenú PROYECTOS /views/proyectos/
    private static final String FXML_USUARIOS        = "/views/seguridad/UsuariosView.fxml";
    private static final String FXML_ROLES_SISTEMA   = "/views/seguridad/RolesPermisosView.fxml";
    private static final String FXML_PERMISOS        = "/views/seguridad/RolesPermisosView.fxml";

    // Submenú PROYECTOS /views/proyectos/
    private static final String FXML_CONFIGURACION   = "/views/configuracion/ConfiguracionView.fxml";

    // ─────────────────────────────────────────────────────────
    // INICIALIZACIÓN — JavaFX llama a este método automáticamente
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Tooltips en código (ImageView no admite <tooltip> en FXML)
        Tooltip.install(img_db_status, new Tooltip("Estado de la conexión a la BD"));
        Tooltip.install(img_about,     new Tooltip("Acerca de AgilTeam Manager"));
        Tooltip.install(img_exit,      new Tooltip("Cerrar sesión y salir"));

        // La cabecera y footer se actualizan con valores vacíos por defecto.
        // El contenido real se carga cuando LoginController llama a iniciarSesion().
        actualizarCabecera();
        actualizarFooter();
    }

    // ─────────────────────────────────────────────────────────
    // MÉTODO PÚBLICO llamado por LoginController tras autenticar
    // ─────────────────────────────────────────────────────────

    /**
     * LoginController llama a este método justo después de cargar MainView.fxml.
     * Recibe los datos de sesión e inicia la vista central según el rol del usuario.
     *
     * @param nombre   Nombre completo del usuario logueado
     * @param rol      Nombre del rol de sistema (ej: "ADMIN", "JEFE_PROYECTO")
     * @param bdOk     true si la BD está conectada
     * @param esAdmin  true si el usuario tiene rol de administrador
     */
    public void iniciarSesion(String nombre, String rol, boolean bdOk, boolean esAdmin) {
        this.nombreUsuario = nombre;
        this.rolUsuario    = rol;
        this.bdConectada   = bdOk;
        this.esAdmin       = esAdmin;

        actualizarCabecera();
        actualizarFooter();

        // Cargamos el dashboard inicial según el rol
        if (esAdmin) {
            cargarVista(FXML_DASHBOARD_ADMIN, "> INICIO");
        } else {
            cargarVista(FXML_DASHBOARD_USER, "> INICIO");
        }
    }

    // ─────────────────────────────────────────────────────────
    // NAVEGACIÓN — métodos @FXML vinculados a los botones del menú
    // ─────────────────────────────────────────────────────────

    @FXML
    public void mostrarInicio() {
        // El botón "Inicio" del menú vuelve al dashboard del rol del usuario
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

    @FXML
    public void mostrarMisTareas() {
        cargarVista(FXML_MIS_TAREAS, "> MI PERFIL > Tareas Personales");
    }

    @FXML
    public void mostrarUsuarios() {
        cargarVista(FXML_USUARIOS, "> ADMINISTRACIÓN > Usuarios");
    }

    @FXML
    public void mostrarRolesPermisos() {
        cargarVista(FXML_ROLES_SISTEMA, "> ADMINISTRACIÓN > Roles de sistema");
    }

    @FXML
    public void mostrarPermisos() {
        cargarVista(FXML_PERMISOS, "> ADMINISTRACIÓN > Permisos");
    }


    @FXML
    public void mostrarConfiguracion() {
        cargarVista(FXML_CONFIGURACION, "> ADMINISTRACIÓN > Configuración");
    }

    // ─────────────────────────────────────────────────────────
    // ICONOS DE CABECERA
    // ─────────────────────────────────────────────────────────

    /**
     * Abre la ventana "Acerca de" como modal.
     * Vinculado al ImageView img_about via onMouseClicked="#abrirAbout"
     */
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
            // Ancla el modal a la ventana principal
            Stage owner = (Stage) contentArea.getScene().getWindow();
            aboutStage.initOwner(owner);
            aboutStage.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir la ventana 'Acerca de'.\n" + e.getMessage());
        }
    }

    /**
     * Confirma y cierra la aplicación completamente.
     * Vinculado al ImageView img_exit via onMouseClicked="#salirAplicacion"
     */
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
    // MÉTODOS PRIVADOS — lógica interna
    // ─────────────────────────────────────────────────────────

    /**
     * Carga un archivo FXML en el AnchorPane central y actualiza
     * el pasillo de navegación en la cabecera.
     *
     * @param rutaFxml  Ruta del FXML dentro del classpath (empieza con /)
     * @param pasillo   Texto que aparece en lbl_path_in_app (ej: "> PROYECTOS > Tareas")
     */
    private void cargarVista(String rutaFxml, String pasillo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Node vista = loader.load();

            // Ajusta la vista para que ocupe todo el AnchorPane
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            contentArea.getChildren().setAll(vista);
            lbl_path_in_app.setText(pasillo);

        } catch (IOException e) {
            // Si el FXML no existe todavía, mostramos un placeholder en vez de crashear
            mostrarPlaceholder(pasillo, rutaFxml);
            lbl_path_in_app.setText(pasillo);
        }
    }

    /**
     * Muestra un Label provisional cuando el FXML de una sección aún
     * no está implementado. Así el menú funciona sin errores.
     */
    private void mostrarPlaceholder(String titulo, String rutaFxml) {
        Label placeholder = new Label("🚧  Vista en construcción\n\n" + titulo
                + "\n\nFXML: " + rutaFxml);
        placeholder.setStyle("-fx-font-size: 18px; -fx-text-fill: #888; "
                + "-fx-alignment: center; -fx-text-alignment: center;");

        AnchorPane.setTopAnchor(placeholder, 0.0);
        AnchorPane.setBottomAnchor(placeholder, 0.0);
        AnchorPane.setLeftAnchor(placeholder, 0.0);
        AnchorPane.setRightAnchor(placeholder, 0.0);

        contentArea.getChildren().setAll(placeholder);
    }

    /**
     * Rellena los labels de la cabecera con los datos de sesión actuales
     * y actualiza el icono de estado de la BD.
     */
    private void actualizarCabecera() {
        if (lbl_user != null)         lbl_user.setText(nombreUsuario);
        if (lbl_system_group != null) lbl_system_group.setText(rolUsuario);

        // Fecha del día formateada en español
        if (lbl_date != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE dd MMM yyyy", new Locale("es", "ES"));
            lbl_date.setText(LocalDate.now().format(fmt));
        }

        // Icono BD: verde si conectado, rojo si no
        actualizarIconoBD(img_db_status);
    }

    /**
     * Rellena los labels del footer con el estado de BD y la hora de inicio de sesión.
     */
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

    /**
     * Cambia el icono de BD según el estado de conexión.
     * Reutilizable para cabecera y footer.
     */
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

    /**
     * Muestra un diálogo de error simple.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
