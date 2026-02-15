package com.iesaguadulce.agilteammanager.service.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.Permiso;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.repository.seguridad.PermisoRepository;
import com.iesaguadulce.agilteammanager.repository.seguridad.RolSistemaRepository;
import lombok.RequiredArgsConstructor;
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
