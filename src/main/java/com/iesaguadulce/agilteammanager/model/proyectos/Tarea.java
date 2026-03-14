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

@Entity
@Table(name = "tareas")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → Proyecto (obligatoria)
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    // FK → Sprint (opcional)
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 50)
    private String estado = "pendiente"; // pendiente, en_progreso, revision, completada, bloqueada

    @Column(precision = 3, scale = 2)
    private BigDecimal prioridad = BigDecimal.valueOf(0.5);

    @Column(name = "estimacion_horas")
    private Integer estimacionHoras;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Relación con Competencias requeridas (N:M)
    @ToString.Exclude
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TareaCompetencia> tareasCompetencias = new HashSet<>();

    // Relación con Asignaciones
    @ToString.Exclude
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL)
    private Set<Asignacion> asignaciones = new HashSet<>();

    // Relación con Asignaciones Sugeridas
    @ToString.Exclude
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL)
    private Set<AsignacionSugerida> asignacionesSugeridas = new HashSet<>();
}
