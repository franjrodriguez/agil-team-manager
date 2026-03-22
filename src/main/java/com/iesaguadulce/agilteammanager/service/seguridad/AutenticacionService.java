package com.iesaguadulce.agilteammanager.service.seguridad;

import com.iesaguadulce.agilteammanager.model.personas.Persona;
import com.iesaguadulce.agilteammanager.repository.personas.PersonaRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio de autenticación con BCrypt.
 *
 * @author Francisco José Rodríguez Ruiz
 * @since 1.0
 */
@Service
public class AutenticacionService {

    /**
     * Autentica un usuario por username y password.
     *
     * @return Persona si es válida y activa, null en caso contrario
     */
    @Autowired
    private PersonaRepository personaRepository;

    public Persona autenticar(String username, String password) {
        Optional<Persona> personaOpt = personaRepository.findByUsuario(username);

        if (personaOpt.isEmpty()) {
            System.err.println("❌ Usuario no encontrado: " + username);
            return null;
        }

        Persona persona = personaOpt.get();

        if (!"activo".equalsIgnoreCase(persona.getEstado())) {
            System.err.println("❌ Usuario inactivo: " + username);
            return null;
        }

        // Usar jBCrypt en lugar de Spring Security
        if (!BCrypt.checkpw(password, persona.getPassword())) {
            System.err.println("❌ Contraseña incorrecta para: " + username);
            return null;
        }

        System.out.println("✅ Login exitoso: " + username);
        return persona;
    }

    /**
     * Verifica si la persona tiene rol ADMIN.
     */
    public boolean esAdministrador(Persona persona) {
        return persona != null &&
                "ADMIN".equalsIgnoreCase(persona.getRol().getNombre());
    }

    /**
     * Genera hash BCrypt de una contraseña.
     *
     * @param password contraseña en texto plano
     * @return hash BCrypt
     */
    public String encriptarPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
