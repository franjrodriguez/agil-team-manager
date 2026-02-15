package com.iesaguadulce.agilteammanager.repository.asignaciones;

import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    /**
     * Obtiene todas las asignaciones de una tarea
     */
    @Query("SELECT a FROM Asignacion a WHERE a.tarea.id = :tareaId ORDER BY a.fechaAsignacion DESC")
    List<Asignacion> findByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Obtiene la asignación activa de una tarea (no completada)
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.tarea.id = :tareaId AND a.fechaCompletada IS NULL")
    Optional<Asignacion> findAsignacionActivaByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Obtiene asignaciones activas de una persona (no completadas)
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.fechaCompletada IS NULL")
    List<Asignacion> findAsignacionesActivasByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene asignaciones completadas de una persona
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.fechaCompletada IS NOT NULL " +
            "ORDER BY a.fechaCompletada DESC")
    List<Asignacion> findAsignacionesCompletadasByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene asignaciones en un rango de fechas
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.fechaAsignacion BETWEEN :desde AND :hasta")
    List<Asignacion> findByFechaAsignacionBetween(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Obtiene asignaciones de un proyecto
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.tarea.proyecto.id = :proyectoId " +
            "ORDER BY a.fechaAsignacion DESC")
    List<Asignacion> findByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Obtiene asignaciones de un sprint
     */
    @Query("SELECT a FROM Asignacion a " +
            "WHERE a.tarea.sprint.id = :sprintId " +
            "ORDER BY a.fechaAsignacion DESC")
    List<Asignacion> findBySprintId(@Param("sprintId") Long sprintId);

    /**
     * Cuenta asignaciones activas por persona
     */
    @Query("SELECT COUNT(a) FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.fechaCompletada IS NULL")
    long countAsignacionesActivasByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene valoración promedio de una persona
     */
    @Query("SELECT AVG(a.valoracionFinal) FROM Asignacion a " +
            "WHERE a.persona.id = :personaId AND a.valoracionFinal IS NOT NULL")
    Optional<Double> findValoracionPromedioByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene asignaciones con carga completa (persona, tarea, proyecto)
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
     * Cuenta tareas activas de una persona por estado
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
