package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class AboutController {

    @FXML private Button btnCerrar;

    @FXML
    public void initialize() {
        btnCerrar.setOnAction(e -> {
            Stage stage = (Stage) btnCerrar.getScene().getWindow();
            stage.close();
        });
    }
}