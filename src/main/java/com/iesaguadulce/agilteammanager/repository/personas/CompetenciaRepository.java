package com.iesaguadulce.agilteammanager.repository.personas;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de competencias.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {

    /**
     * Busca competencia por nombre exacto
     */
    Optional<Competencia> findByNombre(String nombre);

    /**
     * Verifica si existe una competencia con ese nombre
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca competencias por tipo
     */
    List<Competencia> findByTipo(String tipo);

    /**
     * Busca competencias por nombre (búsqueda parcial)
     */
    List<Competencia> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Obtiene todos los tipos de competencias únicos
     */
    @Query("SELECT DISTINCT c.tipo FROM Competencia c WHERE c.tipo IS NOT NULL ORDER BY c.tipo")
    List<String> findDistinctTipos();

    /**
     * Obtiene competencias requeridas por una tarea.
     * @param tareaId ID de la tarea
     */
    @Query("SELECT c FROM Competencia c " +
            "JOIN c.tareasCompetencias tc " +
            "WHERE tc.tarea.id = :tareaId")
    List<Competencia> findByTareaId(@Param("tareaId") Long tareaId);

    /**
     * Obtiene competencias de una persona.
     * @param personaId ID de la persona
     */
    @Query("SELECT c FROM Competencia c " +
            "JOIN c.personasCompetencias pc " +
            "WHERE pc.persona.id = :personaId")
    List<Competencia> findByPersonaId(@Param("personaId") Long personaId);

    /**
     * Cuenta cuántas personas tienen cada competencia
     */
    @Query("SELECT c.nombre, COUNT(pc) FROM Competencia c " +
            "LEFT JOIN c.personasCompetencias pc " +
            "GROUP BY c.id, c.nombre")
    List<Object[]> countPersonasByCompetencia();
}
