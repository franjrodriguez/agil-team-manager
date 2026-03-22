package com.iesaguadulce.agilteammanager.model.asignaciones;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que registra la disponibilidad/carga de trabajo de una persona.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@Entity
@Table(name = "disponibilidad")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilidad {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    private LocalDateTime fecha = LocalDateTime.now();

    @Column(precision = 3, scale = 2)
    private BigDecimal carga; // 0-1 (0=libre, 1=ocupado)
}
