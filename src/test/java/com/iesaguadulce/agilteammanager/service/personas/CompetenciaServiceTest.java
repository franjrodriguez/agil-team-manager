package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.repository.personas.CompetenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CompetenciaService.
 * Usa Mockito para simular el repositorio — no necesita base de datos.
 *
 * @author Francisco José Rodríguez Ruiz
 */
@ExtendWith(MockitoExtension.class)
class CompetenciaServiceTest {

    @Mock
    private CompetenciaRepository competenciaRepository;

    @InjectMocks
    private CompetenciaService competenciaService;

    private Competencia competenciaExistente;

    @BeforeEach
    void setUp() {
        competenciaExistente = new Competencia();
        competenciaExistente.setId(1L);
        competenciaExistente.setNombre("Java");
        competenciaExistente.setDescripcion("Lenguaje Java");
        competenciaExistente.setTipo("Backend");
    }

    // ─────────────────────────────────────────────
    // TEST 5: Crear competencia con nombre duplicado
    // El repositorio dice que "Java" ya existe → debe lanzar excepción
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("crear() — lanza excepción si el nombre ya existe")
    void crear_nombreDuplicado_lanzaExcepcion() {
        when(competenciaRepository.existsByNombre("Java")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> competenciaService.crear("Java", "desc", "Backend"));

        assertEquals("Ya existe una competencia con ese nombre", ex.getMessage());
        verify(competenciaRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 6: Crear competencia con nombre nuevo
    // El repositorio dice que no existe → debe guardar y devolver la competencia
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("crear() — guarda correctamente cuando el nombre es nuevo")
    void crear_nombreNuevo_guardaYDevuelve() {
        when(competenciaRepository.existsByNombre("Python")).thenReturn(false);
        when(competenciaRepository.save(any(Competencia.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Competencia resultado = competenciaService.crear("Python", "Lenguaje Python", "Backend");

        assertNotNull(resultado);
        assertEquals("Python", resultado.getNombre());
        verify(competenciaRepository, times(1)).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 7: Actualizar competencia que no existe en BD
    // El repositorio devuelve Optional.empty() → debe lanzar excepción
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("actualizar() — lanza excepción si la competencia no existe")
    void actualizar_competenciaNoExiste_lanzaExcepcion() {
        when(competenciaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> competenciaService.actualizar(99L, "Java", "desc", "Backend"));

        assertEquals("Competencia no encontrada", ex.getMessage());
    }
}
