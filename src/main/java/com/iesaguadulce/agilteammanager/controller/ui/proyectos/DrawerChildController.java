package com.iesaguadulce.agilteammanager.controller.ui.proyectos;

import com.iesaguadulce.agilteammanager.model.proyectos.Proyecto;
import com.iesaguadulce.agilteammanager.model.proyectos.Sprint;

/**
 * Interfaz para controllers de drawers (Sprint y Tarea).
 * Permite la comunicación bidireccional con {@link ProyectosController}.
 */
public interface DrawerChildController {

    /** Establece el controller padre para callbacks de cierre y refresco. */
    void setParentController(ProyectosController parent);

    /** Establece el proyecto en contexto. */
    void setProyectoActual(Proyecto proyecto);

    /** Establece el sprint para asociar nuevas tareas (modo creación). */
    default void setSprintActual(Sprint sprint) {}

    /** Establece el sprint a editar; null indica modo creación. */
    default void setSprintAEditar(Sprint sprint) {}

    /** Establece la tarea a editar; null indica modo creación. */
    default void setTareaAEditar(com.iesaguadulce.agilteammanager.model.proyectos.Tarea tarea) {}
}