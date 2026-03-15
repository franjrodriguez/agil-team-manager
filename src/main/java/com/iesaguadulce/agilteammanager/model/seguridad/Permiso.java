package com.iesaguadulce.agilteammanager.model.seguridad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permisos")
@Data                   // (Lombok) -> Genera en tiempo de compilación todos los getters/setters
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor      // (Lombok) -> Genera un constructor vacío
@AllArgsConstructor     // (Lombok) -> Genera un constructor con todos los atributos
public class Permiso {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String codigo;

    @Column(length = 255)
    private String descripcion;

    // Relación inversa con Roles
    @ToString.Exclude
    @ManyToMany(mappedBy = "permisos")
    private Set<RolSistema> roles = new HashSet<>();
}
