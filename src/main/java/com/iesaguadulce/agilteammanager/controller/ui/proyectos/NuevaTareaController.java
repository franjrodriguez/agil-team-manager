package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller de NuevaTareaDrawer.fxml
 * Se inyecta dinámicamente dentro del drawerContenido del ProyectosController.
 */
public class NuevaTareaController implements DrawerChildController {

    // ── Campos del formulario ────────────────────────────────
    @FXML private TextField        tituloField;
    @FXML private TextArea         descripcionField;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private ComboBox<String> prioridadCombo;
    @FXML private TextField        horasField;
    @FXML private DatePicker       fechaComienzoDate; // capturado, no enviado al service (Tarea no lo tiene)

    // ── Botones ──────────────────────────────────────────────
    @FXML private Button btnCerrarTop;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    // ── Contexto ─────────────────────────────────────────────
    private ProyectosController parentController;
    private Proyecto            proyectoActual;
    private TareaService        tareaService;

    // ════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        tareaService = SpringContext.getBean(TareaService.class);

        estadoCombo.setItems(FXCollections.observableArrayList(
                List.of("pendiente", "en_progreso", "revision", "completada", "bloqueada")
        ));
        estadoCombo.setValue("pendiente");

        prioridadCombo.setItems(FXCollections.observableArrayList(
                List.of("Baja (0.25)", "Media (0.50)", "Alta (0.75)", "Crítica (1.00)")
        ));
        prioridadCombo.setValue("Media (0.50)");
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

        tareaService.crear(
                proyectoActual.getId(),
                null,                              // sin sprint asignado por defecto
                tituloField.getText().trim(),
                descripcionField.getText(),
                parseHoras(horasField.getText()),
                parsePrioridad(prioridadCombo.getValue())
        );

        if (parentController != null) parentController.refrescarDatos();
    }

    // ════════════════════════════════════════════════════════
    //  VALIDACIÓN Y UTILIDADES
    // ════════════════════════════════════════════════════════

    private boolean validar() {
        if (tituloField.getText() == null || tituloField.getText().isBlank()) {
            mostrarError("El título de la tarea es obligatorio.");
            return false;
        }
        if (proyectoActual == null) {
            mostrarError("No hay ningún proyecto seleccionado.");
            return false;
        }
        return true;
    }

    /** Convierte "Alta (0.75)" → BigDecimal(0.75) */
    private BigDecimal parsePrioridad(String valor) {
        if (valor == null) return BigDecimal.valueOf(0.50);
        try {
            int ini = valor.indexOf('(') + 1;
            int fin = valor.indexOf(')');
            return new BigDecimal(valor.substring(ini, fin));
        } catch (Exception e) {
            return BigDecimal.valueOf(0.50);
        }
    }

    private Integer parseHoras(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
