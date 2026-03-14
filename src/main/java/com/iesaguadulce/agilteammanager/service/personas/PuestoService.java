package com.iesaguadulce.agilteammanager.service.personas;

import com.iesaguadulce.agilteammanager.model.personas.Puesto;
import com.iesaguadulce.agilteammanager.repository.personas.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de puestos profesionales
 */
@Service
@Transactional
public class PuestoService {

    private final PuestoRepository puestoRepository;

    public PuestoService(PuestoRepository puestoRepository) {
        this.puestoRepository = puestoRepository;
    }

    /**
     * Crea un nuevo puesto
     */
    public Puesto crear(String nombre, String descripcion) {
        if (puestoRepository.existsByNombre(nombre)) {
            throw new RuntimeException("Ya existe un puesto con ese nombre");
        }

        Puesto puesto = new Puesto();
        puesto.setNombre(nombre);
        puesto.setDescripcion(descripcion);

        return puestoRepository.save(puesto);
    }

    /**
     * Actualiza un puesto existente
     */
    public Puesto actualizar(Long id, String nombre, String descripcion) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));

        // Validar nombre único si cambió
        if (!puesto.getNombre().equals(nombre) && puestoRepository.existsByNombre(nombre)) {
            throw new RuntimeException("Ya existe un puesto con ese nombre");
        }

        puesto.setNombre(nombre);
        puesto.setDescripcion(descripcion);

        return puestoRepository.save(puesto);
    }

    public Puesto guardar(Puesto puesto) {
        // Validación: nombre no puede estar vacío
        if (puesto.getNombre() == null || puesto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del puesto no puede estar vacío.");
        }

        // Validación: nombre único (comprobamos si existe otro con el mismo nombre)
        Optional<Puesto> existente = puestoRepository.findByNombre(puesto.getNombre().trim());
        if (existente.isPresent() && !existente.get().getId().equals(puesto.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe un puesto con el nombre: " + puesto.getNombre()
            );
        }

        // Limpiamos espacios extra del nombre antes de guardar
        puesto.setNombre(puesto.getNombre().trim());

        return puestoRepository.save(puesto);
    }

    /**
     * Elimina un puesto (solo si no tiene personas asignadas)
     */
    public void eliminar(Long id) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));

        if (!puesto.getPersonas().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un puesto con personas asignadas");
        }

        puestoRepository.delete(puesto);
    }

    /**
     * Obtiene todos los puestos
     */
    @Transactional(readOnly = true)
    public List<Puesto> obtenerTodos() {
        return puestoRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                .toList();
    }

    /**
     * Obtiene un puesto por ID
     */
    @Transactional(readOnly = true)
    public Optional<Puesto> obtenerPorId(Long id) {
        return puestoRepository.findById(id);
    }

    /**
     * Cuenta las personas asignadas a un puesto.
     * Se ejecuta dentro de la transacción para evitar LazyInitializationException.
     */
    @Transactional(readOnly = true)
    public long contarPersonasPorPuesto(Long puestoId) {
        return puestoRepository.findById(puestoId)
                .map(p -> (long) p.getPersonas().size())
                .orElse(0L);
    }

    /**
     * Busca puestos por nombre
     */
    @Transactional(readOnly = true)
    public List<Puesto> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            return obtenerTodos();
        }
        return puestoRepository.findByNombreContainingIgnoreCase(texto.trim());
    }
}
