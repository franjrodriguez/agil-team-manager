package com.iesaguadulce.agilteammanager.util;

import org.mindrot.jbcrypt.BCrypt;

public class GeneradorPassword {
    public static void main(String[] args) {
        String[] usuarios = {"admin", "dbadmin", "backend1", "backend2"};
        String passwordParaTodos = "1234";

        for (String usuario : usuarios) {
            String hash = BCrypt.hashpw(passwordParaTodos, BCrypt.gensalt());
            System.out.println("Usuario: " + usuario);
            System.out.println("Hash: " + hash);
            System.out.println("UPDATE personas SET password='" + hash + "' WHERE usuario='" + usuario + "';");
            System.out.println("---");
        }
    }
}