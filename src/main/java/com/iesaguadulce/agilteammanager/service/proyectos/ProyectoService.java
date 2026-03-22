package com.iesaguadulce.agilteammanager.service.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.repository.proyectos.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de proyectos.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    /**
     * Crea un nuevo proyecto.
     *
     * @throws RuntimeException si las fechas son inválidas
     */
    public Proyecto crear(String nombre, String descripcion,
                          LocalDate fechaInicio, LocalDate fechaFin) {

        // Validar fechas
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la de fin");
        }

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);
        proyecto.setFechaInicio(fechaInicio);
        proyecto.setFechaFin(fechaFin);
        proyecto.setEstado("planificacion");

        return proyectoRepository.save(proyecto);
    }

    /**
     * Actualiza un proyecto existente
     */
    public Proyecto actualizar(Long id, String nombre, String descripcion,
                               LocalDate fechaInicio, LocalDate fechaFin, String estado) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Validar fechas
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la de fin");
        }

        // Validar estado
        if (estado != null && !List.of("planificacion", "activo", "completado", "cancelado")
                .contains(estado)) {
            throw new RuntimeException("Estado inválido");
        }

        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);
        proyecto.setFechaInicio(fechaInicio);
        proyecto.setFechaFin(fechaFin);
        if (estado != null) {
            proyecto.setEstado(estado);
        }

        return proyectoRepository.save(proyecto);
    }

    /**
     * Cambia el estado de un proyecto
     */
    public void cambiarEstado(Long id, String nuevoEstado) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!List.of("planificacion", "activo", "completado", "cancelado").contains(nuevoEstado)) {
            throw new RuntimeException("Estado inválido");
        }

        proyecto.setEstado(nuevoEstado);
        proyectoRepository.save(proyecto);
    }

    /**
     * Elimina un proyecto
     */
    public void eliminar(Long id) {
        if (!proyectoRepository.existsById(id)) {
            throw new RuntimeException("Proyecto no encontrado");
        }
        proyectoRepository.deleteById(id);
    }

    /**
     * Obtiene todos los proyectos
     */
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    /**
     * Obtiene proyectos activos
     */
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerActivos() {
        return proyectoRepository.findProyectosActivos();
    }

    /**
     * Obtiene proyectos por estado
     */
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerPorEstado(String estado) {
        return proyectoRepository.findByEstado(estado);
    }

    /**
     * Obtiene un proyecto por ID
     */
    @Transactional(readOnly = true)
    public Optional<Proyecto> obtenerPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    /**
     * Obtiene un proyecto con sus sprints
     */
    @Transactional(readOnly = true)
    public Optional<Proyecto> obtenerConSprints(Long id) {
        return proyectoRepository.findByIdWithSprints(id);
    }

    /**
     * Obtiene un proyecto con sus tareas
     */
    @Transactional(readOnly = true)
    public Optional<Proyecto> obtenerConTareas(Long id) {
        return proyectoRepository.findByIdWithTareas(id);
    }

    /**
     * Busca proyectos por nombre
     */
    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorNombre(String nombre) {
        return proyectoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Cuenta proyectos por estado
     */
    @Transactional(readOnly = true)
    public long contarPorEstado(String estado) {
        return proyectoRepository.countByEstado(estado);
    }

    /**
     * Obtiene estadísticas de proyectos
     */
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticas() {
        List<Object[]> resultados = proyectoRepository.countProyectosByEstado();

        return resultados.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
}
