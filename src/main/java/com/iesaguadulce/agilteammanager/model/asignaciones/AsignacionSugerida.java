package com.iesaguadulce.agilteammanager.model.asignaciones;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones_sugeridas")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionSugerida {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo = LocalDateTime.now();

    @Column(name = "score_base", precision = 5, scale = 2)
    private BigDecimal scoreBase;

    @Column(name = "score_ajustado", precision = 5, scale = 2)
    private BigDecimal scoreAjustado;
}
