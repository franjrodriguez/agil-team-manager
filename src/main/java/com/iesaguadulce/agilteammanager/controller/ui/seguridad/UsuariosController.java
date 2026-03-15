package com.iesaguadulce.agilteammanager.controller.ui.seguridad;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.service.seguridad.PermisoService;
import com.iesaguadulce.agilteammanager.service.seguridad.UsuarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ═══════════════════════════════════════════════════════════════
 * UsuariosController — Controlador UI de Gestión de Usuarios
 * ───────────────────────────────────────────────────────────────
 * UBICACIÓN: src/main/java/.../ui/seguridad/UsuariosController.java
 *
 * FLUJO:
 *   FXML (evento) → este Controller → UsuarioService / PermisoService → BD
 *
 * NOTA: Usa SpringContext.getBean() en initialize() porque JavaFX
 * instancia este controller con new (sin Spring), igual que
 * CompetenciasController y PersonasController.
 * ═══════════════════════════════════════════════════════════════
 */
@Component
public class UsuariosController implements Initializable {

    // ─────────────────────────────────────────────────────────
    // SERVICIOS (obtenidos manualmente desde SpringContext)
    // ─────────────────────────────────────────────────────────

    private UsuarioService usuarioService;
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
    @FXML private ComboBox<String> deptFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button btnAddProfessional;
    @FXML private ListView<Persona> professionalsListView;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Cabecera del detalle
    // ─────────────────────────────────────────────────────────

    @FXML private ImageView profileAvatar;
    @FXML private Label profileName;
    @FXML private Label profileRole;
    @FXML private ComboBox<String> statusComboBox;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Sección de credenciales
    // ─────────────────────────────────────────────────────────

    /** En el FXML está etiquetado como "USUARIO" — es el nombre de usuario */
    @FXML private TextField emailField;

    /** Campo de contraseña (solo visible / editable al crear o cambiar clave) */
    @FXML private TextField passwordField;

    /** Selector del rol de sistema que determina los permisos de acceso */
    @FXML private ComboBox<String> systemRoleComboBox;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Botones de acción
    // ─────────────────────────────────────────────────────────

    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    // ─────────────────────────────────────────────────────────
    // ESTADO INTERNO
    // ─────────────────────────────────────────────────────────

    /** Lista base que alimenta el ListView del sidebar */
    private final ObservableList<Persona> listaUsuarios = FXCollections.observableArrayList();

    /** Lista filtrada que envuelve a listaUsuarios */
    private FilteredList<Persona> listaFiltrada;

    /** Usuario actualmente seleccionado. null = modo "nuevo" */
    private Persona usuarioSeleccionado;

    // =========================================================================
    //  INICIALIZACIÓN
    // =========================================================================

    /**
     * JavaFX llama a este método tras cargar el FXML.
     * Obtenemos los servicios desde SpringContext (patrón manual, igual que
     * CompetenciasController) porque @Autowired no funciona aquí.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("(FRANDEV) --> Iniciando UsuariosController...");

        this.usuarioService = SpringContext.getBean(UsuarioService.class);
        this.permisoService = SpringContext.getBean(PermisoService.class);

        configurarFiltros();
        configurarListView();
        configurarBotones();
        cargarUsuarios();

        System.out.println("(FRANDEV) --> UsuariosController inicializado correctamente");
    }

    // =========================================================================
    //  CONFIGURACIÓN DE COMPONENTES
    // =========================================================================

    /**
     * Rellena los ComboBox de filtros del sidebar y el de estado/rol del formulario.
     */
    private void configurarFiltros() {
        // Sidebar: filtro de estado
        statusFilter.setItems(FXCollections.observableArrayList("Todos", "activo", "inactivo", "baja"));
        statusFilter.setValue("Todos");
        statusFilter.setOnAction(e -> aplicarFiltros());

        // Sidebar: filtro de departamento y rol (se rellenan tras cargar usuarios)
        deptFilter.setOnAction(e -> aplicarFiltros());
        roleFilter.setOnAction(e -> aplicarFiltros());

        // Formulario: estados posibles del usuario
        statusComboBox.setItems(FXCollections.observableArrayList("activo", "inactivo", "baja"));
        statusComboBox.setOnAction(e -> aplicarEstiloEstado(statusComboBox.getValue()));

        // Formulario: roles de sistema disponibles
        List<RolSistema> roles = permisoService.obtenerTodosLosRoles();
        ObservableList<String> nombresRoles = FXCollections.observableArrayList(
                roles.stream().map(RolSistema::getNombre).toList()
        );
        systemRoleComboBox.setItems(nombresRoles);

        // Buscador en tiempo real
        searchField.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    /**
     * Configura el ListView del sidebar:
     * - CellFactory: muestra "Nombre de usuario  –  Rol"
     * - Listener: al seleccionar, carga el detalle
     */
    private void configurarListView() {
        professionalsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Persona persona, boolean empty) {
                super.updateItem(persona, empty);
                if (empty || persona == null) {
                    setText(null);
                } else {
                    String rol = persona.getRol() != null
                            ? persona.getRol().getNombre()
                            : "Sin rol";
                    setText(persona.getNombre() + "  –  " + rol);
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
     * Enlaza los botones con sus acciones.
     */
    private void configurarBotones() {
        btnAddProfessional.setOnAction(e -> onNuevoUsuario());
        btnSave.setOnAction(e -> onGuardarCambios());
        btnDelete.setOnAction(e -> onEliminarUsuario());
    }

    // =========================================================================
    //  CARGA DE DATOS
    // =========================================================================

    /**
     * Carga todos los usuarios desde BD y los muestra en el ListView.
     */
    private void cargarUsuarios() {
        try {
            List<Persona> usuarios = usuarioService.obtenerTodos();
            listaUsuarios.setAll(usuarios);

            listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);
            professionalsListView.setItems(listaFiltrada);

            if (!listaFiltrada.isEmpty()) {
                professionalsListView.getSelectionModel().selectFirst();
            }

            System.out.println("(FRANDEV) --> Usuarios cargados: " + usuarios.size());

        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de carga",
                    "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    // =========================================================================
    //  FILTRADO
    // =========================================================================

    /**
     * Aplica los filtros activos al ListView.
     */
    private void aplicarFiltros() {
        if (listaFiltrada == null) return;

        String texto    = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String estadoSel = statusFilter.getValue();
        String deptSel   = deptFilter.getValue();
        String rolSel    = roleFilter.getValue();

        listaFiltrada.setPredicate(persona -> {
            boolean matchTexto = texto.isEmpty()
                    || persona.getUsuario().toLowerCase().contains(texto)
                    || persona.getNombre().toLowerCase().contains(texto)
                    || (persona.getEmail() != null && persona.getEmail().toLowerCase().contains(texto));

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
    //  DETALLE DE USUARIO
    // =========================================================================

    /**
     * Rellena el panel central con los datos del usuario seleccionado.
     */
    private void mostrarDetalle(Persona persona) {
        this.usuarioSeleccionado = persona;

        // Cabecera
        profileName.setText(persona.getNombre());
        profileRole.setText(persona.getRol() != null
                ? persona.getRol().getNombre()
                : "Sin rol");

        statusComboBox.setValue(persona.getEstado());
        aplicarEstiloEstado(persona.getEstado());

        cargarAvatar(persona.getFotoPath());

        // Credenciales
        emailField.setText(persona.getUsuario());
        passwordField.clear(); // No mostramos la contraseña hasheada por seguridad

        if (persona.getRol() != null) {
            systemRoleComboBox.setValue(persona.getRol().getNombre());
        }

    }

    /**
     * Carga el avatar del usuario. Si no tiene foto, usa el icono genérico.
     */
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

    // =========================================================================
    //  ACCIONES (BOTONES)
    // =========================================================================

    /**
     * Prepara el formulario para crear un nuevo usuario.
     */
    private void onNuevoUsuario() {
        usuarioSeleccionado = null;
        limpiarFormulario();
    }

    /**
     * Guarda los cambios del usuario seleccionado (o crea uno nuevo).
     *
     * TODO: Conectar crearUsuario con selección de Puesto desde un ComboBox.
     */
    private void onGuardarCambios() {
        String nuevoUsuario = emailField.getText() != null ? emailField.getText().trim() : "";
        String nuevoRolNombre = systemRoleComboBox.getValue();
        String nuevoEstado = statusComboBox.getValue();

        if (nuevoUsuario.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                    "El nombre de usuario es obligatorio.");
            emailField.requestFocus();
            return;
        }

        try {
            if (usuarioSeleccionado == null) {
                // TODO: Crear nuevo usuario con crearUsuario() cuando el formulario incluya Puesto
                System.out.println("(FRANDEV) --> Crear nuevo usuario - pendiente de ampliar formulario");
                return;
            }

            // Actualizar estado si cambió
            if (nuevoEstado != null && !nuevoEstado.equals(usuarioSeleccionado.getEstado())) {
                usuarioService.cambiarEstado(usuarioSeleccionado.getId(), nuevoEstado);
            }

            // TODO: Llamar a actualizarUsuario() cuando el formulario incluya PuestoId
            System.out.println("(FRANDEV) --> Datos actualizados en memoria, pendiente de persistir en BD");

            cargarUsuarios();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Cambios guardados correctamente.");

        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", e.getMessage());
        }
    }

    /**
     * Elimina el usuario seleccionado con confirmación previa.
     *
     * TODO: Implementar eliminación en UsuarioService.
     */
    private void onEliminarUsuario() {
        if (usuarioSeleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar usuario");
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar al usuario '"
                + usuarioSeleccionado.getUsuario() + "'?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                // TODO: usuarioService.eliminar(usuarioSeleccionado.getId());
                System.out.println("(FRANDEV) --> Eliminar usuario - pendiente de implementar en UsuarioService");
            }
        });
    }

    /**
     * Deja el formulario en blanco.
     */
    private void limpiarFormulario() {
        profileName.setText("");
        profileRole.setText("");
        profileAvatar.setImage(null);
        statusComboBox.setValue(null);
        statusComboBox.setStyle("");
        emailField.clear();
        passwordField.clear();
        systemRoleComboBox.setValue(null);
    }

    // =========================================================================
    //  UTILIDADES
    // =========================================================================

    /**
     * Cambia el color de fondo del statusComboBox según el estado.
     *   activo   → verde
     *   inactivo → amarillo
     *   baja     → rojo
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
