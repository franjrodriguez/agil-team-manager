package com.iesaguadulce.agilteammanager.model.personas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad intermedia para competencias de personas.
 *
 * <p>Relación N:M con nivel actual y fecha de actualización.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "personas_competencias")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCompetencia {

    /** Clave compuesta (persona + competencia). */
    @EqualsAndHashCode.Include
    @EmbeddedId
    private PersonaCompetenciaId id;

    /** Persona que posee la competencia. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("personaId")
    @JoinColumn(name = "persona_id")
    private Persona persona;

    /** Competencia poseída. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("competenciaId")
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    /** Nivel actual de la competencia (0-100). */
    @Column(name = "nivel_actual")
    private Integer nivelActual;

    /** Fecha de última actualización del nivel. */
    @Column(name = "fecha_actualizado")
    private LocalDateTime fechaActualizado = LocalDateTime.now();
}