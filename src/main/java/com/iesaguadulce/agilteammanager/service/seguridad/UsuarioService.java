package com.iesaguadulce.agilteammanager.service.seguridad;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.model.personas.Puesto;
import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import com.iesaguadulce.agilteammanager.repository.personas.PuestoRepository;
import com.iesaguadulce.agilteammanager.repository.seguridad.RolSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de gestión de usuarios (Personas).
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final PersonaRepository personaRepository;
    private final RolSistemaRepository rolSistemaRepository;
    private final PuestoRepository puestoRepository;

    /**
     * Crea un nuevo usuario
     */
    public Persona crearUsuario(String usuario, String password, String nombre,
                                String email, Long puestoId, Long rolId) {

        // Validar que no exista el usuario
        if (personaRepository.existsByUsuario(usuario)) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Validar que no exista el email
        if (email != null && personaRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        Puesto puesto = puestoRepository.findById(puestoId)
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));

        RolSistema rol = rolSistemaRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Persona persona = new Persona();
        persona.setUsuario(usuario);
        persona.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        persona.setNombre(nombre);
        persona.setEmail(email);
        persona.setPuesto(puesto);
        persona.setRol(rol);
        persona.setEstado("activo");
        persona.setFechaAlta(LocalDate.now());

        return personaRepository.save(persona);
    }

    /**
     * Actualiza un usuario existente
     */
    public Persona actualizarUsuario(Long id, String nombre, String email,
                                     Long puestoId, Long rolId) {

        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        // Validar email único (si cambió)
        if (email != null && !email.equals(persona.getEmail())
                && personaRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (puestoId != null) {
            Puesto puesto = puestoRepository.findById(puestoId)
                    .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
            persona.setPuesto(puesto);
        }

        if (rolId != null) {
            RolSistema rol = rolSistemaRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            persona.setRol(rol);
        }

        persona.setNombre(nombre);
        persona.setEmail(email);

        return personaRepository.save(persona);
    }

    /**
     * Actualiza las credenciales de acceso de un usuario existente.
     * Solo modifica usuario, password (si se proporciona), rol y estado.
     */
    public Persona actualizarCredenciales(Long id, String usuario, String password,
                                          String rolNombre, String estado) {

        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.equals(persona.getUsuario()) && personaRepository.existsByUsuario(usuario)) {
            throw new RuntimeException("El nombre de usuario '" + usuario + "' ya está en uso");
        }

        persona.setUsuario(usuario);

        if (password != null && !password.isBlank()) {
            persona.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        }

        if (rolNombre != null && !rolNombre.isBlank()) {
            RolSistema rol = rolSistemaRepository.findByNombre(rolNombre)
                    .orElseThrow(() -> new RuntimeException("Rol '" + rolNombre + "' no encontrado"));
            persona.setRol(rol);
        }

        if (estado != null && !estado.isBlank()) {
            if (!List.of("activo", "inactivo", "baja").contains(estado)) {
                throw new RuntimeException("Estado inválido: " + estado);
            }
            persona.setEstado(estado);
        }

        return personaRepository.save(persona);
    }

    /**
     * Activa o desactiva un usuario
     */
    public void cambiarEstado(Long id, String nuevoEstado) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        if (!List.of("activo", "inactivo", "baja").contains(nuevoEstado)) {
            throw new RuntimeException("Estado inválido");
        }

        persona.setEstado(nuevoEstado);
        personaRepository.save(persona);
    }

    /**
     * Obtiene todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<Persona> obtenerTodos() {
        return personaRepository.findAll();
    }

    /**
     * Obtiene usuarios activos
     */
    @Transactional(readOnly = true)
    public List<Persona> obtenerActivos() {
        return personaRepository.findByEstado("activo");
    }

    /**
     * Obtiene un usuario por ID
     */
    @Transactional(readOnly = true)
    public Optional<Persona> obtenerPorId(Long id) {
        return personaRepository.findById(id);
    }

    /**
     * Busca usuarios por nombre
     */
    @Transactional(readOnly = true)
    public List<Persona> buscarPorNombre(String nombre) {
        return personaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene usuarios por rol
     */
    @Transactional(readOnly = true)
    public List<Persona> obtenerPorRol(Long rolId) {
        return personaRepository.findByRolId(rolId);
    }

    /**
     * Cuenta usuarios activos
     */
    @Transactional(readOnly = true)
    public long contarActivos() {
        return personaRepository.countByEstado("activo");
    }
}
