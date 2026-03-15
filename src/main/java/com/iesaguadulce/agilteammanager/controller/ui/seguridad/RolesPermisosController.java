package com.iesaguadulce.agilteammanager.controller.ui.seguridad;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.seguridad.Permiso;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.service.seguridad.PermisoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════════════════
 * RolesPermisosController — Controlador UI de Roles y Permisos
 * ───────────────────────────────────────────────────────────────
 * UBICACIÓN: src/main/java/.../ui/seguridad/RolesPermisosController.java
 *
 * FLUJO:
 *   FXML (evento) → este Controller → PermisoService → BD
 *
 * RESPONSABILIDADES:
 *   - Listar y buscar roles del sistema en el sidebar
 *   - Mostrar/editar nombre y descripción del rol seleccionado
 *   - Listar permisos asignados al rol
 *   - Agregar / quitar permisos de un rol
 *   - Crear y eliminar roles
 *
 * NOTA: Usa SpringContext.getBean() en initialize() porque JavaFX
 * instancia este controller con new (sin Spring).
 * ═══════════════════════════════════════════════════════════════
 */
@Component
public class RolesPermisosController implements Initializable {

    // ─────────────────────────────────────────────────────────
    // SERVICIOS (obtenidos manualmente desde SpringContext)
    // ─────────────────────────────────────────────────────────

    private PermisoService permisoService;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Contenedor raíz
    // ─────────────────────────────────────────────────────────

    @FXML private BorderPane rootPane;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Sidebar
    // ─────────────────────────────────────────────────────────

    @FXML private VBox sidebarPanel;
    @FXML private TextField searchField;
    @FXML private Button btnAddRol;
    @FXML private ListView<RolSistema> rolesListView;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Sección de edición del rol
    // ─────────────────────────────────────────────────────────

    /** Campo nombre del rol */
    @FXML private TextField rolField;

    /** Campo descripción del rol */
    @FXML private TextArea descripcionArea;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Tabla de permisos del rol
    // ─────────────────────────────────────────────────────────

    @FXML private TableView<Permiso> tasksTable;

    /** Columna NOMBRE → Permiso.codigo */
    @FXML private TableColumn<Permiso, String> nombreColumn;

    /** Columna DESCRIPCIÓN → Permiso.descripcion */
    @FXML private TableColumn<Permiso, String> descripcionColumn;

    /**
     * Columna ACCIONES → botón "Quitar" por fila para desasignar el permiso del rol.
     * Usamos Void porque el valor no viene del modelo, lo renderiza la cellFactory.
     */
    @FXML private TableColumn<Permiso, Void> accionesColumn;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Botones
    // ─────────────────────────────────────────────────────────

    @FXML private Button btnAddPermiso;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    // ─────────────────────────────────────────────────────────
    // ESTADO INTERNO
    // ─────────────────────────────────────────────────────────

    /** Lista observable que alimenta el ListView del sidebar */
    private final ObservableList<RolSistema> listaRoles = FXCollections.observableArrayList();

    /** Lista observable de permisos del rol seleccionado */
    private final ObservableList<Permiso> listaPermisos = FXCollections.observableArrayList();

    /** Rol actualmente seleccionado. null = modo "nuevo" */
    private RolSistema rolSeleccionado;

    // =========================================================================
    //  INICIALIZACIÓN
    // =========================================================================

    /**
     * JavaFX llama a este método tras cargar el FXML.
     * Obtenemos PermisoService desde SpringContext (patrón manual).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("(FRANDEV) --> Iniciando RolesPermisosController...");

        this.permisoService = SpringContext.getBean(PermisoService.class);

        configurarListView();
        configurarTablaPermisos();
        configurarBotones();
        cargarRoles();

        System.out.println("(FRANDEV) --> RolesPermisosController inicializado correctamente");
    }

    // =========================================================================
    //  CONFIGURACIÓN DE COMPONENTES
    // =========================================================================

    /**
     * Configura el ListView de roles:
     * - CellFactory: muestra el nombre del rol
     * - Búsqueda en tiempo real con searchField
     * - Listener: al seleccionar un rol, carga su detalle
     */
    private void configurarListView() {
        rolesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RolSistema rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setText(null);
                } else {
                    setText(rol.getNombre());
                }
            }
        });

        rolesListView.setItems(listaRoles);

        rolesListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        mostrarDetalle(newVal);
                    }
                });

        // Búsqueda en tiempo real: filtra la lista según el texto
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrarRoles(newVal));
    }

    /**
     * Configura las tres columnas de la tabla de permisos:
     *   NOMBRE      → Permiso.codigo
     *   DESCRIPCIÓN → Permiso.descripcion
     *   ACCIONES    → Botón "Quitar" para desasignar el permiso del rol
     */
    private void configurarTablaPermisos() {
        nombreColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCodigo())
        );

        descripcionColumn.setCellValueFactory(cell -> {
            String desc = cell.getValue().getDescripcion();
            return new SimpleStringProperty(desc != null ? desc : "");
        });

        // Columna de acciones: un botón "Quitar" por fila
        accionesColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");

            {
                // Estilo del botón inline
                btnQuitar.setStyle("-fx-background-color: -app-error; -fx-text-fill: white;");
                btnQuitar.setOnAction(e -> {
                    Permiso permiso = getTableRow().getItem();
                    if (permiso != null) {
                        quitarPermiso(permiso);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btnQuitar));
            }
        });

        tasksTable.setItems(listaPermisos);
    }

    /**
     * Enlaza los botones principales con sus acciones.
     */
    private void configurarBotones() {
        btnAddRol.setOnAction(e -> onNuevoRol());
        btnAddPermiso.setOnAction(e -> onAgregarPermiso());
        btnSave.setOnAction(e -> onGuardarCambios());
        btnDelete.setOnAction(e -> onEliminarRol());
    }

    // =========================================================================
    //  CARGA DE DATOS
    // =========================================================================

    /**
     * Carga todos los roles desde BD y los muestra en el ListView.
     */
    private void cargarRoles() {
        try {
            List<RolSistema> roles = permisoService.obtenerTodosLosRoles();
            listaRoles.setAll(roles);

            if (!listaRoles.isEmpty()) {
                rolesListView.getSelectionModel().selectFirst();
            }

            System.out.println("(FRANDEV) --> Roles cargados: " + roles.size());

        } catch (Exception e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de carga",
                    "No se pudieron cargar los roles: " + e.getMessage());
        }
    }

    /**
     * Carga los permisos del rol seleccionado en la tabla.
     */
    private void cargarPermisosDelRol(RolSistema rol) {
        try {
            List<Permiso> permisos = permisoService.obtenerPermisosPorRol(rol.getId());
            listaPermisos.setAll(permisos);
        } catch (Exception e) {
            System.err.println("Error al cargar permisos del rol: " + e.getMessage());
            listaPermisos.clear();
        }
    }

    // =========================================================================
    //  BÚSQUEDA / FILTRADO
    // =========================================================================

    /**
     * Filtra la lista de roles en tiempo real según el texto introducido.
     * Si el texto está vacío, muestra todos los roles.
     */
    private void filtrarRoles(String texto) {
        if (texto == null || texto.isBlank()) {
            rolesListView.setItems(listaRoles);
        } else {
            String textoBusqueda = texto.toLowerCase().trim();
            ObservableList<RolSistema> filtrados = listaRoles.filtered(
                    rol -> rol.getNombre().toLowerCase().contains(textoBusqueda)
            );
            rolesListView.setItems(filtrados);
        }
    }

    // =========================================================================
    //  DETALLE DEL ROL
    // =========================================================================

    /**
     * Rellena el formulario central con los datos del rol seleccionado.
     */
    private void mostrarDetalle(RolSistema rol) {
        this.rolSeleccionado = rol;

        rolField.setText(rol.getNombre());
        descripcionArea.setText(rol.getDescripcion() != null ? rol.getDescripcion() : "");

        cargarPermisosDelRol(rol);
    }

    // =========================================================================
    //  ACCIONES (BOTONES)
    // =========================================================================

    /**
     * Prepara el formulario para crear un nuevo rol.
     */
    private void onNuevoRol() {
        rolSeleccionado = null;
        limpiarFormulario();
        rolField.requestFocus();
    }

    /**
     * Guarda los cambios del rol seleccionado o crea uno nuevo.
     *
     * TODO: Añadir crearRol() y actualizarRol() en PermisoService cuando
     *       se necesite persistir nombre y descripción del rol.
     */
    private void onGuardarCambios() {
        String nombre = rolField.getText() != null ? rolField.getText().trim() : "";
        String descripcion = descripcionArea.getText() != null ? descripcionArea.getText().trim() : "";

        if (nombre.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                    "El nombre del rol es obligatorio.");
            rolField.requestFocus();
            return;
        }

        try {
            if (rolSeleccionado == null) {
                permisoService.crearRol(nombre, descripcion);
            } else {
                permisoService.actualizarRol(rolSeleccionado.getId(), nombre, descripcion);
            }
            cargarRoles();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Rol guardado correctamente.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", e.getMessage());
        }
    }

    /**
     * Elimina el rol seleccionado con confirmación previa.
     *
     * TODO: Añadir eliminarRol() en PermisoService.
     */
    private void onEliminarRol() {
        if (rolSeleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar rol");
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar el rol '"
                + rolSeleccionado.getNombre() + "'?\n"
                + "Esta acción afectará a todos los usuarios con este rol.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    permisoService.eliminarRol(rolSeleccionado.getId());
                    limpiarFormulario();
                    cargarRoles();
                } catch (RuntimeException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar", e.getMessage());
                }
            }
        });
    }

    /**
     * Abre un diálogo para crear un nuevo permiso y asignarlo al rol seleccionado.
     * Solicita: código (nombre) y descripción.
     */
    private void onAgregarPermiso() {
        if (rolSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona un rol antes de agregar permisos.");
            return;
        }

        // ── Campos del formulario ──
        TextField codigoField = new TextField();
        codigoField.setPromptText("ej: CREAR_PROYECTO");
        codigoField.setMaxWidth(Double.MAX_VALUE);

        TextArea descripcionField = new TextArea();
        descripcionField.setPromptText("Descripción del permiso...");
        descripcionField.setPrefRowCount(3);
        descripcionField.setWrapText(true);
        descripcionField.setMaxWidth(Double.MAX_VALUE);

        VBox contenido = new VBox(8,
                new Label("NOMBRE / CÓDIGO"),
                codigoField,
                new Label("DESCRIPCIÓN"),
                descripcionField
        );
        contenido.setPadding(new javafx.geometry.Insets(8, 0, 0, 0));

        // ── Dialog ──
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setResizable(true);
        dialogo.setTitle("Nuevo Permiso");
        dialogo.setHeaderText("Añadir permiso al rol \"" + rolSeleccionado.getNombre() + "\"");
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().setMinWidth(460);
        dialogo.getDialogPane().setPrefWidth(460);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Renombramos los botones
        ((Button) dialogo.getDialogPane().lookupButton(ButtonType.OK)).setText("Guardar");
        ((Button) dialogo.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Cancelar");

        dialogo.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                String codigo = codigoField.getText() != null ? codigoField.getText().trim() : "";
                if (codigo.isBlank()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                            "El código del permiso es obligatorio.");
                    return;
                }
                try {
                    Permiso nuevo = permisoService.crearPermisoYAsignarARol(
                            rolSeleccionado.getId(),
                            codigo,
                            descripcionField.getText()
                    );
                    listaPermisos.add(nuevo);
                    System.out.println("(FRANDEV) --> Permiso '" + nuevo.getCodigo() + "' creado y asignado al rol.");
                } catch (RuntimeException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            }
        });
    }

    /**
     * Quita un permiso del rol seleccionado (acción del botón por fila).
     */
    private void quitarPermiso(Permiso permiso) {
        if (rolSeleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Quitar permiso");
        confirmacion.setHeaderText("¿Quitar el permiso '" + permiso.getCodigo() + "' del rol?");
        confirmacion.setContentText("El rol perderá acceso a esta funcionalidad.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                listaPermisos.remove(permiso);

                Set<Long> nuevosIds = listaPermisos.stream()
                        .map(Permiso::getId)
                        .collect(Collectors.toSet());

                try {
                    permisoService.asignarPermisosARol(rolSeleccionado.getId(), nuevosIds);
                    System.out.println("(FRANDEV) --> Permiso '" + permiso.getCodigo() + "' quitado del rol.");
                } catch (Exception e) {
                    // Revertimos si falla
                    listaPermisos.add(permiso);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo quitar el permiso: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Limpia el formulario central.
     */
    private void limpiarFormulario() {
        rolField.clear();
        descripcionArea.clear();
        listaPermisos.clear();
    }

    // =========================================================================
    //  UTILIDADES
    // =========================================================================

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
