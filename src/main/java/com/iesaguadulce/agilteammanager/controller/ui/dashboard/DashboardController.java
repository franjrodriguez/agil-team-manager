package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import com.iesaguadulce.agilteammanager.dto.DashboardKPIs;
import com.iesaguadulce.agilteammanager.service.dashboard.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controlador JavaFX del Dashboard principal
 * Gestiona los 4 widgets con KPIs
 */
@Component
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // ===== REFERENCIAS A LOS 4 WIDGETS IMPORTADOS =====
    @FXML private VBox widgetProyectos;
    @FXML private VBox widgetTareas;
    @FXML private VBox widgetPersonas;
    @FXML private VBox widgetCarga;

    // ----- Relacion de asignaciones de la imagen a su Widget --
    private String imgWidgetProyectos = "/icons/project.png";
    private String imgWidgetTareas = "/icons/task.png";
    private String imgWidgetPersonas = "/icons/professional.png";
    private String imgWidgetCarga = "/icons/charge.png";

    /**
     * Método que se ejecuta automáticamente al cargar el FXML
     */
    @FXML
    public void initialize() {
        cargarKPIs();
    }

    /**
     * Obtiene los datos del service y actualiza los widgets
     */
    private void cargarKPIs() {
        try {
            // Obtener datos desde Spring Boot Service
            DashboardKPIs kpis = dashboardService.obtenerKPIs();

            // Actualizar cada widget usando lookup para acceder a los Labels internos
            actualizarWidget(widgetProyectos,
                    "Proyectos Activos",
                    String.valueOf(kpis.getProyectosActivos()),
                    imgWidgetProyectos
            );

            actualizarWidget(widgetTareas,
                    "Tareas Pendientes",
                    String.valueOf(kpis.getTareasPendientes()),
                    imgWidgetTareas
            );

            actualizarWidget(widgetPersonas,
                    "Personas Activas",
                    String.valueOf(kpis.getPersonasActivas()),
                    imgWidgetPersonas
            );

            actualizarWidget(widgetCarga,
                    "Carga Promedio",
                    String.format("%.0f%%", kpis.getCargaPromedioEquipo() * 100),
                    imgWidgetCarga
            );

        } catch (Exception e) {
            System.err.println("Error cargando KPIs del dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza los Labels dentro de un widget usando lookup
     *
     * @param widget VBox contenedor del widget
     * @param titulo Texto para el título
     * @param valor Valor numérico a mostrar
     * @param rutaIcono Cadena que define la imagen que mostrará
     */
    private void actualizarWidget(VBox widget, String titulo, String valor, String rutaIcono) {
        if (widget == null) {
            System.err.println("⚠️ Widget es null, no se puede actualizar");
            return;
        }

        // Buscar los Labels dentro del VBox usando sus fx:id
        // Dado que se usa una plantilla del widget a mostrar en el Dashboard de Inicio,
        // y cada inserción del fxml tiene asignado un id, los objetos internos a cada uno solo se pueden
        // acceder usando lookup.
        Label lblTitulo = (Label) widget.lookup("#lblTitle");
        Label lblValor = (Label) widget.lookup("#lblValue");
        ImageView imgIcono = (ImageView) widget.lookup("#imgIcon");

        // 🎨 Actualizar ICONO
        if (imgIcono != null && rutaIcono != null) {
            try {
                Image imagen = new Image(getClass().getResourceAsStream(rutaIcono));
                imgIcono.setImage(imagen);
            } catch (Exception e) {
                System.err.println("No se ha podido cargar la imagen: " + rutaIcono);
                e.printStackTrace();
            }
        } else if (imgIcono == null) {
            System.err.println("No se encontró imgIcono en el widget");
        }

        // Actualizar textos si los Labels existen
        if (lblTitulo != null) {
            lblTitulo.setText(titulo);
        } else {
            System.err.println("No se encontró lblTitle en el widget");
        }

        if (lblValor != null) {
            lblValor.setText(valor);
        } else {
            System.err.println("No se encontró lblValue en el widget");
        }

        if (lblValor != null) {
            lblValor.setText(valor);
        } else {
            System.err.println("No se encontró lblValue en el widget");
        }
    }

    /**
     * Método público para refrescar los datos (opcional, para futuro)
     */
    public void refrescarDatos() {
        cargarKPIs();
    }
}
