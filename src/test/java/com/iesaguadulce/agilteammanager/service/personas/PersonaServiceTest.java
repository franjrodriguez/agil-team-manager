package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
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
 * Tests unitarios para PersonaService.
 * Usa Mockito para simular el repositorio — no necesita base de datos.
 *
 * @author Francisco José Rodríguez Ruiz
 */
@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaService personaService;

    private Persona personaExistente;

    @BeforeEach
    void setUp() {
        personaExistente = new Persona();
        personaExistente.setId(1L);
        personaExistente.setNombre("Ana García");
        personaExistente.setEstado("activo");
    }

    // ─────────────────────────────────────────────
    // TEST 8: guardar() devuelve la persona guardada
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("guardar() — devuelve la persona tras persistirla")
    void guardar_devuelvePersonaGuardada() {
        when(personaRepository.save(personaExistente)).thenReturn(personaExistente);

        Persona resultado = personaService.guardar(personaExistente);

        assertNotNull(resultado);
        assertEquals("Ana García", resultado.getNombre());
        verify(personaRepository, times(1)).save(personaExistente);
    }

    // ─────────────────────────────────────────────
    // TEST 9: actualizarFoto() lanza excepción si la persona no existe
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("actualizarFoto() — lanza excepción si la persona no existe")
    void actualizarFoto_personaNoExiste_lanzaExcepcion() {
        when(personaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> personaService.actualizarFoto(99L, "/fotos/foto.jpg"));

        assertEquals("Persona no encontrada", ex.getMessage());
        verify(personaRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 10: contarActivas() devuelve el número correcto
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("contarActivas() — devuelve el número de personas activas")
    void contarActivas_devuelveNumeroCorrect() {
        when(personaRepository.countByEstado("activo")).thenReturn(5L);

        long resultado = personaService.contarActivas();

        assertEquals(5L, resultado);
    }
}
