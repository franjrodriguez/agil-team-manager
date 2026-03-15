package com.iesaguadulce.agilteammanager.service.seguridad;

import com.iesaguadulce.agilteammanager.model.seguridad.RolSistema;
import com.iesaguadulce.agilteammanager.repository.seguridad.RolSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RolSistemaService {

    private final RolSistemaRepository rolSistemaRepository;

    @Transactional(readOnly = true)
    public List<RolSistema> obtenerTodos() {
        return rolSistemaRepository.findAll();
    }

    public long contarTotal() { return rolSistemaRepository.count(); }
}