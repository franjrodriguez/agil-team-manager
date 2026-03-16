package com.iesaguadulce.agilteammanager.service.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.Configuracion;
import com.iesaguadulce.agilteammanager.repository.seguridad.ConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfiguracionService {

    // ── Claves de configuración ──────────────────────────────
    public static final String MOTOR_CARGA_MAXIMA        = "motor.carga.maxima";
    public static final String MOTOR_COMPETENCIA_MINIMA  = "motor.competencia.minima";
    public static final String MOTOR_CANDIDATOS_MAX      = "motor.candidatos.maximos";

    private final ConfiguracionRepository configuracionRepository;

    // ── Lectura genérica con valor por defecto ───────────────

    @Transactional(readOnly = true)
    public String obtener(String clave, String valorDefault) {
        return configuracionRepository.findById(clave)
                .map(Configuracion::getValor)
                .orElse(valorDefault);
    }

    // ── Escritura genérica ───────────────────────────────────

    public void guardar(String clave, String valor, String descripcion) {
        Configuracion cfg = configuracionRepository.findById(clave)
                .orElse(new Configuracion(clave, valor, descripcion));
        cfg.setValor(valor);
        configuracionRepository.save(cfg);
    }

    // ── Acceso tipado a parámetros del motor ─────────────────

    @Transactional(readOnly = true)
    public int obtenerCargaMaxima() {
        return Integer.parseInt(obtener(MOTOR_CARGA_MAXIMA, "80"));
    }

    @Transactional(readOnly = true)
    public int obtenerCompetenciaMinima() {
        return Integer.parseInt(obtener(MOTOR_COMPETENCIA_MINIMA, "1"));
    }

    @Transactional(readOnly = true)
    public int obtenerCandidatosMaximos() {
        return Integer.parseInt(obtener(MOTOR_CANDIDATOS_MAX, "5"));
    }

    public void guardarMotorParams(int cargaMaxima, int competenciaMinima, int candidatosMax) {
        guardar(MOTOR_CARGA_MAXIMA,
                String.valueOf(cargaMaxima),
                "% máximo de carga de trabajo para considerar un profesional como candidato");
        guardar(MOTOR_COMPETENCIA_MINIMA,
                String.valueOf(competenciaMinima),
                "Nivel mínimo de competencia requerido (1-5)");
        guardar(MOTOR_CANDIDATOS_MAX,
                String.valueOf(candidatosMax),
                "Número máximo de candidatos a mostrar en el motor");
    }
}
