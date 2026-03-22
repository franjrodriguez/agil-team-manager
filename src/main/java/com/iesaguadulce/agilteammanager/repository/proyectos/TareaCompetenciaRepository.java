package com.iesaguadulce.agilteammanager.repository.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetencia;
import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para competencias requeridas por tareas.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface TareaCompetenciaRepository extends JpaRepository<TareaCompetencia, TareaCompetenciaId> {

    /**
     * Obtiene competencias de una tarea ordenadas por peso descendente.
     * @param tareaId ID de la tarea
     */
    @Query("SELECT tc FROM TareaCompetencia tc " +
            "WHERE tc.tarea.id = :tareaId " +
            "ORDER BY tc.peso DESC")
    List<TareaCompetencia> findByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Obtiene tareas que requieren una competencia.
     * @param competenciaId ID de la competencia
     */
    @Query("SELECT tc FROM TareaCompetencia tc " +
            "WHERE tc.competencia.id = :competenciaId " +
            "ORDER BY tc.peso DESC")
    List<TareaCompetencia> findByCompetenciaId(@Param("competenciaId") Long competenciaId);

    /**
     * Obtiene peso de una competencia en tarea específica.
     * @param tareaId ID de la tarea
     * @param competenciaId ID de la competencia
     */
    @Query("SELECT tc.peso FROM TareaCompetencia tc " +
            "WHERE tc.tarea.id = :tareaId AND tc.competencia.id = :competenciaId")
    Optional<BigDecimal> findPeso(
            @Param("tareaId") Long tareaId,
            @Param("competenciaId") Long competenciaId
    );

    /**
     * Suma de pesos de una tarea (debe ser 1.0).
     * @param tareaId ID de la tarea
     */
    @Query("SELECT SUM(tc.peso) FROM TareaCompetencia tc WHERE tc.tarea.id = :tareaId")
    Optional<BigDecimal> sumPesosByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Verifica si una tarea tiene competencias definidas
     */
    @Query("SELECT COUNT(tc) > 0 FROM TareaCompetencia tc WHERE tc.tarea.id = :tareaId")
    boolean existsByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Elimina todas las competencias de una tarea
     */
    @Query("DELETE FROM TareaCompetencia tc WHERE tc.tarea.id = :tareaId")
    void deleteByTareaId(@Param("tareaId") Long tareaId);
}
