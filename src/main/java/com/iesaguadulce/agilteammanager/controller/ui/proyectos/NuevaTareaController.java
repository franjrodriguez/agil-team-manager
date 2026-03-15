package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller de NuevaTareaDrawer.fxml
 * Modo creación : tareaAEditar == null
 * Modo edición  : tareaAEditar != null  (rellena campos y llama a actualizar)
 */
public class NuevaTareaController implements DrawerChildController {

    // ── Campos del formulario ────────────────────────────────
    @FXML private Label            tituloLabel;
    @FXML private TextField        tituloField;
    @FXML private TextArea         descripcionField;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private ComboBox<String> prioridadCombo;
    @FXML private TextField        horasField;
    @FXML private DatePicker       fechaComienzoDate;

    // ── Botones ──────────────────────────────────────────────
    @FXML private Button btnCerrarTop;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    // ── Contexto ─────────────────────────────────────────────
    private ProyectosController parentController;
    private Proyecto            proyectoActual;
    private Sprint              sprintActual;    // sprint al que se asocia la tarea nueva
    private Tarea               tareaAEditar;    // null → crear, no null → editar
    private TareaService        tareaService;

    // ════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        tareaService = SpringContext.getBean(TareaService.class);

        // Fecha comienzo por defecto = hoy
        fechaComienzoDate.setValue(LocalDate.now());

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

    @Override
    public void setSprintActual(Sprint sprint) {
        this.sprintActual = sprint;
    }

    @Override
    public void setTareaAEditar(Tarea tarea) {
        this.tareaAEditar = tarea;
        if (tarea != null) {
            tituloLabel.setText("Editar Tarea");
            btnGuardar.setText("Guardar Cambios");
            tituloField.setText(tarea.getTitulo() != null ? tarea.getTitulo() : "");
            descripcionField.setText(tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            estadoCombo.setValue(tarea.getEstado() != null ? tarea.getEstado() : "pendiente");
            prioridadCombo.setValue(prioridadToString(tarea.getPrioridad()));
            horasField.setText(tarea.getEstimacionHoras() != null
                    ? tarea.getEstimacionHoras().toString() : "");
            // Cargar fecha de creación
            if (tarea.getFechaCreacion() != null) {
                fechaComienzoDate.setValue(tarea.getFechaCreacion().toLocalDate());
            }
        }
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

        LocalDateTime fechaDt = fechaComienzoDate.getValue() != null
                ? fechaComienzoDate.getValue().atStartOfDay()
                : LocalDateTime.now();

        if (tareaAEditar != null) {
            // ── MODO EDICIÓN ──
            tareaService.actualizar(
                    tareaAEditar.getId(),
                    tituloField.getText().trim(),
                    descripcionField.getText(),
                    parseHoras(horasField.getText()),
                    parsePrioridad(prioridadCombo.getValue()),
                    estadoCombo.getValue(),
                    fechaDt
            );
        } else {
            // ── MODO CREACIÓN ──
            tareaService.crear(
                    proyectoActual.getId(),
                    sprintActual != null ? sprintActual.getId() : null,
                    tituloField.getText().trim(),
                    descripcionField.getText(),
                    parseHoras(horasField.getText()),
                    parsePrioridad(prioridadCombo.getValue()),
                    fechaDt
            );
        }

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
        if (tareaAEditar == null && proyectoActual == null) {
            mostrarError("No hay ningún proyecto seleccionado.");
            return false;
        }
        return true;
    }

    /** BigDecimal → etiqueta del ComboBox */
    private String prioridadToString(BigDecimal prioridad) {
        if (prioridad == null) return "Media (0.50)";
        double p = prioridad.doubleValue();
        if (p >= 0.99) return "Crítica (1.00)";
        if (p >= 0.70) return "Alta (0.75)";
        if (p >= 0.40) return "Media (0.50)";
        return "Baja (0.25)";
    }

    /** "Alta (0.75)" → BigDecimal(0.75) */
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
