package com.iesaguadulce.agilteammanager.model.personas;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta para PersonaCompetencia.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCompetenciaId implements Serializable {

    /** ID de la persona. */
    private Long personaId;

    /** ID de la competencia. */
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