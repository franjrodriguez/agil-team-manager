package com.iesaguadulce.agilteammanager.model.proyectos;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaCompetenciaId implements Serializable {

    private Long tareaId;
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
