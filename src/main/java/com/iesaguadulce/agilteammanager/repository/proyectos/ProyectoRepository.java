package com.iesaguadulce.agilteammanager.repository.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    /**
     * Busca proyectos por estado
     */
    List<Proyecto> findByEstado(String estado);

    /**
     * Busca proyectos activos
     */
    @Query("SELECT p FROM Proyecto p WHERE p.estado = 'activo'")
    List<Proyecto> findProyectosActivos();

    /**
     * Busca proyectos por nombre (búsqueda parcial)
     */
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca proyecto con sus sprints cargados
     */
    @Query("SELECT p FROM Proyecto p " +
            "LEFT JOIN FETCH p.sprints " +
            "WHERE p.id = :id")
    Optional<Proyecto> findByIdWithSprints(@Param("id") Long id);

    /**
     * Busca proyecto con sus tareas cargadas
     */
    @Query("SELECT p FROM Proyecto p " +
            "LEFT JOIN FETCH p.tareas " +
            "WHERE p.id = :id")
    Optional<Proyecto> findByIdWithTareas(@Param("id") Long id);

    /**
     * Obtiene proyectos que finalizan en un rango de fechas
     */
    @Query("SELECT p FROM Proyecto p " +
            "WHERE p.fechaFin BETWEEN :desde AND :hasta")
    List<Proyecto> findByFechaFinBetween(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    /**
     * Cuenta proyectos por estado
     */
    long countByEstado(String estado);

    /**
     * Obtiene estadísticas de proyectos
     */
    @Query("SELECT p.estado, COUNT(p) FROM Proyecto p GROUP BY p.estado")
    List<Object[]> countProyectosByEstado();
}
