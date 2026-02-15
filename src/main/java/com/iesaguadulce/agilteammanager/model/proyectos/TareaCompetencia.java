package com.iesaguadulce.agilteammanager.model.proyectos;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tareas_competencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaCompetencia {

    @EmbeddedId
    private TareaCompetenciaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tareaId")
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("competenciaId")
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    @Column(precision = 3, scale = 2)
    private BigDecimal peso; // 0-1 (suma debe ser 1)
}
