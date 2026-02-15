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

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    /**
     * Obtiene tareas por proyecto
     */
    @Query("SELECT t FROM Tarea t WHERE t.proyecto.id = :proyectoId")
    List<Tarea> findByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Obtiene tareas por sprint
     */
    @Query("SELECT t FROM Tarea t WHERE t.sprint.id = :sprintId")
    List<Tarea> findBySprintId(@Param("sprintId") Long sprintId);

    /**
     * Obtiene tareas por estado
     */
    List<Tarea> findByEstado(String estado);

    /**
     * Obtiene tareas pendientes de un proyecto
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
     * Busca tarea con sus competencias requeridas cargadas
     */
    @Query("SELECT DISTINCT t FROM Tarea t " +
            "LEFT JOIN FETCH t.tareasCompetencias tc " +
            "LEFT JOIN FETCH tc.competencia " +
            "WHERE t.id = :id")
    Optional<Tarea> findByIdWithCompetencias(@Param("id") Long id);

    /**
     * Obtiene tareas con prioridad mayor o igual a un valor
     */
    @Query("SELECT t FROM Tarea t WHERE t.prioridad >= :prioridadMinima ORDER BY t.prioridad DESC")
    List<Tarea> findByPrioridadGreaterThanEqual(@Param("prioridadMinima") BigDecimal prioridadMinima);

    /**
     * Obtiene tareas creadas en un rango de fechas
     */
    @Query("SELECT t FROM Tarea t " +
            "WHERE t.fechaCreacion BETWEEN :desde AND :hasta")
    List<Tarea> findByFechaCreacionBetween(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Cuenta tareas por estado en un proyecto
     */
    @Query("SELECT t.estado, COUNT(t) FROM Tarea t " +
            "WHERE t.proyecto.id = :proyectoId " +
            "GROUP BY t.estado")
    List<Object[]> countTareasByEstadoAndProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Cuenta tareas por estado en un sprint
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
     * Obtiene las 10 tareas pendientes con mayor prioridad
     */
    List<Tarea> findTop10ByEstadoOrderByPrioridadDesc(String estado);

}
