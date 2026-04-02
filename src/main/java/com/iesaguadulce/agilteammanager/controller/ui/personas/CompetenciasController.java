package com.iesaguadulce.agilteammanager.controller.ui.personas;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetencia;
import com.iesaguadulce.agilteammanager.service.personas.CompetenciaService;
import com.iesaguadulce.agilteammanager.service.personas.PersonaCompetenciaService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
 * Controller de la vista de gestión de competencias técnicas.
 *
 * <p>Implementa CRUD completo para competencias con interfaz de dos paneles:
 * lista filtrable a la izquierda y formulario de edición a la derecha.</p>
 *
 * @author FRANDEV
 * @see CompetenciaService
 */
@Component
public class CompetenciasController implements Initializable {

    @Autowired
    private CompetenciaService competenciaService;

    /** Panel izquierdo: buscador de texto */
    @FXML private TextField searchField;

    /** Panel izquierdo: lista de competencias */
    @FXML private ListView<Competencia> competenciasListView;

    /** Panel derecho: título dinámico */
    @FXML private Label profileName;

    /** Panel derecho: campo nombre */
    @FXML private TextField nombreField;

    /** Panel derecho: campo descripción */
    @FXML private TextArea descripcionField;

    /** Panel derecho: selector de tipo (Lenguaje, Framework, BD, DevOps, Testing, Cloud) */
    @FXML private ComboBox<String> tipoComboBox;

    /** Botón de borrar (visible solo con selección) */
    @FXML private Button btnDelete;

    /** Tabla de profesionales con la competencia seleccionada */
    @FXML private TableView<PersonaCompetencia>           tblEquipoActivo;
    @FXML private TableColumn<PersonaCompetencia, String> colNombre;
    @FXML private TableColumn<PersonaCompetencia, String> colNivel;

    /** Competencia actualmente seleccionada. null = modo "nueva" */
    private Competencia competenciaSeleccionada = null;

    /** true cuando hay una nueva competencia sin guardar */
    private boolean formularioNuevoSinGuardar = false;

    /** Lista observable que alimenta el ListView */
    private ObservableList<Competencia> listaCompetencias = FXCollections.observableArrayList();

    private PersonaCompetenciaService personaCompetenciaService;

    /** Inicializa el controller cargando datos y configurando componentes. */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("(FRANDEV) --> Entramos en CompetenciasController");
        this.competenciaService          = SpringContext.getBean(CompetenciaService.class);
        this.personaCompetenciaService   = SpringContext.getBean(PersonaCompetenciaService.class);

        configurarComboBoxTipo();
        configurarListView();
        configurarTablaEquipo();
        cargarTodasLasCompetencias();
        limpiarFormulario();

        System.out.println("(FRANDEV) --> CompetenciasController inicializado correctamente");
    }

    /**
     * Carga los tipos fijos en el ComboBox.
     * Los tipos están definidos en el modelo: Lenguaje, Framework, BD, DevOps, Testing, Cloud
     */
    private void configurarComboBoxTipo() {
        ObservableList<String> tipos = FXCollections.observableArrayList(
                "Lenguaje", "Framework", "BD", "DevOps", "Testing", "Cloud"
        );
        tipoComboBox.setItems(tipos);
    }

    /**
     * Configura el ListView para mostrar nombre y tipo de cada competencia.
     */
    private void configurarListView() {
        competenciasListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Competencia competencia, boolean empty) {
                super.updateItem(competencia, empty);
                if (empty || competencia == null) {
                    setText(null);
                } else {
                    // Mostramos: "Java [Lenguaje]"
                    String tipo = competencia.getTipo() != null ? " [" + competencia.getTipo() + "]" : "";
                    setText(competencia.getNombre() + tipo);
                }
            }
        });

        competenciasListView.setItems(listaCompetencias);
    }

    /** Carga todas las competencias desde el servicio. */
    private void cargarTodasLasCompetencias() {
        List<Competencia> competencias = competenciaService.obtenerTodas();
        listaCompetencias.setAll(competencias);
    }

    /**
     * Filtra el ListView en tiempo real al escribir en el buscador.
     * Referenciado en FXML: onKeyReleased="#filtrarLista"
     */
    @FXML
    private void filtrarLista() {
        String texto = searchField.getText();
        if (texto == null || texto.isBlank()) {
            competenciasListView.setItems(listaCompetencias);
        } else {
            ObservableList<Competencia> filtradas = listaCompetencias.filtered(
                    c -> c.getNombre().toLowerCase().contains(texto.toLowerCase().trim())
            );
            competenciasListView.setItems(filtradas);
        }
    }

    /**
     * Carga la competencia seleccionada en el formulario.
     * @param event evento de clic del ratón
     */
    @FXML
    private void seleccionarCompetencia(MouseEvent event) {
        Competencia seleccionada = competenciasListView.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        if (formularioNuevoSinGuardar) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cambios sin guardar");
            confirm.setHeaderText("Hay una nueva competencia sin guardar.");
            confirm.setContentText("¿Descartar los cambios y seleccionar otra competencia?");
            confirm.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    formularioNuevoSinGuardar = false;
                    competenciaSeleccionada = seleccionada;
                    cargarDatosEnFormulario(competenciaSeleccionada);
                } else {
                    Platform.runLater(() ->
                            competenciasListView.getSelectionModel().clearSelection());
                }
            });
        } else {
            competenciaSeleccionada = seleccionada;
            cargarDatosEnFormulario(competenciaSeleccionada);
        }
    }

    /**
     * Limpia el formulario para introducir una competencia nueva.
     * Referenciado en FXML: onAction="#nuevaCompetencia"
     */
    @FXML
    private void nuevaCompetencia() {
        competenciaSeleccionada = null;
        formularioNuevoSinGuardar = true;
        limpiarFormulario();
        nombreField.requestFocus();
    }

    /**
     * Guarda la competencia (nueva o editada).
     * Referenciado en FXML: onAction="#guardarCompetencia"
     */
    @FXML
    private void guardarCompetencia() {
        System.out.println("(FRANDEV) --> ACABO DE LLEGAR... GUARDANAO");
        String nombre = nombreField.getText();
        String descripcion = descripcionField.getText();
        String tipo = tipoComboBox.getValue();

        System.out.println("(FRANDEV) --> DATOS QUE GUARDO: " + nombre + " | " + descripcion + " | " + tipo);

        // Validación básica
        if (nombre == null || nombre.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "El nombre es obligatorio.");
            nombreField.requestFocus();
            return;
        }

        System.out.println("(FRANDEV) --> vamos con el intento...");
        try {
            Competencia guardada;

            if (competenciaSeleccionada == null) {
                // CREAR nueva
                guardada = competenciaService.crear(
                        nombre.trim(),
                        descripcion != null ? descripcion.trim() : "",
                        tipo
                );
            } else {
                // ACTUALIZAR existente
                guardada = competenciaService.actualizar(
                        competenciaSeleccionada.getId(),
                        nombre.trim(),
                        descripcion != null ? descripcion.trim() : "",
                        tipo
                );
            }

            formularioNuevoSinGuardar = false;
            cargarTodasLasCompetencias();
            competenciaSeleccionada = guardada;
            // seleccionarEnLista(guardada);
            cargarDatosEnFormulario(guardada);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado",
                    "Competencia '" + guardada.getNombre() + "' guardada correctamente.");

        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Elimina la competencia seleccionada (con confirmación).
     * Referenciado en FXML: onAction="#borrarCompetencia"
     */
    @FXML
    private void borrarCompetencia() {
        if (competenciaSeleccionada == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Borrar '" + competenciaSeleccionada.getNombre() + "'?");
        confirm.setContentText("Solo se puede borrar si no está asignada a ningún profesional o tarea.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    competenciaService.eliminar(competenciaSeleccionada.getId());
                    cargarTodasLasCompetencias();
                    limpiarFormulario();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Competencia eliminada correctamente.");
                } catch (RuntimeException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "No se puede eliminar", e.getMessage());
                }
            }
        });
    }

    /**
     * Cancela la edición y limpia el formulario.
     * Referenciado en FXML: onAction="#cancelar"
     */
    @FXML
    private void cancelar() {
        competenciaSeleccionada = null;
        formularioNuevoSinGuardar = false;
        limpiarFormulario();
        competenciasListView.getSelectionModel().clearSelection();
    }

    // ─────────────────────────────────────────────────────────
    // MÉTODOS AUXILIARES PRIVADOS
    // ─────────────────────────────────────────────────────────

    /** Configura las columnas de la tabla de profesionales. */
    private void configurarTablaEquipo() {
        colNombre.setCellValueFactory(data -> {
            PersonaCompetencia pc = data.getValue();
            String nombre = (pc.getPersona() != null) ? pc.getPersona().getNombre() : "—";
            return new SimpleStringProperty(nombre);
        });
        colNivel.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNivelActual() != null
                                ? data.getValue().getNivelActual().toString()
                                : "—"));
        colNivel.setStyle("-fx-alignment: CENTER;");
    }

    /** Carga en la tabla los profesionales que tienen la competencia indicada. */
    private void cargarProfesionalesConCompetencia(Long competenciaId) {
        try {
            List<PersonaCompetencia> lista =
                    personaCompetenciaService.obtenerPersonasConCompetencia(competenciaId);
            tblEquipoActivo.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            tblEquipoActivo.setItems(FXCollections.emptyObservableList());
            System.err.println("Error cargando profesionales de la competencia: " + e.getMessage());
        }
    }

    private void cargarDatosEnFormulario(Competencia competencia) {
        profileName.setText(competencia.getNombre());
        nombreField.setText(competencia.getNombre());
        descripcionField.setText(competencia.getDescripcion() != null ? competencia.getDescripcion() : "");
        System.out.println("(FRANDEV) --> TIPO QUE LLEGA: " + competencia.getTipo());
        tipoComboBox.setValue(competencia.getTipo());
        btnDelete.setVisible(true);
        cargarProfesionalesConCompetencia(competencia.getId());
    }

    private void limpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        tipoComboBox.setValue(null);
        btnDelete.setVisible(false);
        if (tblEquipoActivo != null) tblEquipoActivo.setItems(FXCollections.emptyObservableList());
    }

    private void seleccionarEnLista(Competencia competencia) {
        competenciasListView.getSelectionModel().select(competencia);
        competenciasListView.scrollTo(competencia);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert