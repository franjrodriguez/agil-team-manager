package com.iesaguadulce.agilteammanager.controller.ui.perfil;

import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetencia;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.service.personas.PersonaService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador JavaFX para la vista PersonasView.fxml
 *
 * Gestiona:
 * - Panel lateral: búsqueda, filtros y lista de profesionales
 * - Panel central: detalle, edición, tareas y competencias de la persona seleccionada
 */
@Component
public class MisTareasController {

    // ===== SERVICIOS =====
    @Autowired
    private PersonaService personaService;

    // ===== CONTENEDORES RAÍZ =====
    @FXML private BorderPane rootPane;

    // ===== SIDEBAR: búsqueda, filtros y lista =====
    @FXML private VBox sidebarPanel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> deptFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button btnAddProfessional;
    @FXML private ListView<Persona> professionalsListView;

    // ===== DETALLE: cabecera =====
    @FXML private ImageView profileAvatar;
    @FXML private Label profileName;
    @FXML private Label profileRole;

    // ===== DETALLE: estado (cabecera) =====
    @FXML private ComboBox<String> statusComboBox;

    // ===== DETALLE: datos personales =====
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private DatePicker joinDatePicker;
    @FXML private ComboBox<String> systemRoleComboBox;

    // ===== DETALLE: tabla de tareas (vinculadas via Asignacion) =====
    @FXML private TableView<Asignacion> tasksTable;
    @FXML private TableColumn<Asignacion, Image> prioColumn;
    @FXML private TableColumn<Asignacion, String> tareaColumn;
    @FXML private TableColumn<Asignacion, String> fechaColumn;
    @FXML private TableColumn<Asignacion, String> horaColumn;
    @FXML private TableColumn<Asignacion, String> estadoColumn;
    @FXML private TableColumn<Asignacion, Boolean> completadoColumn;

    // ===== DETALLE: tabla de competencias =====
    @FXML private TableView<PersonaCompetencia> competenciesTable;
    @FXML private TableColumn<PersonaCompetencia, String> tipoColumn;
    @FXML private TableColumn<PersonaCompetencia, String> nombreColumn;
    @FXML private TableColumn<PersonaCompetencia, String> descripcionColumn;

    // ===== DETALLE: botones de acción =====
    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    // ===== ESTADO INTERNO =====
    private final ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();
    private FilteredList<Persona> listaFiltrada;
    private Persona personaSeleccionada;

    /** Caché de imágenes de prioridad para no recargarlas en cada celda */
    private final Map<String, Image> cachePrioridad = new HashMap<>();

    /**
     * Guarda el estado "completada" por cada Asignacion que el usuario marque/desmarque.
     * Clave = Asignacion.id, Valor = true si el usuario marcó "completada"
     */
    private final Map<Long, Boolean> cambiosEstadoTareas = new HashMap<>();

    /** Formateador para fechas en la tabla de tareas */
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // =========================================================================
    //  INICIALIZACIÓN
    // =========================================================================

    @FXML
    public void initialize() {
        precargarImagenesPrioridad();
        configurarFiltros();
        configurarListView();
        configurarTablaTareas();
        configurarTablaCompetencias();
        configurarBotones();
        cargarPersonas();
    }

    // =========================================================================
    //  CONFIGURACIÓN DE COMPONENTES
    // =========================================================================

    /**
     * Pre-carga las 3 imágenes de prioridad en un mapa para reutilizarlas.
     */
    private void precargarImagenesPrioridad() {
        cachePrioridad.put("alta",  new Image(getClass().getResourceAsStream("/icons/prioridad-alta.png")));
        cachePrioridad.put("media", new Image(getClass().getResourceAsStream("/icons/prioridad-media.png")));
        cachePrioridad.put("baja",  new Image(getClass().getResourceAsStream("/icons/prioridad-baja.png")));
    }

    /**
     * Rellena los ComboBox de filtros y el de sexo / rol del formulario.
     */
    private void configurarFiltros() {
        // Filtro de estado
        statusFilter.setItems(FXCollections.observableArrayList("Todos", "activo", "inactivo", "baja"));
        statusFilter.setValue("Todos");
        statusFilter.setOnAction(e -> aplicarFiltros());

        // Filtro de departamento (se llenará con los puestos reales)
        deptFilter.setOnAction(e -> aplicarFiltros());

        // Filtro de rol
        roleFilter.setOnAction(e -> aplicarFiltros());

        // ComboBox de estado de persona (cabecera del detalle)
        statusComboBox.setItems(FXCollections.observableArrayList("activo", "inactivo", "baja"));
        statusComboBox.setOnAction(e -> aplicarEstiloEstado(statusComboBox.getValue()));

        // ComboBox de sexo en el formulario
        sexComboBox.setItems(FXCollections.observableArrayList("Hombre", "Mujer", "Otro"));

        // Búsqueda por texto
        searchField.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    /**
     * Configura el ListView con CellFactory y listener de selección.
     */
    private void configurarListView() {
        professionalsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Persona persona, boolean empty) {
                super.updateItem(persona, empty);
                if (empty || persona == null) {
                    setText(null);
                } else {
                    String puesto = persona.getPuesto() != null
                            ? persona.getPuesto().getNombre()
                            : "Sin puesto";
                    setText(persona.getNombre() + "  –  " + puesto);
                }
            }
        });

        professionalsListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        mostrarDetalle(newVal);
                    }
                });
    }

    /**
     * Configura las 6 columnas de la tabla de tareas asignadas.
     *
     * - PRIORIDAD: ImageView según valor numérico (>0.7 alta, >0.3 media, resto baja)
     * - TAREA: título de la tarea
     * - FECHA: fecha de creación formateada
     * - HORA: hora de creación formateada
     * - ESTADO: texto del estado actual
     * - COMPLETADO: CheckBox editable → único campo modificable
     */
    private void configurarTablaTareas() {
        // Permitir edición solo en la columna de completado
        tasksTable.setEditable(true);

        // --- PRIORIDAD (imagen) ---
        prioColumn.setCellValueFactory(cell -> null); // no usamos value, solo el graphic
        prioColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imgView = new ImageView();
            {
                imgView.setFitWidth(20);
                imgView.setFitHeight(20);
                imgView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Asignacion asignacion = getTableRow().getItem();
                Tarea tarea = asignacion.getTarea();
                if (tarea != null && tarea.getPrioridad() != null) {
                    String nivel = clasificarPrioridad(tarea.getPrioridad());
                    imgView.setImage(cachePrioridad.get(nivel));
                }
                setGraphic(imgView);
                setAlignment(Pos.CENTER);
            }
        });

        // --- TAREA (título) ---
        tareaColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            return new SimpleStringProperty(tarea != null ? tarea.getTitulo() : "");
        });

        // --- FECHA (dd/MM/yyyy) ---
        fechaColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            if (tarea != null && tarea.getFechaCreacion() != null) {
                return new SimpleStringProperty(tarea.getFechaCreacion().format(FMT_FECHA));
            }
            return new SimpleStringProperty("");
        });

        // --- HORA (HH:mm) ---
        horaColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            if (tarea != null && tarea.getFechaCreacion() != null) {
                return new SimpleStringProperty(tarea.getFechaCreacion().format(FMT_HORA));
            }
            return new SimpleStringProperty("");
        });

        // --- ESTADO (texto) ---
        estadoColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            return new SimpleStringProperty(tarea != null ? tarea.getEstado() : "");
        });

        // --- COMPLETADO (CheckBox editable) ---
        completadoColumn.setCellValueFactory(cell -> {
            Asignacion asig = cell.getValue();
            Tarea tarea = asig.getTarea();
            boolean completada = tarea != null && "completada".equalsIgnoreCase(tarea.getEstado());

            SimpleBooleanProperty prop = new SimpleBooleanProperty(completada);

            // Listener: al cambiar el check, registramos el cambio pendiente
            prop.addListener((obs, oldVal, newVal) -> {
                cambiosEstadoTareas.put(asig.getId(), newVal);
            });

            return prop;
        });
        completadoColumn.setCellFactory(CheckBoxTableCell.forTableColumn(completadoColumn));
    }

    /**
     * Configura las columnas de la tabla de competencias.
     */
    private void configurarTablaCompetencias() {
        tipoColumn.setCellValueFactory(cell -> {
            Competencia comp = cell.getValue().getCompetencia();
            return new SimpleStringProperty(comp != null ? comp.getTipo() : "");
        });

        nombreColumn.setCellValueFactory(cell -> {
            Competencia comp = cell.getValue().getCompetencia();
            return new SimpleStringProperty(comp != null ? comp.getNombre() : "");
        });

        descripcionColumn.setCellValueFactory(cell -> {
            Competencia comp = cell.getValue().getCompetencia();
            return new SimpleStringProperty(comp != null ? comp.getDescripcion() : "");
        });
    }

    /**
     * Asocia acciones a los botones principales.
     */
    private void configurarBotones() {
        btnAddProfessional.setOnAction(e -> onNuevoProfesional());
        btnSave.setOnAction(e -> onGuardarCambios());
        btnDelete.setOnAction(e -> onEliminarProfesional());
    }

    // =========================================================================
    //  CARGA DE DATOS
    // =========================================================================

    private void cargarPersonas() {
        try {
            List<Persona> personas = personaService.obtenerTodas();
            listaPersonas.setAll(personas);

            listaFiltrada = new FilteredList<>(listaPersonas, p -> true);
            professionalsListView.setItems(listaFiltrada);

            if (!listaFiltrada.isEmpty()) {
                professionalsListView.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar personas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  FILTRADO
    // =========================================================================

    private void aplicarFiltros() {
        if (listaFiltrada == null) return;

        String textoSearch = searchField.getText() != null
                ? searchField.getText().toLowerCase().trim()
                : "";
        String estadoSel = statusFilter.getValue();
        String deptSel = deptFilter.getValue();
        String rolSel = roleFilter.getValue();

        listaFiltrada.setPredicate(persona -> {
            boolean matchTexto = textoSearch.isEmpty()
                    || persona.getNombre().toLowerCase().contains(textoSearch)
                    || (persona.getEmail() != null && persona.getEmail().toLowerCase().contains(textoSearch))
                    || persona.getUsuario().toLowerCase().contains(textoSearch);

            boolean matchEstado = estadoSel == null || "Todos".equals(estadoSel)
                    || estadoSel.equalsIgnoreCase(persona.getEstado());

            boolean matchDept = deptSel == null || "Todos".equals(deptSel)
                    || (persona.getPuesto() != null
                    && persona.getPuesto().getNombre().equalsIgnoreCase(deptSel));

            boolean matchRol = rolSel == null || "Todos".equals(rolSel)
                    || (persona.getRol() != null
                    && persona.getRol().getNombre().equalsIgnoreCase(rolSel));

            return matchTexto && matchEstado && matchDept && matchRol;
        });
    }

    // =========================================================================
    //  DETALLE DE PERSONA
    // =========================================================================

    private void mostrarDetalle(Persona persona) {
        this.personaSeleccionada = persona;

        // Limpiar cambios pendientes de la persona anterior
        cambiosEstadoTareas.clear();

        // Cabecera
        profileName.setText(persona.getNombre());
        profileRole.setText(persona.getPuesto() != null
                ? persona.getPuesto().getNombre()
                : "Sin puesto");

        // Estado de la persona
        statusComboBox.setValue(persona.getEstado());
        aplicarEstiloEstado(persona.getEstado());

        cargarAvatar(persona.getFotoPath());

        // Datos personales
        emailField.setText(persona.getEmail());
        usernameField.setText(persona.getUsuario());

        if (persona.getSexo() != null) {
            switch (persona.getSexo()) {
                case 'M' -> sexComboBox.setValue("Hombre");
                case 'F' -> sexComboBox.setValue("Mujer");
                default  -> sexComboBox.setValue("Otro");
            }
        }

        joinDatePicker.setValue(persona.getFechaAlta());

        if (persona.getRol() != null) {
            systemRoleComboBox.setValue(persona.getRol().getNombre());
        }

        // Tareas asignadas
        cargarTareas(persona);

        // Competencias
        cargarCompetencias(persona);
    }

    private void cargarAvatar(String fotoPath) {
        try {
            if (fotoPath != null && !fotoPath.isBlank()) {
                profileAvatar.setImage(new Image(fotoPath));
            } else {
                profileAvatar.setImage(
                        new Image(getClass().getResourceAsStream("/icons/professional.png"))
                );
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el avatar: " + e.getMessage());
        }
    }

    /**
     * Carga las asignaciones (tareas) de la persona en la tabla.
     */
    private void cargarTareas(Persona persona) {
        if (persona.getAsignaciones() != null) {
            ObservableList<Asignacion> asignaciones =
                    FXCollections.observableArrayList(persona.getAsignaciones());
            tasksTable.setItems(asignaciones);
        } else {
            tasksTable.setItems(FXCollections.emptyObservableList());
        }
    }

    private void cargarCompetencias(Persona persona) {
        if (persona.getPersonasCompetencias() != null) {
            ObservableList<PersonaCompetencia> competencias =
                    FXCollections.observableArrayList(persona.getPersonasCompetencias());
            competenciesTable.setItems(competencias);
        } else {
            competenciesTable.setItems(FXCollections.emptyObservableList());
        }
    }

    // =========================================================================
    //  UTILIDADES
    // =========================================================================

    /**
     * Cambia el color de fondo del statusComboBox según el estado seleccionado.
     *   activo   → verde  (-app-success)
     *   inactivo → amarillo (-app-warning)
     *   baja     → rojo   (-app-error)
     */
    private void aplicarEstiloEstado(String estado) {
        if (estado == null) return;
        String color = switch (estado) {
            case "activo"   -> "-app-success";
            case "inactivo" -> "-app-warning";
            case "baja"     -> "-app-error";
            default         -> "-app-bg-card";
        };
        statusComboBox.setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Clasifica la prioridad numérica (BigDecimal 0-1) en alta/media/baja.
     *   > 0.7 → alta
     *   > 0.3 → media
     *   resto → baja
     */
    private String clasificarPrioridad(BigDecimal prioridad) {
        if (prioridad.compareTo(BigDecimal.valueOf(0.7)) > 0) return "alta";
        if (prioridad.compareTo(BigDecimal.valueOf(0.3)) > 0) return "media";
        return "baja";
    }

    // =========================================================================
    //  ACCIONES (BOTONES)
    // =========================================================================

    private void onNuevoProfesional() {
        personaSeleccionada = null;
        cambiosEstadoTareas.clear();
        limpiarFormulario();
    }

    /**
     * Guarda los datos del formulario (personalDataGrid) y los estados
     * de tareas que el usuario haya cambiado via CheckBox.
     */
    private void onGuardarCambios() {
        if (personaSeleccionada == null) {
            // TODO: Crear nueva persona
            System.out.println("Crear nueva persona - pendiente de implementar");
            return;
        }

        // 1) Actualizar datos personales desde el formulario
        personaSeleccionada.setEmail(emailField.getText());
        personaSeleccionada.setUsuario(usernameField.getText());
        personaSeleccionada.setFechaAlta(joinDatePicker.getValue());
        personaSeleccionada.setEstado(statusComboBox.getValue());

        // Convertir sexo: "Hombre"→M, "Mujer"→F, "Otro"→O
        String sexoTexto = sexComboBox.getValue();
        if (sexoTexto != null) {
            switch (sexoTexto) {
                case "Hombre" -> personaSeleccionada.setSexo('M');
                case "Mujer"  -> personaSeleccionada.setSexo('F');
                default       -> personaSeleccionada.setSexo('O');
            }
        }

        // TODO: Actualizar rol de sistema según systemRoleComboBox

        // 2) Aplicar cambios de estado en las tareas marcadas/desmarcadas
        if (!cambiosEstadoTareas.isEmpty()) {
            for (Asignacion asig : tasksTable.getItems()) {
                Boolean nuevoEstado = cambiosEstadoTareas.get(asig.getId());
                if (nuevoEstado != null && asig.getTarea() != null) {
                    asig.getTarea().setEstado(nuevoEstado ? "completada" : "pendiente");
                }
            }
            // TODO: Persistir cambios de tareas con TareaService
        }

        // TODO: Llamar a personaService para persistir los cambios
        System.out.println("Guardar cambios - pendiente de persistir");

        cambiosEstadoTareas.clear();
    }

    private void onEliminarProfesional() {
        if (personaSeleccionada == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar profesional");
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar a "
                + personaSeleccionada.getNombre() + "?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                // TODO: Llamar a personaService para eliminar
                System.out.println("Eliminar persona - pendiente de implementar");
            }
        });
    }

    private void limpiarFormulario() {
        profileName.setText("");
        profileRole.setText("");
        profileAvatar.setImage(null);
        statusComboBox.setValue(null);
        statusComboBox.setStyle("");
        emailField.clear();
        usernameField.clear();
        sexComboBox.setValue(null);
        joinDatePicker.setValue(null);
        systemRoleComboBox.setValue(null);
        tasksTable.setItems(FXCollections.emptyObservableList());
        competenciesTable.setItems(FXCollections.emptyObservableList());
    }

    // =========================================================================
    //  MÉTODO PÚBLICO PARA REFRESCAR DESDE FUERA
    // =========================================================================

    public void refrescarDatos() {
        cargarPersonas();
    }
}