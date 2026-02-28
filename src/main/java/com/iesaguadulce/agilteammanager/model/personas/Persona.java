package com.iesaguadulce.agilteammanager.model.personas;

import com.iesaguadulce.agilteammanager.model.asignaciones.Disponibilidad;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "personas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(unique = true, length = 150)
    private String email;

    @Column(name = "foto_path", columnDefinition = "TEXT")
    private String fotoPath;

    @Column(length = 1)
    private Character sexo = 'O'; // M, F, O

    @Column(length = 50)
    private String estado = "activo"; // activo, inactivo, baja

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta = LocalDate.now();

    // FK → Puesto
    // EAGER: necesario porque JavaFX accede al puesto fuera del contexto JPA (sesión ya cerrada)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    // FK → RolSistema
    // EAGER: necesario para acceder al rol desde el LoginController sin LazyInitializationException
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolSistema rol;

    // Relación con Competencias (N:M)
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PersonaCompetencia> personasCompetencias = new HashSet<>();

    // Relación con Disponibilidad (historial)
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Disponibilidad> disponibilidades = new HashSet<>();

    // Relación con Asignaciones
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
    private Set<Asignacion> asignaciones = new HashSet<>();
}
