package com.iesaguadulce.agilteammanager.service.asignaciones;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Calculador de scores para el motor de asignación.
 * Encapsula la lógica matemática pura para facilitar su testing.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Component
public class ScoreCalculator {

    /**
     * Calcula el score base (aptitud técnica pura).
     * Fórmula: score_base = Σ(nivel × peso)
     *
     * @param niveles lista de niveles de cada competencia (0-100)
     * @param pesos   lista de pesos de cada competencia (suma debe ser 1.0)
     * @return score entre 0 y 100
     */
    public BigDecimal calcularScoreBase(List<Integer> niveles, List<BigDecimal> pesos) {
        if (niveles.size() != pesos.size()) {
            throw new IllegalArgumentException("Listas de niveles y pesos deben tener el mismo tamaño");
        }

        BigDecimal scoreBase = BigDecimal.ZERO;
        for (int i = 0; i < niveles.size(); i++) {
            BigDecimal aporte = BigDecimal.valueOf(niveles.get(i)).multiply(pesos.get(i));
            scoreBase = scoreBase.add(aporte);
        }
        return scoreBase.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el score ajustado (idoneidad final).
     * Fórmula: score_ajustado = (score_base / 100) × (1 - carga) × prioridad
     *
     * @param scoreBase  resultado de calcularScoreBase()
     * @param carga      carga actual de la persona (0.0 a 1.0)
     * @param prioridad  prioridad de la tarea (0.0 a 1.0)
     * @return score entre 0 y 1
     */
    public BigDecimal calcularScoreAjustado(BigDecimal scoreBase,
                                            BigDecimal carga,
                                            BigDecimal prioridad) {
        BigDecimal scoreNormalizado = scoreBase.divide(
                BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal disponibilidad = BigDecimal.ONE.subtract(carga);

        return scoreNormalizado
                .multiply(disponibilidad)
                .multiply(prioridad)
                .setScale(4, RoundingMode.HALF_UP);
    }
}
