package com.iesaguadulce.agilteammanager.repository.personas;

import com.iesaguadulce.agilteammanager.model.personas.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de puestos de trabajo.
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    /**
     * Busca puesto por nombre exacto
     */
    Optional<Puesto> findByNombre(String nombre);

    /**
     * Verifica si existe un puesto con ese nombre
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca puestos por nombre (búsqueda parcial)
     */
    List<Puesto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Cuenta cuántas personas tienen asignado cada puesto
     */
    @Query("SELECT p.nombre, COUNT(per) FROM Puesto p " +
            "LEFT JOIN p.personas per " +
            "GROUP BY p.id, p.nombre")
    List<Object[]> countPersonasByPuesto();
}
