package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;

/**
 * Interfaz que deben implementar los controllers de los drawers (Sprint y Tarea).
 * Permite que el controller padre (ProyectosController) les pase contexto
 * y que los hijos puedan cerrar el drawer y refrescar los datos.
 */
public interface DrawerChildController {

    void setParentController(ProyectosController parent);

    void setProyectoActual(Proyecto proyecto);
}