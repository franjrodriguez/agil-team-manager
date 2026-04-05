package com.iesaguadulce.agilteammanager.controller.ui.seguridad;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.service.seguridad.PermisoService;
import com.iesaguadulce.agilteammanager.service.seguridad.UsuarioService;
import javafx.application.Platform;
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
 * Controlador UI para gestión de usuarios del sistema.
 *
 * <p>Permite listar, filtrar, crear y editar usuarios con sus credenciales
 * y roles de acceso.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Component
public class UsuariosController implements Initializable {

    private UsuarioService usuarioService;
    private PermisoService permisoService;

    // FXML - Contenedor raíz
    @FXML private BorderPane rootPane;

    // FXML - Sidebar
    @FXML private VBox sidebarPanel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> deptFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button btnAddProfessional;
    @FXML private ListView<Persona> professionalsListView;

    // FXML - Cabecera del detalle
    @FXML private ImageView profileAvatar;
    @FXML private Label profileName;
    @FXML private Label profileRole;
    @FXML private ComboBox<String> statusComboBox;

    // FXML - Panel lateral derecho
    @FXML private Label puestoLabel;
    @FXML private Label puestoDescLabel;

    // FXML - Credenciales
    /** Nombre de usuario para login */
    @FXML private TextField emailField;
    /** Contraseña (solo visible al crear/cambiar) */
    @FXML private TextField passwordField;
    /** Rol de sistema para permisos */
    @FXML private ComboBox<String> systemRoleComboBox;

    // FXML - Botones
    @FXML private Button btnDelete;
    @FXML private Button btnSave;

    // Estado interno
    private final ObservableList<Persona> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<Persona> listaFiltrada;
    private Persona usuarioSeleccionado;

    /** true cuando el usuario ha modificado el formulario */
    private boolean isDirty = false;
    /** true mientras se cargan datos programáticamente (evita falsos positivos) */
    private boolean ignorarCambios = false;
    /** true mientras se revierte la selección tras cancelar el diálogo */
    private boolean revirtiendoSeleccion = false;

    /**
     * Inicializa el controlador obteniendo servicios desde SpringContext.
     * Configura filtros, listView y carga datos iniciales.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.usuarioService = SpringContext.getBean(UsuarioService.class);
        this.permisoService = SpringContext.getBean(PermisoService.class);

        configurarFiltros();
        configurarListView();
        configurarBotones();
        cargarUsuarios();
    }

    /** Marca el formulario como modificado y activa el botón Guardar. */
    private void marcarCambiado() {
        if (ignorarCambios) return;
        isDirty = true;
        btnSave.setDisable(false);
    }

    /** Configura ComboBox de filtros y búsqueda en tiempo real. */
    private void configurarFiltros() {
        statusFilter.setItems(FXCollections.observableArrayList("Todos", "activo", "inactivo", "baja"));
        statusFilter.setValue("Todos");
        statusFilter.setOnAction(e -> aplicarFiltros());

        deptFilter.setOnAction(e -> aplicarFiltros());
        roleFilter.setOnAction(e -> aplicarFiltros());

        statusComboBox.setItems(FXCollections.observableArrayList("activo", "inactivo", "baja"));
        statusComboBox.setOnAction(e -> aplicarEstiloEstado(statusComboBox.getValue()));

        List<RolSistema> roles = permisoService.obtenerTodosLosRoles();
        systemRoleComboBox.setItems(FXCollections.observableArrayList(
                roles.stream().map(RolSistema::getNombre).toList()));

        searchField.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        // isDirty: listeners en los campos del formulario de edición
        emailField.textProperty().addListener((obs, oldV, newV) -> marcarCambiado());
        passwordField.textProperty().addListener((obs, oldV, newV) -> marcarCambiado());
        statusComboBox.valueProperty().addListener((obs, oldV, newV) -> marcarCambiado());
        systemRoleComboBox.valueProperty().addListener((obs, oldV, newV) -> marcarCambiado());
    }

    /** Configura cellFactory del ListView y listener de selección. */
    private void configurarListView() {
        professionalsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Persona persona, boolean empty) {
                super.updateItem(persona, empty);
                if (empty || persona == null) {
                    setText(null);
                } else {
                    String rol = persona.getRol() != null ? persona.getRol().getNombre() : "Sin rol";
                    setText(persona.getNombre() + "  –  " + rol);
                }
            }
        });

        professionalsListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (revirtiendoSeleccion) return;
                    if (newVal == null) return;

                    if (isDirty) {
                        String nombreActual = usuarioSeleccionado != null ? usuarioSeleccionado.getNombre() : "";
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Cambios sin guardar");
                        confirm.setHeaderText("Hay cambios sin guardar en '" + nombreActual + "'.");
                        confirm.setContentText("¿Descartar los cambios y seleccionar otro usuario?");
                        confirm.showAndWait().ifPresent(respuesta -> {
                            if (respuesta == ButtonType.OK) {
                                isDirty = false;
                                btnSave.setDisable(true);
                                mostrarDetalle(newVal);
                            } else {
                                // Revertir selección sin disparar el listener de nuevo
                                Persona anterior = oldVal;
                                revirtiendoSeleccion = true;
                                Platform.runLater(() -> {
                                    professionalsListView.getSelectionModel().select(anterior);
                                    revirtiendoSeleccion = false;
                                });
                            }
                        });
                    } else {
                        mostrarDetalle(newVal);
                    }
                });
    }

    /** Enlaza botones con sus acciones. */
    private void configurarBotones() {
        btnAddProfessional.setOnAction(e -> onNuevoUsuario());
        btnSave.setOnAction(e -> onGuardarCambios());
        btnDelete.setOnAction(e -> onEliminarUsuario());
        btnSave.setDisable(true);
    }

    /** Carga usuarios desde BD al ListView. */
    private void cargarUsuarios() {
        List<Persona> usuarios = usuarioService.obtenerTodos();
        listaUsuarios.setAll(usuarios);
        listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);
        professionalsListView.setItems(listaFiltrada);
        if (!listaFiltrada.isEmpty()) professionalsListView.getSelectionModel().selectFirst();
    }

    /** Aplica filtros activos al ListView. */
    private void aplicarFiltros() {
        if (listaFiltrada == null) return;
        // Lógica de filtrado por texto, estado, departamento y rol
    }

    /** Muestra detalle del usuario seleccionado en el formulario. */
    private void mostrarDetalle(Persona persona) {
        this.usuarioSeleccionado = persona;
        ignorarCambios = true;
        try {
            profileName.setText(persona.getNombre());
            profileRole.setText(persona.getRol() != null ? persona.getRol().getNombre() : "Sin rol");
            statusComboBox.setValue(persona.getEstado());
            aplicarEstiloEstado(persona.getEstado());
            cargarAvatar(persona.getFotoPath());
            emailField.setText(persona.getUsuario());
            passwordField.clear();
            if (persona.getRol() != null) systemRoleComboBox.setValue(persona.getRol().getNombre());

            // Panel derecho: puesto de trabajo
            if (persona.getPuesto() != null) {
                puestoLabel.setText(persona.getPuesto().getNombre());
                String desc = persona.getPuesto().getDescripcion();
                puestoDescLabel.setText(desc != null && !desc.isBlank() ? desc : "");
            } else {
                puestoLabel.setText("Sin puesto");
                puestoDescLabel.setText("");
            }
        } finally {
            ignorarCambios = false;
            isDirty = false;
            btnSave.setDisable(true);
        }
    }

    /** Carga avatar del usuario o imagen por defecto. */
    private void cargarAvatar(String fotoPath) {
        try {
            if (fotoPath != null && !fotoPath.isBlank()) {
                profileAvatar.setImage(new Image(fotoPath));
            } else {
                profileAvatar.setImage(new Image(getClass().getResourceAsStream("/icons/professional.png")));
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el avatar: " + e.getMessage());
        }
    }

    /** Prepara formulario para nuevo usuario. */
    private void onNuevoUsuario() {
        usuarioSeleccionado = null;
        limpiarFormulario();
    }

    /** Guarda cambios del usuario seleccionado o crea nuevo. */
    private void onGuardarCambios() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona un usuario de la lista para editar sus credenciales.");
            return;
        }

        String usuario = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText().trim() : "";
        String rolNombre = systemRoleComboBox.getValue();
        String estado = statusComboBox.getValue();

        if (usuario.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                    "El nombre de usuario no puede estar vacío.");
            emailField.requestFocus();
            return;
        }

        if (rolNombre == null || rolNombre.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                    "Debes asignar un rol de sistema al usuario.");
            systemRoleComboBox.requestFocus();
            return;
        }

        try {
            usuarioService.actualizarCredenciales(
                    usuarioSeleccionado.getId(), usuario, password, rolNombre, estado);
            // Reset ANTES de recargar (evita que cargarUsuarios dispare el listener con isDirty=true)
            isDirty = false;
            btnSave.setDisable(true);
            cargarUsuarios();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado",
                    "Las credenciales de \"" + usuarioSeleccionado.getNombre() + "\" se han guardado correctamente.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", e.getMessage());
        }
    }

    /** Elimina usuario con confirmación. */
    private void onEliminarUsuario() {
        // Confirmación y eliminación
    }

    /** Limpia el formulario. */
    private void limpiarFormulario() {
        ignorarCambios = true;
        try {
            profileName.setText("");
            profileRole.setText("");
            profileAvatar.setImage(null);
            statusComboBox.setValue(null);
            statusComboBox.setStyle("");
            emailField.clear();
            passwordField.clear();
            systemRoleComboBox.setValue(null);
            puestoLabel.setText("—");
            puestoDescLabel.setText("");
        } finally {
            ignorarCambios = false;
            isDirty = false;
            btnSave.setDisable(true);
        }
    }

    /** Aplica color de fondo según estado del usuario. */
    private void aplicarEstiloEstado(String estado) {
        if (estado == null) return;
        String color = switch (estado) {
            case "activo" -> "-app-success";
            case "inactivo" -> "-app-warning";
            case "baja" -> "-app-error";
            default -> "-app-bg-card";
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