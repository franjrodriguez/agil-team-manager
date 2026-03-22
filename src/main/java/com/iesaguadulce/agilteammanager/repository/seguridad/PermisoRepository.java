package com.iesaguadulce.agilteammanager.repository.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de permisos del sistema.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    /**
     * Busca un permiso por su código único
     */
    Optional<Permiso> findByCodigo(String codigo);

    /**
     * Verifica si existe un permiso con ese código
     */
    boolean existsByCodigo(String codigo);

    /**
     * Obtiene permisos asociados a un rol.
     * @param rolId ID del rol
     */
    @Query("SELECT p FROM Permiso p JOIN p.roles r WHERE r.id = :rolId")
    List<Permiso> findByRolId(Long rolId);

    /**
     * Busca permisos por código que contenga el texto (ignora mayusculas)
     */
    List<Permiso> findByCodigoContainingIgnoreCase(String codigo);
}
