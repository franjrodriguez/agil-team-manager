package com.iesaguadulce.agilteammanager.service.proyectos;

import com.iesaguadulce.agilteammanager.model.personas.Competencia;
import com.iesaguadulce.agilteammanager.model.proyectos.Tarea;
import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetencia;
import com.iesaguadulce.agilteammanager.model.proyectos.TareaCompetenciaId;
import com.iesaguadulce.agilteammanager.repository.personas.CompetenciaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.TareaCompetenciaRepository;
import com.iesaguadulce.agilteammanager.repository.proyectos.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para la relación TareaCompetencia.
 *
 * Gestiona las competencias técnicas requeridas por cada tarea.
 * NO confundir con CompetenciaService, que gestiona el catálogo maestro
 * de competencias (entidad Competencia en model/personas/).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TareaCompetenciaService {

    private final TareaCompetenciaRepository tareaCompetenciaRepository;
    private final TareaRepository             tareaRepository;
    private final CompetenciaRepository       competenciaRepository;

    // ══════════════════════════════════════════════════════════════
    //  CONSULTA
    // ══════════════════════════════════════════════════════════════

    /**
     * Devuelve todas las TareaCompetencia de una tarea (ordenadas por peso desc).
     * Inicializa las relaciones lazy para evitar LazyInitializationException en el controller UI.
     */
    @Transactional(readOnly = true)
    public List<TareaCompetencia> obtenerPorTarea(Long tareaId) {
        List<TareaCompetencia> lista = tareaCompetenciaRepository.findByTareaId(tareaId);
        lista.forEach(tc -> Hibernate.initialize(tc.getCompetencia()));
        return lista;
    }

    // ══════════════════════════════════════════════════════════════
    //  AÑADIR
    // ══════════════════════════════════════════════════════════════

    /**
     * Añade una competencia requerida a una tarea.
     *
     * @param tareaId       ID de la tarea
     * @param competenciaId ID de la competencia del catálogo
     * @param peso          Relevancia de la competencia para la tarea (0.00 – 1.00)
     * @throws RuntimeException si la relación ya existe o el peso es inválido
     */
    public TareaCompetencia añadir(Long tareaId, Long competenciaId, BigDecimal peso) {
        validarPeso(peso);

        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada: " + tareaId));

        Competencia competencia = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new RuntimeException("Competencia no encontrada: " + competenciaId));

        TareaCompetenciaId id = new TareaCompetenciaId();
        id.setTareaId(tareaId);
        id.setCompetenciaId(competenciaId);

        if (tareaCompetenciaRepository.existsById(id)) {
            throw new RuntimeException(
                    "La competencia \"" + competencia.getNombre() + "\" ya está asignada a esta tarea.");
        }

        TareaCompetencia tc = new TareaCompetencia();
        tc.setId(id);
        tc.setTarea(tarea);
        tc.setCompetencia(competencia);
        tc.setPeso(peso);

        return tareaCompetenciaRepository.save(tc);
    }

    // ══════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ══════════════════════════════════════════════════════════════

    /**
     * Elimina una competencia requerida de una tarea.
     */
    public void eliminar(Long tareaId, Long competenciaId) {
        TareaCompetenciaId id = new TareaCompetenciaId();
        id.setTareaId(tareaId);
        id.setCompetenciaId(competenciaId);

        if (!tareaCompetenciaRepository.existsById(id)) {
            throw new RuntimeException("La relación tarea-competencia no existe.");
        }
        tareaCompetenciaRepository.deleteById(id);
    }

    // ══════════════════════════════════════════════════════════════
    //  UTILIDADES PRIVADAS
    // ══════════════════════════════════════════════════════════════

    private void validarPeso(BigDecimal peso) {
        if (peso == null
                || peso.compareTo(BigDecimal.ZERO) < 0
                || peso.compareTo(BigDecimal.ONE) > 0) {
            throw new RuntimeException("El peso debe estar entre 0.00 y 1.00");
        }
    }
}
