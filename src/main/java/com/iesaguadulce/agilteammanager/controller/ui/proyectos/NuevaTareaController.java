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
 * Controller del drawer de tareas. Soporta creación y edición.
 */
public class NuevaTareaController implements DrawerChildController {

    @FXML private Label tituloLabel;
    @FXML private TextField tituloField;
    @FXML private TextArea descripcionField;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private ComboBox<String> prioridadCombo;
    @FXML private TextField horasField;
    @FXML private DatePicker fechaComienzoDate;

    @FXML private Button btnCerrarTop;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private ProyectosController parentController;
    private Proyecto proyectoActual;
    private Sprint sprintActual;
    private Tarea tareaAEditar;
    private TareaService tareaService;

    /** Inicializa combos y valores por defecto. */
    @FXML
    public void initialize() {
        tareaService = SpringContext.getBean(TareaService.class);
        fechaComienzoDate.setValue(LocalDate.now());
        estadoCombo.setItems(FXCollections.observableArrayList(
                List.of("pendiente", "en_progreso", "revision", "completada", "bloqueada")));
        estadoCombo.setValue("pendiente");
        prioridadCombo.setItems(FXCollections.observableArrayList(
                List.of("Baja (0.25)", "Media (0.50)", "Alta (0.75)", "Crítica (1.00)")));
        prioridadCombo.setValue("Media (0.50)");
    }

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

    /**
     * Configura el modo edición. Si tarea es null, modo creación.
     * @param tarea tarea existente o null
     */
    @Override
    public void setTareaAEditar(Tarea tarea) {
        this.tareaAEditar = tarea;
        if (tarea != null) {
            tituloLabel.setText("Editar Tarea");
            btnGuardar.setText("Guardar Cambios");
            tituloField.setText(tarea.getTitulo());
            descripcionField.setText(tarea.getDescripcion());
            estadoCombo.setValue(tarea.getEstado());
            prioridadCombo.setValue(prioridadToString(tarea.getPrioridad()));
            horasField.setText(tarea.getEstimacionHoras() != null
                    ? tarea.getEstimacionHoras().toString() : "");
            if (tarea.getFechaCreacion() != null) {
                fechaComienzoDate.setValue(tarea.getFechaCreacion().toLocalDate());
            }
        }
    }

    /** Cierra el drawer. */
    @FXML
    public void onCerrar() {
        if (parentController != null) parentController.cerrarDrawer();
    }

    /** Guarda o actualiza la tarea según el modo. */
    @FXML
    public void onGuardar() {
        if (!validar()) return;

        LocalDateTime fecha = fechaComienzoDate.getValue() != null
                ? fechaComienzoDate.getValue().atStartOfDay()
                : LocalDateTime.now();

        if (tareaAEditar != null) {
            tareaService.actualizar(tareaAEditar.getId(), tituloField.getText().trim(),
                    descripcionField.getText(), parseHoras(horasField.getText()),
                    parsePrioridad(prioridadCombo.getValue()), estadoCombo.getValue(), fecha);
        } else {
            tareaService.crear(proyectoActual.getId(),
                    sprintActual != null ? sprintActual.getId() : null,
                    tituloField.getText().trim(), descripcionField.getText(),
                    parseHoras(horasField.getText()), parsePrioridad(prioridadCombo.getValue()), fecha);
        }
        parentController.refrescarDatos();
    }

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

    private String prioridadToString(BigDecimal prioridad) { /* ... */ return null; }
    private BigDecimal parsePrioridad(String valor) { /* ... */ return null; }
    private Integer parseHoras(String texto) { /* ... */ return null; }
    private void mostrarError(String mensaje) { /* ... */ }
}