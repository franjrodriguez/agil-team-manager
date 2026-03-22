package com.iesaguadulce.agilteammanager.model.personas;

import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para competencias técnicas o habilidades.
 *
 * <p>Pueden asignarse a personas y requerirse en tareas.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "competencias")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Competencia {

    /** Identificador único de la competencia. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de la competencia (ej: Java, Spring, MySQL). */
    @Column(nullable = false, length = 150)
    private String nombre;

    /** Descripción de la competencia. */
    @Column(length = 255)
    private String descripcion;

    /** Tipo: Lenguaje, Framework, BD, DevOps, Testing, Cloud. */
    @Column(length = 50)
    private String tipo;

    /** Personas con esta competencia. */
    @OneToMany(mappedBy = "competencia", cascade = CascadeType.ALL)
    private Set<PersonaCompetencia> personasCompetencias = new HashSet<>();

    /** Tareas que requieren esta competencia. */
    @OneToMany(mappedBy = "competencia", cascade = CascadeType.ALL)
    private Set<TareaCompetencia> tareasCompetencias = new HashSet<>();
}