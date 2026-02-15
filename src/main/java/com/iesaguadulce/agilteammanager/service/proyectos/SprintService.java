package com.iesaguadulce.agilteammanager.service.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;
import com.iesaguadulce.agilteammanager.repository.proyectos.ProyectoRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de sprints
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProyectoRepository proyectoRepository;

    /**
     * Crea un nuevo sprint
     */
    public Sprint crear(Long proyectoId, String objetivo,
                        LocalDate fechaInicio, LocalDate fechaFin) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Validar fechas
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la de fin");
        }

        // Obtener siguiente número de sprint
        Integer maxNumero = sprintRepository.findMaxNumeroByProyectoId(proyectoId).orElse(0);

        Sprint sprint = new Sprint();
        sprint.setProyecto(proyecto);
        sprint.setNumero(maxNumero + 1);
        sprint.setObjetivo(objetivo);
        sprint.setFechaInicio(fechaInicio);
        sprint.setFechaFin(fechaFin);
        sprint.setEstado("planificacion");

        return sprintRepository.save(sprint);
    }

    /**
     * Actualiza un sprint existente
     */
    public Sprint actualizar(Long id, String objetivo,
                             LocalDate fechaInicio, LocalDate fechaFin, String estado) {

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        // Validar fechas
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la de fin");
        }

        // Validar estado
        if (estado != null && !List.of("planificacion", "activo", "completado", "cancelado")
                .contains(estado)) {
            throw new RuntimeException("Estado inválido");
        }

        sprint.setObjetivo(objetivo);
        sprint.setFechaInicio(fechaInicio);
        sprint.setFechaFin(fechaFin);
        if (estado != null) {
            sprint.setEstado(estado);
        }

        return sprintRepository.save(sprint);
    }

    /**
     * Inicia un sprint (cambia estado a activo)
     */
    public void iniciar(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        // Verificar que no haya otro sprint activo en el proyecto
        Optional<Sprint> sprintActivo = sprintRepository
                .findSprintActivoByProyectoId(sprint.getProyecto().getId());

        if (sprintActivo.isPresent() && !sprintActivo.get().getId().equals(id)) {
            throw new RuntimeException("Ya existe un sprint activo en este proyecto");
        }

        sprint.setEstado("activo");
        sprintRepository.save(sprint);
    }

    /**
     * Completa un sprint
     */
    public void completar(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        sprint.setEstado("completado");
        sprintRepository.save(sprint);
    }

    /**
     * Elimina un sprint
     */
    public void eliminar(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (!sprint.getTareas().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un sprint con tareas asignadas");
        }

        sprintRepository.delete(sprint);
    }

    /**
     * Obtiene todos los sprints de un proyecto
     */
    @Transactional(readOnly = true)
    public List<Sprint> obtenerPorProyecto(Long proyectoId) {
        return sprintRepository.findByProyectoId(proyectoId);
    }

    /**
     * Obtiene el sprint activo de un proyecto
     */
    @Transactional(readOnly = true)
    public Optional<Sprint> obtenerSprintActivo(Long proyectoId) {
        return sprintRepository.findSprintActivoByProyectoId(proyectoId);
    }

    /**
     * Obtiene un sprint con sus tareas
     */
    @Transactional(readOnly = true)
    public Optional<Sprint> obtenerConTareas(Long id) {
        return sprintRepository.findByIdWithTareas(id);
    }

    /**
     * Obtiene un sprint por ID
     */
    @Transactional(readOnly = true)
    public Optional<Sprint> obtenerPorId(Long id) {
        return sprintRepository.findById(id);
    }
}
