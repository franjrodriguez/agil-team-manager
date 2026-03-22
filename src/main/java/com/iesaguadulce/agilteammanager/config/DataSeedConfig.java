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

/**
 * Configuración de datos iniciales de la aplicación.
 * Crea roles, puestos y usuario admin al arrancar si la BD está vacía.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@Configuration
public class DataSeedConfig {

    /**
     * Carga datos iniciales: rol ADMIN, puesto Administrador y usuario admin/admin123.
     * Solo ejecuta si no existen datos previos (rolRepo.count() == 0).
     *
     * @param rolRepo repositorio de roles
     * @param puestoRepo repositorio de puestos
     * @param personaRepo repositorio de personas
     * @return CommandLineRunner con la lógica de seed
     */
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

            // Crear usuario admin (password: "1234" con jBCrypt)
            Persona admin = new Persona();
            admin.setUsuario("admin");
            admin.setPassword(BCrypt.hashpw("1234", BCrypt.gensalt())); // ← jBCrypt
            admin.setNombre("Administrador del Sistema");
            admin.setEmail("admin@agilteam.com");
            admin.setEstado("activo");
            admin.setPuesto(puestoAdmin);
            admin.setRol(rolAdmin);
            personaRepo.save(admin);

            System.out.println("✅ Datos iniciales cargados");
            System.out.println("   Usuario: admin / Password: 1234");
        };
    }
}
