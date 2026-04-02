package com.iesaguadulce.agilteammanager.controller.ui.personas;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.util.FotoUtil;
import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetencia;
import com.iesaguadulce.agilteammanager.model.personas.Puesto;
import com.iesaguadulce.agilteammanager.service.personas.PuestoService;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.service.personas.CompetenciaService;
import com.iesaguadulce.agilteammanager.service.personas.PersonaCompetenciaService;
import com.iesaguadulce.agilteammanager.service.personas.PersonaService;
import com.iesaguadulce.agilteammanager.service.seguridad.RolSistemaService;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import javafx.stage.FileChooser;
import org.mindrot.jbcrypt.BCrypt;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller principal de la gestión de profesionales/personas.
 *
 * <p>Gestiona el ciclo completo de profesionales: datos personales, asignaciones a tareas,
 * competencias técnicas y configuración de roles. Implementa patrón de lista maestra-detalle
 * con filtros en tiempo real.</p>
 *
 * @author FRANDEV
 * @see PersonaService
 * @see PersonaCompetenciaService
 */
@Component
public class PersonasController implements Initializable {

    // ─────────────────────────────────────────────────────────
    // SERVICIOS
    // ─────────────────────────────────────────────────────────

    private PersonaService personaService;
    private RolSistemaService rolSistemaService;
    private PuestoService puestoService;
    private CompetenciaService competenciaService;
    private PersonaCompetenciaService personaCompetenciaService;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Contenedor raíz
    // ─────────────────────────────────────────────────────────

    @FXML private BorderPane rootPane;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Sidebar (búsqueda, filtros, lista)
    // ─────────────────────────────────────────────────────────

    @FXML private VBox sidebarPanel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button btnAddProfessional;
    @FXML private ListView<Persona> professionalsListView;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Cabecera del detalle
    // ─────────────────────────────────────────────────────────

    @FXML private ImageView profileAvatar;
    @FXML private Button btnCambiarFoto;
    @FXML private Label profileName;
    @FXML private Label profileRole;
    @FXML private ComboBox<String> statusComboBox;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Formulario de datos personales
    // ─────────────────────────────────────────────────────────

    @FXML private TextField nombreField;
    @FXML private ComboBox<Puesto> puestoComboBox;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private DatePicker joinDatePicker;
    @FXML private ComboBox<String> systemRoleComboBox;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Tabla de tareas asignadas
    // ─────────────────────────────────────────────────────────

    @FXML private TableView<Asignacion> tasksTable;
    @FXML private TableColumn<Asignacion, Image> prioColumn;
    @FXML private TableColumn<Asignacion, String> tareaColumn;
    @FXML private TableColumn<Asignacion, String> fechaColumn;
    @FXML private TableColumn<Asignacion, String> horaColumn;
    @FXML private TableColumn<Asignacion, String> estadoColumn;
    @FXML private TableColumn<Asignacion, Boolean> completadoColumn;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Tabla de competencias técnicas
    // ─────────────────────────────────────────────────────────

    @FXML private TableView<PersonaCompetencia> competenciesTable;
    @FXML private TableColumn<PersonaCompetencia, String> tipoColumn;
    @FXML private TableColumn<PersonaCompetencia, String> nombreColumn;
    @FXML private TableColumn<PersonaCompetencia, String> descripcionColumn;
    @FXML private TableColumn<PersonaCompetencia, Integer> nivelColumn;
    @FXML private TableColumn<PersonaCompetencia, Void> accionesCompetenciaColumn;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Botones de acción
    // ─────────────────────────────────────────────────────────

    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    /** Lista base observable que alimenta el ListView */
    private final ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();

    /** Lista filtrada: envuelve a listaPersonas y aplica predicados de búsqueda */
    private FilteredList<Persona> listaFiltrada;

    /** Persona actualmente seleccionada en el sidebar. null = modo "nueva" */
    private Persona personaSeleccionada;

    /** true cuando el usuario está en modo "añadir nuevo" y aún no ha guardado */
    private boolean formularioNuevoSinGuardar = false;

    /** Caché de imágenes de prioridad para no recargarlas en cada celda */
    private final Map<String, Image> cachePrioridad = new HashMap<>();

    /**
     * Registra los cambios de estado de tareas que el usuario marca/desmarca
     * con el CheckBox antes de pulsar "Guardar".
     * Clave = Asignacion.id, Valor = true si se marcó como completada
     */
    private final Map<Long, Boolean> cambiosEstadoTareas = new HashMap<>();

    /** Formatos de fecha y hora para la tabla de tareas */
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA  = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Método de inicialización llamado por JavaFX tras cargar el FXML.
     *
     * IMPORTANTE: obtenemos PersonaService desde SpringContext porque JavaFX
     * instancia este controlador con new (sin Spring), por lo que @Autowired
     * no funciona aquí. Este es el mismo patrón que usa CompetenciasController.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("(FRANDEV) --> Iniciando PersonasController...");

        // Obtener los services desde el contexto Spring (patrón manual)
        this.personaService = SpringContext.getBean(PersonaService.class);
        this.rolSistemaService = SpringContext.getBean(RolSistemaService.class);
        this.puestoService = SpringContext.getBean(PuestoService.class);
        this.competenciaService = SpringContext.getBean(CompetenciaService.class);
        this.personaCompetenciaService = SpringContext.getBean(PersonaCompetenciaService.class);

        precargarImagenesPrioridad();
        configurarFiltros();
        configurarListView();
        configurarTablaTareas();
        configurarTablaCompetencias();
        configurarBotones();
        cargarPersonas();

        System.out.println("(FRANDEV) --> PersonasController inicializado correctamente");
    }

    /**
     * Pre-carga las 3 imágenes de prioridad en un mapa reutilizable.
     * Así no hacemos I/O en cada fila de la tabla.
     */
    private void precargarImagenesPrioridad() {
        cachePrioridad.put("alta",  new Image(getClass().getResourceAsStream("/icons/prioridad-alta.png")));
        cachePrioridad.put("media", new Image(getClass().getResourceAsStream("/icons/prioridad-media.png")));
        cachePrioridad.put("baja",  new Image(getClass().getResourceAsStream("/icons/prioridad-baja.png")));
    }

    /**
     * Rellena los ComboBox de filtros del sidebar y el de estado/sexo del formulario.
     * También conecta el buscador de texto al predicado de filtrado.
     */
    private void configurarFiltros() {
        // Sidebar: filtro de estado
        statusFilter.setItems(FXCollections.observableArrayList("Todos", "activo", "inactivo", "baja"));
        statusFilter.setValue("Todos");
        statusFilter.setOnAction(e -> aplicarFiltros());

        // Formulario: ComboBox de estado de la persona en la cabecera del detalle
        statusComboBox.setItems(FXCollections.observableArrayList("activo", "inactivo", "baja"));
        statusComboBox.setOnAction(e -> aplicarEstiloEstado(statusComboBox.getValue()));

        // Formulario: ComboBox de sexo
        sexComboBox.setItems(FXCollections.observableArrayList("Hombre", "Mujer", "Otro"));

        // Formulario: ComboBox de puesto de trabajo — carga los puestos desde BD
        List<Puesto> puestos = puestoService.obtenerTodos();
        puestoComboBox.setItems(FXCollections.observableArrayList(puestos));
        puestoComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Puesto p) { return p != null ? p.getNombre() : ""; }
            @Override public Puesto fromString(String s) { return null; }
        });

        // Formulario: ComboBox de rol de sistema — carga los roles desde BD
        List<String> nombresRoles = rolSistemaService.obtenerTodos().stream()
                .map(RolSistema::getNombre)
                .toList();
        systemRoleComboBox.setItems(FXCollections.observableArrayList(nombresRoles));

        // Buscador de texto: filtra en tiempo real al escribir
        searchField.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    /**
     * Configura el ListView del sidebar:
     * - CellFactory: muestra "Nombre – Puesto"
     * - Listener: al seleccionar, carga el detalle en el panel central
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
                    if (newVal == null) return;

                    if (formularioNuevoSinGuardar) {
                        // Hay un nuevo profesional sin guardar: pedimos confirmación
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Cambios sin guardar");
                        confirm.setHeaderText("Tiene un profesional nuevo sin guardar.");
                        confirm.setContentText(
                                "Si continúa perderá los datos introducidos.\n\n" +
                                "¿Desea continuar sin guardar?");

                        confirm.showAndWait().ifPresent(respuesta -> {
                            if (respuesta == ButtonType.OK) {
                                // Confirma: descarta el nuevo y carga el seleccionado
                                formularioNuevoSinGuardar = false;
                                mostrarDetalle(newVal);
                            } else {
                                // Cancela: revierte la selección y vuelve al formulario nuevo
                                Platform.runLater(() ->
                                        professionalsListView.getSelectionModel().clearSelection());
                            }
                        });
                    } else {
                        mostrarDetalle(newVal);
                    }
                });
    }

    /**
     * Configura las columnas de la tabla de tareas asignadas.
     *
     * Columnas:
     *   PRIORIDAD → ImageView (alta/media/baja según valor numérico)
     *   TAREA     → título de la Tarea
     *   FECHA     → fecha de creación formateada (dd/MM/yyyy)
     *   HORA      → hora de creación formateada (HH:mm)
     *   ESTADO    → texto del estado actual
     *   (check)   → CheckBox editable para marcar como completada
     */
    private void configurarTablaTareas() {
        tasksTable.setEditable(true);

        // PRIORIDAD: muestra un icono según el nivel numérico de la tarea
        prioColumn.setCellValueFactory(cell -> null);
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

        // TAREA: título de la tarea vinculada
        tareaColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            return new SimpleStringProperty(tarea != null ? tarea.getTitulo() : "");
        });

        // FECHA: fecha de creación de la tarea
        fechaColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            if (tarea != null && tarea.getFechaCreacion() != null) {
                return new SimpleStringProperty(tarea.getFechaCreacion().format(FMT_FECHA));
            }
            return new SimpleStringProperty("");
        });

        // HORA: hora de creación de la tarea
        horaColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            if (tarea != null && tarea.getFechaCreacion() != null) {
                return new SimpleStringProperty(tarea.getFechaCreacion().format(FMT_HORA));
            }
            return new SimpleStringProperty("");
        });

        // ESTADO: texto del estado actual de la tarea
        estadoColumn.setCellValueFactory(cell -> {
            Tarea tarea = cell.getValue().getTarea();
            return new SimpleStringProperty(tarea != null ? tarea.getEstado() : "");
        });

        // COMPLETADO: CheckBox editable — registra cambios pendientes sin persistir aún
        completadoColumn.setCellValueFactory(cell -> {
            Asignacion asig = cell.getValue();
            Tarea tarea = asig.getTarea();
            boolean completada = tarea != null && "completada".equalsIgnoreCase(tarea.getEstado());

            SimpleBooleanProperty prop = new SimpleBooleanProperty(completada);
            prop.addListener((obs, oldVal, newVal) -> cambiosEstadoTareas.put(asig.getId(), newVal));
            return prop;
        });
        completadoColumn.setCellFactory(CheckBoxTableCell.forTableColumn(completadoColumn));
    }

    /**
     * Configura las columnas de la tabla de competencias técnicas.
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

        nivelColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getNivelActual()));

        // Columna "✕" — botón de eliminar por fila
        accionesCompetenciaColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btnQuitar = new Button("✕");
            {
                btnQuitar.setStyle("-fx-background-color: -app-error; -fx-text-fill: white; -fx-font-size: 11px;");
                btnQuitar.setPrefWidth(40);
                btnQuitar.setOnAction(e -> {
                    PersonaCompetencia pc = getTableView().getItems().get(getIndex());
                    onQuitarCompetencia(pc);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnQuitar);
            }
        });
    }

    /**
     * Abre un diálogo para seleccionar una competencia del catálogo y asignarla a la persona.
     * Referenciado en FXML: onAction="#onAñadirCompetencia"
     */
    @FXML
    private void onAñadirCompetencia() {
        if (personaSeleccionada == null) return;

        if (personaSeleccionada.getId() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Profesional no guardado",
                    "Guarda el profesional primero antes de asignar competencias técnicas.");
            return;
        }

        // Competencias ya asignadas (para excluirlas del desplegable)
        List<Long> yaAsignadas = competenciesTable.getItems().stream()
                .map(pc -> pc.getCompetencia().getId())
                .toList();

        List<Competencia> disponibles = competenciaService.obtenerTodas().stream()
                .filter(c -> !yaAsignadas.contains(c.getId()))
                .toList();

        if (disponibles.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin competencias",
                    "Esta persona ya tiene todas las competencias del catálogo asignadas.");
            return;
        }

        // Construir el diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Añadir competencia");
        dialog.setHeaderText("Selecciona la competencia y el nivel inicial (0–100)");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<Competencia> comboComp = new ComboBox<>();
        comboComp.setItems(FXCollections.observableArrayList(disponibles));
        comboComp.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Competencia c) { return c != null ? c.getNombre() + "  [" + c.getTipo() + "]" : ""; }
            @Override public Competencia fromString(String s) { return null; }
        });
        comboComp.setMaxWidth(Double.MAX_VALUE);

        Spinner<Integer> spinnerNivel = new Spinner<>(0, 100, 50);
        spinnerNivel.setEditable(true);
        spinnerNivel.setMaxWidth(Double.MAX_VALUE);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(8,
                new Label("Competencia:"), comboComp,
                new Label("Nivel inicial:"), spinnerNivel);
        content.setPrefWidth(320);
        dialog.getDialogPane().setContent(content);

        // Activar OK solo cuando hay competencia seleccionada
        javafx.scene.Node okBtn = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setDisable(true);
        comboComp.valueProperty().addListener((obs, o, n) -> okBtn.setDisable(n == null));

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK && comboComp.getValue() != null) {
                try {
                    personaCompetenciaService.asignarNivel(
                            personaSeleccionada.getId(),
                            comboComp.getValue().getId(),
                            spinnerNivel.getValue());
                    recargarCompetencias();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            }
        });
    }

    /**
     * Abre un FileChooser para seleccionar la foto del profesional.
     * Convierte la ruta a URL file:// y la asigna a personaSeleccionada.
     * El cambio se persiste al pulsar "Guardar Cambios".
     */
    @FXML
    private void onCambiarFoto() {
        if (personaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Pulsa 'Añadir Profesional' o selecciona uno de la lista antes de cambiar la foto.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto del profesional");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );

        File archivo = fileChooser.showOpenDialog(profileAvatar.getScene().getWindow());
        if (archivo != null) {
            try {
                // Copiamos la imagen a ~/.agilteammanager/avatars/ y guardamos solo el nombre
                String nombreFichero = FotoUtil.copiarAvatar(archivo);
                personaSeleccionada.setFotoPath(nombreFichero);
                profileAvatar.setImage(FotoUtil.cargarImagen(nombreFichero));
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al cambiar foto",
                        "No se pudo copiar la imagen: " + e.getMessage());
            }
        }
    }

    /**
     * Elimina la asociación persona-competencia tras confirmación.
     */
    private void onQuitarCompetencia(PersonaCompetencia pc) {
        if (pc == null || pc.getCompetencia() == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Quitar competencia");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Quitar '" + pc.getCompetencia().getNombre() + "' de este profesional?");

        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    personaCompetenciaService.eliminarCompetencia(
                            personaSeleccionada.getId(),
                            pc.getCompetencia().getId());
                    recargarCompetencias();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            }
        });
    }

    /**
     * Recarga solo la tabla de competencias sin tocar el resto del formulario.
     */
    private void recargarCompetencias() {
        List<PersonaCompetencia> competencias =
                personaCompetenciaService.obtenerCompetenciasDePersona(personaSeleccionada.getId());
        competenciesTable.setItems(FXCollections.observableArrayList(competencias));
    }

    /**
     * Enlaza los botones del panel inferior con sus acciones correspondientes.
     */
    private void configurarBotones() {
        btnAddProfessional.setOnAction(e -> onNuevoProfesional());
        btnSave.setOnAction(e -> onGuardarCambios());
        btnDelete.setOnAction(e -> onEliminarProfesional());
    }

    // =========================================================================
    //  CARGA DE DATOS
    // =========================================================================

    /**
     * Carga todas las personas desde la base de datos y las muestra en el ListView.
     * Selecciona automáticamente la primera si existe.
     */
    private void cargarPersonas() {
        try {
            List<Persona> personas = personaService.obtenerTodas();
            listaPersonas.setAll(personas);

            listaFiltrada = new FilteredList<>(listaPersonas, p -> true);
            professionalsListView.setItems(listaFiltrada);

            if (!listaFiltrada.isEmpty()) {
                professionalsListView.getSelectionModel().selectFirst();
            }

            System.out.println("(FRANDEV) --> Personas cargadas: " + personas.size());

        } catch (Exception e) {
            System.err.println("Error al cargar personas: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de carga",
                    "No se pudieron cargar los profesionales: " + e.getMessage());
        }
    }

    // =========================================================================
    //  FILTRADO
    // =========================================================================

    /**
     * Aplica los filtros activos (texto, estado, departamento, rol) a la lista.
     * Se llama automáticamente cada vez que cambia cualquier filtro.
     */
    private void aplicarFiltros() {
        if (listaFiltrada == null) return;

        String textoSearch = searchField.getText() != null
                ? searchField.getText().toLowerCase().trim()
                : "";
        String estadoSel = statusFilter.getValue();

        listaFiltrada.setPredicate(persona -> {
            boolean matchTexto = textoSearch.isEmpty()
                    || persona.getNombre().toLowerCase().contains(textoSearch)
                    || (persona.getEmail() != null && persona.getEmail().toLowerCase().contains(textoSearch))
                    || persona.getUsuario().toLowerCase().contains(textoSearch);

            boolean matchEstado = estadoSel == null || "Todos".equals(estadoSel)
                    || estadoSel.equalsIgnoreCase(persona.getEstado());

            return matchTexto && matchEstado;
        });
    }

    // =========================================================================
    //  DETALLE DE PERSONA
    // =========================================================================

    /**
     * Rellena el panel central con los datos de la persona seleccionada.
     * Recarga la persona desde BD con todas las colecciones inicializadas (evita LazyInitializationException).
     */
    private void mostrarDetalle(Persona persona) {
        // Recargamos con asignaciones+tareas+competencias para evitar LazyInitializationException
        persona = personaService.obtenerPorIdConTodo(persona.getId()).orElse(persona);
        this.personaSeleccionada = persona;
        cambiosEstadoTareas.clear();

        // Cabecera
        profileName.setText(persona.getNombre());
        profileRole.setText(persona.getPuesto() != null
                ? persona.getPuesto().getNombre()
                : "Sin puesto");

        // Estado con color visual
        statusComboBox.setValue(persona.getEstado());
        aplicarEstiloEstado(persona.getEstado());

        cargarAvatar(persona.getFotoPath());

        // Formulario de datos personales
        nombreField.setText(persona.getNombre());
        puestoComboBox.setValue(persona.getPuesto());
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

        cargarTareas(persona);
        cargarCompetencias(persona);
    }

    /**
     * Carga el avatar de la persona. Si no tiene foto, usa el icono genérico.
     */
    private void cargarAvatar(String fotoPath) {
        try {
            Image imagen = FotoUtil.cargarImagen(fotoPath);
            if (imagen != null) {
                profileAvatar.setImage(imagen);
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
     * Carga las asignaciones (tareas vinculadas) de la persona en la tabla.
     */
    private void cargarTareas(Persona persona) {
        if (persona.getAsignaciones() != null) {
            tasksTable.setItems(FXCollections.observableArrayList(persona.getAsignaciones()));
        } else {
            tasksTable.setItems(FXCollections.emptyObservableList());
        }
    }

    /**
     * Carga las competencias técnicas de la persona en la tabla.
     */
    private void cargarCompetencias(Persona persona) {
        if (persona.getPersonasCompetencias() != null) {
            competenciesTable.setItems(
                    FXCollections.observableArrayList(persona.getPersonasCompetencias()));
        } else {
            competenciesTable.setItems(FXCollections.emptyObservableList());
        }
    }

    // =========================================================================
    //  ACCIONES (BOTONES)
    // =========================================================================

    /**
     * Prepara el formulario para crear un nuevo profesional (limpia todos los campos).
     * Se crea un Persona vacío (id == null) para que foto y guardado funcionen.
     */
    private void onNuevoProfesional() {
        professionalsListView.getSelectionModel().clearSelection();
        personaSeleccionada = new Persona();
        cambiosEstadoTareas.clear();
        limpiarFormulario();
        formularioNuevoSinGuardar = true;
    }

    /**
     * Recoge los datos del formulario y los persiste.
     * Si personaSeleccionada no tiene id, crea un nuevo profesional.
     * Si ya tiene id, actualiza el existente.
     */
    private void onGuardarCambios() {
        if (personaSeleccionada == null) return;

        if (personaSeleccionada.getId() == null) {
            crearNuevaPersona();
            return;
        }

        // 1) Validar email antes de persistir
        String emailActualizado = emailField.getText().trim();
        if (!esEmailValido(emailActualizado)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Email inválido",
                    "El formato del email no es correcto (ejemplo: usuario@dominio.com).");
            return;
        }

        // 2) Actualizar modelo con los valores del formulario
        personaSeleccionada.setNombre(nombreField.getText());
        personaSeleccionada.setPuesto(puestoComboBox.getValue());
        personaSeleccionada.setEmail(emailActualizado.isEmpty() ? null : emailActualizado);
        personaSeleccionada.setUsuario(usernameField.getText());
        personaSeleccionada.setFechaAlta(joinDatePicker.getValue());
        personaSeleccionada.setEstado(statusComboBox.getValue());

        String sexoTexto = sexComboBox.getValue();
        if (sexoTexto != null) {
            switch (sexoTexto) {
                case "Hombre" -> personaSeleccionada.setSexo('M');
                case "Mujer"  -> personaSeleccionada.setSexo('F');
                default       -> personaSeleccionada.setSexo('O');
            }
        }

        // 2) Rol de sistema: buscar por nombre y asignar
        String rolNombre = systemRoleComboBox.getValue();
        if (rolNombre != null) {
            rolSistemaService.obtenerTodos().stream()
                    .filter(r -> r.getNombre().equals(rolNombre))
                    .findFirst()
                    .ifPresent(personaSeleccionada::setRol);
        }

        // Capturamos id y nombre ANTES de recargar la lista,
        // porque cargarPersonas() → selectFirst() → listener → personaSeleccionada cambia.
        final Long idGuardado     = personaSeleccionada.getId();
        final String nombreGuardado = personaSeleccionada.getNombre();

        try {
            personaService.guardar(personaSeleccionada);

            // Recarga la lista (esto cambia personaSeleccionada como efecto secundario)
            cargarPersonas();

            // Re-seleccionamos la persona que acabamos de guardar
            listaFiltrada.stream()
                    .filter(p -> p.getId().equals(idGuardado))
                    .findFirst()
                    .ifPresent(p -> {
                        professionalsListView.getSelectionModel().select(p);
                        professionalsListView.scrollTo(p);
                    });

            cambiosEstadoTareas.clear();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado",
                    "Los cambios de '" + nombreGuardado + "' se han guardado correctamente.");

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudieron guardar los cambios: " + e.getMessage());
        }
    }

    /**
     * Valida el formulario y persiste el nuevo profesional.
     * Pide una contraseña inicial mediante un diálogo sencillo.
     */
    private void crearNuevaPersona() {
        String nombre   = nombreField.getText().trim();
        String usuario  = usernameField.getText().trim();
        Puesto puesto   = puestoComboBox.getValue();
        String rolNombre = systemRoleComboBox.getValue();

        if (nombre.isEmpty() || usuario.isEmpty() || puesto == null || rolNombre == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos",
                    "Nombre, usuario, puesto y rol de sistema son obligatorios.");
            return;
        }

        String emailNuevo = emailField.getText().trim();
        if (!esEmailValido(emailNuevo)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Email inválido",
                    "El formato del email no es correcto (ejemplo: usuario@dominio.com).");
            return;
        }

        // Pedir contraseña inicial
        TextInputDialog passDialog = new TextInputDialog();
        passDialog.setTitle("Contraseña inicial");
        passDialog.setHeaderText("Establece la contraseña inicial del profesional");
        passDialog.setContentText("Contraseña:");
        Optional<String> passResult = passDialog.showAndWait();
        if (passResult.isEmpty() || passResult.get().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin contraseña",
                    "Debes establecer una contraseña inicial.");
            return;
        }

        // Rellenar el objeto Persona con los datos del formulario
        personaSeleccionada.setNombre(nombre);
        personaSeleccionada.setUsuario(usuario);
        String emailTexto = emailField.getText().trim();
        personaSeleccionada.setEmail(emailTexto.isEmpty() ? null : emailTexto);
        personaSeleccionada.setPuesto(puesto);
        personaSeleccionada.setEstado(statusComboBox.getValue() != null ? statusComboBox.getValue() : "activo");
        personaSeleccionada.setFechaAlta(joinDatePicker.getValue() != null ? joinDatePicker.getValue() : LocalDate.now());

        String sexoTexto = sexComboBox.getValue();
        personaSeleccionada.setSexo(switch (sexoTexto != null ? sexoTexto : "") {
            case "Hombre" -> 'M';
            case "Mujer"  -> 'F';
            default       -> 'O';
        });

        rolSistemaService.obtenerTodos().stream()
                .filter(r -> r.getNombre().equals(rolNombre))
                .findFirst()
                .ifPresent(personaSeleccionada::setRol);

        personaSeleccionada.setPassword(BCrypt.hashpw(passResult.get(), BCrypt.gensalt()));

        try {
            Persona guardada = personaService.guardar(personaSeleccionada);
            formularioNuevoSinGuardar = false;
            final Long idGuardado = guardada.getId();
            cargarPersonas();
            listaFiltrada.stream()
                    .filter(p -> p.getId().equals(idGuardado))
                    .findFirst()
                    .ifPresent(p -> {
                        professionalsListView.getSelectionModel().select(p);
                        professionalsListView.scrollTo(p);
                    });
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado",
                    "Profesional '" + nombre + "' creado correctamente.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al crear",
                    "No se pudo crear el profesional: " + e.getMessage());
        }
    }

    /**
     * Solicita confirmación y elimina la persona seleccionada.
     *
     * TODO: Llamar a personaService.eliminar() cuando esté implementado.
     */
    private void onEliminarProfesional() {
        if (personaSeleccionada == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar profesional");
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar a "
                + personaSeleccionada.getNombre() + "?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                // TODO: personaService.eliminar(personaSeleccionada.getId());
                System.out.println("(FRANDEV) --> Eliminar persona - pendiente de implementar");
            }
        });
    }

    /**
     * Deja todos los campos del formulario en blanco.
     */
    private void limpiarFormulario() {
        profileName.setText("Nuevo profesional");
        profileRole.setText("Sin puesto asignado");
        cargarAvatar(null);
        statusComboBox.setValue("activo");
        aplicarEstiloEstado("activo");
        nombreField.clear();
        puestoComboBox.setValue(null);
        emailField.clear();
        usernameField.clear();
        sexComboBox.setValue(null);
        joinDatePicker.setValue(null);
        systemRoleComboBox.setValue(null);
        tasksTable.setItems(FXCollections.emptyObservableList());
        competenciesTable.setItems(FXCollections.emptyObservableList());
    }

    // =========================================================================
    //  UTILIDADES
    // =========================================================================

    /**
     * Devuelve true si el email tiene formato válido o está vacío (campo opcional).
     */
    private boolean esEmailValido(String email) {
        if (email == null || email.isBlank()) return true;
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Cambia el color de fondo del ComboBox de estado según el valor.
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
     * Convierte el valor numérico (BigDecimal 0-1) de prioridad en una categoría.
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
    //  MÉTODO PÚBLICO — Refresco externo
    // =========================================================================

    /**
     * Permite refrescar la lista desde otro controlador (si fuese necesario).
     */
    public void refrescarDatos() {
        cargarPersonas();
    }

    /**
     * Muestra un diálogo de alerta estándar de JavaFX.
     *
     * @param tipo    WARNING, ERROR, INFORMATION, CONFIRMATION
     * @param titulo  Texto del título de la ventana
     * @param mensaje Texto del cuerpo del mensaje
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
