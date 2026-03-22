package com.iesaguadulce.agilteammanager.model.seguridad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para permisos del sistema de seguridad.
 *
 * <p>Representa una acción o recurso protegido que puede asignarse a roles.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "permisos")
@Data                   // (Lombok) -> Genera en tiempo de compilación todos los getters/setters
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor      // (Lombok) -> Genera un constructor vacío
@AllArgsConstructor     // (Lombok) -> Genera un constructor con todos los atributos
public class Permiso {

    /** Identificador único del permiso. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código único del permiso (ej: TAREAS_CREAR). */
    @Column(unique = true, nullable = false, length = 100)
    private String codigo;

    /** Descripción legible del permiso. */
    @Column(length = 255)
    private String descripcion;

    /** Roles que tienen este permiso (relación inversa N:M). */
    @ToString.Exclude
    @ManyToMany(mappedBy = "permisos")
    private Set<RolSistema> roles = new HashSet<>();
}
