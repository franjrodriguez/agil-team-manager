package com.iesaguadulce.agilteammanager.service.asignaciones;

import com.iesaguadulce.agilteammanager.model.asignaciones.Asignacion;
import com.iesaguadulce.agilteammanager.model.asignaciones.Disponibilidad;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.repository.asignaciones.AsignacionRepository;
import com.iesaguadulce.agilteammanager.repository.personas.DisponibilidadRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.ProyectoRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para cálculo de métricas y KPIs del dashboard
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MetricasService {

    private final PersonaRepository personaRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final AsignacionRepository asignacionRepository;
    private final DisponibilidadRepository disponibilidadRepository;

    /**
     * Obtiene KPIs principales del dashboard
     */
    public Map<String, Object> obtenerKPIsPrincipales() {

        Map<String, Object> kpis = new HashMap<>();

        // Personas activas
        long personasActivas = personaRepository.countByEstado("activo");
        kpis.put("personasActivas", personasActivas);

        // Proyectos activos
        long proyectosActivos = proyectoRepository.countByEstado("activo");
        kpis.put("proyectosActivos", proyectosActivos);

        // Tareas por estado
        Map<String, Long> tareasPorEstado = new HashMap<>();
        tareasPorEstado.put("pendientes", tareaRepository.countByEstado("pendiente"));
        tareasPorEstado.put("enProgreso", tareaRepository.countByEstado("en_progreso"));
        tareasPorEstado.put("completadas", tareaRepository.countByEstado("completada"));
        kpis.put("tareasPorEstado", tareasPorEstado);

        // Carga promedio del equipo
        BigDecimal cargaPromedio = calcularCargaPromedio();
        kpis.put("cargaPromedioEquipo", cargaPromedio);

        return kpis;
    }

    /**
     * Calcula la carga promedio del equipo
     */
    public BigDecimal calcularCargaPromedio() {
        List<Disponibilidad> ultimasDisponibilidades =
                disponibilidadRepository.findUltimaDisponibilidadPorPersona();

        if (ultimasDisponibilidades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sumaCargas = ultimasDisponibilidades.stream()
                .map(Disponibilidad::getCarga)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sumaCargas.divide(
                BigDecimal.valueOf(ultimasDisponibilidades.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Obtiene distribución de carga por persona
     */
    public List<Map<String, Object>> obtenerDistribucionCarga() {
        List<Disponibilidad> disponibilidades =
                disponibilidadRepository.findUltimaDisponibilidadPorPersona();

        return disponibilidades.stream()
                .map(d -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("persona", d.getPersona().getNombre());
                    item.put("carga", d.getCarga().multiply(BigDecimal.valueOf(100)).intValue());
                    item.put("disponibilidad",
                            BigDecimal.ONE.subtract(d.getCarga())
                                    .multiply(BigDecimal.valueOf(100)).intValue());
                    return item;
                })
                .sorted((a, b) ->
                        ((Integer) b.get("carga")).compareTo((Integer) a.get("carga")))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de asignaciones
     */
    public Map<String, Object> obtenerEstadisticasAsignaciones(Long personaId) {

        Map<String, Object> stats = new HashMap<>();

        List<Asignacion> asignaciones = asignacionRepository.findByPersonaId(personaId);
        List<Asignacion> completadas = asignaciones.stream()
                .filter(a -> a.getFechaCompletada() != null)
                .toList();

        stats.put("totalAsignaciones", asignaciones.size());
        stats.put("completadas", completadas.size());
        stats.put("activas", asignaciones.size() - completadas.size());
        // Valoración promedio
        Double valoracionPromedio = asignacionRepository
                .findValoracionPromedioByPersonaId(personaId)
                .orElse(0.0);
        stats.put("valoracionPromedio", valoracionPromedio);

        return stats;
    }

    /**
     * Obtiene últimas asignaciones
     */
    public List<Map<String, Object>> obtenerUltimasAsignaciones(int limit) {

        List<Asignacion> asignaciones = asignacionRepository.findAll();

        return asignaciones.stream()
                .sorted((a1, a2) -> a2.getFechaAsignacion().compareTo(a1.getFechaAsignacion()))
                .limit(limit)
                .map(a -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", a.getId());
                    item.put("tarea", a.getTarea().getTitulo());
                    item.put("persona", a.getPersona().getNombre());
                    item.put("fechaAsignacion", a.getFechaAsignacion());
                    item.put("estado", a.getTarea().getEstado());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene métricas de un proyecto
     */
    public Map<String, Object> obtenerMetricasProyecto(Long proyectoId) {

        Map<String, Object> metricas = new HashMap<>();

        List<Tarea> tareas = tareaRepository.findByProyectoId(proyectoId);
        List<Asignacion> asignaciones = asignacionRepository.findByProyectoId(proyectoId);

        // Tareas por estado
        Map<String, Long> tareasPorEstado = tareas.stream()
                .collect(Collectors.groupingBy(Tarea::getEstado, Collectors.counting()));
        metricas.put("tareasPorEstado", tareasPorEstado);

        // Progreso del proyecto
        long tareasCompletadas = tareas.stream()
                .filter(t -> "completada".equals(t.getEstado()))
                .count();
        double progreso = tareas.isEmpty() ? 0 :
                (tareasCompletadas * 100.0) / tareas.size();
        metricas.put("progreso", Math.round(progreso));

        // Asignaciones
        metricas.put("totalAsignaciones", asignaciones.size());
        metricas.put("asignacionesCompletadas",
                asignaciones.stream()
                        .filter(a -> a.getFechaCompletada() != null)
                        .count());

        return metricas;
    }

    /**
     * Genera datos para Burndown Chart de un sprint
     */
    public List<Map<String, Object>> generarBurndownChart(Long sprintId) {

        List<Asignacion> asignaciones = asignacionRepository.findBySprintId(sprintId);

        // Agrupar por fecha de completado
        Map<LocalDateTime, Long> completadasPorFecha = asignaciones.stream()
                .filter(a -> a.getFechaCompletada() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getFechaCompletada().toLocalDate().atStartOfDay(),
                        Collectors.counting()
                ));

        // Construir serie temporal
        List<Map<String, Object>> puntos = new ArrayList<>();
        long tareasRestantes = asignaciones.size();

        for (Map.Entry<LocalDateTime, Long> entry : completadasPorFecha.entrySet()) {
            Map<String, Object> punto = new HashMap<>();
            punto.put("fecha", entry.getKey());
            tareasRestantes -= entry.getValue();
            punto.put("tareasRestantes", tareasRestantes);
            puntos.add(punto);
        }

        puntos.sort((p1, p2) ->
                ((LocalDateTime) p1.get("fecha")).compareTo((LocalDateTime) p2.get("fecha")));

        return puntos;
    }
}
