package com.kinormotors.entity;

import jakarta.persistence.*;
import lombok.*;

/** Ajustes del sitio editables desde el admin (clave/valor), por ejemplo la financiacion global. */
@Entity
@Table(name = "ajustes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ajuste {

    public static final String MOSTRAR_FINANCIACION = "mostrarFinanciacion";

    @Id
    @Column(length = 80)
    private String clave;

    @Column(nullable = false, length = 500)
    private String valor;
}
