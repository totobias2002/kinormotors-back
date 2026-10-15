package com.kinormotors.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "modelos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String categoria;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "imagen_hero_url", length = 500)
    private String imagenHeroUrl;

    @Column(length = 200)
    private String tagline;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "link_oficial", length = 500)
    private String linkOficial;

    @Column(name = "destacado_hero", nullable = false)
    private boolean destacadoHero;

    @Column(name = "mostrar_financiacion", nullable = false)
    private boolean mostrarFinanciacion;

    @Column(name = "texto_financiacion", length = 200)
    private String textoFinanciacion;

    @Column(nullable = false)
    private int orden;

    @Column(nullable = false)
    private boolean activo;
}
