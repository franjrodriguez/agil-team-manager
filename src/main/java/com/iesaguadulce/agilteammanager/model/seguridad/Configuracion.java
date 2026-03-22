package com.iesaguadulce.agilteammanager.model.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad para parámetros de configuración del sistema.
 *
 * <p>Almacena pares clave-valor con descripción opcional.</p>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 */
@Entity
@Table(name = "configuracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion {

    /** Clave única del parámetro (PK). */
    @Id
    @Column(name = "clave", nullable = false, length = 100)
    private String clave;

    /** Valor del parámetro. */
    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    /** Descripción opcional del parámetro. */
    @Column(name = "descripcion", length = 500)
    private String descripcion;
}
