package com.iesaguadulce.agilteammanager.model.proyectos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para proyectos de gestión ágil.
 *
 * <p>Contiene sprints y tareas asociadas.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "proyectos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {

    /** Identificador único del proyecto. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del proyecto. */
    @Column(nullable = false, length = 200)
    private String nombre;

    /** Descripción detallada del proyecto. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Fecha de inicio planificada. */
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    /** Fecha de finalización planificada. */
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    /** Estado: planificacion, activo, completado, cancelado. */
    @Column(length = 50)
    private String estado = "planificacion"; // planificacion, activo, completado, cancelado

    /** Sprints del proyecto. */
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Sprint> sprints = new HashSet<>();

    /** Tareas del proyecto. */
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tarea> tareas = new HashSet<>();
}
