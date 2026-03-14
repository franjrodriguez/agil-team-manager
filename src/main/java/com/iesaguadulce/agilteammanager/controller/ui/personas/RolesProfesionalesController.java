package com.iesaguadulce.agilteammanager.controller.ui.personas;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.personas.Puesto;
import com.iesaguadulce.agilteammanager.service.personas.PuestoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ═══════════════════════════════════════════════════════════════
 * RoleProfesionalesController — Controlador UI de Puestos de Trabajo
 * ───────────────────────────────────────────────────────────────
 * UBICACIÓN: src/main/java/.../ui/personas/RoleProfesionalesController.java
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
 *   Delega toda la lógica en PuestoService.
 *
 * FLUJO:
 *   FXML (evento) → este Controller → PuestoService → PuestoRepository → BD
 * ═══════════════════════════════════════════════════════════════
 */
@Component  // Spring lo gestiona como bean (necesario para inyección de dependencias)
public class RolesProfesionalesController implements Initializable {

    // ─────────────────────────────────────────────────────────
    // INYECCIÓN DE DEPENDENCIAS
    // Spring inyecta PuestoService automáticamente
    // ─────────────────────────────────────────────────────────
    @Autowired
    private PuestoService puestoService;


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

    // ─────────────────────────────────────────────────────────
    // ESTADO INTERNO DEL CONTROLADOR
    // ─────────────────────────────────────────────────────────

    /** El puesto actualmente seleccionado/editado. null = modo "nuevo" */
    private Puesto puestoSeleccionado = null;

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

        configurarListView();
        System.out.println("(FRANDEV) --> REGRESAMOS DE PREPARAR AL LISTVIEW...");

        cargarTodosLosPuestos();
        System.out.println("(FRANDEV) --> REGRESO AL FUTURO CARGANDO PUESTOS DE CURRELE");

        limpiarFormulario();
        System.out.println("(FRANDEV) --> REGRESA LIMPIO Y SIN DAÑO DE ASEAR EL CULETE DEL FORMULARIO");
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
        if (seleccionado == null) return;  // Clic en zona vacía de la lista → ignoramos

        puestoSeleccionado = seleccionado;
        cargarDatosEnFormulario(puestoSeleccionado);
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
        limpiarFormulario();
        nombreField.requestFocus();  // El cursor va directo al campo nombre
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

            // Actualizamos la lista y seleccionamos el puesto recién guardado
            cargarTodosLosPuestos();
            puestoSeleccionado = guardado;
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
        profileName.setText(puesto.getNombre());
        nombreField.setText(puesto.getNombre());
        descripcionField.setText(puesto.getDescripcion() != null ? puesto.getDescripcion() : "");

        // Mostrar botón borrar solo cuando hay un puesto seleccionado
        btnDelete.setVisible(true);
    }

    /**
     * Limpia el formulario para el estado "sin selección" o "nuevo".
     */
    private void limpiarFormulario() {
        // profileName.setText("Selecciona un puesto de la lista");
        nombreField.clear();
        descripcionField.clear();
        // btnDelete.setVisible(false);  // Ocultamos el botón borrar
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

