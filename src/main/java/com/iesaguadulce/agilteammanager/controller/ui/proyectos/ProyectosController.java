package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.service.proyectos.ProyectoService;
import com.iesaguadulce.agilteammanager.service.proyectos.SprintService;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;

import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller principal de ProyectosTareasView.fxml
 * Gestiona:
 *   - Lista de proyectos (sidebar izquierdo)
 *   - Detalle del proyecto seleccionado (centro)
 *   - Tablas de Sprints y Tareas
 *   - Apertura/cierre del drawer lateral (NuevoSprint / NuevaTarea)
 */
public class ProyectosController {

    // ── Sidebar ──────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private ComboBox<String> deptFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button btnAddProyecto;
    @FXML private ListView<Proyecto> proyectoListView;

    // ── Cabecera detalle ─────────────────────────────────────
    @FXML private Label profileName;
    @FXML private Label profileRole;
    @FXML private ComboBox<String> statusComboBox;

    // ── Formulario del proyecto ──────────────────────────────
    @FXML private TextField nombreField;
    @FXML private TextArea  descripcionField;
    @FXML private DatePicker fechacomienzoDate;
    @FXML private DatePicker fechafinDate;

    // ── Tabla Sprints ────────────────────────────────────────
    @FXML private TableView<Sprint>      sprintsTable;
    @FXML private TableColumn<Sprint, String>    objetivoColumn;
    @FXML private TableColumn<Sprint, String>    estadoColumn;
    @FXML private TableColumn<Sprint, LocalDate> inicioColumn;
    @FXML private TableColumn<Sprint, LocalDate> finColumn;
    @FXML private TableColumn<Sprint, Void>      accionesColumn;
    @FXML private Button btnAddSprint;

    // ── Tabla Tareas ─────────────────────────────────────────
    @FXML private TableView<Tarea>      tareasTable;
    @FXML private TableColumn<Tarea, String>  tituloTareasColumn;
    @FXML private TableColumn<Tarea, String>  estadoTareasColumn;
    @FXML private TableColumn<Tarea, String>  prioridadTareasColumn;
    @FXML private TableColumn<Tarea, Integer> tiempoTareasColumn;
    @FXML private TableColumn<Tarea, Void>    accionesTareasColumn;
    @FXML private Button btnAddTask;

    // ── Botones acción ───────────────────────────────────────
    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    // ── Drawer (overlay + panel deslizante) ─────────────────
    @FXML private Pane      overlayOscuro;
    @FXML private VBox      drawerPanel;
    @FXML private StackPane drawerContenido;

    // ── Servicios ────────────────────────────────────────────
    private ProyectoService proyectoService;
    private SprintService   sprintService;
    private TareaService    tareaService;

    // ── Estado actual ────────────────────────────────────────
    private Proyecto proyectoSeleccionado;

    // ════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        proyectoService = SpringContext.getBean(ProyectoService.class);
        sprintService   = SpringContext.getBean(SprintService.class);
        tareaService    = SpringContext.getBean(TareaService.class);

        configurarComboEstados();
        configurarColumnasSprints();
        configurarColumnasTareas();
        cargarListaProyectos();
        configurarSeleccionProyecto();
        configurarBuscador();
    }

    // ════════════════════════════════════════════════════════
    //  CONFIGURACIÓN INICIAL
    // ════════════════════════════════════════════════════════

    private void configurarComboEstados() {
        List<String> estados = List.of("planificacion", "activo", "completado", "cancelado");
        statusComboBox.setItems(FXCollections.observableArrayList(estados));
        statusFilter.setItems(FXCollections.observableArrayList(estados));
    }

    private void configurarColumnasSprints() {
        objetivoColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getObjetivo()));
        estadoColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
        inicioColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaInicio()));
        finColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaFin()));
    }

    private void configurarColumnasTareas() {
        tituloTareasColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getTitulo()));
        estadoTareasColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
        prioridadTareasColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getPrioridad() != null ? c.getValue().getPrioridad().toString() : ""));
        tiempoTareasColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getEstimacionHoras()));
    }

    private void configurarSeleccionProyecto() {
        proyectoListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, nuevo) -> {
                    if (nuevo != null) {
                        proyectoSeleccionado = nuevo;
                        mostrarDetalleProyecto(nuevo);
                    }
                }
        );
        // Celda personalizada: muestra el nombre del proyecto en la lista
        proyectoListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Proyecto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private void configurarBuscador() {
        searchField.textProperty().addListener((obs, anterior, nuevo) -> filtrarProyectos(nuevo));
    }

    // ════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ════════════════════════════════════════════════════════

    private void cargarListaProyectos() {
        List<Proyecto> proyectos = proyectoService.obtenerTodos();
        proyectoListView.setItems(FXCollections.observableArrayList(proyectos));
    }

    private void filtrarProyectos(String texto) {
        List<Proyecto> todos = proyectoService.obtenerTodos();
        if (texto == null || texto.isBlank()) {
            proyectoListView.setItems(FXCollections.observableArrayList(todos));
        } else {
            String lower = texto.toLowerCase();
            proyectoListView.setItems(FXCollections.observableArrayList(
                    todos.stream()
                            .filter(p -> p.getNombre().toLowerCase().contains(lower))
                            .toList()
            ));
        }
    }

    private void mostrarDetalleProyecto(Proyecto p) {
        profileName.setText(p.getNombre());
        profileRole.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
        nombreField.setText(p.getNombre());
        descripcionField.setText(p.getDescripcion());
        fechacomienzoDate.setValue(p.getFechaInicio());
        fechafinDate.setValue(p.getFechaFin());
        if (p.getEstado() != null) statusComboBox.setValue(p.getEstado());

        cargarSprints(p);
        cargarTareas(p);
    }

    private void cargarSprints(Proyecto p) {
        List<Sprint> sprints = sprintService.obtenerPorProyecto(p.getId());
        sprintsTable.setItems(FXCollections.observableArrayList(sprints));
    }

    private void cargarTareas(Proyecto p) {
        List<Tarea> tareas = tareaService.obtenerPorProyecto(p.getId());
        tareasTable.setItems(FXCollections.observableArrayList(tareas));
    }

    // ════════════════════════════════════════════════════════
    //  ACCIONES FXML — Proyecto
    // ════════════════════════════════════════════════════════

    @FXML
    private void onGuardarProyecto() {
        if (proyectoSeleccionado == null) return;
        proyectoSeleccionado = proyectoService.actualizar(
                proyectoSeleccionado.getId(),
                nombreField.getText(),
                descripcionField.getText(),
                fechacomienzoDate.getValue(),
                fechafinDate.getValue(),
                statusComboBox.getValue()
        );
        cargarListaProyectos();
    }

    @FXML
    private void onBorrarProyecto() {
        if (proyectoSeleccionado == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el proyecto \"" + proyectoSeleccionado.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                proyectoService.eliminar(proyectoSeleccionado.getId());
                proyectoSeleccionado = null;
                cargarListaProyectos();
            }
        });
    }

    @FXML
    private void onAddProyecto() {
        proyectoService.crear("Nuevo Proyecto", null, null, null);
        cargarListaProyectos();
    }

    // ════════════════════════════════════════════════════════
    //  ACCIONES FXML — Drawer
    // ════════════════════════════════════════════════════════

    @FXML
    public void onNuevoSprint() {
        abrirDrawer("/fxml/views/proyectos/NuevoSprintDrawer.fxml");
    }

    @FXML
    public void onNuevaTarea() {
        abrirDrawer("/fxml/views/proyectos/NuevaTareaDrawer.fxml");
    }

    @FXML
    public void onCerrarDrawer() {
        cerrarDrawer();
    }

    // ════════════════════════════════════════════════════════
    //  ANIMACIÓN DRAWER
    // ════════════════════════════════════════════════════════

    private void abrirDrawer(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node contenido = loader.load();

            // Pasar referencia al controller hijo para que pueda cerrar el drawer
            Object childController = loader.getController();
            if (childController instanceof DrawerChildController hijo) {
                hijo.setParentController(this);
                hijo.setProyectoActual(proyectoSeleccionado);
            }

            drawerContenido.getChildren().setAll(contenido);

            // Overlay: aparece con fade
            overlayOscuro.setMouseTransparent(false);
            FadeTransition fade = new FadeTransition(Duration.millis(200), overlayOscuro);
            fade.setToValue(1.0);
            fade.play();

            // Panel: desliza desde la derecha
            drawerPanel.setMouseTransparent(false);
            TranslateTransition slide = new TranslateTransition(Duration.millis(280), drawerPanel);
            slide.setToX(0);
            slide.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarDrawer() {
        // Panel: desliza hacia la derecha
        TranslateTransition slide = new TranslateTransition(Duration.millis(250), drawerPanel);
        slide.setToX(380);
        slide.setOnFinished(e -> {
            drawerContenido.getChildren().clear();
            drawerPanel.setMouseTransparent(true);
        });
        slide.play();

        // Overlay: desaparece con fade
        FadeTransition fade = new FadeTransition(Duration.millis(200), overlayOscuro);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> overlayOscuro.setMouseTransparent(true));
        fade.play();
    }

    /**
     * Llamado por los controllers hijos tras guardar, para refrescar las tablas.
     */
    public void refrescarDatos() {
        if (proyectoSeleccionado != null) {
            cargarSprints(proyectoSeleccionado);
            cargarTareas(proyectoSeleccionado);
        }
        cerrarDrawer();
    }
}