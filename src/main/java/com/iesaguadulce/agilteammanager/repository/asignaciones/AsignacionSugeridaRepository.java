package com.iesaguadulce.agilteammanager.repository.asignaciones;

import com.iesaguadulce.agilteammanager.model.asignaciones.AsignacionSugerida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AsignacionSugeridaRepository extends JpaRepository<AsignacionSugerida, Long> {

    /**
     * Obtiene todas las sugerencias para una tarea ordenadas por score ajustado
     */
    @Query("SELECT a FROM AsignacionSugerida a " +
            "WHERE a.tarea.id = :tareaId " +
            "ORDER BY a.scoreAjustado DESC")
    List<AsignacionSugerida> findByTareaIdOrderByScoreDesc(@Param("tareaId") Long tareaId);

    /**
     * Obtiene las N mejores sugerencias para una tarea
     */
    @Query("SELECT a FROM AsignacionSugerida a " +
            "WHERE a.tarea.id = :tareaId " +
            "ORDER BY a.scoreAjustado DESC " +
            "LIMIT :limit")
    List<AsignacionSugerida> findTopNByTareaId(
            @Param("tareaId") Long tareaId,
            @Param("limit") int limit
    );

    /**
     * Obtiene sugerencias para una persona
     */
    @Query("SELECT a FROM AsignacionSugerida a " +
            "WHERE a.persona.id = :personaId " +
            "ORDER BY a.scoreAjustado DESC")
    List<AsignacionSugerida> findByPersonaIdOrderByScoreDesc(@Param("personaId") Long personaId);

    /**
     * Obtiene la mejor sugerencia para una tarea
     */
    @Query("SELECT a FROM AsignacionSugerida a " +
            "WHERE a.tarea.id = :tareaId " +
            "ORDER BY a.scoreAjustado DESC " +
            "LIMIT 1")
    AsignacionSugerida findMejorSugerenciaByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Obtiene sugerencias calculadas en un rango de fechas
     */
    @Query("SELECT a FROM AsignacionSugerida a " +
            "WHERE a.fechaCalculo BETWEEN :desde AND :hasta")
    List<AsignacionSugerida> findByFechaCalculoBetween(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Obtiene sugerencias de un proyecto
     */
    @Query("SELECT a FROM AsignacionSugerida a " +
            "WHERE a.tarea.proyecto.id = :proyectoId " +
            "ORDER BY a.scoreAjustado DESC")
    List<AsignacionSugerida> findByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Elimina sugerencias antiguas (más de X días)
     */
    @Modifying
    @Query("DELETE FROM AsignacionSugerida a " +
            "WHERE a.fechaCalculo < :fechaLimite")
    void deleteSugerenciasAntiguas(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Elimina sugerencias de una tarea
     */
    @Modifying
    @Query("DELETE FROM AsignacionSugerida a WHERE a.tarea.id = :tareaId")
    void deleteByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Verifica si existen sugerencias recientes para una tarea (últimas 24h)
     */
    @Query("SELECT COUNT(a) > 0 FROM AsignacionSugerida a " +
            "WHERE a.tarea.id = :tareaId " +
            "AND a.fechaCalculo >= :hace24h")
    boolean existeSugerenciaRecienteByTareaId(
            @Param("tareaId") Long tareaId,
            @Param("hace24h") LocalDateTime hace24h
    );

    /**
     * Obtiene sugerencias con carga completa (persona, tarea, competencias)
     */
    @Query("SELECT DISTINCT a FROM AsignacionSugerida a " +
            "JOIN FETCH a.persona p " +
            "JOIN FETCH a.tarea t " +
            "LEFT JOIN FETCH p.personasCompetencias pc " +
            "LEFT JOIN FETCH pc.competencia " +
            "WHERE a.tarea.id = :tareaId " +
            "ORDER BY a.scoreAjustado DESC")
    List<AsignacionSugerida> findByTareaIdWithDetails(@Param("tareaId") Long tareaId);
}
