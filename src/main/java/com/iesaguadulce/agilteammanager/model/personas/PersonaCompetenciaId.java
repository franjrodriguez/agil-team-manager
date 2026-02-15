package com.iesaguadulce.agilteammanager.model.personas;

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
public class PersonaCompetenciaId implements Serializable {

    private Long personaId;
    private Long competenciaId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonaCompetenciaId)) return false;
        PersonaCompetenciaId that = (PersonaCompetenciaId) o;
        return Objects.equals(personaId, that.personaId) &&
                Objects.equals(competenciaId, that.competenciaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personaId, competenciaId);
    }
}
