package com.iesaguadulce.agilteammanager.model.seguridad;


import com.iesaguadulce.agilteammanager.model.personas.Persona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    // Relación con Permisos (N:M)
    @ManyToMany
    @JoinTable(
            name = "roles_permisos",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    // Relación inversa con Personas
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    private Set<Persona> personas = new HashSet<>();
}
