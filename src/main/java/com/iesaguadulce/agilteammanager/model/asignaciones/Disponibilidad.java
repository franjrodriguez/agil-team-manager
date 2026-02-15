package com.iesaguadulce.agilteammanager.model.asignaciones;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilidad {

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
