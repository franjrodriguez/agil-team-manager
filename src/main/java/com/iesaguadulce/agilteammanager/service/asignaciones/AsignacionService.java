package com.iesaguadulce.agilteammanager.service.asignaciones;

import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import com.iesaguadulce.agilteammanager.model.asignaciones.AsignacionSugerida;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.repository.asignaciones.AsignacionRepository;
import com.iesaguadulce.agilteammanager.repository.asignaciones.AsignacionSugeridaRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.TareaRepository;
import com.iesaguadulce.agilteammanager.service.personas.DisponibilidadService;
import com.iesaguadulce.agilteammanager.service.proyectos.TareaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de asignaciones reales de tareas
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final AsignacionSugeridaRepository asignacionSugeridaRepository;
    private final TareaRepository tareaRepository;
    private final PersonaRepository personaRepository;
    private final DisponibilidadService disponibilidadService;
    private final TareaService tareaService;

    /**
     * Asigna una tarea manualmente a una persona
     */
    public Asignacion asignarManualmente(Long tareaId, Long personaId) {

        log.info("📌 Asignación manual: Tarea {} → Persona {}", tareaId, personaId);

        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        // Verificar que la tarea no esté ya asignada
        Optional<Asignacion> asignacionActiva = asignacionRepository
                .findAsignacionActivaByTareaId(tareaId);

        if (asignacionActiva.isPresent()) {
            throw new RuntimeException("La tarea ya está asignada a " +
                    asignacionActiva.get().getPersona().getNombre());
        }

        // Crear asignación
        Asignacion asignacion = new Asignacion();
        asignacion.setTarea(tarea);
        asignacion.setPersona(persona);
        asignacion.setFechaAsignacion(LocalDateTime.now());

        asignacion = asignacionRepository.save(asignacion);

        // Actualizar estado de la tarea a "en_progreso"
        tareaService.cambiarEstado(tareaId, "en_progreso");

        // Actualizar carga de la persona
        if (tarea.getEstimacionHoras() != null) {
            disponibilidadService.incrementarCarga(personaId, tarea.getEstimacionHoras());
        }

        log.info("✅ Asignación creada exitosamente");

        return asignacion;
    }

    /**
     * Acepta una sugerencia del motor y crea la asignación
     */
    public Asignacion aceptarSugerencia(Long sugerenciaId) {

        AsignacionSugerida sugerencia = asignacionSugeridaRepository.findById(sugerenciaId)
                .orElseThrow(() -> new RuntimeException("Sugerencia no encontrada"));

        log.info("✨ Aceptando sugerencia: {} → {}",
                sugerencia.getTarea().getTitulo(),
                sugerencia.getPersona().getNombre());

        return asignarManualmente(
                sugerencia.getTarea().getId(),
                sugerencia.getPersona().getId()
        );
    }

    /**
     * Completa una asignación (marca tarea como completada)
     */
    public Asignacion completar(Long asignacionId, BigDecimal valoracion, String observaciones) {

        Asignacion asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        if (asignacion.getFechaCompletada() != null) {
            throw new RuntimeException("La asignación ya está completada");
        }

        // Validar valoración
        if (valoracion != null &&
                (valoracion.compareTo(BigDecimal.ZERO) < 0 ||
                        valoracion.compareTo(BigDecimal.TEN) > 0)) {
            throw new RuntimeException("La valoración debe estar entre 0 y 10");
        }

        asignacion.setFechaCompletada(LocalDateTime.now());
        asignacion.setValoracionFinal(valoracion);
        asignacion.setObservaciones(observaciones);

        asignacion = asignacionRepository.save(asignacion);

        // Cambiar estado de la tarea
        tareaService.cambiarEstado(asignacion.getTarea().getId(), "completada");

        // Liberar carga de la persona
        if (asignacion.getTarea().getEstimacionHoras() != null) {
            disponibilidadService.liberarCarga(
                    asignacion.getPersona().getId(),
                    asignacion.getTarea().getEstimacionHoras()
            );
        }

        log.info("✅ Tarea completada: {} (Valoración: {})",
                asignacion.getTarea().getTitulo(), valoracion);

        return asignacion;
    }

    /**
     * Reasigna una tarea a otra persona
     */
    public Asignacion reasignar(Long asignacionId, Long nuevaPersonaId) {

        Asignacion asignacionAnterior = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        if (asignacionAnterior.getFechaCompletada() != null) {
            throw new RuntimeException("No se puede reasignar una tarea completada");
        }

        // Liberar carga de la persona anterior
        if (asignacionAnterior.getTarea().getEstimacionHoras() != null) {
            disponibilidadService.liberarCarga(
                    asignacionAnterior.getPersona().getId(),
                    asignacionAnterior.getTarea().getEstimacionHoras()
            );
        }

        // Eliminar asignación anterior
        asignacionRepository.delete(asignacionAnterior);

        // Crear nueva asignación
        return asignarManualmente(asignacionAnterior.getTarea().getId(), nuevaPersonaId);
    }

    /**
     * Obtiene todas las asignaciones de una persona
     */
    @Transactional(readOnly = true)
    public List<Asignacion> obtenerPorPersona(Long personaId) {
        return asignacionRepository.findByPersonaId(personaId);
    }

    /**
     * Obtiene asignaciones activas de una persona
     */
    @Transactional(readOnly = true)
    public List<Asignacion> obtenerActivasPorPersona(Long personaId) {
        return asignacionRepository.findAsignacionesActivasByPersonaId(personaId);
    }

    /**
     * Obtiene asignaciones completadas de una persona
     */
    @Transactional(readOnly = true)
    public List<Asignacion> obtenerCompletadasPorPersona(Long personaId) {
        return asignacionRepository.findAsignacionesCompletadasByPersonaId(personaId);
    }

    /**
     * Obtiene todas las asignaciones de un proyecto
     */
    @Transactional(readOnly = true)
    public List<Asignacion> obtenerPorProyecto(Long proyectoId) {
        return asignacionRepository.findByProyectoId(proyectoId);
    }

    /**
     * Obtiene todas las asignaciones de un sprint
     */
    @Transactional(readOnly = true)
    public List<Asignacion> obtenerPorSprint(Long sprintId) {
        return asignacionRepository.findBySprintId(sprintId);
    }

    /**
     * Obtiene una asignación con todos los detalles cargados
     */
    @Transactional(readOnly = true)
    public Optional<Asignacion> obtenerConDetalles(Long id) {
        return asignacionRepository.findByIdWithDetails(id);
    }

    /**
     * Obtiene la valoración promedio de una persona
     */
    @Transactional(readOnly = true)
    public Double obtenerValoracionPromedio(Long personaId) {
        return asignacionRepository.findValoracionPromedioByPersonaId(personaId)
                .orElse(0.0);
    }

    /**
     * Cuenta asignaciones activas de una persona
     */
    @Transactional(readOnly = true)
    public long contarActivasPorPersona(Long personaId) {
        return asignacionRepository.countAsignacionesActivasByPersonaId(personaId);
    }
}
