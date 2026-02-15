package com.iesaguadulce.agilteammanager.model.personas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "puestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    // Relación inversa con Personas
    @OneToMany(mappedBy = "puesto", cascade = CascadeType.ALL)
    private Set<Persona> personas = new HashSet<>();
}
