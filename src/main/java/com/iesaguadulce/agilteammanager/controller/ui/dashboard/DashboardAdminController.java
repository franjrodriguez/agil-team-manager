package com.iesaguadulce.agilteammanager.controller.ui.dashboard;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import com.iesaguadulce.agilteammanager.service.personas.PersonaService;
import com.iesaguadulce.agilteammanager.service.seguridad.RolSistemaService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class DashboardAdminController implements Initializable {

    // --- Fila 1: KPIs de usuarios y roles ---
    @FXML private Label valueUsuarios;
    @FXML private Label valueUsuariosActivos;
    @FXML private Label valueUsuariosNoActivos;
    @FXML private Label valueRoles;

    // --- Fila 2: Info del sistema ---
    @FXML private Label valueVersion;
    @FXML private Label valueHoraUltimoLogin;
    @FXML private Label valueUltimaConexionFallida;
    @FXML private Label valueErrorConexion;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private RolSistemaService rolSistemaService;

    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        personaService    = SpringContext.getBean(PersonaService.class);
        rolSistemaService = SpringContext.getBean(RolSistemaService.class);
        mainController    = SpringContext.getBean(MainController.class);

        cargarKpisUsuarios();
        cargarInfoSistema();
    }

    private void cargarKpisUsuarios() {
        long total    = personaService.contarTotal();
        long activos  = personaService.contarActivas();
        long inactivos = total - activos;
        long roles    = rolSistemaService.contarTotal();

        valueUsuarios.setText(String.valueOf(total));
        valueUsuariosActivos.setText(String.valueOf(activos));
        valueUsuariosNoActivos.setText(String.valueOf(inactivos));
        valueRoles.setText(String.valueOf(roles));
    }

    private void cargarInfoSistema() {
        valueVersion.setText("1.0");

        String horaActual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        valueHoraUltimoLogin.setText(horaActual);

        // Sin tabla de auditoría — valores neutros
        valueUltimaConexionFallida.setText("--:--");
        valueErrorConexion.setText("0");
    }

    // --- Handlers de navegación ---
    @FXML
    private void mostrarUsuarios() {
        mainController.mostrarUsuarios();
    }

    @FXML
    private void mostrarRoles() {
        mainController.mostrarRolesPermisos();
    }

    @FXML
    private void mostrarConfig() {
        mainController.mostrarConfiguracion();
    }
}