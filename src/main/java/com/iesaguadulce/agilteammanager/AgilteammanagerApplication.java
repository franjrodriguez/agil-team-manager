package com.iesaguadulce.agilteammanager;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Punto de entrada de la aplicación.
 * Lanza la interfaz JavaFX mediante {@link JavaFxApplication}.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@SpringBootApplication
public class AgilteammanagerApplication {

    /**
     * Método principal. Inicia Spring Boot y lanza JavaFX.
     *
     * @param args argumentos de línea de comandos
     */
	public static void main(String[] args) {

        System.out.println("(FRANDEV)---> CARGANDO JAVAFXAPPLICATION");
        Application.launch(JavaFxApplication.class, args);
	}
}
