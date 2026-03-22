package com.iesaguadulce.agilteammanager.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración de la capa de persistencia JPA.
 * Escanea repositorios, entidades y habilita gestión transaccional.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.iesaguadulce.agilteammanager.repository")
@EntityScan(basePackages = "com.iesaguadulce.agilteammanager.model")
@EnableTransactionManagement
public class DatabaseConfig {
}
