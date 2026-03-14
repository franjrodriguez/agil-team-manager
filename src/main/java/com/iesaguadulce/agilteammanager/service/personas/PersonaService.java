package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de personas del equipo
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaRepository personaRepository;

    /**
     * Obtiene todas las personas
     */
    @Transactional(readOnly = true)
    public List<Persona> obtenerTodas() {
        return personaRepository.findAll();
    }

    /**
     * Obtiene personas activas
     */
    @Transactional(readOnly = true)
    public List<Persona> obtenerActivas() {
        return personaRepository.findByEstado("activo");
    }

    /**
     * Obtiene personas activas con sus competencias cargadas
     */
    @Transactional(readOnly = true)
    public List<Persona> obtenerActivasConCompetencias() {
        return personaRepository.findActivasWithCompetencias();
    }

    /**
     * Obtiene una persona por ID
     */
    @Transactional(readOnly = true)
    public Optional<Persona> obtenerPorId(Long id) {
        return personaRepository.findById(id);
    }

    /**
     * Obtiene una persona con sus competencias
     */
    @Transactional(readOnly = true)
    public Optional<Persona> obtenerPorIdConCompetencias(Long id) {
        return personaRepository.findByIdWithCompetencias(id);
    }

    /**
     * Obtiene una persona con asignaciones, tareas y competencias cargadas eagerly.
     * Necesario para mostrar el detalle en la UI sin LazyInitializationException.
     */
    @Transactional(readOnly = true)
    public Optional<Persona> obtenerPorIdConTodo(Long id) {
        return personaRepository.findByIdWithAll(id);
    }

    /**
     * Busca personas por nombre
     */
    @Transactional(readOnly = true)
    public List<Persona> buscarPorNombre(String nombre) {
        return personaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene personas por puesto
     */
    @Transactional(readOnly = true)
    public List<Persona> obtenerPorPuesto(Long puestoId) {
        return personaRepository.findByPuestoId(puestoId);
    }

    /**
     * Guarda (crea o actualiza) una persona
     */
    public Persona guardar(Persona persona) {
        return personaRepository.save(persona);
    }

    /**
     * Actualiza foto de perfil
     */
    public void actualizarFoto(Long personaId, String fotoPath) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        persona.setFotoPath(fotoPath);
        personaRepository.save(persona);
    }

    /**
     * Cuenta personas activas
     */
    @Transactional(readOnly = true)
    public long contarActivas() {
        return personaRepository.countByEstado("activo");
    }

    /**
     * Cuenta personas totales
     */
    @Transactional(readOnly = true)
    public long contarTotal() {
        return personaRepository.count();
    }
}
