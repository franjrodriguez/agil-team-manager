package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.service.proyectos.ProyectoService;
import com.iesaguadulce.agilteammanager.service.proyectos.SprintService;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller principal de ProyectosTareasView.fxml
 * Gestiona:
 *   - Lista de proyectos (sidebar izquierdo)
 *   - Detalle del proyecto seleccionado (centro)
 *   - Tablas de Sprints y Tareas con botón de borrado por fila
 *   - Apertura/cierre del drawer lateral (NuevoSprint / NuevaTarea)
 */
@Component
public class ProyectosController {

    // ── Sidebar ──────────────────────────────────────────────
    @FXML private TextField        searchField;
    @FXML private ComboBox<String> deptFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button           btnAddProyecto;
    @FXML private ListView<Proyecto> proyectoListView;

    // ── Cabecera detalle ─────────────────────────────────────
    @FXML private Label            projectName;
    @FXML private Label            projectDescriptionBreve;
    @FXML private ComboBox<String> statusComboBox;

    // ── Formulario del proyecto ──────────────────────────────
    @FXML private TextField  nombreField;
    @FXML private TextArea   descripcionField;
    @FXML private DatePicker fechacomienzoDate;
    @FXML private DatePicker fechafinDate;

    // ── Tabla Sprints ────────────────────────────────────────
    @FXML private TableView<Sprint>              sprintsTable;
    @FXML private TableColumn<Sprint, String>    objetivoColumn;
    @FXML private TableColumn<Sprint, String>    estadoColumn;
    @FXML private TableColumn<Sprint, LocalDate> inicioColumn;
    @FXML private TableColumn<Sprint, LocalDate> finColumn;
    @FXML private TableColumn<Sprint, Void>      accionesColumn;
    @FXML private Button btnAddSprint;

    // ── Tabla Tareas ─────────────────────────────────────────
    @FXML private TableView<Tarea>              tareasTable;
    @FXML private TableColumn<Tarea, String>    tituloTareasColumn;
    @FXML private TableColumn<Tarea, String>    estadoTareasColumn;
    @FXML private TableColumn<Tarea, String>    prioridadTareasColumn;
    @FXML private TableColumn<Tarea, Integer>   tiempoTareasColumn;
    @FXML private TableColumn<Tarea, Void>      accionesTareasColumn;
    @FXML private Button btnAddTask;

    // ── Botones acción ───────────────────────────────────────
    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    // ── Drawer (overlay + panel deslizante) ─────────────────
    @FXML private Pane      overlayOscuro;
    @FXML private HBox      drawerWrapper;   // HBox contenedor del drawerPanel
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
        configurarFiltroEstado();
    }

    // ════════════════════════════════════════════════════════
    //  CONFIGURACIÓN INICIAL
    // ════════════════════════════════════════════════════════

    private void configurarComboEstados() {
        List<String> estados = List.of("planificacion", "activo", "completado", "cancelado");
        statusComboBox.setItems(FXCollections.observableArrayList(estados));

        List<String> estadosFiltro = new ArrayList<>();
        estadosFiltro.add("Todos");
        estadosFiltro.addAll(estados);
        statusFilter.setItems(FXCollections.observableArrayList(estadosFiltro));
        statusFilter.setValue("Todos");
    }

    private void configurarColumnasSprints() {
        objetivoColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getObjetivo()));
        estadoColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEstado()));
        inicioColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getFechaInicio()));
        finColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getFechaFin()));
        configurarColumnaBorrarSprint();
    }

    private void configurarColumnaBorrarSprint() {
        accionesColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btnBorrar = new Button("Borrar");
            {
                btnBorrar.setStyle("-fx-background-color: -app-error; -fx-text-fill: white;");
                btnBorrar.setOnAction(e -> {
                    Sprint sprint = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Eliminar el sprint \"" + sprint.getObjetivo() + "\"?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            sprintService.eliminar(sprint.getId());
                            if (proyectoSeleccionado != null) cargarSprints(proyectoSeleccionado);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnBorrar);
            }
        });
    }

    private void configurarColumnasTareas() {
        tituloTareasColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitulo()));
        estadoTareasColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEstado()));
        prioridadTareasColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getPrioridad() != null ? c.getValue().getPrioridad().toString() : ""));
        tiempoTareasColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getEstimacionHoras()));
        configurarColumnaBorrarTarea();
    }

    private void configurarColumnaBorrarTarea() {
        accionesTareasColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btnBorrar = new Button("Borrar");
            {
                btnBorrar.setStyle("-fx-background-color: -app-error; -fx-text-fill: white;");
                btnBorrar.setOnAction(e -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Eliminar la tarea \"" + tarea.getTitulo() + "\"?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            tareaService.eliminar(tarea.getId());
                            if (proyectoSeleccionado != null) cargarTareas(proyectoSeleccionado);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnBorrar);
            }
        });
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
        proyectoListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Proyecto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private void configurarBuscador() {
        searchField.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
    }

    private void configurarFiltroEstado() {
        statusFilter.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
    }

    // ════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ════════════════════════════════════════════════════════

    private void cargarListaProyectos() {
        List<Proyecto> proyectos = proyectoService.obtenerTodos();
        proyectoListView.setItems(FXCollections.observableArrayList(proyectos));
    }

    private void aplicarFiltros() {
        List<Proyecto> todos = proyectoService.obtenerTodos();
        String texto = searchField.getText();
        String estadoFiltro = statusFilter.getValue();

        var stream = todos.stream();

        if (texto != null && !texto.isBlank()) {
            String lower = texto.toLowerCase();
            stream = stream.filter(p -> p.getNombre().toLowerCase().contains(lower));
        }
        if (estadoFiltro != null && !estadoFiltro.equals("Todos")) {
            stream = stream.filter(p -> estadoFiltro.equals(p.getEstado()));
        }

        proyectoListView.setItems(FXCollections.observableArrayList(stream.toList()));
    }

    private void mostrarDetalleProyecto(Proyecto p) {
        projectName.setText(p.getNombre());
        projectDescriptionBreve.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
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

        // Validación: fecha fin no puede ser anterior a fecha comienzo
        LocalDate inicio = fechacomienzoDate.getValue();
        LocalDate fin    = fechafinDate.getValue();
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            mostrarError("La fecha de finalización no puede ser anterior a la de comienzo.");
            return;
        }

        try {
            proyectoSeleccionado = proyectoService.actualizar(
                    proyectoSeleccionado.getId(),
                    nombreField.getText(),
                    descripcionField.getText(),
                    inicio,
                    fin,
                    statusComboBox.getValue()
            );
            cargarListaProyectos();
            mostrarInfo("Proyecto \"" + proyectoSeleccionado.getNombre() + "\" guardado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al guardar el proyecto: " + e.getMessage());
        }
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
        Proyecto nuevo = proyectoService.crear("Nuevo Proyecto", null, null, null);
        cargarListaProyectos();
        proyectoListView.getSelectionModel().select(nuevo);
    }

    // ════════════════════════════════════════════════════════
    //  ACCIONES FXML — Drawer
    // ════════════════════════════════════════════════════════

    @FXML
    public void onNuevoSprint() {
        if (proyectoSeleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona primero un proyecto.", ButtonType.OK).showAndWait();
            return;
        }
        abrirDrawer("/views/proyectos/NuevoSprintDrawer.fxml");
    }

    @FXML
    public void onNuevaTarea() {
        if (proyectoSeleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona primero un proyecto.", ButtonType.OK).showAndWait();
            return;
        }
        abrirDrawer("/views/proyectos/NuevaTareaDrawer.fxml");
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

            Object childController = loader.getController();
            if (childController instanceof DrawerChildController hijo) {
                hijo.setParentController(this);
                hijo.setProyectoActual(proyectoSeleccionado);
            }

            drawerContenido.getChildren().setAll(contenido);

            // Activar wrapper y panel ANTES de animar (el HBox padre debe recibir eventos)
            drawerWrapper.setMouseTransparent(false);
            drawerPanel.setMouseTransparent(false);

            overlayOscuro.setMouseTransparent(false);
            FadeTransition fade = new FadeTransition(Duration.millis(200), overlayOscuro);
            fade.setToValue(1.0);
            fade.play();

            TranslateTransition slide = new TranslateTransition(Duration.millis(280), drawerPanel);
            slide.setToX(0);
            slide.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarDrawer() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(250), drawerPanel);
        slide.setToX(380);
        slide.setOnFinished(e -> {
            drawerContenido.getChildren().clear();
            drawerPanel.setMouseTransparent(true);
            drawerWrapper.setMouseTransparent(true);  // devolver transparencia al wrapper
        });
        slide.play();

        FadeTransition fade = new FadeTransition(Duration.millis(200), overlayOscuro);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> overlayOscuro.setMouseTransparent(true));
        fade.play();
    }

    // ════════════════════════════════════════════════════════
    //  UTILIDADES
    // ════════════════════════════════════════════════════════

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error de validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guardado");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /** Llamado por los controllers hijos tras guardar, para refrescar las tablas. */
    public void refrescarDatos() {
        if (proyectoSeleccionado != null) {
            cargarSprints(proyectoSeleccionado);
            cargarTareas(proyectoSeleccionado);
        }
        cerrarDrawer();
    }
}
