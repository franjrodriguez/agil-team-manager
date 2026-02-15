package com.iesaguadulce.agilteammanager.model.asignaciones;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion = LocalDateTime.now();

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "valoracion_final", precision = 3, scale = 2)
    private BigDecimal valoracionFinal;
}
