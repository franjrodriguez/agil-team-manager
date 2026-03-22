package com.iesaguadulce.agilteammanager.model.personas;

import com.iesaguadulce.agilteammanager.model.asignaciones.Disponibilidad;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para miembros del equipo.
 *
 * <p>Incluye credenciales, datos personales, puesto, rol y relaciones
 * con competencias, disponibilidad y asignaciones.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "personas")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    /** Identificador único de la persona. */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario único para login. */
    @Column(unique = true, nullable = false, length = 100)
    private String usuario;

    /** Contraseña encriptada. */
    @Column(nullable = false, length = 255)
    private String password;

    /** Nombre completo de la persona. */
    @Column(nullable = false, length = 150)
    private String nombre;

    /** Email único de contacto. */
    @Column(unique = true, length = 150)
    private String email;

    /** Ruta a la foto de perfil. */
    @Column(name = "foto_path", columnDefinition = "TEXT")
    private String fotoPath;

    /** Sexo: M, F, O. */
    @Column(length = 1)
    private Character sexo = 'O';

    /** Estado: activo, inactivo, baja. */
    @Column(length = 50)
    private String estado = "activo";

    /** Fecha de alta en el sistema. */
    @Column(name = "fecha_alta")
    private LocalDate fechaAlta = LocalDate.now();

    /**
     * Puesto del equipo (EAGER para acceso desde JavaFX).
     * @see FetchType#EAGER
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    /**
     * Rol de seguridad (EAGER para acceso desde LoginController).
     * @see FetchType#EAGER
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolSistema rol;

    /** Competencias con niveles asignados. */
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PersonaCompetencia> personasCompetencias = new HashSet<>();

    /** Historial de disponibilidad y carga. */
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Disponibilidad> disponibilidades = new HashSet<>();

    /** Asignaciones de tareas. */
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
    private Set<Asignacion> asignaciones = new HashSet<>();
}