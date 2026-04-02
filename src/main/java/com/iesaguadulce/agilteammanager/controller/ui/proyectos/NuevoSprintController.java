package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;
import com.iesaguadulce.agilteammanager.service.proyectos.SprintService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controller del drawer de sprints. Soporta creación y edición.
 */
public class NuevoSprintController implements DrawerChildController {

    @FXML private Label tituloLabel;
    @FXML private TextField objetivoField;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private DatePicker fechaInicioDate;
    @FXML private DatePicker fechaFinDate;

    @FXML private Button btnCerrarTop;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private ProyectosController parentController;
    private Proyecto proyectoActual;
    private Sprint sprintAEditar;
    private SprintService sprintService;

    /** Inicializa combo de estados. */
    @FXML
    public void initialize() {
        sprintService = SpringContext.getBean(SprintService.class);
        estadoCombo.setItems(FXCollections.observableArrayList(
                List.of("planificacion", "activo", "completado", "cancelado")));
        estadoCombo.setValue("planificacion");
    }

    @Override
    public void setParentController(ProyectosController parent) {
        this.parentController = parent;
    }

    @Override
    public void setProyectoActual(Proyecto proyecto) {
        this.proyectoActual = proyecto;
    }

    /**
     * Configura el modo edición. Si sprint es null, modo creación.
     * @param sprint sprint existente o null
     */
    @Override
    public void setSprintAEditar(Sprint sprint) {
        this.sprintAEditar = sprint;
        if (sprint != null) {
            tituloLabel.setText("Editar Sprint");
            btnGuardar.setText("Guardar Cambios");
            objetivoField.setText(sprint.getObjetivo());
            estadoCombo.setValue(sprint.getEstado());
            fechaInicioDate.setValue(sprint.getFechaInicio());
            fechaFinDate.setValue(sprint.getFechaFin());
        }
    }

    /** Cierra el drawer. */
    @FXML
    public void onCerrar() {
        if (parentController != null) parentController.cerrarDrawer();
    }

    /** Guarda o actualiza el sprint según el modo. */
    @FXML
    public void onGuardar() {
        if (!validar()) return;

        if (sprintAEditar != null) {
            sprintService.actualizar(sprintAEditar.getId(), objetivoField.getText().trim(),
                    fechaInicioDate.getValue(), fechaFinDate.getValue(), estadoCombo.getValue());
        } else {
            sprintService.crear(proyectoActual.getId(), objetivoField.getText().trim(),
                    fechaInicioDate.getValue(), fechaFinDate.getValue());
        }
        parentController.refrescarDatos();
    }

    private boolean validar() {
        if (objetivoField.getText() == null || objetivoField.getText().isBlank()) {
            mostrarError("El objetivo del sprint es obligatorio.");
            return false;
        }
        if (sprintAEditar == null && proyectoActual == null) {
            mostrarError("No hay ningún proyecto seleccionado.");
            return false;
        }
        if (fechaInicioDate.getValue() == null || fechaFinDate.getValue() == null) {
            mostrarError("Las fechas de inicio y fin del sprint son obligatorias.");
            return false;
        }
        if (!fechaFinDate.getValue().isAfter(fechaInicioDate.getValue())) {
            mostrarError("La fecha de fin debe ser posterior a la fecha de inicio.");
            return false;
        }
        if (proyectoActual != null) {
            if (proyectoActual.getFechaInicio() != null
                    && fechaInicioDate.getValue().isBefore(proyectoActual.getFechaInicio())) {
                mostrarError("La fecha de inicio del sprint no puede ser anterior\n"
                        + "a la fecha de inicio del proyecto ("
                        + proyectoActual.getFechaInicio() + ").");
                return false;
            }
            if (proyectoActual.getFechaFin() != null
                    && fechaFinDate.getValue().isAfter(proyectoActual.getFechaFin())) {
                mostrarError("La fecha de fin del sprint no puede ser posterior\n"
                        + "a la fecha de fin del proyecto ("
                        + proyectoActual.getFechaFin() + ").");
                return false;
            }
        }
        return true;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}