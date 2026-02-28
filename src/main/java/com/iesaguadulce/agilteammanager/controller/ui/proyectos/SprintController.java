package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.service.proyectos.SprintService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller de NuevoSprintDrawer.fxml
 * Se inyecta dinámicamente dentro del drawerContenido del ProyectosController.
 */
public class SprintController implements DrawerChildController {

    // ── Campos del formulario ────────────────────────────────
    @FXML private TextField  objetivoField;
    @FXML private TextArea   descripcionField;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private DatePicker fechaInicioDate;
    @FXML private DatePicker fechaFinDate;

    // ── Botones ──────────────────────────────────────────────
    @FXML private Button btnCerrarTop;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    // ── Contexto ─────────────────────────────────────────────
    private ProyectosController parentController;
    private Proyecto proyectoActual;
    private SprintService sprintService;

    // ════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        sprintService = SpringContext.getBean(SprintService.class);

        estadoCombo.setItems(FXCollections.observableArrayList(
                List.of("planificacion", "activo", "completado", "cancelado")
        ));
        estadoCombo.setValue("planificacion");
    }

    // ════════════════════════════════════════════════════════
    //  DrawerChildController
    // ════════════════════════════════════════════════════════

    @Override
    public void setParentController(ProyectosController parent) {
        this.parentController = parent;
    }

    @Override
    public void setProyectoActual(Proyecto proyecto) {
        this.proyectoActual = proyecto;
    }

    // ════════════════════════════════════════════════════════
    //  ACCIONES FXML
    // ════════════════════════════════════════════════════════

    @FXML
    public void onCerrar() {
        if (parentController != null) parentController.cerrarDrawer();
    }

    @FXML
    public void onGuardar() {
        if (!validar()) return;

        sprintService.crear(
                proyectoActual.getId(),
                objetivoField.getText().trim(),
                fechaInicioDate.getValue(),
                fechaFinDate.getValue()
        );

        if (parentController != null) parentController.refrescarDatos();
    }

    // ════════════════════════════════════════════════════════
    //  VALIDACIÓN
    // ════════════════════════════════════════════════════════

    private boolean validar() {
        if (objetivoField.getText() == null || objetivoField.getText().isBlank()) {
            mostrarError("El objetivo del sprint es obligatorio.");
            return false;
        }
        if (proyectoActual == null) {
            mostrarError("No hay ningún proyecto seleccionado.");
            return false;
        }
        return true;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}