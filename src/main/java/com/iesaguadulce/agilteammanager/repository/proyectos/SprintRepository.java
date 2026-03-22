package com.iesaguadulce.agilteammanager.repository.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de sprints.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    /**
     * Obtiene sprints de un proyecto ordenados por número.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT s FROM Sprint s WHERE s.proyecto.id = :proyectoId ORDER BY s.numero")
    List<Sprint> findByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Obtiene sprints por estado
     */
    List<Sprint> findByEstado(String estado);

    /**
     * Obtiene sprint activo de un proyecto.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT s FROM Sprint s " +
            "WHERE s.proyecto.id = :proyectoId AND s.estado = 'activo'")
    Optional<Sprint> findSprintActivoByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Busca sprint con tareas cargadas (JOIN FETCH).
     * @param id ID del sprint
     */
    @Query("SELECT s FROM Sprint s " +
            "LEFT JOIN FETCH s.tareas " +
            "WHERE s.id = :id")
    Optional<Sprint> findByIdWithTareas(@Param("id") Long id);

    /**
     * Obtiene sprints con fecha de fin en rango.
     * @param desde fecha inicio del rango
     * @param hasta fecha fin del rango
     */
    @Query("SELECT s FROM Sprint s " +
            "WHERE s.fechaFin BETWEEN :desde AND :hasta")
    List<Sprint> findByFechaFinBetween(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    /**
     * Obtiene último número de sprint en un proyecto.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT MAX(s.numero) FROM Sprint s WHERE s.proyecto.id = :proyectoId")
    Optional<Integer> findMaxNumeroByProyectoId(@Param("proyectoId") Long proyectoId);

    /**
     * Cuenta sprints por estado en un proyecto.
     * @param proyectoId ID del proyecto
     */
    @Query("SELECT s.estado, COUNT(s) FROM Sprint s " +
            "WHERE s.proyecto.id = :proyectoId " +
            "GROUP BY s.estado")
    List<Object[]> countSprintsByEstadoAndProyectoId(@Param("proyectoId") Long proyectoId);
}
