package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class WidgetCardController {
    @FXML private Label lblValue;
    @FXML private Label lblTitle;
    @FXML private ImageView imgIcon;

    public void setData(String imagePath, String title, String value) {
        lblTitle.setText(title);
        lblValue.setText(value);
        Image image = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        imgIcon.setImage(image);
    }
}
