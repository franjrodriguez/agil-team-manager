package com.iesaguadulce.agilteammanager.model.proyectos;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta para TareaCompetencia.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaCompetenciaId implements Serializable {

    /** ID de la tarea. */
    private Long tareaId;

    /** ID de la competencia. */
    private Long competenciaId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TareaCompetenciaId)) return false;
        TareaCompetenciaId that = (TareaCompetenciaId) o;
        return Objects.equals(tareaId, that.tareaId) &&
                Objects.equals(competenciaId, that.competenciaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tareaId, competenciaId);
    }
}
