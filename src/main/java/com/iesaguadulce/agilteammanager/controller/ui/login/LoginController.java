package com.iesaguadulce.agilteammanager.controller.ui.login;

import com.iesaguadulce.agilteammanager.config.SpringContext;
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
     * Carga el dashboard correspondiente según el rol del usuario
     */
    private void cargarDashboard(Persona persona) {
        try {
            String fxmlPath;
            String titulo;

            // Decidir qué dashboard cargar
            if (autenticacionService.esAdministrador(persona)) {
                fxmlPath = "/views/dashboard_admin.fxml";
                titulo = "AgilTeam Manager - Administración";
            } else {
                fxmlPath = "/views/dashboard.fxml";
                titulo = "AgilTeam Manager - Dashboard";
            }

            // Cargar FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Cambiar escena
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(titulo + " - " + persona.getNombre());
            stage.setMaximized(true); // Pantalla completa
            stage.show();

            System.out.println("✅ Dashboard cargado para: " + persona.getNombre());

        } catch (Exception e) {
            System.err.println("❌ Error cargando dashboard: " + e.getMessage());
            e.printStackTrace();
            mostrarError("No se pudo cargar el dashboard. Verifica que el archivo FXML existe.");
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
