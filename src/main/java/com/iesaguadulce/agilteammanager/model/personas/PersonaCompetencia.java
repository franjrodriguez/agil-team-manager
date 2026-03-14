package com.iesaguadulce.agilteammanager.model.personas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "personas_competencias")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCompetencia {

    @EqualsAndHashCode.Include
    @EmbeddedId
    private PersonaCompetenciaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("personaId")
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("competenciaId")
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    @Column(name = "nivel_actual")
    private Integer nivelActual; // 0-100

    @Column(name = "fecha_actualizado")
    private LocalDateTime fechaActualizado = LocalDateTime.now();
}
