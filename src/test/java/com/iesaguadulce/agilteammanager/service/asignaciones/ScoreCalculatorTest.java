package com.iesaguadulce.agilteammanager.service.asignaciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ScoreCalculator.
 * Verifica la matemática del motor de asignación sin dependencias externas.
 *
 * @author Francisco José Rodríguez Ruiz
 */
class ScoreCalculatorTest {

    private ScoreCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ScoreCalculator();
    }

    // ─────────────────────────────────────────────
    // TEST 1: Caso del ejemplo de la memoria técnica
    // María: Java=90(peso 0.5), SQL=40(peso 0.2), Spring=80(peso 0.3)
    // Esperado: (90×0.5)+(40×0.2)+(80×0.3) = 45+8+24 = 77
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Score base — caso María (memoria técnica)")
    void calcularScoreBase_casoMaria() {
        List<Integer> niveles = List.of(90, 40, 80);
        List<BigDecimal> pesos = List.of(
                new BigDecimal("0.5"),
                new BigDecimal("0.2"),
                new BigDecimal("0.3")
        );

        BigDecimal resultado = calculator.calcularScoreBase(niveles, pesos);

        assertEquals(new BigDecimal("77.00"), resultado);
    }

    // ─────────────────────────────────────────────
    // TEST 2: Score ajustado con los datos de María
    // scoreBase=77, carga=0.3, prioridad=0.8
    // Esperado: (77/100) × (1-0.3) × 0.8 = 0.77 × 0.7 × 0.8 = 0.4312
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Score ajustado — caso María (memoria técnica)")
    void calcularScoreAjustado_casoMaria() {
        BigDecimal scoreBase  = new BigDecimal("77");
        BigDecimal carga      = new BigDecimal("0.3");
        BigDecimal prioridad  = new BigDecimal("0.8");

        BigDecimal resultado = calculator.calcularScoreAjustado(scoreBase, carga, prioridad);

        assertEquals(new BigDecimal("0.4312"), resultado);
    }

    // ─────────────────────────────────────────────
    // TEST 3: Persona sin ninguna competencia requerida
    // Todos los niveles = 0 → score base debe ser 0
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Score base — persona sin competencias (nivel 0 en todo)")
    void calcularScoreBase_sinCompetencias() {
        List<Integer> niveles = List.of(0, 0, 0);
        List<BigDecimal> pesos = List.of(
                new BigDecimal("0.5"),
                new BigDecimal("0.2"),
                new BigDecimal("0.3")
        );

        BigDecimal resultado = calculator.calcularScoreBase(niveles, pesos);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    // ─────────────────────────────────────────────
    // TEST 4: Persona con carga = 1.0 (100% ocupada)
    // Score ajustado debe ser 0 sin importar el score base
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Score ajustado — persona al 100% de carga siempre da 0")
    void calcularScoreAjustado_cargaTotal() {
        BigDecimal scoreBase = new BigDecimal("90");
        BigDecimal carga     = new BigDecimal("1.0");
        BigDecimal prioridad = new BigDecimal("0.8");

        BigDecimal resultado = calculator.calcularScoreAjustado(scoreBase, carga, prioridad);

        assertEquals(new BigDecimal("0.0000"), resultado);
    }
}
