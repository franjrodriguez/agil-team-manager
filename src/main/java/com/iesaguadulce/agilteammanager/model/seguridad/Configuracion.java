package com.iesaguadulce.agilteammanager.model.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion {

    @Id
    @Column(name = "clave", nullable = false, length = 100)
    private String clave;

    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Column(name = "descripcion", length = 500)
    private String descripcion;
}
