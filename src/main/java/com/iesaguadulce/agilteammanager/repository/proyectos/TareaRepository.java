package com.iesaguadulce.agilteammanager.repository.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de tareas.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    /**
     * Obtiene tareas de un proyecto.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT t FROM Tarea t WHERE t.proyecto.id = :proyectoId")
    List<Tarea> findByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Obtiene tareas de un sprint.
     * @param sprintId ID del sprint
     */
    @Query("SELECT t FROM Tarea t WHERE t.sprint.id = :sprintId")
    List<Tarea> findBySprintId(@Param("sprintId") Long sprintId);

    /**
     * Obtiene tareas por estado
     */
    List<Tarea> findByEstado(String estado);

    /**
     * Obtiene tareas pendientes de un proyecto.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT t FROM Tarea t " +
            "WHERE t.proyecto.id = :proyectoId AND t.estado = 'pendiente'")
    List<Tarea> findTareasPendientesByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Obtiene tareas sin asignar
     */
    @Query("SELECT t FROM Tarea t " +
            "WHERE NOT EXISTS (SELECT 1 FROM Asignacion a WHERE a.tarea.id = t.id)")
    List<Tarea> findTareasSinAsignar();

    /**
     * Obtiene tareas sin asignar con proyecto y sprint ya inicializados (evita LazyInitializationException).
     */
    @Query("SELECT t FROM Tarea t " +
            "JOIN FETCH t.proyecto " +
            "LEFT JOIN FETCH t.sprint " +
            "WHERE NOT EXISTS (SELECT 1 FROM Asignacion a WHERE a.tarea.id = t.id)")
    List<Tarea> findTareasSinAsignarConRelaciones();

    /**
     * Busca tarea con competencias cargadas (JOIN FETCH).
     * @param id ID de la tarea
     */
    @Query("SELECT DISTINCT t FROM Tarea t " +
            "LEFT JOIN FETCH t.tareasCompetencias tc " +
            "LEFT JOIN FETCH tc.competencia " +
            "WHERE t.id = :id")
    Optional<Tarea> findByIdWithCompetencias(@Param("id") Long id);

    /**
     * Obtiene tareas con prioridad mayor o igual al valor dado.
     * @param prioridadMinima valor mínimo de prioridad
     */
    @Query("SELECT t FROM Tarea t WHERE t.prioridad >= :prioridadMinima ORDER BY t.prioridad DESC")
    List<Tarea> findByPrioridadGreaterThanEqual(@Param("prioridadMinima") BigDecimal prioridadMinima);

    /**
     * Obtiene tareas creadas en rango de fechas.
     * @param desde fecha inicio
     * @param hasta fecha fin
     */
    @Query("SELECT t FROM Tarea t " +
            "WHERE t.fechaCreacion BETWEEN :desde AND :hasta")
    List<Tarea> findByFechaCreacionBetween(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Cuenta tareas por estado en un proyecto.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT t.estado, COUNT(t) FROM Tarea t " +
            "WHERE t.proyecto.id = :proyectoId " +
            "GROUP BY t.estado")
    List<Object[]> countTareasByEstadoAndProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Cuenta tareas por estado en un sprint.
     * @param sprintId ID del sprint
     */
    @Query("SELECT t.estado, COUNT(t) FROM Tarea t " +
            "WHERE t.sprint.id = :sprintId " +
            "GROUP BY t.estado")
    List<Object[]> countTareasByEstadoAndSprintId(@Param("sprintId") Long sprintId);

    /**
     * Busca tareas por título (búsqueda parcial)
     */
    List<Tarea> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Cuenta tareas por estado
     */
    long countByEstado(String estado);

    /**
     * Obtiene todas las tareas de un estado ordenadas por prioridad
     */
    List<Tarea> findByEstadoOrderByPrioridadDesc(String estado);

    /**
     * Cuenta tareas completadas en un rango de fecha_terminacion (para la gráfica)
     */
    long countByEstadoAndFechaTerminacionBetween(String estado, LocalDateTime inicio, LocalDateTime fin);

}
