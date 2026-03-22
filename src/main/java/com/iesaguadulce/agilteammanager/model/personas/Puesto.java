package com.iesaguadulce.agilteammanager.model.personas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para puestos de trabajo del equipo.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "puestos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Puesto {

    /** Identificador único del puesto. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del puesto (ej: Desarrollador, Scrum Master). */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Descripción del puesto. */
    @Column(length = 255)
    private String descripcion;

    /** Personas con este puesto. */
    @OneToMany(mappedBy = "puesto", cascade = CascadeType.ALL)
    private Set<Persona> personas = new HashSet<>();
}