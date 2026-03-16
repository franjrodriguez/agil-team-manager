package com.iesaguadulce.agilteammanager.controller.ui.seguridad;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.service.personas.PersonaService;
import com.iesaguadulce.agilteammanager.service.seguridad.ConfiguracionService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ConfiguracionController implements Initializable {

    private ConfiguracionService configuracionService;
    private PersonaService personaService;

    // ── Sección A: Seguridad ─────────────────────────────────
    @FXML private Label lblResetEstado;

    // ── Sección B: Motor de Asignación ───────────────────────
    @FXML private Spinner<Integer> spinnerCargaMaxima;
    @FXML private Spinner<Integer> spinnerCompetenciaMinima;
    @FXML private Spinner<Integer> spinnerCandidatosMax;
    @FXML private Label lblMotorEstado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configuracionService = SpringContext.getBean(ConfiguracionService.class);
        personaService       = SpringContext.getBean(PersonaService.class);

        inicializarSpinners();
        cargarParamMotor();
    }

    // ── Inicialización ───────────────────────────────────────

    private void inicializarSpinners() {
        spinnerCargaMaxima.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100, 80, 5));
        spinnerCompetenciaMinima.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1, 1));
        spinnerCandidatosMax.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5, 1));
    }

    private void cargarParamMotor() {
        try {
            spinnerCargaMaxima.getValueFactory()
                    .setValue(configuracionService.obtenerCargaMaxima());
            spinnerCompetenciaMinima.getValueFactory()
                    .setValue(configuracionService.obtenerCompetenciaMinima());
            spinnerCandidatosMax.getValueFactory()
                    .setValue(configuracionService.obtenerCandidatosMaximos());
        } catch (Exception e) {
            System.err.println("Error cargando parámetros del motor: " + e.getMessage());
        }
    }

    // ── Acciones ─────────────────────────────────────────────

    @FXML
    public void onResetearPasswords() {
        try {
            int total = personaService.resetearPasswordsTodas("1234");
            lblResetEstado.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 13px;");
            lblResetEstado.setText("✓ " + total + " contraseñas reseteadas a '1234'");
        } catch (Exception e) {
            lblResetEstado.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 13px;");
            lblResetEstado.setText("✗ Error: " + e.getMessage());
        }
    }

    @FXML
    public void onGuardarMotor() {
        try {
            configuracionService.guardarMotorParams(
                    spinnerCargaMaxima.getValue(),
                    spinnerCompetenciaMinima.getValue(),
                    spinnerCandidatosMax.getValue()
            );
            lblMotorEstado.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 13px;");
            lblMotorEstado.setText("✓ Parámetros guardados correctamente");
        } catch (Exception e) {
            lblMotorEstado.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 13px;");
            lblMotorEstado.setText("✗ Error: " + e.getMessage());
        }
    }
}
