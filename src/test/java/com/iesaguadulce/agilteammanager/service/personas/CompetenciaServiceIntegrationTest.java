package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración para CompetenciaService.
 * Arranca el contexto completo de Spring con H2 en memoria.
 * No toca la base de datos PostgreSQL de desarrollo.
 *
 * @author Francisco José Rodríguez Ruiz
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CompetenciaServiceIntegrationTest {

    @Autowired
    private CompetenciaService competenciaService;

    @Test
    @DisplayName("Integración — crear y recuperar competencia desde BD")
    void crearYRecuperarCompetencia() {

        // DADO: creamos una competencia real en H2
        Competencia creada = competenciaService.crear("Docker", "Contenedores", "DevOps");

        // CUANDO: la buscamos por tipo
        List<Competencia> resultado = competenciaService.obtenerPorTipo("DevOps");

        // ENTONCES: debe estar en la lista
        assertNotNull(creada.getId());
        assertTrue(resultado.stream()
                .anyMatch(c -> c.getNombre().equals("Docker")));
    }
}