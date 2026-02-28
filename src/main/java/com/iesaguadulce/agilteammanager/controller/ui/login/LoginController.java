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
 * Controlador de la pantalla de Login
 */
public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnCancel;

    private AutenticacionService autenticacionService;

    /**
     * Se ejecuta automáticamente al cargar el FXML
     */
    @FXML
    public void initialize() {
        // Obtener el service desde el contexto de Spring
        autenticacionService = SpringContext.getBean(AutenticacionService.class);

        System.out.println("✅ LoginController inicializado correctamente");
    }

    /**
     * Evento del botón "Iniciar Sesión"
     */
    @FXML
    private void onLoginClick() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, introduce usuario y contraseña");
            return;
        }

        try {
            // Intentar autenticar
            Persona persona = autenticacionService.autenticar(usuario, password);

            if (persona == null) {
                mostrarError("Usuario o contraseña incorrectos");
                return;
            }

            // Login exitoso → Cargar dashboard según rol
            cargarDashboard(persona);

        } catch (Exception e) {
            System.err.println("❌ Error en autenticación: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error de conexión. Verifica la base de datos.");
        }
    }

    /**
     * Carga la vista principal (MainView) e inyecta los datos de sesión.
     *
     * MainView es la estructura completa: cabecera + menú lateral + footer.
     * El dashboard (Admin o Usuario) se carga DENTRO del contentArea de MainView,
     * no como escena raíz — esa era la lógica anterior incorrecta.
     */
    private void cargarDashboard(Persona persona) {
        try {
            boolean esAdmin = autenticacionService.esAdministrador(persona);
            String rolNombre = persona.getRol().getNombre();

            // Cargamos siempre la estructura principal (cabecera + menú + footer)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard/MainView.fxml"));
            Parent root = loader.load();

            // Pasamos los datos de sesión al controlador principal.
            // Él decide qué dashboard cargar en el contentArea central.
            MainController mainController = loader.getController();
            mainController.iniciarSesion(persona.getNombre(), rolNombre, true, esAdmin);

            // Cambiar escena
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
