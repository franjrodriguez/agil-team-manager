package com.iesaguadulce.agilteammanager.repository.personas;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
     * Busca personas por puesto
     */
    @Query("SELECT p FROM Persona p WHERE p.puesto.id = :puestoId")
    List<Persona> findByPuestoId(@Param("puestoId") Long puestoId);

    /**
     * Busca personas por rol del sistema
     */
    @Query("SELECT p FROM Persona p WHERE p.rol.id = :rolId")
    List<Persona> findByRolId(@Param("rolId") Long rolId);

    /**
     * Busca persona con sus competencias cargadas
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
     * Busca personas por nombre (búsqueda parcial)
     */
    List<Persona> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Cuenta personas activas
     */
    long countByEstado(String estado);

}
