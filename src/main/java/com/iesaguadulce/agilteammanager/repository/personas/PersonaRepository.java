package com.iesaguadulce.agilteammanager.repository.personas;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de personas del equipo.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    /**
     * Busca persona por usuario (login)
     */
    Optional<Persona> findByUsuario(String usuario);

    /**
     * Busca persona por usuario y estado (login validando estado activo)
     */
    Optional<Persona> findByUsuarioAndEstado(String usuario, String estado);

    /**
     * Busca persona por email
     */
    Optional<Persona> findByEmail(String email);

    /**
     * Verifica si existe un usuario
     */
    boolean existsByUsuario(String usuario);

    /**
     * Verifica si existe un email
     */
    boolean existsByEmail(String email);

    /**
     * Busca personas activas
     */
    List<Persona> findByEstado(String estado);

    /**
     * Busca personas por puesto.
     * @param puestoId ID del puesto
     */
    @Query("SELECT p FROM Persona p WHERE p.puesto.id = :puestoId")
    List<Persona> findByPuestoId(@Param("puestoId") Long puestoId);

    /**
     * Busca personas por rol del sistema.
     * @param rolId ID del rol
     */
    @Query("SELECT p FROM Persona p WHERE p.rol.id = :rolId")
    List<Persona> findByRolId(@Param("rolId") Long rolId);

    /**
     * Busca persona con competencias cargadas (JOIN FETCH).
     * @param id ID de la persona
     */
    @Query("SELECT DISTINCT p FROM Persona p " +
            "LEFT JOIN FETCH p.personasCompetencias pc " +
            "LEFT JOIN FETCH pc.competencia " +
            "WHERE p.id = :id")
    Optional<Persona> findByIdWithCompetencias(@Param("id") Long id);

    /**
     * Busca personas activas con competencias cargadas
     */
    @Query("SELECT DISTINCT p FROM Persona p " +
            "LEFT JOIN FETCH p.personasCompetencias pc " +
            "LEFT JOIN FETCH pc.competencia " +
            "WHERE p.estado = 'activo'")
    List<Persona> findActivasWithCompetencias();

    /**
     * Carga persona completa con competencias y asignaciones.
     * Evita LazyInitializationException.
     * @param id ID de la persona
     */
    @Query("SELECT DISTINCT p FROM Persona p " +
           "LEFT JOIN FETCH p.personasCompetencias pc " +
           "LEFT JOIN FETCH pc.competencia " +
           "LEFT JOIN FETCH p.asignaciones a " +
           "LEFT JOIN FETCH a.tarea " +
           "WHERE p.id = :id")
    Optional<Persona> findByIdWithAll(@Param("id") Long id);

    /**
     * Busca personas por nombre (búsqueda parcial)
     */
    List<Persona> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Cuenta personas activas
     */
    long countByEstado(String estado);

}
