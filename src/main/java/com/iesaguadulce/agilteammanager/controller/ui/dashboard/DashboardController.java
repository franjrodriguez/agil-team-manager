package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.dto.DashboardKPIs;
import com.iesaguadulce.agilteammanager.dto.PersonaActivaDTO;
import com.iesaguadulce.agilteammanager.dto.RendimientoDiaDTO;
import com.iesaguadulce.agilteammanager.dto.TareaPendienteDTO;
import com.iesaguadulce.agilteammanager.service.dashboard.DashboardService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardController {

    // SpringContext.getBean() NO puede llamarse aquí (el contexto aún no existe).
    // Se asigna en initialize(), que JavaFX invoca cuando el FXML ya está cargado.
    private DashboardService dashboardService;

    // ===== WIDGETS (cabecera) =====
    @FXML private VBox widgetProyectos;
    @FXML private VBox widgetTareas;
    @FXML private VBox widgetPersonas;
    @FXML private VBox widgetCarga;

    // ===== GRÁFICA =====
    @FXML private BarChart<String, Number> barChartRendimiento;

    // ===== TABLAS =====
    @FXML private TableView<TareaPendienteDTO> tblTareasPendientes;
    @FXML private TableView<PersonaActivaDTO>  tblEquipoActivo;

    // Rutas de iconos
    private static final String IMG_PROYECTOS = "/icons/project.png";
    private static final String IMG_TAREAS    = "/icons/task.png";
    private static final String IMG_PERSONAS  = "/icons/professional.png";
    private static final String IMG_CARGA     = "/icons/charge.png";

    @FXML
    public void initialize() {
        // Obtener el service aquí, no en el campo — el contexto ya está listo
        dashboardService = SpringContext.getBean(DashboardService.class);

        cargarKPIs();
        cargarGrafica();
        configurarTablas();
        cargarTablas();
    }

    // ─── KPIs ──────────────────────────────────────────────────────────────────

    private void cargarKPIs() {
        try {
            DashboardKPIs kpis = dashboardService.obtenerKPIs();
            actualizarWidget(widgetProyectos, "Proyectos Activos",
                    String.valueOf(kpis.getProyectosActivos()), IMG_PROYECTOS);
            actualizarWidget(widgetTareas, "Tareas Pendientes",
                    String.valueOf(kpis.getTareasPendientes()), IMG_TAREAS);
            actualizarWidget(widgetPersonas, "Personas Activas",
                    String.valueOf(kpis.getPersonasActivas()), IMG_PERSONAS);
            actualizarWidget(widgetCarga, "Carga Promedio",
                    String.format("%.0f%%", kpis.getCargaPromedioEquipo() * 100), IMG_CARGA);
        } catch (Exception e) {
            System.err.println("Error cargando KPIs: " + e.getMessage());
        }
    }

    private void actualizarWidget(VBox widget, String titulo, String valor, String rutaIcono) {
        if (widget == null) {
            System.err.println("Widget null: " + titulo);
            return;
        }
        Label lblTitulo    = (Label)     widget.lookup("#lblTitle");
        Label lblValor     = (Label)     widget.lookup("#lblValue");
        ImageView imgIcono = (ImageView) widget.lookup("#imgIcon");

        if (lblTitulo != null) lblTitulo.setText(titulo);
        if (lblValor  != null) lblValor.setText(valor);
        if (imgIcono  != null && rutaIcono != null) {
            try {
                imgIcono.setImage(new Image(getClass().getResourceAsStream(rutaIcono)));
            } catch (Exception e) {
                System.err.println("No se pudo cargar icono: " + rutaIcono);
            }
        }
    }

    // ─── GRÁFICA ───────────────────────────────────────────────────────────────

    private void cargarGrafica() {
        if (barChartRendimiento == null) {
            System.err.println("barChartRendimiento es null — revisa fx:controller en el FXML");
            return;
        }
        try {
            List<RendimientoDiaDTO> datos = dashboardService.obtenerRendimientoUltimos7Dias();
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Tareas completadas");
            for (RendimientoDiaDTO dia : datos) {
                serie.getData().add(
                        new XYChart.Data<>(dia.getDiaSemana(), dia.getTareasCompletadas()));
            }
            barChartRendimiento.getData().clear();
            barChartRendimiento.getData().add(serie);
        } catch (Exception e) {
            System.err.println("Error cargando gráfica: " + e.getMessage());
        }
    }

    // ─── TABLAS ────────────────────────────────────────────────────────────────

    // Las columnas no tienen fx:id en el FXML, se configuran por índice
    @SuppressWarnings("unchecked")
    private void configurarTablas() {
        if (tblTareasPendientes != null && tblTareasPendientes.getColumns().size() >= 2) {
            TableColumn<TareaPendienteDTO, String> colTarea =
                    (TableColumn<TareaPendienteDTO, String>) tblTareasPendientes.getColumns().get(0);
            TableColumn<TareaPendienteDTO, String> colSprint =
                    (TableColumn<TareaPendienteDTO, String>) tblTareasPendientes.getColumns().get(1);
            colTarea.setText("Tarea");
            colTarea.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
            colSprint.setText("Sprint");
            colSprint.setCellValueFactory(new PropertyValueFactory<>("nombreSprint"));
        }

        if (tblEquipoActivo != null && tblEquipoActivo.getColumns().size() >= 2) {
            TableColumn<PersonaActivaDTO, String> colNombre =
                    (TableColumn<PersonaActivaDTO, String>) tblEquipoActivo.getColumns().get(0);
            TableColumn<PersonaActivaDTO, String> colCarga =
                    (TableColumn<PersonaActivaDTO, String>) tblEquipoActivo.getColumns().get(1);
            colNombre.setText("Profesional");
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colCarga.setText("Carga");
            colCarga.setCellValueFactory(new PropertyValueFactory<>("cargaFormateada"));
        }
    }

    private void cargarTablas() {
        if (tblTareasPendientes != null) {
            try {
                tblTareasPendientes.setItems(FXCollections.observableArrayList(
                        dashboardService.obtenerTareasPendientes()));
            } catch (Exception e) {
                System.err.println("Error cargando tareas pendientes: " + e.getMessage());
            }
        }
        if (tblEquipoActivo != null) {
            try {
                tblEquipoActivo.setItems(FXCollections.observableArrayList(
                        dashboardService.obtenerEquipoActivo()));
            } catch (Exception e) {
                System.err.println("Error cargando equipo activo: " + e.getMessage());
            }
        }
    }

    public void refrescarDatos() {
        cargarKPIs();
        cargarGrafica();
        cargarTablas();
    }
}
