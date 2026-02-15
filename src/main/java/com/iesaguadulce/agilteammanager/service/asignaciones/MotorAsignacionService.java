package com.iesaguadulce.agilteammanager.service.asignaciones;

import com.iesaguadulce.agilteammanager.model.asignaciones.AsignacionSugerida;
import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.personas.PersonaCompetencia;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetencia;
import com.iesaguadulce.agilteammanager.repository.asignaciones.AsignacionSugeridaRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaCompetenciaRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.TareaCompetenciaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.TareaRepository;
import com.iesaguadulce.agilteammanager.service.personas.DisponibilidadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ⭐ SERVICIO CORE DEL SISTEMA
 *
 * Implementa el motor de recomendación inteligente para asignación de tareas.
 *
 * Algoritmo:
 * 1. Calcula score_base = Σ(nivel_persona × peso_competencia)
 * 2. Calcula score_ajustado = (score_base / 100) × (1 - carga) × prioridad
 * 3. Ordena candidatos por score_ajustado descendente
 * 4. Genera sugerencias con explicación detallada
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MotorAsignacionService {

    private final TareaRepository tareaRepository;
    private final PersonaRepository personaRepository;
    private final TareaCompetenciaRepository tareaCompetenciaRepository;
    private final PersonaCompetenciaRepository personaCompetenciaRepository;
    private final AsignacionSugeridaRepository asignacionSugeridaRepository;
    private final DisponibilidadService disponibilidadService;

    /**
     * Calcula y genera sugerencias de asignación para una tarea
     *
     * @param tareaId ID de la tarea
     * @return Lista de sugerencias ordenadas por idoneidad (mejor primero)
     */
    public List<AsignacionSugerida> calcularAsignaciones(Long tareaId) {

        log.info("🎯 Iniciando cálculo de asignaciones para tarea {}", tareaId);

        // 1. Obtener la tarea con sus competencias requeridas
        Tarea tarea = tareaRepository.findByIdWithCompetencias(tareaId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        List<TareaCompetencia> competenciasRequeridas =
                tareaCompetenciaRepository.findByTareaId(tareaId);

        if (competenciasRequeridas.isEmpty()) {
            throw new RuntimeException("La tarea no tiene competencias requeridas definidas");
        }

        // 2. Validar que la suma de pesos sea 1
        BigDecimal sumaPesos = competenciasRequeridas.stream()
                .map(TareaCompetencia::getPeso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumaPesos.compareTo(BigDecimal.ONE) != 0) {
            log.warn("⚠️ Suma de pesos no es 1.0, normalizando automáticamente");
            normalizarPesos(competenciasRequeridas);
        }

        // 3. Obtener personas activas
        List<Persona> candidatos = personaRepository.findActivasWithCompetencias();

        if (candidatos.isEmpty()) {
            throw new RuntimeException("No hay personas activas disponibles");
        }

        log.info("📊 Evaluando {} candidatos", candidatos.size());

        // 4. Calcular scores para cada candidato
        List<AsignacionSugerida> sugerencias = new ArrayList<>();

        for (Persona persona : candidatos) {
            AsignacionSugerida sugerencia = calcularSugerencia(tarea, persona, competenciasRequeridas);
            sugerencias.add(sugerencia);
        }

        // 5. Ordenar por score ajustado descendente
        sugerencias.sort((s1, s2) -> s2.getScoreAjustado().compareTo(s1.getScoreAjustado()));

        // 6. Eliminar sugerencias antiguas de esta tarea
        asignacionSugeridaRepository.deleteByTareaId(tareaId);

        // 7. Guardar nuevas sugerencias
        sugerencias = asignacionSugeridaRepository.saveAll(sugerencias);

        log.info("✅ Cálculo completado. Top 3:");
        sugerencias.stream().limit(3).forEach(s ->
                log.info("  - {} → Score: {}",
                        s.getPersona().getNombre(),
                        s.getScoreAjustado())
        );

        return sugerencias;
    }

    /**
     * Calcula la sugerencia para un candidato específico
     */
    private AsignacionSugerida calcularSugerencia(Tarea tarea, Persona persona,
                                                  List<TareaCompetencia> competenciasRequeridas) {

        // PASO 1: Calcular score base (aptitud técnica pura)
        BigDecimal scoreBase = calcularScoreBase(persona, competenciasRequeridas);

        // PASO 2: Obtener carga actual
        BigDecimal carga = disponibilidadService.obtenerCargaActual(persona.getId());

        // PASO 3: Calcular score ajustado
        BigDecimal scoreAjustado = calcularScoreAjustado(scoreBase, carga, tarea.getPrioridad());

        // PASO 4: Crear sugerencia
        AsignacionSugerida sugerencia = new AsignacionSugerida();
        sugerencia.setTarea(tarea);
        sugerencia.setPersona(persona);
        sugerencia.setScoreBase(scoreBase);
        sugerencia.setScoreAjustado(scoreAjustado);
        sugerencia.setFechaCalculo(LocalDateTime.now());

        return sugerencia;
    }

    /**
     * Calcula el score base (aptitud técnica)
     *
     * Formula: score_base = Σ(nivel_actual × peso)
     *
     * @return Score entre 0 y 100
     */
    private BigDecimal calcularScoreBase(Persona persona, List<TareaCompetencia> competenciasRequeridas) {

        BigDecimal scoreBase = BigDecimal.ZERO;

        for (TareaCompetencia tc : competenciasRequeridas) {
            Long competenciaId = tc.getCompetencia().getId();
            BigDecimal peso = tc.getPeso();

            // Obtener nivel actual de la persona en esta competencia
            Optional<Integer> nivelOpt = personaCompetenciaRepository
                    .findNivelActual(persona.getId(), competenciaId);

            int nivel = nivelOpt.orElse(0); // Si no tiene la competencia, nivel = 0

            // Calcular aporte: nivel × peso
            BigDecimal aporte = BigDecimal.valueOf(nivel).multiply(peso);
            scoreBase = scoreBase.add(aporte);
        }

        return scoreBase.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el score ajustado (idoneidad final)
     *
     * Formula: score_ajustado = (score_base / 100) × (1 - carga) × prioridad
     *
     * @return Score entre 0 y 1
     */
    private BigDecimal calcularScoreAjustado(BigDecimal scoreBase, BigDecimal carga,
                                             BigDecimal prioridad) {

        // Normalizar score base a rango 0-1
        BigDecimal scoreNormalizado = scoreBase.divide(
                BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP
        );

        // Calcular disponibilidad (1 - carga)
        BigDecimal disponibilidad = BigDecimal.ONE.subtract(carga);

        // Aplicar formula
        BigDecimal scoreAjustado = scoreNormalizado
                .multiply(disponibilidad)
                .multiply(prioridad);

        return scoreAjustado.setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Normaliza los pesos de competencias para que sumen 1
     */
    private void normalizarPesos(List<TareaCompetencia> competencias) {
        BigDecimal sumaActual = competencias.stream()
                .map(TareaCompetencia::getPeso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (TareaCompetencia tc : competencias) {
            BigDecimal pesoNormalizado = tc.getPeso()
                    .divide(sumaActual, 4, RoundingMode.HALF_UP);
            tc.setPeso(pesoNormalizado);
            tareaCompetenciaRepository.save(tc);
        }
    }

    /**
     * Obtiene las N mejores sugerencias para una tarea
     */
    @Transactional(readOnly = true)
    public List<AsignacionSugerida> obtenerTopSugerencias(Long tareaId, int limit) {
        return asignacionSugeridaRepository.findTopNByTareaId(tareaId, limit);
    }

    /**
     * Obtiene la mejor sugerencia para una tarea
     */
    @Transactional(readOnly = true)
    public AsignacionSugerida obtenerMejorSugerencia(Long tareaId) {
        return asignacionSugeridaRepository.findMejorSugerenciaByTareaId(tareaId);
    }

    /**
     * Obtiene todas las sugerencias para una tarea con detalles completos
     */
    @Transactional(readOnly = true)
    public List<AsignacionSugerida> obtenerSugerenciasConDetalles(Long tareaId) {
        return asignacionSugeridaRepository.findByTareaIdWithDetails(tareaId);
    }

    /**
     * Verifica si existen sugerencias recientes (últimas 24h)
     */
    @Transactional(readOnly = true)
    public boolean existenSugerenciasRecientes(Long tareaId) {
        LocalDateTime hace24h = LocalDateTime.now().minusHours(24);
        return asignacionSugeridaRepository.existeSugerenciaRecienteByTareaId(tareaId, hace24h);
    }

    /**
     * Genera explicación detallada de una sugerencia
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generarExplicacion(Long sugerenciaId) {

        AsignacionSugerida sugerencia = asignacionSugeridaRepository.findById(sugerenciaId)
                .orElseThrow(() -> new RuntimeException("Sugerencia no encontrada"));

        Tarea tarea = sugerencia.getTarea();
        Persona persona = sugerencia.getPersona();

        // Obtener competencias requeridas
        List<TareaCompetencia> competenciasRequeridas =
                tareaCompetenciaRepository.findByTareaId(tarea.getId());

        // Calcular desglose por competencia
        List<Map<String, Object>> desglose = new ArrayList<>();

        for (TareaCompetencia tc : competenciasRequeridas) {
            Long competenciaId = tc.getCompetencia().getId();
            BigDecimal peso = tc.getPeso();

            Optional<Integer> nivelOpt = personaCompetenciaRepository
                    .findNivelActual(persona.getId(), competenciaId);

            int nivel = nivelOpt.orElse(0);
            BigDecimal aporte = BigDecimal.valueOf(nivel).multiply(peso);

            Map<String, Object> item = new HashMap<>();
            item.put("competencia", tc.getCompetencia().getNombre());
            item.put("nivel", nivel);
            item.put("peso", peso.multiply(BigDecimal.valueOf(100)).intValue() + "%");
            item.put("aporte", aporte.setScale(2, RoundingMode.HALF_UP));

            desglose.add(item);
        }

        // Obtener carga actual
        BigDecimal carga = disponibilidadService.obtenerCargaActual(persona.getId());
        BigDecimal disponibilidad = BigDecimal.ONE.subtract(carga);

        // Construir explicación
        Map<String, Object> explicacion = new HashMap<>();
        explicacion.put("persona", persona.getNombre());
        explicacion.put("tarea", tarea.getTitulo());
        explicacion.put("scoreBase", sugerencia.getScoreBase());
        explicacion.put("scoreAjustado", sugerencia.getScoreAjustado());
        explicacion.put("desglosePorCompetencia", desglose);
        explicacion.put("cargaActual", carga.multiply(BigDecimal.valueOf(100)).intValue() + "%");
        explicacion.put("disponibilidad", disponibilidad.multiply(BigDecimal.valueOf(100)).intValue() + "%");
        explicacion.put("prioridad", tarea.getPrioridad());
        explicacion.put("fechaCalculo", sugerencia.getFechaCalculo());

        return explicacion;
    }
}
