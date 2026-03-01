package com.iesaguadulce.agilteammanager;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class AgilteammanagerApplication {

	public static void main(String[] args) {

        System.out.println("(FRANDEV)---> CARGANDO JAVAFXAPPLICATION");
        Application.launch(JavaFxApplication.class, args);
	}
}
