package com.iesaguadulce.agilteammanager.controller.ui.perfil;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.service.asignaciones.AsignacionService;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * MisTareasController — Vista personal del usuario normal.
 *
 * Funcionalidades:
 *  - Muestra el perfil del usuario logueado (nombre, rol, estado) — solo lectura
 *  - Lista las tareas asignadas al usuario
 *  - Permite buscar/filtrar en la lista de tareas
 *  - Permite cambiar el estado de una tarea seleccionada
 *
 * PATRÓN: Usa SpringContext.getBean() en initialize() porque JavaFX
 * crea el controlador con 'new', sin pasar por el contexto de Spring.
 */
@Component
public class MisTareasController implements Initializable {

    // ─────────────────────────────────────────────────────────
    // SIDEBAR — Perfil del usuario
    // ─────────────────────────────────────────────────────────

    @FXML private ImageView profileAvatar;
    @FXML private Label     profileName;
    @FXML private Label     profileRole;
    @FXML private Label     profileStatus;

    // ─────────────────────────────────────────────────────────
    // SIDEBAR — Lista de tareas
    // ─────────────────────────────────────────────────────────

    @FXML private TextField              searchField;
    @FXML private ListView<Asignacion>   tasksListView;

    // ─────────────────────────────────────────────────────────
    // PANEL DETALLE — Información de la tarea (solo lectura)
    // ─────────────────────────────────────────────────────────

    @FXML private TextField  nombreField;
    @FXML private TextArea   descripcionField;
    @FXML private HBox       estimationLabel;   // contiene un Label interno
    @FXML private DatePicker asignDatePicker;
    @FXML private Label      proyectNameLabel;
    @FXML private Label      sprintTitleLabel;

    // ─────────────────────────────────────────────────────────
    // PANEL ACCIÓN — Único campo editable
    // ─────────────────────────────────────────────────────────

    @FXML private ComboBox<String> statusTaskCombobox;

    // ─────────────────────────────────────────────────────────
    // BOTONES
    // ─────────────────────────────────────────────────────────

    @FXML private Button btnSave;
    @FXML private Button btnDelete;   // "Cancelar" en esta vista

    // ─────────────────────────────────────────────────────────
    // ESTADO INTERNO
    // ─────────────────────────────────────────────────────────

    private AsignacionService asignacionService;
    private TareaService      tareaService;

    private Persona                      personaEnSesion;
    private ObservableList<Asignacion>   todasLasAsignaciones;
    private FilteredList<Asignacion>     asignacionesFiltradas;
    private Asignacion                   asignacionSeleccionada;
    private String                       estadoOriginal;   // para cancelar cambios

    /** Estados posibles de una tarea (según el modelo de la BD) */
    private static final List<String> ESTADOS_TAREA = List.of(
            "pendiente", "en_progreso", "revision", "completada", "bloqueada"
    );

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─────────────────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cargamos los servicios manualmente desde Spring
        asignacionService = SpringContext.getBean(AsignacionService.class);
        tareaService      = SpringContext.getBean(TareaService.class);

        configurarListView();
        configurarStatusComboBox();
        configurarBuscador();
        bloquearCamposDetalle();   // sin tarea seleccionada, todo desactivado
    }

    // ─────────────────────────────────────────────────────────
    // MÉTODO PÚBLICO — lo llama MainController tras cargar la vista
    // ─────────────────────────────────────────────────────────

    /**
     * Recibe la Persona en sesión y carga sus tareas asignadas.
     * MainController llama a este método justo después de cargar el FXML.
     *
     * @param persona Usuario autenticado
     */
    public void setPersona(Persona persona) {
        this.personaEnSesion = persona;

        // Rellenamos el perfil en el sidebar
        profileName.setText(persona.getNombre());
        profileRole.setText(persona.getRol().getNombre());
        profileStatus.setText(persona.getEstado().toUpperCase());

        // Cargamos las tareas asignadas al usuario
        cargarAsignaciones();
    }

    // ─────────────────────────────────────────────────────────
    // CONFIGURACIÓN INICIAL DE CONTROLES
    // ─────────────────────────────────────────────────────────

    /**
     * Configura la celda personalizada del ListView para mostrar
     * la tarea en dos líneas: título + estado con emoji de prioridad.
     */
    private void configurarListView() {
        tasksListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Asignacion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getTarea() == null) {
                    setText(null);
                } else {
                    Tarea t = item.getTarea();
                    String emoji  = resolverEmoji(t);
                    String titulo = t.getTitulo();
                    String estado = t.getEstado();
                    // Dos líneas: emoji + título / estado
                    setText(emoji + " " + titulo + "\n    → " + estado);
                }
            }
        });
    }

    /** Carga los valores posibles en el ComboBox de estado */
    private void configurarStatusComboBox() {
        statusTaskCombobox.setItems(FXCollections.observableArrayList(ESTADOS_TAREA));
    }

    /** Filtra la lista según el texto del buscador */
    private void configurarBuscador() {
        searchField.textProperty().addListener((obs, anterior, nuevo) -> {
            if (asignacionesFiltradas == null) return;

            asignacionesFiltradas.setPredicate(asig -> {
                if (nuevo == null || nuevo.isBlank()) return true;
                String filtro = nuevo.toLowerCase();
                Tarea t = asig.getTarea();
                if (t == null) return false;
                return t.getTitulo().toLowerCase().contains(filtro)
                        || t.getEstado().toLowerCase().contains(filtro);
            });
        });
    }

    /** Desactiva el panel de detalle mientras no hay tarea seleccionada */
    private void bloquearCamposDetalle() {
        nombreField.setDisable(true);
        descripcionField.setDisable(true);
        asignDatePicker.setDisable(true);
        statusTaskCombobox.setDisable(true);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
    }

    /** Activa el panel de detalle con una tarea seleccionada */
    private void desbloquearCamposDetalle() {
        // Los campos de texto siguen siendo solo lectura (editable=false)
        nombreField.setEditable(false);
        descripcionField.setEditable(false);
        // Solo el ComboBox de estado y los botones se activan
        statusTaskCombobox.setDisable(false);
        btnSave.setDisable(false);
        btnDelete.setDisable(false);
    }

    // ─────────────────────────────────────────────────────────
    // CARGA DE DATOS
    // ─────────────────────────────────────────────────────────

    /** Carga desde la BD todas las asignaciones del usuario en sesión */
    private void cargarAsignaciones() {
        if (personaEnSesion == null) return;

        List<Asignacion> lista = asignacionService.obtenerPorPersona(personaEnSesion.getId());

        todasLasAsignaciones  = FXCollections.observableArrayList(lista);
        asignacionesFiltradas = new FilteredList<>(todasLasAsignaciones, a -> true);

        tasksListView.setItems(asignacionesFiltradas);
    }

    // ─────────────────────────────────────────────────────────
    // EVENTOS DE LA LISTA
    // ─────────────────────────────────────────────────────────

    /**
     * Se lanza al hacer clic en un item del ListView.
     * En FXML: onMouseClicked="#seleccionarTarea"
     */
    @FXML
    private void seleccionarTarea() {
        Asignacion seleccionada = tasksListView.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;
        mostrarDetalleTarea(seleccionada);
    }

    /** Rellena el panel de detalle con los datos de la asignación seleccionada */
    private void mostrarDetalleTarea(Asignacion asignacion) {
        this.asignacionSeleccionada = asignacion;
        Tarea tarea = asignacion.getTarea();

        // Campos de solo lectura
        nombreField.setText(tarea.getTitulo());
        descripcionField.setText(tarea.getDescripcion() != null ? tarea.getDescripcion() : "");

        // Etiqueta de estimación (el Label está dentro del HBox estimationLabel)
        if (estimationLabel != null && !estimationLabel.getChildren().isEmpty()) {
            Label etiqueta = (Label) estimationLabel.getChildren().get(0);
            String horas = tarea.getEstimacionHoras() != null
                    ? tarea.getEstimacionHoras() + " h"
                    : "No estimada";
            etiqueta.setText(horas);
        }

        // Fecha de asignación
        if (asignacion.getFechaAsignacion() != null) {
            asignDatePicker.setValue(asignacion.getFechaAsignacion().toLocalDate());
        }

        // Proyecto y Sprint
        if (tarea.getProyecto() != null) {
            proyectNameLabel.setText(tarea.getProyecto().getNombre());
        }
        if (tarea.getSprint() != null) {
            sprintTitleLabel.setText("Sprint " + tarea.getSprint().getNumero());
        } else {
            sprintTitleLabel.setText("Sin sprint");
        }

        // Estado actual en el ComboBox
        this.estadoOriginal = tarea.getEstado();
        statusTaskCombobox.setValue(estadoOriginal);

        desbloquearCamposDetalle();
    }

    // ─────────────────────────────────────────────────────────
    // EVENTOS DE LOS BOTONES
    // ─────────────────────────────────────────────────────────

    /**
     * Guarda el nuevo estado seleccionado en el ComboBox.
     * Solo actualiza el estado de la tarea, nada más.
     */
    @FXML
    private void onGuardarEstado() {
        if (asignacionSeleccionada == null) return;

        String nuevoEstado = statusTaskCombobox.getValue();
        if (nuevoEstado == null || nuevoEstado.equals(estadoOriginal)) return;

        Long tareaId = asignacionSeleccionada.getTarea().getId();
        tareaService.cambiarEstado(tareaId, nuevoEstado);

        // Actualizamos el estado local para reflejar el cambio
        estadoOriginal = nuevoEstado;

        // Refrescamos la lista para que la celda muestre el nuevo estado
        tasksListView.refresh();

        mostrarInfo("Estado actualizado a: " + nuevoEstado);
    }

    /**
     * Descarta el cambio en el ComboBox y vuelve al estado original.
     * El botón se llama "Cancelar" en la UI.
     */
    @FXML
    private void onCancelar() {
        if (estadoOriginal != null) {
            statusTaskCombobox.setValue(estadoOriginal);
        }
    }

    // ─────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────

    /**
     * Devuelve un emoji de prioridad basado en el valor numérico de la tarea.
     * prioridad >= 0.7 → alta, >= 0.4 → media, < 0.4 → baja
     */
    private String resolverEmoji(Tarea tarea) {
        if (tarea.getPrioridad() == null) return "⚪";
        double p = tarea.getPrioridad().doubleValue();
        if (p >= 0.7) return "🔴";
        if (p >= 0.4) return "🟡";
        return "🟢";
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualización");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
