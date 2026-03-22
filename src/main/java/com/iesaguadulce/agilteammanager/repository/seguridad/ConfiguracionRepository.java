package com.iesaguadulce.agilteammanager.repository.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para parámetros de configuración del sistema.
 *
 * <p>La clave primaria es el código del parámetro (String).</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {
}
