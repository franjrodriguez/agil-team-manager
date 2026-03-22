package com.iesaguadulce.agilteammanager.controller.ui.asignaciones;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.model.asignaciones.AsignacionSugerida;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.service.asignaciones.AsignacionService;
import com.iesaguadulce.agilteammanager.service.asignaciones.MotorAsignacionService;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * ═══════════════════════════════════════════════════════════════
 * MotorAsignacionController — Controlador UI Motor de Asignación
 * ───────────────────────────────────────────────────────────────
 * UBICACIÓN: src/main/java/.../ui/asignaciones/MotorAsignacionController.java
 *
 * FLUJO:
 *   FXML (evento) → este Controller → MotorAsignacionService → BD
 *   Botón Calcular → calcularAsignaciones(tareaId) × tarea
 *   Clic en tarea  → obtenerTopSugerencias(tareaId, 3) → generarExplicacion()
 *   Botón Asignar  → aceptarSugerencia(sugerenciaId)
 * ═══════════════════════════════════════════════════════════════
 */
@Component
public class MotorAsignacionController implements Initializable {

    // ─────────────────────────────────────────────────────────
    // SERVICIOS (se obtienen manualmente desde SpringContext)
    // ─────────────────────────────────────────────────────────

    private TareaService           tareaService;
    private MotorAsignacionService motorAsignacionService;
    private AsignacionService      asignacionService;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Cabecera
    // ─────────────────────────────────────────────────────────

    @FXML private Button btnCalcular;
    @FXML private Label  lblEstadoMotor;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Panel izquierdo (tabla de tareas)
    // ─────────────────────────────────────────────────────────

    @FXML private Label                      lblTotalTareas;
    @FXML private TableView<Tarea>           tareasTable;
    @FXML private TableColumn<Tarea, String> tituloTareaColumn;
    @FXML private TableColumn<Tarea, String> proyectoTareaColumn;
    @FXML private TableColumn<Tarea, String> sprintTareaColumn;
    @FXML private TableColumn<Tarea, String> prioridadTareaColumn;

    // ─────────────────────────────────────────────────────────
    // REFERENCIAS AL FXML — Panel derecho (candidatos)
    // ─────────────────────────────────────────────────────────

    @FXML private Label      lblNombreTareaSeleccionada;
    @FXML private VBox       panelPlaceholder;
    @FXML private VBox       panelCalculando;
    @FXML private ScrollPane scrollCandidatos;
    @FXML private VBox       contenedorCandidatos;

    // ─────────────────────────────────────────────────────────
    // ESTADO INTERNO
    // ─────────────────────────────────────────────────────────

    private Tarea tareaSeleccionada;

    // ─────────────────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaService           = SpringContext.getBean(TareaService.class);
        motorAsignacionService = SpringContext.getBean(MotorAsignacionService.class);
        asignacionService      = SpringContext.getBean(AsignacionService.class);

        configurarColumnasTareas();
        configurarSeleccionTarea();
        cargarTareasPendientes();
    }

    // ─────────────────────────────────────────────────────────
    // CARGA INICIAL DE TAREAS (sin ejecutar el motor)
    // ─────────────────────────────────────────────────────────

    private void cargarTareasPendientes() {
        try {
            List<Tarea> tareas = tareaService.obtenerSinAsignar();
            tareasTable.setItems(FXCollections.observableArrayList(tareas));
            lblTotalTareas.setText(tareas.size() +
                    (tareas.size() == 1 ? " tarea" : " tareas"));
            if (!tareas.isEmpty()) {
                setEstado("Listo para calcular", "#EFF6FF", "#1E40AF", "#BFDBFE");
            }
        } catch (Exception e) {
            mostrarError("Error al cargar tareas: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    // CONFIGURACIÓN DE TABLA
    // ─────────────────────────────────────────────────────────

    private void configurarColumnasTareas() {

        tituloTareaColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitulo()));

        proyectoTareaColumn.setCellValueFactory(data -> {
            try {
                Tarea t = data.getValue();
                return new SimpleStringProperty(
                        t.getProyecto() != null ? t.getProyecto().getNombre() : "—");
            } catch (Exception e) {
                return new SimpleStringProperty("—");
            }
        });

        sprintTareaColumn.setCellValueFactory(data -> {
            try {
                Tarea t = data.getValue();
                return new SimpleStringProperty(
                        t.getSprint() != null ? t.getSprint().getObjetivo() : "—");
            } catch (Exception e) {
                return new SimpleStringProperty("—");
            }
        });

        prioridadTareaColumn.setCellValueFactory(data -> {
            BigDecimal p = data.getValue().getPrioridad();
            String label = p == null ? "—"
                    : p.compareTo(BigDecimal.valueOf(0.7)) >= 0 ? "Alta"
                    : p.compareTo(BigDecimal.valueOf(0.4)) >= 0 ? "Media"
                    : "Baja";
            return new SimpleStringProperty(label);
        });

    }

    private void configurarSeleccionTarea() {
        tareasTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, nueva) -> {
                    if (nueva != null) {
                        tareaSeleccionada = nueva;
                        mostrarCandidatos(nueva);
                    }
                });
    }

    // ─────────────────────────────────────────────────────────
    // ACCIÓN: BOTÓN CALCULAR
    // ─────────────────────────────────────────────────────────

    @FXML
    private void onCalcular() {
        btnCalcular.setDisable(true);
        setEstado("⚙  Calculando...", "#FFF7ED", "#92400E", "#FDE68A");

        try {
            List<Tarea> tareasSinAsignar = tareaService.obtenerSinAsignar();

            if (tareasSinAsignar.isEmpty()) {
                setEstado("✓  Sin tareas pendientes", "#ECFDF5", "#065F46", "#A7F3D0");
                lblTotalTareas.setText("0 tareas");
                tareasTable.setItems(FXCollections.emptyObservableList());
                return;
            }

            // Calcular asignaciones para cada tarea (ignoramos las que no tengan competencias)
            int errores = 0;
            for (Tarea tarea : tareasSinAsignar) {
                try {
                    motorAsignacionService.calcularAsignaciones(tarea.getId());
                } catch (Exception e) {
                    errores++;
                }
            }

            tareasTable.setItems(FXCollections.observableArrayList(tareasSinAsignar));
            lblTotalTareas.setText(tareasSinAsignar.size() +
                    (tareasSinAsignar.size() == 1 ? " tarea" : " tareas"));

            if (errores == 0) {
                setEstado("✓  Listo", "#ECFDF5", "#065F46", "#A7F3D0");
            } else {
                setEstado("⚠  " + errores + " sin competencias", "#FFFBEB", "#92400E", "#FDE68A");
            }

            // Resetear panel derecho
            tareaSeleccionada = null;
            lblNombreTareaSeleccionada.setText("Candidatos sugeridos");
            contenedorCandidatos.getChildren().clear();
            mostrarSolo(true, false, false);

        } catch (Exception e) {
            mostrarError("Error al calcular asignaciones: " + e.getMessage());
            setEstado("✗  Error", "#FEF2F2", "#991B1B", "#FECACA");
        } finally {
            btnCalcular.setDisable(false);
        }
    }

    // ─────────────────────────────────────────────────────────
    // MOSTRAR CANDIDATOS DE UNA TAREA
    // ─────────────────────────────────────────────────────────

    private void mostrarCandidatos(Tarea tarea) {
        lblNombreTareaSeleccionada.setText("Candidatos → " + tarea.getTitulo());
        contenedorCandidatos.getChildren().clear();
        mostrarSolo(false, false, true);   // mostrar "calculando…" mientras se carga

        try {
            List<AsignacionSugerida> top3 =
                    motorAsignacionService.obtenerTopSugerencias(tarea.getId(), 3);

            contenedorCandidatos.getChildren().clear();

            if (top3.isEmpty()) {
                Label lbl = new Label(
                        "No hay candidatos calculados para esta tarea.\n" +
                        "Asegúrate de que la tarea tiene competencias definidas\n" +
                        "y vuelve a pulsar 'Calcular Asignaciones'.");
                lbl.setStyle("-fx-text-fill: -app-text-secondary; -fx-font-size: 13px;");
                lbl.setWrapText(true);
                contenedorCandidatos.getChildren().add(lbl);
                mostrarSolo(false, true, false);
                return;
            }

            String[] medallas = {"🥇", "🥈", "🥉"};
            for (int i = 0; i < top3.size(); i++) {
                AsignacionSugerida sugerencia = top3.get(i);
                String medalla = i < medallas.length ? medallas[i] : "•";
                try {
                    Map<String, Object> explicacion =
                            motorAsignacionService.generarExplicacion(sugerencia.getId());
                    VBox card = buildCandidatoCard(sugerencia.getId(), explicacion, medalla);
                    contenedorCandidatos.getChildren().add(card);
                } catch (Exception ignored) {
                    // Si falla la explicación de un candidato, continuamos con el siguiente
                }
            }

            mostrarSolo(false, true, false);

        } catch (Exception e) {
            mostrarError("Error al cargar candidatos: " + e.getMessage());
            mostrarSolo(true, false, false);
        }
    }

    // ─────────────────────────────────────────────────────────
    // CONSTRUCCIÓN DINÁMICA DE CARD DE CANDIDATO
    // ─────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox buildCandidatoCard(Long sugerenciaId,
                                    Map<String, Object> explicacion,
                                    String medalla) {

        String     nombre   = (String)     explicacion.getOrDefault("persona",       "—");
        BigDecimal scoreB   = (BigDecimal) explicacion.getOrDefault("scoreBase",     BigDecimal.ZERO);
        BigDecimal scoreA   = (BigDecimal) explicacion.getOrDefault("scoreAjustado", BigDecimal.ZERO);
        String     carga    = (String)     explicacion.getOrDefault("cargaActual",   "?");
        String     disponib = (String)     explicacion.getOrDefault("disponibilidad","?");

        List<Map<String, Object>> desglose =
                (List<Map<String, Object>>) explicacion.getOrDefault("desglosePorCompetencia", List.of());

        boolean advertencia = scoreA.compareTo(BigDecimal.valueOf(0.15)) < 0;

        // ── Card root ──────────────────────────────────────────
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: " + (advertencia ? "#FFFBEB" : "white") + ";" +
                "-fx-border-color: "     + (advertencia ? "#FCD34D" : "#E5E7EB") + ";" +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-padding: 16;"
        );

        // ── Cabecera: medalla + nombre + score badge ───────────
        HBox cabecera = new HBox(10);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        Label lblMedalla = new Label(medalla);
        lblMedalla.setStyle("-fx-font-size: 22px;");

        Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;" +
                           "-fx-text-fill: #1F2937;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        int scorePct = scoreA.multiply(BigDecimal.valueOf(100))
                             .setScale(0, RoundingMode.HALF_UP)
                             .intValue();
        Label lblScore = new Label(scorePct + "%");
        lblScore.setStyle(
                "-fx-background-color: " + (advertencia ? "#FEF3C7" : "#ECFDF5")  + ";" +
                "-fx-text-fill: "        + (advertencia ? "#92400E" : "#065F46")   + ";" +
                "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-width: 1;" +
                "-fx-border-color: "     + (advertencia ? "#FCD34D" : "#A7F3D0")   + ";" +
                "-fx-padding: 3 10 3 10; -fx-font-size: 12px; -fx-font-weight: bold;"
        );

        cabecera.getChildren().addAll(lblMedalla, lblNombre, lblScore);

        // ── Fila de stats ──────────────────────────────────────
        HBox stats = new HBox(12);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
                buildStat("Técnico",   scoreB.setScale(1, RoundingMode.HALF_UP).toPlainString()),
                buildStat("Disponib.", disponib),
                buildStat("Carga",     carga)
        );

        // ── Barras de competencias ─────────────────────────────
        VBox barras = new VBox(6);
        for (Map<String, Object> item : desglose) {
            String compNombre = (String) item.getOrDefault("competencia", "—");
            int    nivel      = (int)    item.getOrDefault("nivel", 0);
            String peso       = (String) item.getOrDefault("peso",  "?");

            HBox fila = new HBox(8);
            fila.setAlignment(Pos.CENTER_LEFT);

            Label lblComp = new Label(compNombre + " (" + peso + ")");
            lblComp.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
            lblComp.setPrefWidth(190);

            ProgressBar bar = new ProgressBar(nivel / 10.0);
            bar.setPrefWidth(150);
            bar.setPrefHeight(8);
            bar.setStyle(nivel >= 7 ? "-fx-accent: #10B981;"
                       : nivel >= 4 ? "-fx-accent: #F59E0B;"
                       :              "-fx-accent: #EF4444;");

            Label lblNivel = new Label(nivel + "/10");
            lblNivel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

            fila.getChildren().addAll(lblComp, bar, lblNivel);
            barras.getChildren().add(fila);
        }

        // ── Footer: botón Asignar ──────────────────────────────
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(4, 0, 0, 0));

        Button btnAsignar = new Button("✓  Asignar");
        btnAsignar.getStyleClass().add("primary");
        btnAsignar.setPrefHeight(34);
        if (advertencia) {
            btnAsignar.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white;" +
                                "-fx-background-radius: 6;");
        }
        btnAsignar.setOnAction(e -> onAsignar(sugerenciaId, nombre));
        footer.getChildren().add(btnAsignar);

        card.getChildren().addAll(cabecera, new Separator(), stats, barras, footer);
        return card;
    }

    /** Construye un mini-widget de estadística (título arriba, valor grande abajo). */
    private HBox buildStat(String titulo, String valor) {
        VBox stat = new VBox(2);
        stat.setAlignment(Pos.CENTER);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 10px; -fx-text-fill: #6B7280;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;" +
                          "-fx-text-fill: #1F2937;");

        stat.getChildren().addAll(lblTitulo, lblValor);

        HBox box = new HBox(stat);
        box.setPadding(new Insets(4, 10, 4, 10));
        box.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 6;" +
                     "-fx-border-color: #E5E7EB; -fx-border-radius: 6;" +
                     "-fx-border-width: 1;");
        return box;
    }

    // ─────────────────────────────────────────────────────────
    // CONFIRMAR Y EJECUTAR ASIGNACIÓN
    // ─────────────────────────────────────────────────────────

    private void onAsignar(Long sugerenciaId, String nombrePersona) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Asignación");
        confirm.setHeaderText("Asignar tarea a " + nombrePersona);
        confirm.setContentText(
                "¿Confirmas la asignación?\n" +
                "La tarea pasará a estado 'en progreso' y se actualizará la carga del profesional.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                asignacionService.aceptarSugerencia(sugerenciaId);
                mostrarInfo("Tarea asignada correctamente a " + nombrePersona + ".");
                // La tarea ya no está sin asignar → refrescar lista
                onCalcular();
            } catch (Exception e) {
                mostrarError("Error al asignar: " + e.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // GESTIÓN DE VISIBILIDAD EN EL STACKPANE
    // ─────────────────────────────────────────────────────────

    /**
     * Muestra solo uno de los tres paneles del StackPane derecho.
     *
     * @param placeholder  panel "selecciona una tarea"
     * @param candidatos   ScrollPane con las cards calculadas
     * @param calculando   panel "calculando…"
     */
    private void mostrarSolo(boolean placeholder, boolean candidatos, boolean calculando) {
        panelPlaceholder.setVisible(placeholder);
        panelPlaceholder.setManaged(placeholder);
        scrollCandidatos.setVisible(candidatos);
        scrollCandidatos.setManaged(candidatos);
        panelCalculando.setVisible(calculando);
        panelCalculando.setManaged(calculando);
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS: BADGE DE ESTADO Y ALERTAS
    // ─────────────────────────────────────────────────────────

    private void setEstado(String texto, String bg, String fg, String border) {
        lblEstadoMotor.setText(texto);
        lblEstadoMotor.setStyle(
                "-fx-background-color: " + bg     + ";" +
                "-fx-text-fill: "        + fg     + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: "     + border + ";" +
                "-fx-border-radius: 20; -fx-border-width: 1;" +
                "-fx-padding: 4 14 4 14; -fx-font-size: 12px; -fx-font-weight: bold;"
        );
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
