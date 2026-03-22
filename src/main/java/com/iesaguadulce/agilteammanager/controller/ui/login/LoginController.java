package com.iesaguadulce.agilteammanager.controller.ui.login;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.controller.ui.dashboard.MainController;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.service.seguridad.AutenticacionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller de la pantalla de autenticación de usuarios.
 *
 * <p>Gestiona el proceso de login, validación de credenciales y transición
 * a la vista principal {@link MainController} según el rol del usuario.</p>
 *
 * @author FRANDEV
 * @see AutenticacionService
 * @see MainController
 */
public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnCancel;

    private AutenticacionService autenticacionService;

    /** Si no es null, estamos en modo re-login (sesión cerrada, MainView ya existe). */
    private MainController mainController;

    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    /**
     * Inicializa el servicio de autenticación desde el contexto Spring. Se ejecuta automáticamente
     */
    @FXML
    public void initialize() {
        // Obtener el service desde el contexto de Spring
        autenticacionService = SpringContext.getBean(AutenticacionService.class);
        System.out.println("(FRANDEV)---> LoginController inicializado correctamente");
    }
    /**
     * Procesa el intento de login tras pulsar el botón.
     * Valida credenciales y carga el dashboard correspondiente.
     */
    @FXML
    private void onLoginClick() {
        System.out.println("(FRANDEV)---> ESTÁN LLAMANDO A LA PUERTA... A VER QUIEN ES");

        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, introduce usuario y contraseña");
            System.out.println("(FRANDEV)---> NO TE CONOZCO CHAVAL...");
            return;
        }

        try {
            // Intentar autenticar... me dan los sudores de la muerte colega!!!
            Persona persona = autenticacionService.autenticar(usuario, password);

            if (persona == null) {
                mostrarError("Usuario o contraseña incorrectos");
                System.out.println("(FRANDEV)---> NO TE CONOZCO CHAVAL SEGUNDA PARTE...");
                return;
            }

            // Login exitoso → Cargar dashboard según rol
            System.out.println("(FRANDEV)---> ME PREPARO PARA EL SALTO AL HIPERESPACIO...");
            cargarDashboard(persona);

        } catch (Exception e) {
            System.err.println("❌ Error en autenticación: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error de conexión. Verifica la base de datos.");
        }
    }

    /**
     * Carga la vista principal {@code MainView.fxml} e inicia la sesión.
     *
     * <p>Utiliza {@code setControllerFactory} para garantizar que {@link MainController}
     * sea gestionado por Spring, evitando instancias duplicadas con @FXML no inyectados.</p>
     *
     * @param persona usuario autenticado
     */
    private void cargarDashboard(Persona persona) {
        try {
            boolean esAdmin = autenticacionService.esAdministrador(persona);

            if (mainController != null) {
                // ── Re-login: MainView ya existe, solo reiniciar sesión ──
                mainController.iniciarSesion(persona, true, esAdmin);
                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
                loginStage.close();
                return;
            }

            // ── Primer login: cargar MainView desde cero ──
            String rolNombre = persona.getRol().getNombre();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard/MainView.fxml"));
            loader.setControllerFactory(SpringContext::getBean);
            Parent root = loader.load();

            MainController mc = loader.getController();
            mc.iniciarSesion(persona, true, esAdmin);

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AgilTeam Manager - " + persona.getNombre());
            stage.setMaximized(true);
            stage.show();

            System.out.println("✅ MainView cargada para: " + persona.getNombre() + " [" + rolNombre + "]");

        } catch (Exception e) {
            System.err.println("❌ Error cargando vista principal: " + e.getMessage());
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista principal. Verifica que MainView.fxml existe.");
        }
    }

    /**
     * Muestra diálogo de error
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Login");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Se presiona el boton de cancelar logueo
     */
    @FXML
    private void onCancelClick() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }
}
