package com.kinormotors.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "promociones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "modelo_slug", length = 80)
    private String modeloSlug;

    @Column(name = "vigente_desde")
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private int orden;
}
