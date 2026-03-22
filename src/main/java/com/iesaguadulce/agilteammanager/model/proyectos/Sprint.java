package com.iesaguadulce.agilteammanager.model.proyectos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para sprints dentro de un proyecto.
 *
 * <p>Iteración de desarrollo con duración fija y objetivo definido.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "sprints")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sprint {

    /** Identificador único del sprint. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Proyecto al que pertenece. */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    /** Número secuencial del sprint dentro del proyecto. */
    private Integer numero;

    /** Estado: planificacion, activo, completado, cancelado. */
    @Column(length = 50)
    private String estado = "planificacion"; // planificacion, activo, completado, cancelado

    /** Objetivo del sprint. */
    @Column(columnDefinition = "TEXT")
    private String objetivo;

    /** Fecha de inicio. */
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    /** Fecha de finalización. */
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    /** Tareas asignadas al sprint. */
    @ToString.Exclude
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL)
    private Set<Tarea> tareas = new HashSet<>();
}
