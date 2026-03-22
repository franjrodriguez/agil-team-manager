package com.iesaguadulce.agilteammanager.dto;

/**
 * DTO para mostrar personas activas en la tabla del equipo.
 * Calcula automáticamente el color del indicador según la carga de trabajo.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
public class PersonaActivaDTO {
    private String nombre;
    private double cargaPorcentaje;  // 0.75 → 75%
    private int numTareasActivas;
    private String fotoPath;

    // Campos calculados
    private String colorIndicador;   // "verde", "amarillo", "rojo"

    /**
     * Constructor. Calcula el color del indicador automáticamente.
     *
     * @param cargaPorcentaje valor entre 0.0 y 1.0 (0.75 = 75%)
     */
    public PersonaActivaDTO(String nombre, double cargaPorcentaje, int numTareasActivas, String fotoPath) {
        this.nombre = nombre;
        this.cargaPorcentaje = cargaPorcentaje;
        this.numTareasActivas = numTareasActivas;
        this.fotoPath = fotoPath;

        // Calcular color del indicador según carga
        if (cargaPorcentaje < 0.5) {
            this.colorIndicador = "verde";
        } else if (cargaPorcentaje < 0.8) {
            this.colorIndicador = "amarillo";
        } else {
            this.colorIndicador = "rojo";
        }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getCargaPorcentaje() { return cargaPorcentaje; }
    public void setCargaPorcentaje(double cargaPorcentaje) { this.cargaPorcentaje = cargaPorcentaje; }

    public int getNumTareasActivas() { return numTareasActivas; }
    public void setNumTareasActivas(int numTareasActivas) { this.numTareasActivas = numTareasActivas; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }

    public String getColorIndicador() { return colorIndicador; }
    public void setColorIndicador(String colorIndicador) { this.colorIndicador = colorIndicador; }

    /**
     * Devuelve la carga formateada como porcentaje (ej: "75%").
     *
     * @return cadena con formato "##%"
     */
    public String getCargaFormateada() {
        return String.format("%.0f%%", cargaPorcentaje * 100);
    }
}
