package com.iesaguadulce.agilteammanager.repository.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {
}
