package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetencia;
import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetenciaId;
import com.iesaguadulce.agilteammanager.repository.personas.CompetenciaRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaCompetenciaRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Asigna o actualiza el nivel de una competencia (0-100).
 * Crea registro histórico con timestamp.
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PersonaCompetenciaService {

    private final PersonaCompetenciaRepository personaCompetenciaRepository;
    private final PersonaRepository personaRepository;
    private final CompetenciaRepository competenciaRepository;

    /**
     * Asigna o actualiza el nivel de una competencia para una persona
     * Crea un nuevo registro histórico con timestamp
     * @throws RuntimeException si el nivel no está entre 0 y 100
     */
    public PersonaCompetencia asignarNivel(Long personaId, Long competenciaId, Integer nivel) {

        // Validar nivel
        if (nivel < 0 || nivel > 100) {
            throw new RuntimeException("El nivel debe estar entre 0 y 100");
        }

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        Competencia competencia = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new RuntimeException("Competencia no encontrada"));

        // Crear nuevo registro (histórico)
        PersonaCompetencia pc = new PersonaCompetencia();

        PersonaCompetenciaId id = new PersonaCompetenciaId();
        id.setPersonaId(personaId);
        id.setCompetenciaId(competenciaId);

        pc.setId(id);
        pc.setPersona(persona);
        pc.setCompetencia(competencia);
        pc.setNivelActual(nivel);
        pc.setFechaActualizado(LocalDateTime.now());

        return personaCompetenciaRepository.save(pc);
    }

    /**
     * Obtiene el nivel actual de una competencia para una persona
     */
    @Transactional(readOnly = true)
    public Optional<Integer> obtenerNivelActual(Long personaId, Long competenciaId) {
        return personaCompetenciaRepository.findNivelActual(personaId, competenciaId);
    }

    /**
     * Obtiene todas las competencias de una persona con sus niveles actuales.
     * Inicializa el proxy de Competencia dentro de la sesión para evitar LazyInitializationException.
     */
    @Transactional(readOnly = true)
    public List<PersonaCompetencia> obtenerCompetenciasDePersona(Long personaId) {
        List<PersonaCompetencia> lista = personaCompetenciaRepository.findByPersonaId(personaId);
        lista.forEach(pc -> Hibernate.initialize(pc.getCompetencia()));
        return lista;
    }

    /**
     * Obtiene todas las personas que tienen una competencia
     */
    @Transactional(readOnly = true)
    public List<PersonaCompetencia> obtenerPersonasConCompetencia(Long competenciaId) {
        return personaCompetenciaRepository.findByCompetenciaId(competenciaId);
    }

    /**
     * Obtiene el historial completo de una competencia para una persona
     */
    @Transactional(readOnly = true)
    public List<PersonaCompetencia> obtenerHistorial(Long personaId, Long competenciaId) {
        return personaCompetenciaRepository.findHistorial(personaId, competenciaId);
    }

    /**
     * Obtiene cambios de niveles en un rango de fechas
     */
    @Transactional(readOnly = true)
    public List<PersonaCompetencia> obtenerCambiosEnPeriodo(Long personaId,
                                                            LocalDateTime desde,
                                                            LocalDateTime hasta) {
        return personaCompetenciaRepository.findByPersonaIdAndFechaBetween(
                personaId, desde, hasta
        );
    }

    /**
     * Obtiene las competencias más fuertes de una persona
     * @param limit número de competencias a retornar
     */
    @Transactional(readOnly = true)
    public List<PersonaCompetencia> obtenerTopCompetencias(Long personaId, int limit) {
        List<PersonaCompetencia> todas = personaCompetenciaRepository
                .findTopCompetenciasByPersonaId(personaId);

        return todas.stream()
                .limit(limit)
                .toList();
    }

    /**
     * Elimina una competencia de una persona
     */
    public void eliminarCompetencia(Long personaId, Long competenciaId) {
        PersonaCompetenciaId id = new PersonaCompetenciaId();
        id.setPersonaId(personaId);
        id.setCompetenciaId(competenciaId);

        if (!personaCompetenciaRepository.existsById(id)) {
            throw new RuntimeException("La persona no tiene asignada esa competencia");
        }

        personaCompetenciaRepository.deleteById(id);
    }

    /**
     * Incrementa el nivel de una competencia
     */
    public PersonaCompetencia incrementarNivel(Long personaId, Long competenciaId, int incremento) {
        Optional<Integer> nivelActualOpt = obtenerNivelActual(personaId, competenciaId);

        if (nivelActualOpt.isEmpty()) {
            throw new RuntimeException("La persona no tiene asignada esa competencia");
        }

        int nivelNuevo = Math.min(100, nivelActualOpt.get() + incremento);
        return asignarNivel(personaId, competenciaId, nivelNuevo);
    }
}
