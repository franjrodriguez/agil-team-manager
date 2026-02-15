package com.iesaguadulce.agilteammanager.repository.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolSistemaRepository extends JpaRepository<RolSistema, Long> {

    /**
     * Busca un rol por su nombre exacto
     */
    Optional<RolSistema> findByNombre(String nombre);

    /**
     * Verifica si existe un rol con ese nombre
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca un rol con todos sus permisos cargados
     */
    @Query("SELECT r FROM RolSistema r LEFT JOIN FETCH r.permisos WHERE r.id = :id")
    Optional<RolSistema> findByIdWithPermisos(Long id);

    /**
     * Busca un rol por nombre con permisos cargados
     */
    @Query("SELECT r FROM RolSistema r LEFT JOIN FETCH r.permisos WHERE r.nombre = :nombre")
    Optional<RolSistema> findByNombreWithPermisos(String nombre);
}
