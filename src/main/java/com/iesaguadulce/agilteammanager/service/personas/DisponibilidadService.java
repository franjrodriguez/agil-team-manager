package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.asignaciones.Disponibilidad;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.repository.asignaciones.AsignacionRepository;
import com.iesaguadulce.agilteammanager.repository.personas.DisponibilidadRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de gestión de disponibilidad y carga de trabajo del equipo.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final PersonaRepository personaRepository;
    private final AsignacionRepository asignacionRepository;

    private static final BigDecimal HORAS_SEMANA_DISPONIBLES = BigDecimal.valueOf(40);

    /**
     * Actualiza la carga de trabajo de una persona
     * @param carga valor entre 0 (libre) y 1 (ocupado)
     */
    public Disponibilidad actualizarCarga(Long personaId, BigDecimal carga) {

        // Validar carga
        if (carga.compareTo(BigDecimal.ZERO) < 0 || carga.compareTo(BigDecimal.ONE) > 0) {
            throw new RuntimeException("La carga debe estar entre 0 y 1");
        }

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setPersona(persona);
        disponibilidad.setCarga(carga);
        disponibilidad.setFecha(LocalDateTime.now());

        return disponibilidadRepository.save(disponibilidad);
    }

    /**
     * Obtiene la carga actual de una persona
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerCargaActual(Long personaId) {
        return disponibilidadRepository.findCargaActualByPersonaId(personaId)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Calcula automáticamente la carga basada en asignaciones activas
     * @param personaId ID de la persona
     * @return carga calculada (0-1)
     */
    public BigDecimal calcularCargaAutomatica(Long personaId) {
        long numAsignacionesActivas = asignacionRepository
                .countAsignacionesActivasByPersonaId(personaId);

        // Obtener suma de horas estimadas de tareas asignadas activas
        var asignaciones = asignacionRepository.findAsignacionesActivasByPersonaId(personaId);

        int totalHoras = asignaciones.stream()
                .map(a -> a.getTarea().getEstimacionHoras())
                .filter(h -> h != null)
                .mapToInt(Integer::intValue)
                .sum();

        // Calcular carga: horas_asignadas / horas_disponibles_semana
        BigDecimal carga = BigDecimal.valueOf(totalHoras)
                .divide(HORAS_SEMANA_DISPONIBLES, 2, RoundingMode.HALF_UP);

        // Limitar a 1.0 máximo
        if (carga.compareTo(BigDecimal.ONE) > 0) {
            carga = BigDecimal.ONE;
        }

        return carga;
    }

    /**
     * Actualiza la carga automáticamente y la registra
     */
    public Disponibilidad actualizarCargaAutomatica(Long personaId) {
        BigDecimal carga = calcularCargaAutomatica(personaId);
        return actualizarCarga(personaId, carga);
    }

    /**
     * Obtiene el historial de disponibilidad de una persona
     */
    @Transactional(readOnly = true)
    public List<Disponibilidad> obtenerHistorial(Long personaId,
                                                 LocalDateTime desde,
                                                 LocalDateTime hasta) {
        return disponibilidadRepository.findHistorialByPersonaId(personaId, desde, hasta);
    }

    /**
     * Obtiene la carga promedio de una persona en un período
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerCargaPromedio(Long personaId,
                                           LocalDateTime desde,
                                           LocalDateTime hasta) {
        return disponibilidadRepository.findCargaPromedioByPersonaId(personaId, desde, hasta)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Obtiene personas disponibles (carga menor a un umbral)
     * @param cargaMaxima umbral de carga (ej: 0.7 = 70% o menos)
     */
    @Transactional(readOnly = true)
    public List<Disponibilidad> obtenerPersonasDisponibles(BigDecimal cargaMaxima) {
        return disponibilidadRepository.findPersonasDisponibles(cargaMaxima);
    }

    /**
     * Obtiene todas las personas con su última carga registrada
     */
    @Transactional(readOnly = true)
    public List<Disponibilidad> obtenerUltimasDisponibilidades() {
        return disponibilidadRepository.findUltimaDisponibilidadPorPersona();
    }

    /**
     * Incrementa la carga tras asignar una tarea
     */
    public void incrementarCarga(Long personaId, Integer horasEstimadas) {
        BigDecimal cargaActual = obtenerCargaActual(personaId);

        BigDecimal incremento = BigDecimal.valueOf(horasEstimadas)
                .divide(HORAS_SEMANA_DISPONIBLES, 2, RoundingMode.HALF_UP);

        BigDecimal nuevaCarga = cargaActual.add(incremento);

        // Limitar a 1.0
        if (nuevaCarga.compareTo(BigDecimal.ONE) > 0) {
            nuevaCarga = BigDecimal.ONE;
        }

        actualizarCarga(personaId, nuevaCarga);
    }

    /**
     * Libera carga tras completar una tarea
     */
    public void liberarCarga(Long personaId, Integer horasEstimadas) {
        BigDecimal cargaActual = obtenerCargaActual(personaId);

        BigDecimal decremento = BigDecimal.valueOf(horasEstimadas)
                .divide(HORAS_SEMANA_DISPONIBLES, 2, RoundingMode.HALF_UP);

        BigDecimal nuevaCarga = cargaActual.subtract(decremento);

        // No puede ser menor que 0
        if (nuevaCarga.compareTo(BigDecimal.ZERO) < 0) {
            nuevaCarga = BigDecimal.ZERO;
        }

        actualizarCarga(personaId, nuevaCarga);
    }
}
