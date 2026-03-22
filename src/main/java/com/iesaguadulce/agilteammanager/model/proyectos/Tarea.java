package com.iesaguadulce.agilteammanager.model.proyectos;

import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import com.iesaguadulce.agilteammanager.model.asignaciones.AsignacionSugerida;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para tareas de trabajo.
 *
 * <p>Unidad de trabajo asignable a personas con competencias requeridas.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "tareas")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    /** Identificador único de la tarea. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Proyecto al que pertenece (obligatorio). */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    /** Sprint al que pertenece (opcional). */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    /** Título de la tarea. */
    @Column(nullable = false, length = 200)
    private String titulo;

    /** Descripción detallada. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Estado: pendiente, en_progreso, revision, completada, bloqueada. */
    @Column(length = 50)
    private String estado = "pendiente"; // pendiente, en_progreso, revision, completada, bloqueada

    /** Prioridad entre 0 y 1 (mayor = más prioritaria). */
    @Column(precision = 3, scale = 2)
    private BigDecimal prioridad = BigDecimal.valueOf(0.5);

    /** Estimación de horas de trabajo. */
    @Column(name = "estimacion_horas")
    private Integer estimacionHoras;

    /** Fecha de creación de la tarea. */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Fecha de finalización real. */
    @Column(name = "fecha_terminacion")
    private LocalDateTime fechaTerminacion;

    /** Competencias requeridas con pesos. */
    @ToString.Exclude
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TareaCompetencia> tareasCompetencias = new HashSet<>();

    /** Asignaciones reales de la tarea. */
    @ToString.Exclude
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL)
    private Set<Asignacion> asignaciones = new HashSet<>();

    /** Sugerencias del motor de asignación. */
    @ToString.Exclude
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL)
    private Set<AsignacionSugerida> asignacionesSugeridas = new HashSet<>();
}
