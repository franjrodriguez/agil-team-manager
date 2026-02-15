package com.iesaguadulce.agilteammanager.service.dashboard;

import com.iesaguadulce.agilteammanager.dto.DashboardKPIs;
import com.iesaguadulce.agilteammanager.dto.PersonaActivaDTO;
import com.iesaguadulce.agilteammanager.dto.RendimientoDiaDTO;
import com.iesaguadulce.agilteammanager.dto.TareaPendienteDTO;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.repository.asignaciones.AsignacionRepository;
import com.iesaguadulce.agilteammanager.repository.personas.DisponibilidadRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.ProyectoRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que obtiene los KPIs del Dashboard desde la base de datos
 */
@Service
public class DashboardService {

    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private TareaRepository tareaRepository;
    @Autowired private PersonaRepository personaRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private AsignacionRepository asignacionRepository;

    /**
     * Obtiene todos los KPIs del dashboard en un solo método
     */
    public DashboardKPIs obtenerKPIs() {
        long proyectosActivos = contarProyectosActivos();
        long tareasPendientes = contarTareasPendientes();
        long personasActivas = contarPersonasActivas();
        double cargaPromedio = calcularCargaPromedioEquipo();

        return new DashboardKPIs(proyectosActivos, tareasPendientes, personasActivas, cargaPromedio);
    }

    // ===== MÉTODOS INDIVIDUALES =====

    /**
     * Widget 1: Proyectos activos
     * SELECT COUNT(*) FROM proyectos WHERE estado = 'activo'
     */
    private long contarProyectosActivos() {
        return proyectoRepository.countByEstado("activo");
    }

    /**
     * Widget 2: Tareas pendientes
     * SELECT COUNT(*) FROM tareas WHERE estado = 'pendiente'
     */
    private long contarTareasPendientes() {
        return tareaRepository.countByEstado("pendiente");
    }

    /**
     * Widget 3: Personas activas
     * SELECT COUNT(*) FROM personas WHERE estado = 'activo'
     */
    private long contarPersonasActivas() {
        return personaRepository.countByEstado("activo");
    }

    /**
     * Widget 4: Carga promedio del equipo
     * SELECT AVG(carga) FROM disponibilidad
     * WHERE fecha = (SELECT MAX(fecha) FROM disponibilidad)
     */
    private double calcularCargaPromedioEquipo() {
        Double promedio = disponibilidadRepository.promedioUltimaCarga();
        return (promedio != null) ? promedio : 0.0;
    }

    // ===== MÉTODOS ASOCIADOS A GRAFICA Y TABLAS INFERIORES

    /**
     * 📊 Obtiene datos de rendimiento (tareas completadas) de los últimos 7 días
     */
    public List<RendimientoDiaDTO> obtenerRendimientoUltimos7Dias() {
        List<RendimientoDiaDTO> rendimiento = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            LocalDateTime inicioDia = fecha.atStartOfDay();
            LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();

            // Contar tareas completadas en ese día
            long tareasCompletadas = asignacionRepository.countByFechaCompletadaBetween(inicioDia, finDia);

            rendimiento.add(new RendimientoDiaDTO(fecha, tareasCompletadas));
        }

        return rendimiento;
    }

    /**
     * 📋 Obtiene lista de tareas pendientes (máximo 10)
     */
    public List<TareaPendienteDTO> obtenerTareasPendientes() {
        List<Tarea> tareas = tareaRepository.findTop10ByEstadoOrderByPrioridadDesc("pendiente");

        return tareas.stream()
                .map(t -> new TareaPendienteDTO(
                        t.getTitulo(),
                        t.getSprint() != null ? t.getSprint().getObjetivo() : null
                ))
                .collect(Collectors.toList());
    }

    /**
     * 👥 Obtiene lista de personas activas con su carga y tareas
     */
    public List<PersonaActivaDTO> obtenerEquipoActivo() {
        List<Persona> personasActivas = personaRepository.findByEstado("activo");

        return personasActivas.stream()
                .map(p -> {
                    // Obtener última carga de la persona
                    Double carga = disponibilidadRepository.findUltimaCargaByPersonaId(p.getId());
                    double cargaActual = (carga != null) ? carga : 0.0;

                    // Contar tareas activas (en progreso)
                    int numTareas = asignacionRepository.countByPersonaIdAndTareaEstado(
                            p.getId(), "en_progreso"
                    );

                    return new PersonaActivaDTO(
                            p.getNombre(),
                            cargaActual,
                            numTareas,
                            p.getFotoPath()
                    );
                })
                .collect(Collectors.toList());
    }
}
