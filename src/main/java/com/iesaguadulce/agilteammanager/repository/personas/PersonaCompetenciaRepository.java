package com.iesaguadulce.agilteammanager.repository.personas;

import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetencia;
import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaCompetenciaRepository extends JpaRepository<PersonaCompetencia, PersonaCompetenciaId> {

    /**
     * Obtiene todas las competencias de una persona
     */
    @Query("SELECT pc FROM PersonaCompetencia pc " +
            "WHERE pc.persona.id = :personaId " +
            "ORDER BY pc.nivelActual DESC")
    List<PersonaCompetencia> findByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene todas las personas que tienen una competencia
     */
    @Query("SELECT pc FROM PersonaCompetencia pc " +
            "WHERE pc.competencia.id = :competenciaId " +
            "ORDER BY pc.nivelActual DESC")
    List<PersonaCompetencia> findByCompetenciaId(@Param("competenciaId") Long competenciaId);

    /**
     * Busca el nivel actual de una competencia para una persona
     */
    @Query("SELECT pc.nivelActual FROM PersonaCompetencia pc " +
            "WHERE pc.persona.id = :personaId AND pc.competencia.id = :competenciaId")
    Optional<Integer> findNivelActual(
            @Param("personaId") Long personaId,
            @Param("competenciaId") Long competenciaId
    );

    /**
     * Obtiene el historial de una competencia para una persona
     */
    @Query("SELECT pc FROM PersonaCompetencia pc " +
            "WHERE pc.persona.id = :personaId AND pc.competencia.id = :competenciaId " +
            "ORDER BY pc.fechaActualizado DESC")
    List<PersonaCompetencia> findHistorial(
            @Param("personaId") Long personaId,
            @Param("competenciaId") Long competenciaId
    );

    /**
     * Obtiene cambios de nivel en un rango de fechas
     */
    @Query("SELECT pc FROM PersonaCompetencia pc " +
            "WHERE pc.persona.id = :personaId " +
            "AND pc.fechaActualizado BETWEEN :desde AND :hasta " +
            "ORDER BY pc.fechaActualizado DESC")
    List<PersonaCompetencia> findByPersonaIdAndFechaBetween(
            @Param("personaId") Long personaId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Obtiene las N competencias más fuertes de una persona
     */
    @Query("SELECT pc FROM PersonaCompetencia pc " +
            "WHERE pc.persona.id = :personaId " +
            "ORDER BY pc.nivelActual DESC")
    List<PersonaCompetencia> findTopCompetenciasByPersonaId(
            @Param("personaId") Long personaId
    );
}
