package com.iesaguadulce.agilteammanager.controller.ui.personas;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.dto.PersonaActivaDTO;
import com.iesaguadulce.agilteammanager.model.personas.Puesto;
import com.iesaguadulce.agilteammanager.service.dashboard.DashboardService;
import com.iesaguadulce.agilteammanager.service.personas.PuestoService;
import com.iesaguadulce.agilteammanager.util.FotoUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ═══════════════════════════════════════════════════════════════
 * RolesProfesionalesController — Controlador UI de Puestos de Trabajo
 * ───────────────────────────────────────────────────────────────
 *
 * ¿QUÉ HACE?
 *   Gestiona toda la interacción de la pantalla RolesProfesionalesView.fxml:
 *     1. Al arrancar: carga la lista de puestos desde la BD
 *     2. Al escribir en el buscador: filtra la lista en tiempo real
 *     3. Al hacer clic en un puesto: carga sus datos en el formulario
 *     4. Al pulsar "Guardar": valída y guarda el puesto (nuevo o editado)
 *     5. Al pulsar "Borrar": confirma y elimina el puesto seleccionado
 *     6. Al pulsar "Cancelar" o "+ Añadir": limpia el formulario
 *
 * PATRÓN:
 *   Este controlador NO accede directamente a la BD.
 *   Delega toda la lógica en PuestoService ( → Puesto de trabajo).
 *
 * FLUJO:
 *   FXML (evento) → este Controller → PuestoService → PuestoRepository → BD
 *
 * @author FRANDEV
 * @see PuestoService
 */
@Component  // Spring lo gestiona como bean (necesario para inyección de dependencias)
public class RolesProfesionalesController implements Initializable {

    // ─────────────────────────────────────────────────────────
    // INYECCIÓN DE DEPENDENCIAS
    // Spring inyecta PuestoService automáticamente
    // ─────────────────────────────────────────────────────────
    @Autowired
    private PuestoService puestoService;

    private DashboardService dashboardService;


    // ─────────────────────────────────────────────────────────
    // REFERENCIAS A ELEMENTOS DEL FXML
    // @FXML conecta el campo Java con el fx:id del elemento FXML
    // ─────────────────────────────────────────────────────────

    /** Panel izquierdo: buscador de texto */
    @FXML private TextField searchField;

    /** Panel izquierdo: lista de puestos de trabajo */
    @FXML private ListView<Puesto> puestotrabajoListView;

    /** Panel derecho: título dinámico */
    @FXML private Label profileName;

    /** Panel derecho: campo nombre del puesto */
    @FXML private TextField nombreField;

    /** Panel derecho: campo descripción */
    @FXML private TextArea descripcionField;

    /** Botón de borrar (oculto si no hay selección) */
    @FXML private Button btnDelete;

    /** Botón de guardar cambios */
    @FXML private Button btnSave;

    /** Tabla de profesionales del puesto seleccionado */
    @FXML private TableView<PersonaActivaDTO> tblEquipoActivo;

    // ─────────────────────────────────────────────────────────
    // ESTADO INTERNO DEL CONTROLADOR
    // ─────────────────────────────────────────────────────────

    /** El puesto actualmente seleccionado/editado. null = modo "nuevo" */
    private Puesto puestoSeleccionado = null;

    /** true cuando hay un formulario nuevo sin guardar */
    private boolean formularioNuevoSinGuardar = false;

    /** true cuando el usuario ha modificado el formulario de un puesto existente */
    private boolean isDirty = false;

    /** true mientras se cargan datos en el formulario (evita falsos positivos en listeners) */
    private boolean ignorarCambios = false;

    /** Lista observable que alimenta el ListView */
    private ObservableList<Puesto> listaPuestos = FXCollections.observableArrayList();

    // ─────────────────────────────────────────────────────────
    // INICIALIZACIÓN — se ejecuta automáticamente al cargar el FXML
    // ─────────────────────────────────────────────────────────

    /**
     * Equivalente al "constructor de pantalla".
     * JavaFX lo llama justo después de conectar los @FXML.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("(FRANDEV) --> Entramos en RolesProfesionalesController");
        this.puestoService = SpringContext.getBean(PuestoService.class);
        this.dashboardService = SpringContext.getBean(DashboardService.class);

        configurarListView();
        configurarTablaEquipo();
        System.out.println("(FRANDEV) --> REGRESAMOS DE PREPARAR AL LISTVIEW...");

        // isDirty: btnSave desactivado al arrancar
        btnSave.setDisable(true);

        // isDirty: listeners en los dos campos del formulario
        nombreField.textProperty().addListener((obs, oldV, newV) -> marcarCambiado());
        descripcionField.textProperty().addListener((obs, oldV, newV) -> marcarCambiado());

        cargarTodosLosPuestos();
        System.out.println("(FRANDEV) --> REGRESO AL FUTURO CARGANDO PUESTOS DE CURRELE");

        limpiarFormulario();
        System.out.println("(FRANDEV) --> REGRESA LIMPIO Y SIN DAÑO DE ASEAR EL CULETE DEL FORMULARIO");
    }

    /**
     * Marca el formulario como modificado y activa el botón Guardar.
     * Se ignora si estamos cargando datos programáticamente (ignorarCambios=true).
     */
    private void marcarCambiado() {
        if (ignorarCambios) return;
        isDirty = true;
        btnSave.setDisable(false);
    }

    /**
     * Configura el ListView para mostrar solo el nombre del puesto.
     * Por defecto ListView llama a toString() — aquí lo personalizamos.
     */
    private void configurarListView() {
        puestotrabajoListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Puesto puesto, boolean empty) {
                super.updateItem(puesto, empty);
                // Si la celda está vacía o el objeto es null, no mostramos nada
                if (empty || puesto == null) {
                    setText(null);
                } else {
                    setText(puesto.getNombre());
                }
            }
        });

        // Conectamos la lista observable al ListView
        puestotrabajoListView.setItems(listaPuestos);
    }

    // ─────────────────────────────────────────────────────────
    // CARGA DE DATOS
    // ─────────────────────────────────────────────────────────

    /**
     * Carga todos los puestos de la BD y los muestra en el ListView.
     * Se llama al inicializar y después de guardar/borrar.
     */
    private void cargarTodosLosPuestos() {
        List<Puesto> puestos = puestoService.obtenerTodos();
        listaPuestos.setAll(puestos);
    }

    // ─────────────────────────────────────────────────────────
    // EVENTOS DEL FXML (métodos referenciados con onAction="#..." o onKeyReleased="#...")
    // ─────────────────────────────────────────────────────────

    /**
     * Se llama cuando el usuario escribe en el buscador.
     * Filtra el ListView en tiempo real sin ir a la BD (usa la lista en memoria).
     *
     * Referenciado en FXML: onKeyReleased="#filtrarLista"
     */
    @FXML
    private void filtrarLista() {
        String texto = searchField.getText();
        if (texto == null || texto.isBlank()) {
            // Sin texto → mostramos todos
            puestotrabajoListView.setItems(listaPuestos);
        } else {
            // Con texto → filtramos la lista en memoria (sin consulta BD)
            ObservableList<Puesto> filtrados = listaPuestos.filtered(
                    p -> p.getNombre().toLowerCase().contains(texto.toLowerCase().trim())
            );
            puestotrabajoListView.setItems(filtrados);
        }
    }

    /**
     * Se llama cuando el usuario hace clic en un elemento del ListView.
     * Carga los datos del puesto seleccionado en el formulario de la derecha.
     *
     * Referenciado en FXML: onMouseClicked="#seleccionarPuesto"
     */
    @FXML
    private void seleccionarPuesto(MouseEvent event) {
        Puesto seleccionado = puestotrabajoListView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;
        // Si es el mismo puesto, ignoramos el clic (evita recarga innecesaria)
        if (seleccionado.equals(puestoSeleccionado) && !formularioNuevoSinGuardar) return;

        if (formularioNuevoSinGuardar || isDirty) {
            String header = formularioNuevoSinGuardar
                    ? "Hay un nuevo puesto sin guardar."
                    : "Hay cambios sin guardar en '" + puestoSeleccionado.getNombre() + "'.";

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cambios sin guardar");
            confirm.setHeaderText(header);
            confirm.setContentText("¿Descartar los cambios y seleccionar otro puesto?");
            confirm.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    formularioNuevoSinGuardar = false;
                    isDirty = false;
                    btnSave.setDisable(true);
                    puestoSeleccionado = seleccionado;
                    cargarDatosEnFormulario(puestoSeleccionado);
                } else {
                    // Cancelar: volvemos a seleccionar el puesto anterior
                    Puesto anterior = puestoSeleccionado;
                    Platform.runLater(() -> {
                        if (anterior != null) {
                            puestotrabajoListView.getSelectionModel().select(anterior);
                        } else {
                            puestotrabajoListView.getSelectionModel().clearSelection();
                        }
                    });
                }
            });
        } else {
            puestoSeleccionado = seleccionado;
            cargarDatosEnFormulario(puestoSeleccionado);
        }
    }

    /**
     * Se llama al pulsar "+ Añadir Puesto de Trabajo".
     * Limpia el formulario para introducir un puesto nuevo.
     *
     * Referenciado en FXML: onAction="#nuevoPuesto"
     */
    @FXML
    private void nuevoPuesto() {
        puestoSeleccionado = null;
        formularioNuevoSinGuardar = true;
        limpiarFormulario();
        nombreField.requestFocus();
    }

    /**
     * Se llama al pulsar "Guardar Cambios".
     * Si puestoSeleccionado == null → crea uno nuevo.
     * Si puestoSeleccionado != null → actualiza el existente.
     *
     * Referenciado en FXML: onAction="#guardarPuesto"
     */
    @FXML
    private void guardarPuesto() {
        String nombre = nombreField.getText();
        String descripcion = descripcionField.getText();

        // Validación básica en el controlador (la lógica de negocio está en el Service)
        if (nombre == null || nombre.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Campo requerido",
                    "El nombre del puesto no puede estar vacío.");
            nombreField.requestFocus();
            return;
        }

        try {
            // Preparamos el objeto Puesto
            Puesto puesto = (puestoSeleccionado != null)
                    ? puestoSeleccionado   // Editando existente
                    : new Puesto();        // Creando nuevo

            puesto.setNombre(nombre.trim());
            puesto.setDescripcion(descripcion != null ? descripcion.trim() : "");

            // Delegamos el guardado al Service
            Puesto guardado = puestoService.guardar(puesto);

            // Reset de estado ANTES de recargar la lista (evita falsos positivos)
            formularioNuevoSinGuardar = false;
            isDirty = false;
            btnSave.setDisable(true);
            puestoSeleccionado = guardado;

            cargarTodosLosPuestos();
            seleccionarEnLista(guardado);
            cargarDatosEnFormulario(guardado);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Guardado",
                    "Puesto '" + guardado.getNombre() + "' guardado correctamente.");

        } catch (IllegalArgumentException e) {
            // Error de validación del Service (nombre duplicado, etc.)
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        }
    }

    /**
     * Se llama al pulsar "Borrar Puesto".
     * Pide confirmación antes de eliminar.
     *
     * Referenciado en FXML: onAction="#borrarPuesto"
     */
    @FXML
    private void borrarPuesto() {
        if (puestoSeleccionado == null) return;

        // Diálogo de confirmación
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Borrar el puesto '" + puestoSeleccionado.getNombre() + "'?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    puestoService.eliminar(puestoSeleccionado.getId());
                    cargarTodosLosPuestos();
                    limpiarFormulario();
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Eliminado",
                            "Puesto eliminado correctamente.");
                } catch (IllegalStateException e) {
                    // El Service lanza esta excepción si tiene profesionales asignados
                    mostrarAlerta(Alert.AlertType.ERROR, "No se puede eliminar", e.getMessage());
                }
            }
        });
    }

    /**
     * Se llama al pulsar "Cancelar".
     * Limpia el formulario sin guardar nada.
     *
     * Referenciado en FXML: onAction="#cancelar"
     */
    @FXML
    private void cancelar() {
        puestoSeleccionado = null;
        formularioNuevoSinGuardar = false;
        isDirty = false;
        limpiarFormulario();
        puestotrabajoListView.getSelectionModel().clearSelection();
    }

    // ─────────────────────────────────────────────────────────
    // MÉTODOS AUXILIARES PRIVADOS
    // ─────────────────────────────────────────────────────────

    /**
     * Rellena el formulario con los datos del puesto recibido.
     * También actualiza el título y la sección de relacionados.
     */
    private void cargarDatosEnFormulario(Puesto puesto) {
        ignorarCambios = true;
        try {
            profileName.setText(puesto.getNombre());
            nombreField.setText(puesto.getNombre());
            descripcionField.setText(puesto.getDescripcion() != null ? puesto.getDescripcion() : "");
            btnDelete.setVisible(true);
        } finally {
            ignorarCambios = false;
        }

        cargarProfesionalesDelPuesto(puesto);
    }

    /**
     * Limpia el formulario para el estado "sin selección" o "nuevo".
     */
    private void limpiarFormulario() {
        ignorarCambios = true;
        try {
            nombreField.clear();
            descripcionField.clear();
            tblEquipoActivo.getItems().clear();
        } finally {
            ignorarCambios = false;
            isDirty = false;
            btnSave.setDisable(true);
        }
    }

    /**
     * Selecciona visualmente en el ListView el puesto indicado.
     * Útil después de guardar para que la lista refleje la selección.
     */
    private void seleccionarEnLista(Puesto puesto) {
        puestotrabajoListView.getSelectionModel().select(puesto);
        puestotrabajoListView.scrollTo(puesto);
    }

    /**
     * Configura las columnas de la tabla de profesionales.
     * Patrón idéntico al DashboardController.configurarTablas() para tblEquipoActivo.
     */
    @SuppressWarnings("unchecked")
    private void configurarTablaEquipo() {
        // Columna 0: % Carga
        TableColumn<PersonaActivaDTO, String> colCarga =
                (TableColumn<PersonaActivaDTO, String>) tblEquipoActivo.getColumns().get(0);
        colCarga.setCellValueFactory(new PropertyValueFactory<>("cargaFormateada"));

        // Columna 1: nombre del profesional
        TableColumn<PersonaActivaDTO, String> colNombre =
                (TableColumn<PersonaActivaDTO, String>) tblEquipoActivo.getColumns().get(1);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Columna 2: foto
        TableColumn<PersonaActivaDTO, Void> colFoto =
                (TableColumn<PersonaActivaDTO, Void>) tblEquipoActivo.getColumns().get(2);
        colFoto.setCellFactory(col -> new TableCell<>() {
            private final ImageView iv = new ImageView();
            {
                iv.setFitWidth(32);
                iv.setFitHeight(32);
                iv.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                String fotoPath = getTableRow().getItem().getFotoPath();
                Image imagen = FotoUtil.cargarImagen(fotoPath);
                if (imagen != null) {
                    iv.setImage(imagen);
                } else {
                    iv.setImage(new Image(
                            getClass().getResourceAsStream("/icons/professional.png")));
                }
                setGraphic(iv);
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });
    }

    /**
     * Carga en la tabla los profesionales que tienen asignado el puesto recibido.
     */
    private void cargarProfesionalesDelPuesto(Puesto puesto) {
        List<PersonaActivaDTO> personas = dashboardService.obtenerEquipoActivoPorPuesto(puesto.getId());
        tblEquipoActivo.setItems(FXCollections.observableArrayList(personas));
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

