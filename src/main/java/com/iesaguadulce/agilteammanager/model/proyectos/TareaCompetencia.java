package com.iesaguadulce.agilteammanager.model.proyectos;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad intermedia para competencias requeridas por una tarea.
 *
 * <p>Relación N:M con atributo peso (suma de pesos debe ser 1.0).</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "tareas_competencias")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class TareaCompetencia {

    /** Clave compuesta (tarea + competencia). */
    @EqualsAndHashCode.Include
    @EmbeddedId
    private TareaCompetenciaId id;

    /** Tarea que requiere la competencia. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tareaId")
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    /** Competencia requerida. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("competenciaId")
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    /** Peso de la competencia en la tarea (0-1). */
    @Column(precision = 3, scale = 2)
    private BigDecimal peso; // 0-1 (suma debe ser 1)
}
