package com.iesaguadulce.agilteammanager.dto;

/**
 * DTO para agrupar los KPIs del Dashboard principal
 */
public class DashboardKPIs {
    private long proyectosActivos;
    private long tareasPendientes;
    private long personasActivas;
    private double cargaPromedioEquipo;

    // Constructor
    public DashboardKPIs(long proyectosActivos, long tareasPendientes,
                         long personasActivas, double cargaPromedioEquipo) {
        this.proyectosActivos = proyectosActivos;
        this.tareasPendientes = tareasPendientes;
        this.personasActivas = personasActivas;
        this.cargaPromedioEquipo = cargaPromedioEquipo;
    }

    // Getters y Setters
    public long getProyectosActivos() { return proyectosActivos; }
    public void setProyectosActivos(long proyectosActivos) { this.proyectosActivos = proyectosActivos; }

    public long getTareasPendientes() { return tareasPendientes; }
    public void setTareasPendientes(long tareasPendientes) { this.tareasPendientes = tareasPendientes; }

    public long getPersonasActivas() { return personasActivas; }
    public void setPersonasActivas(long personasActivas) { this.personasActivas = personasActivas; }

    public double getCargaPromedioEquipo() { return cargaPromedioEquipo; }
    public void setCargaPromedioEquipo(double cargaPromedioEquipo) { this.cargaPromedioEquipo = cargaPromedioEquipo; }
}
