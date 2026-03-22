package com.iesaguadulce.agilteammanager.repository.asignaciones;

import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de asignaciones de tareas a personas.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    /**
     * Obtiene asignaciones de una tarea.
     * @param tareaId ID de la tarea
     */
    @Query("SELECT a FROM Asignacion a WHERE a.tarea.id = :tareaId ORDER BY a.fechaAsignacion DESC")
    List<Asignacion> findByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Obtiene asignación activa (no completada) de una tarea.
     * @param tareaId ID de la tarea
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.tarea.id = :tareaId AND a.fechaCompletada IS NULL")
    Optional<Asignacion> findAsignacionActivaByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Obtiene asignaciones activas de una persona.
     * @param personaId ID de la persona
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.fechaCompletada IS NULL")
    List<Asignacion> findAsignacionesActivasByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene asignaciones completadas de una persona.
     * @param personaId ID de la persona
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.fechaCompletada IS NOT NULL " +
            "ORDER BY a.fechaCompletada DESC")
    List<Asignacion> findAsignacionesCompletadasByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene asignaciones en rango de fechas.
     * @param desde fecha inicio
     * @param hasta fecha fin
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.fechaAsignacion BETWEEN :desde AND :hasta")
    List<Asignacion> findByFechaAsignacionBetween(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Obtiene asignaciones de un proyecto.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.tarea.proyecto.id = :proyectoId " +
            "ORDER BY a.fechaAsignacion DESC")
    List<Asignacion> findByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Obtiene asignaciones de un sprint.
     * @param sprintId ID del sprint
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.tarea.sprint.id = :sprintId " +
            "ORDER BY a.fechaAsignacion DESC")
    List<Asignacion> findBySprintId(@Param("sprintId") Long sprintId);

    /**
     * Cuenta asignaciones activas de una persona.
     * @param personaId ID de la persona
     */
    @Query("SELECT COUNT(a) FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.fechaCompletada IS NULL")
    long countAsignacionesActivasByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene valoración promedio de una persona.
     * @param personaId ID de la persona
     */
    @Query("SELECT AVG(a.valoracionFinal) FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.valoracionFinal IS NOT NULL")
    Optional<Double> findValoracionPromedioByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene asignación con persona, tarea y proyecto cargados (JOIN FETCH).
     * @param id ID de la asignación
     */
    @Query("SELECT a FROM Asignacion a " +
            "JOIN FETCH a.persona p " +
            "JOIN FETCH a.tarea t " +
            "JOIN FETCH t.proyecto " +
            "WHERE a.id = :id")
    Optional<Asignacion> findByIdWithDetails(@Param("id") Long id);

    /**
     * Cuenta asignaciones completadas entre dos fechas
     */
    long countByFechaCompletadaBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Cuenta asignaciones activas de una persona por estado de tarea.
     * @param personaId ID de la persona
     * @param estado estado de la tarea
     */
    @Query("SELECT COUNT(a) FROM Asignacion a WHERE a.persona.id = :personaId " +
            "AND a.tarea.estado = :estado AND a.fechaCompletada IS NULL")
    int countByPersonaIdAndTareaEstado(@Param("personaId") Long personaId,
                                       @Param("estado") String estado);

    /**
     * Busca todas las asignaciones de una persona
     */
    List<Asignacion> findByPersonaId(Long personaId);
}
