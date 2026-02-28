package com.iesaguadulce.agilteammanager;

import com.iesaguadulce.agilteammanager.config.SpringContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        // Arrancar Spring Boot ANTES de la UI
        System.out.println("Iniciando contexto Spring Boot...");

        this.springContext = new SpringApplicationBuilder()
                .sources(AgilteammanagerApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));

        // Guardar contexto para uso en los controladores
        SpringContext.setApplicationContext(this.springContext);

        System.out.println("Spring Boot listo (" +
                springContext.getBeanDefinitionCount() + " beans cargados)");
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("(FRANDEV)---> Iniciando interfaz JavaFX...");

        //
        // Se procede con la carga de la ventana de Logueo...
        //
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login/LoginView.fxml"));
        Parent root = loader.load();
        System.out.println("(FRANDEV)---> SE HA CARGADO PANTALLA DE LOGUEO");

        // Configurar la ventana de logueo
        Scene scene = new Scene(root);
        primaryStage.setTitle("AgilTeam Manager - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        System.out.println("Ventana Login mostrada");
    }

    @Override
    public void stop() {
        // Cerrar contexto Spring al salir
        System.out.println("(FRANDEV)---> Cerrando aplicación... LA PREGUNTA ES: SE CIERRA FX CON LOGIN?");
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }
}
