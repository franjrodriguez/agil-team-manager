package com.iesaguadulce.agilteammanager.model.seguridad;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para roles del sistema de seguridad.
 *
 * <p>Agrupa permisos y se asigna a personas para control de acceso.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "roles_sistema")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class RolSistema {

    /** Identificador único del rol. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre único del rol (ej: ADMIN, GESTOR). */
    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    /** Descripción del rol. */
    @Column(length = 255)
    private String descripcion;

    /** Permisos asignados al rol (relación N:M). */
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "roles_permisos",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    /** Personas con este rol (relación inversa 1:N). */
    @ToString.Exclude
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    private Set<Persona> personas = new HashSet<>();
}
