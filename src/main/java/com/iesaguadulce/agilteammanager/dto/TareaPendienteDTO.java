package com.iesaguadulce.agilteammanager.dto;

/**
 * DTO para mostrar tareas pendientes en la tabla.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
public class TareaPendienteDTO {
    private String nombreTarea;
    private String nombreSprint;

    /**
     * Constructor. Si el sprint es null, asigna "Sin Sprint".
     */
    public TareaPendienteDTO(String nombreTarea, String nombreSprint) {
        this.nombreTarea = nombreTarea;
        this.nombreSprint = nombreSprint != null ? nombreSprint : "Sin Sprint";
    }

    // Getters y Setters
    public String getNombreTarea() { return nombreTarea; }
    public void setNombreTarea(String nombreTarea) { this.nombreTarea = nombreTarea; }

    public String getNombreSprint() { return nombreSprint; }
    public void setNombreSprint(String nombreSprint) { this.nombreSprint = nombreSprint; }
}
