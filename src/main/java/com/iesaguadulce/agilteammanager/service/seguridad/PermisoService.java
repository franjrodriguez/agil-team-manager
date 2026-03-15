package com.iesaguadulce.agilteammanager.service.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.Permiso;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.repository.seguridad.PermisoRepository;
import com.iesaguadulce.agilteammanager.repository.seguridad.RolSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para gestión de roles y permisos del sistema
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PermisoService {

    private final RolSistemaRepository rolSistemaRepository;
    private final PermisoRepository permisoRepository;

    /**
     * Obtiene todos los roles del sistema
     */
    @Transactional(readOnly = true)
    public List<RolSistema> obtenerTodosLosRoles() {
        return rolSistemaRepository.findAll();
    }

    /**
     * Obtiene un rol con sus permisos
     */
    @Transactional(readOnly = true)
    public Optional<RolSistema> obtenerRolConPermisos(Long rolId) {
        return rolSistemaRepository.findByIdWithPermisos(rolId);
    }

    /**
     * Obtiene todos los permisos disponibles
     */
    @Transactional(readOnly = true)
    public List<Permiso> obtenerTodosLosPermisos() {
        return permisoRepository.findAll();
    }

    /**
     * Obtiene los permisos de un rol
     */
    @Transactional(readOnly = true)
    public List<Permiso> obtenerPermisosPorRol(Long rolId) {
        return permisoRepository.findByRolId(rolId);
    }

    /**
     * Crea un nuevo permiso y lo asigna al rol indicado
     */
    public Permiso crearPermisoYAsignarARol(Long rolId, String codigo, String descripcion) {
        if (permisoRepository.findByCodigo(codigo).isPresent()) {
            throw new RuntimeException("Ya existe un permiso con el código: " + codigo);
        }
        Permiso permiso = new Permiso();
        permiso.setCodigo(codigo);
        permiso.setDescripcion(descripcion);
        Permiso guardado = permisoRepository.save(permiso);

        // Asignar al rol
        RolSistema rol = rolSistemaRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Hibernate.initialize(rol.getPermisos());
        rol.getPermisos().add(guardado);
        rolSistemaRepository.save(rol);

        return guardado;
    }

    /**
     * Crea un nuevo rol de sistema
     */
    public RolSistema crearRol(String nombre, String descripcion) {
        if (rolSistemaRepository.findByNombre(nombre).isPresent()) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + nombre);
        }
        RolSistema rol = new RolSistema();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        return rolSistemaRepository.save(rol);
    }

    /**
     * Actualiza nombre y descripción de un rol existente
     */
    public RolSistema actualizarRol(Long rolId, String nombre, String descripcion) {
        RolSistema rol = rolSistemaRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        return rolSistemaRepository.save(rol);
    }

    /**
     * Elimina un rol. Lanza excepción si tiene personas asignadas.
     */
    public void eliminarRol(Long rolId) {
        RolSistema rol = rolSistemaRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Hibernate.initialize(rol.getPersonas());
        if (!rol.getPersonas().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el rol: tiene usuarios asignados.");
        }
        rolSistemaRepository.deleteById(rolId);
    }

    /**
     * Asigna permisos a un rol
     */
    public void asignarPermisosARol(Long rolId, Set<Long> permisosIds) {
        RolSistema rol = rolSistemaRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Set<Permiso> permisos = Set.copyOf(permisoRepository.findAllById(permisosIds));
        rol.setPermisos(permisos);

        rolSistemaRepository.save(rol);
    }

    /**
     * Verifica si un rol tiene un permiso específico
     */
    @Transactional(readOnly = true)
    public boolean rolTienePermiso(Long rolId, String codigoPermiso) {
        RolSistema rol = rolSistemaRepository.findByIdWithPermisos(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        return rol.getPermisos().stream()
                .anyMatch(p -> p.getCodigo().equals(codigoPermiso));
    }
}
