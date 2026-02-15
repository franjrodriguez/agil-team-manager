package com.iesaguadulce.agilteammanager.service.proyectos;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.model.proyectos.*;
import com.iesaguadulce.agilteammanager.repository.personas.CompetenciaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestión de tareas
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;
    private final SprintRepository sprintRepository;
    private final TareaCompetenciaRepository tareaCompetenciaRepository;
    private final CompetenciaRepository competenciaRepository;

    /**
     * Crea una nueva tarea
     */
    public Tarea crear(Long proyectoId, Long sprintId, String titulo, String descripcion,
                       Integer estimacionHoras, BigDecimal prioridad) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Sprint sprint = null;
        if (sprintId != null) {
            sprint = sprintRepository.findById(sprintId)
                    .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));
        }

        // Validar prioridad
        if (prioridad != null &&
                (prioridad.compareTo(BigDecimal.ZERO) < 0 || prioridad.compareTo(BigDecimal.ONE) > 0)) {
            throw new RuntimeException("La prioridad debe estar entre 0 y 1");
        }

        Tarea tarea = new Tarea();
        tarea.setProyecto(proyecto);
        tarea.setSprint(sprint);
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setEstimacionHoras(estimacionHoras);
        tarea.setPrioridad(prioridad != null ? prioridad : BigDecimal.valueOf(0.5));
        tarea.setEstado("pendiente");
        tarea.setFechaCreacion(LocalDateTime.now());

        return tareaRepository.save(tarea);
    }

    /**
     * Actualiza una tarea existente
     */
    public Tarea actualizar(Long id, String titulo, String descripcion,
                            Integer estimacionHoras, BigDecimal prioridad, String estado) {

        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Validar prioridad
        if (prioridad != null &&
                (prioridad.compareTo(BigDecimal.ZERO) < 0 || prioridad.compareTo(BigDecimal.ONE) > 0)) {
            throw new RuntimeException("La prioridad debe estar entre 0 y 1");
        }

        // Validar estado
        if (estado != null && !List.of("pendiente", "en_progreso", "revision", "completada", "bloqueada")
                .contains(estado)) {
            throw new RuntimeException("Estado inválido");
        }

        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setEstimacionHoras(estimacionHoras);
        if (prioridad != null) {
            tarea.setPrioridad(prioridad);
        }
        if (estado != null) {
            tarea.setEstado(estado);
        }

        return tareaRepository.save(tarea);
    }

    /**
     * Asigna competencias requeridas a una tarea con sus pesos
     *
     * @param competenciasPesos Mapa competenciaId -> peso
     */
    public void asignarCompetencias(Long tareaId, Map<Long, BigDecimal> competenciasPesos) {

        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Validar que la suma de pesos sea 1
        BigDecimal sumaPesos = competenciasPesos.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumaPesos.compareTo(BigDecimal.ONE) != 0) {
            throw new RuntimeException("La suma de los pesos debe ser exactamente 1.0");
        }

        // Eliminar competencias anteriores
        tareaCompetenciaRepository.deleteByTareaId(tareaId);

        // Asignar nuevas competencias
        for (Map.Entry<Long, BigDecimal> entry : competenciasPesos.entrySet()) {
            Long competenciaId = entry.getKey();
            BigDecimal peso = entry.getValue();

            Competencia competencia = competenciaRepository.findById(competenciaId)
                    .orElseThrow(() -> new RuntimeException("Competencia no encontrada: " + competenciaId));

            TareaCompetencia tc = new TareaCompetencia();

            TareaCompetenciaId id = new TareaCompetenciaId();
            id.setTareaId(tareaId);
            id.setCompetenciaId(competenciaId);

            tc.setId(id);
            tc.setTarea(tarea);
            tc.setCompetencia(competencia);
            tc.setPeso(peso);

            tareaCompetenciaRepository.save(tc);
        }
    }

    /**
     * Cambia el estado de una tarea
     */
    public void cambiarEstado(Long id, String nuevoEstado) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (!List.of("pendiente", "en_progreso", "revision", "completada", "bloqueada")
                .contains(nuevoEstado)) {
            throw new RuntimeException("Estado inválido");
        }

        tarea.setEstado(nuevoEstado);
        tareaRepository.save(tarea);
    }

    /**
     * Elimina una tarea
     */
    public void eliminar(Long id) {
        if (!tareaRepository.existsById(id)) {
            throw new RuntimeException("Tarea no encontrada");
        }
        tareaRepository.deleteById(id);
    }

    /**
     * Obtiene todas las tareas
     */
    @Transactional(readOnly = true)
    public List<Tarea> obtenerTodas() {
        return tareaRepository.findAll();
    }

    /**
     * Obtiene tareas por proyecto
     */
    @Transactional(readOnly = true)
    public List<Tarea> obtenerPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoId(proyectoId);
    }

    /**
     * Obtiene tareas por sprint
     */
    @Transactional(readOnly = true)
    public List<Tarea> obtenerPorSprint(Long sprintId) {
        return tareaRepository.findBySprintId(sprintId);
    }

    /**
     * Obtiene tareas por estado
     */
    @Transactional(readOnly = true)
    public List<Tarea> obtenerPorEstado(String estado) {
        return tareaRepository.findByEstado(estado);
    }

    /**
     * Obtiene tareas sin asignar
     */
    @Transactional(readOnly = true)
    public List<Tarea> obtenerSinAsignar() {
        return tareaRepository.findTareasSinAsignar();
    }

    /**
     * Obtiene una tarea con sus competencias requeridas
     */
    @Transactional(readOnly = true)
    public Optional<Tarea> obtenerConCompetencias(Long id) {
        return tareaRepository.findByIdWithCompetencias(id);
    }

    /**
     * Obtiene una tarea por ID
     */
    @Transactional(readOnly = true)
    public Optional<Tarea> obtenerPorId(Long id) {
        return tareaRepository.findById(id);
    }

    /**
     * Busca tareas por título
     */
    @Transactional(readOnly = true)
    public List<Tarea> buscarPorTitulo(String titulo) {
        return tareaRepository.findByTituloContainingIgnoreCase(titulo);
    }
}
