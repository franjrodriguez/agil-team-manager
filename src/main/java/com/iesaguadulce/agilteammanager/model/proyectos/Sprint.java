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

@Entity
@Table(name = "sprints")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sprint {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → Proyecto
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    private Integer numero;

    @Column(length = 50)
    private String estado = "planificacion"; // planificacion, activo, completado, cancelado

    @Column(columnDefinition = "TEXT")
    private String objetivo;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // Relación con Tareas
    @ToString.Exclude
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL)
    private Set<Tarea> tareas = new HashSet<>();
}
