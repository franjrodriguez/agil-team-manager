package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Controller de tarjeta widget reutilizable para dashboards.
 *
 * <p>Muestra un valor numérico, título e icono. Utilizado por
 * {@link DashboardController} para los KPIs de cabecera.</p>
 *
 * @see DashboardController
 */
public class WidgetCardController {
    @FXML private Label lblValue;
    @FXML private Label lblTitle;
    @FXML private ImageView imgIcon;

    /**
     * Configura los datos visuales del widget.
     *
     * @param imagePath ruta del icono en resources
     * @param title texto descriptivo
     * @param value valor numérico a mostrar
     */
    public void setData(String imagePath, String title, String value) {
        lblTitle.setText(title);
        lblValue.setText(value);
        Image image = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        imgIcon.setImage(image);
    }
}
