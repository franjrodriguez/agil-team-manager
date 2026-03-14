package com.iesaguadulce.agilteammanager.model.personas;

import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "competencias")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Competencia {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(length = 50)
    private String tipo; // Lenguaje, Framework, BD, DevOps, Testing, Cloud

    // Relación inversa con PersonaCompetencia
    @OneToMany(mappedBy = "competencia", cascade = CascadeType.ALL)
    private Set<PersonaCompetencia> personasCompetencias = new HashSet<>();

    // Relación inversa con TareaCompetencia
    @OneToMany(mappedBy = "competencia", cascade = CascadeType.ALL)
    private Set<TareaCompetencia> tareasCompetencias = new HashSet<>();
}
