package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetencia;
import com.iesaguadulce.agilteammanager.service.personas.CompetenciaService;
import com.iesaguadulce.agilteammanager.service.proyectos.ProyectoService;
import com.iesaguadulce.agilteammanager.service.proyectos.SprintService;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaCompetenciaService;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.StringConverter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @FXML private TableColumn<Tarea, Integer>   tiempoTareasColumn;   // oculta en FXML, no eliminar
    @FXML private TableColumn<Tarea, Void>      accionesTareasColumn;
    @FXML private Button btnAddTask;

    // ── Tabla Competencias requeridas por la tarea ───────────
    @FXML private TableView<TareaCompetencia>              competenciasTable;
    @FXML private TableColumn<TareaCompetencia, String>    tituloTablaCompetencias;
    @FXML private TableColumn<TareaCompetencia, String>    pesoTablaCompetencias;
    @FXML private TableColumn<TareaCompetencia, Void>      accionesTablaCompetencias;
    @FXML private Button btnAddCompetencia;

    // ── Botones acción ───────────────────────────────────────
    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    // ── Drawer (overlay + panel deslizante) ─────────────────
    @FXML private Pane      overlayOscuro;
    @FXML private HBox      drawerWrapper;   // HBox contenedor del drawerPanel
    @FXML private VBox      drawerPanel;
    @FXML private StackPane drawerContenido;

    // ── Servicios ────────────────────────────────────────────
    private ProyectoService          proyectoService;
    private SprintService            sprintService;
    private TareaService             tareaService;
    private TareaCompetenciaService  tareaCompetenciaService;
    private CompetenciaService       competenciaService;

    // ── Estado actual ────────────────────────────────────────
    private Proyecto proyectoSeleccionado;
    private Sprint   sprintSeleccionado;
    private Tarea    tareaSeleccionada;


    /** Inicializa servicios, tablas y listeners. */
    @FXML
    public void initialize() {
        proyectoService         = SpringContext.getBean(ProyectoService.class);
        sprintService           = SpringContext.getBean(SprintService.class);
        tareaService            = SpringContext.getBean(TareaService.class);
        tareaCompetenciaService = SpringContext.getBean(TareaCompetenciaService.class);
        competenciaService      = SpringContext.getBean(CompetenciaService.class);

        configurarComboEstados();
        configurarColumnasSprints();
        configurarColumnasTareas();
        configurarColumnasCompetencias();
        cargarListaProyectos();
        configurarSeleccionProyecto();
        configurarSeleccionSprint();
        configurarSeleccionTarea();
        configurarBuscador();
        configurarFiltroEstado();

        btnAddProyecto.setOnAction(e -> onNuevoProyecto());

        // Sin sprint seleccionado, los botones de tarea y competencia permanecen desactivados
        btnAddTask.setDisable(true);
        btnAddCompetencia.setDisable(true);
        tareasTable.setPlaceholder(new Label("Selecciona un sprint para ver sus tareas."));
        competenciasTable.setPlaceholder(new Label("Selecciona una tarea para ver sus competencias."));
    }

    //
    //  CONFIGURACIÓN INICIAL
    //

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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        inicioColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getFechaInicio()));
        inicioColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(fmt));
            }
        });
        finColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getFechaFin()));
        finColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(fmt));
            }
        });
        configurarColumnaBorrarSprint();
    }

    private void configurarColumnaBorrarSprint() {
        accionesColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏");
            private final Button btnBorrar = new Button("✕");
            private final HBox   acciones  = new HBox(6, btnEditar, btnBorrar);
            {
                btnEditar.setPrefWidth(28);
                btnEditar.setPrefHeight(26);
                btnEditar.setStyle("-fx-font-size: 13px; -fx-padding: 2 4 2 4;" +
                        "-fx-background-color: -app-info; -fx-text-fill: white;" +
                        "-fx-background-radius: 4; -fx-cursor: hand;");

                btnBorrar.setPrefWidth(28);
                btnBorrar.setPrefHeight(26);
                btnBorrar.setStyle("-fx-font-size: 13px; -fx-padding: 2 4 2 4;" +
                        "-fx-background-color: -app-error; -fx-text-fill: white;" +
                        "-fx-background-radius: 4; -fx-cursor: hand;");

                acciones.setAlignment(javafx.geometry.Pos.CENTER);

                btnEditar.setOnAction(e -> {
                    Sprint sprint = getTableView().getItems().get(getIndex());
                    abrirDrawerEditarSprint(sprint);
                });
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
                setGraphic(empty ? null : acciones);
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
            private final Button btnEditar = new Button("✏");
            private final Button btnBorrar = new Button("✕");
            private final HBox   acciones  = new HBox(6, btnEditar, btnBorrar);
            {
                btnEditar.setPrefWidth(28);
                btnEditar.setPrefHeight(26);
                btnEditar.setStyle("-fx-font-size: 13px; -fx-padding: 2 4 2 4;" +
                        "-fx-background-color: -app-info; -fx-text-fill: white;" +
                        "-fx-background-radius: 4; -fx-cursor: hand;");

                btnBorrar.setPrefWidth(28);
                btnBorrar.setPrefHeight(26);
                btnBorrar.setStyle("-fx-font-size: 13px; -fx-padding: 2 4 2 4;" +
                        "-fx-background-color: -app-error; -fx-text-fill: white;" +
                        "-fx-background-radius: 4; -fx-cursor: hand;");

                acciones.setAlignment(javafx.geometry.Pos.CENTER);

                btnEditar.setOnAction(e -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    abrirDrawerEditarTarea(tarea);
                });
                btnBorrar.setOnAction(e -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Eliminar la tarea \"" + tarea.getTitulo() + "\"?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            tareaService.eliminar(tarea.getId());
                            if (sprintSeleccionado != null) cargarTareasDeSprint(sprintSeleccionado);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : acciones);
            }
        });
    }

    private void configurarColumnasCompetencias() {
        tituloTablaCompetencias.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCompetencia().getNombre()));

        pesoTablaCompetencias.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getPeso() != null
                                ? c.getValue().getPeso().toPlainString()
                                : "—"));

        accionesTablaCompetencias.setCellFactory(col -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.setPrefHeight(26);
                btnQuitar.setStyle("-fx-font-size: 11px; -fx-padding: 4 8 4 8;" +
                        "-fx-background-color: -app-error; -fx-text-fill: white;");
                btnQuitar.setOnAction(e -> {
                    TareaCompetencia tc = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Quitar la competencia \"" + tc.getCompetencia().getNombre() + "\" de esta tarea?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            tareaCompetenciaService.eliminar(
                                    tc.getTarea().getId(),
                                    tc.getCompetencia().getId());
                            if (tareaSeleccionada != null)
                                cargarCompetenciasDeTarea(tareaSeleccionada);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnQuitar);
            }
        });
    }

    private void configurarSeleccionTarea() {
        tareasTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, nuevo) -> {
                    if (nuevo != null) {
                        tareaSeleccionada = nuevo;
                        cargarCompetenciasDeTarea(nuevo);
                        btnAddCompetencia.setDisable(false);
                        competenciasTable.setPlaceholder(
                                new Label("Esta tarea no tiene competencias asignadas."));
                    } else {
                        tareaSeleccionada = null;
                        competenciasTable.setItems(FXCollections.emptyObservableList());
                        btnAddCompetencia.setDisable(true);
                        competenciasTable.setPlaceholder(
                                new Label("Selecciona una tarea para ver sus competencias."));
                    }
                }
        );
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

    private void configurarSeleccionSprint() {
        sprintsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, nuevo) -> {
                    if (nuevo != null) {
                        sprintSeleccionado = nuevo;
                        cargarTareasDeSprint(nuevo);
                        btnAddTask.setDisable(false);
                        tareasTable.setPlaceholder(new Label("Este sprint no tiene tareas todavía."));
                    } else {
                        sprintSeleccionado = null;
                        tareasTable.setItems(FXCollections.emptyObservableList());
                        btnAddTask.setDisable(true);
                        tareasTable.setPlaceholder(new Label("Selecciona un sprint para ver sus tareas."));
                    }
                }
        );
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

        // Resetear sprint, tareas y competencias al cambiar de proyecto
        sprintSeleccionado = null;
        tareaSeleccionada  = null;
        tareasTable.setItems(FXCollections.emptyObservableList());
        competenciasTable.setItems(FXCollections.emptyObservableList());
        btnAddTask.setDisable(true);
        btnAddCompetencia.setDisable(true);
        tareasTable.setPlaceholder(new Label("Selecciona un sprint para ver sus tareas."));
        competenciasTable.setPlaceholder(new Label("Selecciona una tarea para ver sus competencias."));

        cargarSprints(p);
    }

    private void cargarSprints(Proyecto p) {
        List<Sprint> sprints = sprintService.obtenerPorProyecto(p.getId());
        sprintsTable.setItems(FXCollections.observableArrayList(sprints));
    }

    private void cargarTareasDeSprint(Sprint s) {
        List<Tarea> tareas = tareaService.obtenerPorSprint(s.getId());
        tareasTable.setItems(FXCollections.observableArrayList(tareas));
        // Al cambiar sprint, limpiar selección de tarea y tabla competencias
        tareaSeleccionada = null;
        competenciasTable.setItems(FXCollections.emptyObservableList());
        btnAddCompetencia.setDisable(true);
    }

    private void cargarCompetenciasDeTarea(Tarea t) {
        List<TareaCompetencia> lista = tareaCompetenciaService.obtenerPorTarea(t.getId());
        competenciasTable.setItems(FXCollections.observableArrayList(lista));
    }

    // ════════════════════════════════════════════════════════
    //  ACCIONES FXML — Proyecto
    // ════════════════════════════════════════════════════════

    private void onNuevoProyecto() {
        proyectoSeleccionado = null;
        projectName.setText("Nuevo Proyecto");
        projectDescriptionBreve.setText("");
        nombreField.clear();
        descripcionField.clear();
        fechacomienzoDate.setValue(null);
        fechafinDate.setValue(null);
        statusComboBox.setValue("planificacion");
        sprintsTable.setItems(FXCollections.emptyObservableList());
        tareasTable.setItems(FXCollections.emptyObservableList());
        btnAddTask.setDisable(true);
        nombreField.requestFocus();
    }

    @FXML
    private void onGuardarProyecto() {
        String nombre = nombreField.getText() != null ? nombreField.getText().trim() : "";
        if (nombre.isBlank()) {
            mostrarError("El nombre del proyecto es obligatorio.");
            return;
        }

        // Validación: fecha fin no puede ser anterior a fecha comienzo
        LocalDate inicio = fechacomienzoDate.getValue();
        LocalDate fin    = fechafinDate.getValue();
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            mostrarError("La fecha de finalización no puede ser anterior a la de comienzo.");
            return;
        }

        try {
            if (proyectoSeleccionado == null) {
                // ── MODO CREACIÓN ──
                proyectoSeleccionado = proyectoService.crear(
                        nombre,
                        descripcionField.getText(),
                        inicio,
                        fin
                );
            } else {
                // ── MODO EDICIÓN ──
                proyectoSeleccionado = proyectoService.actualizar(
                        proyectoSeleccionado.getId(),
                        nombre,
                        descripcionField.getText(),
                        inicio,
                        fin,
                        statusComboBox.getValue()
                );
            }

            // Capturamos id y nombre ANTES de recargar la lista,
            // porque setItems() puede disparar el listener y sobreescribir proyectoSeleccionado.
            final Long   idGuardado     = proyectoSeleccionado.getId();
            final String nombreGuardado = proyectoSeleccionado.getNombre();

            cargarListaProyectos();

            // Re-seleccionamos el proyecto que acabamos de guardar
            proyectoListView.getItems().stream()
                    .filter(p -> p.getId().equals(idGuardado))
                    .findFirst()
                    .ifPresent(p -> {
                        proyectoListView.getSelectionModel().select(p);
                        proyectoListView.scrollTo(p);
                    });

            mostrarInfo("Proyecto \"" + nombreGuardado + "\" guardado correctamente.");
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
        if (sprintSeleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona primero un sprint para asociar la tarea.", ButtonType.OK).showAndWait();
            return;
        }
        abrirDrawer("/views/proyectos/NuevaTareaDrawer.fxml");
    }

    @FXML
    public void onNuevaCompetencia() {
        if (tareaSeleccionada == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecciona primero una tarea.", ButtonType.OK).showAndWait();
            return;
        }

        // ── ComboBox con el catálogo de competencias ──
        List<Competencia> catalogo = competenciaService.obtenerTodas();
        ComboBox<Competencia> cbCompetencia = new ComboBox<>(
                FXCollections.observableArrayList(catalogo));
        cbCompetencia.setPromptText("Selecciona una competencia");
        cbCompetencia.setPrefWidth(260);
        cbCompetencia.setConverter(new StringConverter<>() {
            @Override public String toString(Competencia c) {
                return c == null ? "" : c.getNombre() + "  [" + c.getTipo() + "]";
            }
            @Override public Competencia fromString(String s) { return null; }
        });

        // ── TextField para el peso ──
        TextField tfPeso = new TextField();
        tfPeso.setPromptText("Peso (0.00 – 1.00)");
        tfPeso.setPrefWidth(120);

        // ── Layout del diálogo ──
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));
        grid.add(new Label("Competencia:"), 0, 0);
        grid.add(cbCompetencia,            1, 0);
        grid.add(new Label("Peso:"),       0, 1);
        grid.add(tfPeso,                   1, 1);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Añadir competencia requerida");
        dialog.setHeaderText("Tarea: " + tareaSeleccionada.getTitulo());
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;

            Competencia competenciaElegida = cbCompetencia.getValue();
            if (competenciaElegida == null) {
                mostrarError("Debes seleccionar una competencia.");
                return;
            }

            String pesoTexto = tfPeso.getText() != null ? tfPeso.getText().trim() : "";
            if (pesoTexto.isBlank()) {
                mostrarError("El peso no puede estar vacío.");
                return;
            }

            try {
                BigDecimal peso = new BigDecimal(pesoTexto.replace(",", "."));
                tareaCompetenciaService.añadir(
                        tareaSeleccionada.getId(),
                        competenciaElegida.getId(),
                        peso);
                cargarCompetenciasDeTarea(tareaSeleccionada);
            } catch (NumberFormatException ex) {
                mostrarError("El peso debe ser un número decimal (ej: 0.75).");
            } catch (RuntimeException ex) {
                mostrarError(ex.getMessage());
            }
        });
    }

    @FXML
    public void onCerrarDrawer() {
        cerrarDrawer();
    }

    // ════════════════════════════════════════════════════════
    //  ANIMACIÓN DRAWER
    // ════════════════════════════════════════════════════════

    /** Abre el drawer de Sprint en modo edición con el sprint indicado. */
    private void abrirDrawerEditarSprint(Sprint sprint) {
        abrirDrawer("/views/proyectos/NuevoSprintDrawer.fxml", null, sprint, null);
    }

    /** Abre el drawer de Tarea en modo edición con la tarea indicada. */
    private void abrirDrawerEditarTarea(Tarea tarea) {
        abrirDrawer("/views/proyectos/NuevaTareaDrawer.fxml", null, null, tarea);
    }

    private void abrirDrawer(String fxmlPath) {
        abrirDrawer(fxmlPath, sprintSeleccionado, null, null);
    }

    private void abrirDrawer(String fxmlPath, Sprint sprintCtx, Sprint sprintEditar, Tarea tareaEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node contenido = loader.load();

            Object childController = loader.getController();
            if (childController instanceof DrawerChildController hijo) {
                hijo.setParentController(this);
                hijo.setProyectoActual(proyectoSeleccionado);
                hijo.setSprintActual(sprintCtx);
                hijo.setSprintAEditar(sprintEditar);
                hijo.setTareaAEditar(tareaEditar);
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
        }
        if (sprintSeleccionado != null) {
            cargarTareasDeSprint(sprintSeleccionado);
        }
        cerrarDrawer();
    }
}
