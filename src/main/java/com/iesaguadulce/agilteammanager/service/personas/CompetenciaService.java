package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.repository.personas.CompetenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión del catálogo de competencias técnicas.
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CompetenciaService {

    private final CompetenciaRepository competenciaRepository;

    /**
     * Crea una nueva competencia
     *
     * @throws RuntimeException si ya existe una competencia con ese nombre
     */
    public Competencia crear(String nombre, String descripcion, String tipo) {
        if (competenciaRepository.existsByNombre(nombre)) {
            throw new RuntimeException("Ya existe una competencia con ese nombre");
        }

        Competencia competencia = new Competencia();
        competencia.setNombre(nombre);
        competencia.setDescripcion(descripcion);
        competencia.setTipo(tipo);

        return competenciaRepository.save(competencia);
    }

    /**
     * Actualiza una competencia existente
     */
    public Competencia actualizar(Long id, String nombre, String descripcion, String tipo) {
        Competencia competencia = competenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competencia no encontrada"));

        // Validar nombre único si cambió
        if (!competencia.getNombre().equals(nombre)
                && competenciaRepository.existsByNombre(nombre)) {
            throw new RuntimeException("Ya existe una competencia con ese nombre");
        }

        competencia.setNombre(nombre);
        competencia.setDescripcion(descripcion);
        competencia.setTipo(tipo);

        return competenciaRepository.save(competencia);
    }

    /**
     * Elimina una competencia (solo si no está en uso)
     */
    public void eliminar(Long id) {
        Competencia competencia = competenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competencia no encontrada"));

        if (!competencia.getPersonasCompetencias().isEmpty()
                || !competencia.getTareasCompetencias().isEmpty()) {
            throw new RuntimeException("No se puede eliminar una competencia en uso");
        }

        competenciaRepository.delete(competencia);
    }

    /**
     * Obtiene todas las competencias
     */
    @Transactional(readOnly = true)
    public List<Competencia> obtenerTodas() {
        return competenciaRepository.findAll();
    }

    /**
     * Obtiene competencias por tipo
     */
    @Transactional(readOnly = true)
    public List<Competencia> obtenerPorTipo(String tipo) {
        return competenciaRepository.findByTipo(tipo);
    }

    /**
     * Obtiene todos los tipos de competencias
     */
    @Transactional(readOnly = true)
    public List<String> obtenerTipos() {
        return competenciaRepository.findDistinctTipos();
    }

    /**
     * Busca competencias por nombre
     */
    @Transactional(readOnly = true)
    public List<Competencia> buscarPorNombre(String nombre) {
        return competenciaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene competencias de una persona
     */
    @Transactional(readOnly = true)
    public List<Competencia> obtenerPorPersona(Long personaId) {
        return competenciaRepository.findByPersonaId(personaId);
    }

    /**
     * Obtiene competencias requeridas por una tarea
     */
    @Transactional(readOnly = true)
    public List<Competencia> obtenerPorTarea(Long tareaId) {
        return competenciaRepository.findByTareaId(tareaId);
    }
}
