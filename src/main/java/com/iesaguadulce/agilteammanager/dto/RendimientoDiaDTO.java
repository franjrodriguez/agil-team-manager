package com.iesaguadulce.agilteammanager.dto;

import java.time.LocalDate;

/**
 * DTO para datos del gráfico de rendimiento por día
 */
public class RendimientoDiaDTO {
    private LocalDate fecha;
    private long tareasCompletadas;

    public RendimientoDiaDTO(LocalDate fecha, long tareasCompletadas) {
        this.fecha = fecha;
        this.tareasCompletadas = tareasCompletadas;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public long getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(long tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }

    /**
     * Devuelve el día de la semana (ej: "Lun", "Mar")
     */
    public String getDiaSemana() {
        String[] dias = {"", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        return dias[fecha.getDayOfWeek().getValue()];
    }
}
