package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;

/**
 * Interfaz que deben implementar los controllers de los drawers (Sprint y Tarea).
 * Permite que el controller padre (ProyectosController) les pase contexto
 * y que los hijos puedan cerrar el drawer y refrescar los datos.
 */
public interface DrawerChildController {

    void setParentController(ProyectosController parent);

    void setProyectoActual(Proyecto proyecto);

    /** Solo relevante para NuevaTareaController al crear. */
    default void setSprintActual(Sprint sprint) {}

    /** Pasa un Sprint existente para editar. Si es null → modo creación. */
    default void setSprintAEditar(Sprint sprint) {}

    /** Pasa una Tarea existente para editar. Si es null → modo creación. */
    default void setTareaAEditar(com.iesaguadulce.agilteammanager.model.proyectos.Tarea tarea) {}
}