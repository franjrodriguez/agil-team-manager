package com.iesaguadulce.agilteammanager.repository.personas;

import com.iesaguadulce.agilteammanager.model.asignaciones.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para historial de disponibilidad y carga de trabajo.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    /**
     * Obtiene registros de una persona ordenados por fecha.
     * @param personaId ID de la persona
     */
    @Query("SELECT d FROM Disponibilidad d " +
            "WHERE d.persona.id = :personaId " +
            "ORDER BY d.fecha DESC")
    List<Disponibilidad> findByPersonaIdOrderByFechaDesc(@Param("personaId") Long personaId);

    /**
     * Obtiene carga actual (registro más reciente) de una persona.
     * @param personaId ID de la persona
     */
    @Query("SELECT d.carga FROM Disponibilidad d " +
            "WHERE d.persona.id = :personaId " +
            "ORDER BY d.fecha DESC " +
            "LIMIT 1")
    Optional<BigDecimal> findCargaActualByPersonaId(@Param("personaId") Long personaId);

    /**
     * Obtiene historial de disponibilidad en rango de fechas.
     * @param personaId ID de la persona
     * @param desde fecha inicio
     * @param hasta fecha fin
     */
    @Query("SELECT d FROM Disponibilidad d " +
            "WHERE d.persona.id = :personaId " +
            "AND d.fecha BETWEEN :desde AND :hasta " +
            "ORDER BY d.fecha DESC")
    List<Disponibilidad> findHistorialByPersonaId(
            @Param("personaId") Long personaId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Calcula carga promedio de una persona en período.
     * @param personaId ID de la persona
     * @param desde fecha inicio
     * @param hasta fecha fin
     */
    @Query("SELECT AVG(d.carga) FROM Disponibilidad d " +
            "WHERE d.persona.id = :personaId " +
            "AND d.fecha BETWEEN :desde AND :hasta")
    Optional<BigDecimal> findCargaPromedioByPersonaId(
            @Param("personaId") Long personaId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Obtiene todas las personas con su última carga
     */
    @Query("SELECT d FROM Disponibilidad d " +
            "WHERE d.fecha = (" +
            "  SELECT MAX(d2.fecha) FROM Disponibilidad d2 " +
            "  WHERE d2.persona.id = d.persona.id" +
            ")")
    List<Disponibilidad> findUltimaDisponibilidadPorPersona();

    /**
     * Obtiene personas con carga menor a un valor (disponibles)
     */
    @Query("SELECT d FROM Disponibilidad d " +
            "WHERE d.fecha = (" +
            "  SELECT MAX(d2.fecha) FROM Disponibilidad d2 " +
            "  WHERE d2.persona.id = d.persona.id" +
            ") AND d.carga < :cargaMaxima")
    List<Disponibilidad> findPersonasDisponibles(@Param("cargaMaxima") BigDecimal cargaMaxima);

    /**
     * Calcula la carga promedio del equipo usando solo los registros más recientes
     */
    @Query("SELECT AVG(d.carga) FROM Disponibilidad d " +
            "WHERE d.fecha = (SELECT MAX(d2.fecha) FROM Disponibilidad d2)")
    Double promedioUltimaCarga();

    /**
     * Obtiene última carga registrada de una persona.
     * @param personaId ID de la persona
     */
    @Query("SELECT d.carga FROM Disponibilidad d " +
            "WHERE d.persona.id = :personaId " +
            "ORDER BY d.fecha DESC LIMIT 1")
    BigDecimal findUltimaCargaByPersonaId(@Param("personaId") Long personaId);

}
