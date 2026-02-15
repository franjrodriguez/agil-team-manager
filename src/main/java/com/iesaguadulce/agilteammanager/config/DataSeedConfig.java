package com.iesaguadulce.agilteammanager.config;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.personas.Puesto;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PuestoRepository;
import com.iesaguadulce.agilteammanager.repository.seguridad.RolSistemaRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeedConfig {

    @Bean
    public CommandLineRunner loadInitialData(
            RolSistemaRepository rolRepo,
            PuestoRepository puestoRepo,
            PersonaRepository personaRepo) {

        return args -> {

            if (rolRepo.count() > 0) {
                System.out.println("✅ Datos ya existentes, omitiendo seed");
                return;
            }

            System.out.println("📦 Cargando datos iniciales...");

            // Crear roles
            RolSistema rolAdmin = new RolSistema();
            rolAdmin.setNombre("ADMIN");
            rolAdmin.setDescripcion("Administrador del sistema");
            rolRepo.save(rolAdmin);

            // Crear puesto
            Puesto puestoAdmin = new Puesto();
            puestoAdmin.setNombre("Administrador");
            puestoAdmin.setDescripcion("Administrador del sistema");
            puestoRepo.save(puestoAdmin);

            // Crear usuario admin (password: "admin123" con jBCrypt)
            Persona admin = new Persona();
            admin.setUsuario("admin");
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt())); // ← jBCrypt
            admin.setNombre("Administrador del Sistema");
            admin.setEmail("admin@agilteam.com");
            admin.setEstado("activo");
            admin.setPuesto(puestoAdmin);
            admin.setRol(rolAdmin);
            personaRepo.save(admin);

            System.out.println("✅ Datos iniciales cargados");
            System.out.println("   Usuario: admin / Password: admin123");
        };
    }
}
