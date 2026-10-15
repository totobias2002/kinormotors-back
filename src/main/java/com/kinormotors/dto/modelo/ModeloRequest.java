package com.kinormotors.dto.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ModeloRequest(
        @NotBlank @Size(max = 80) @Pattern(regexp = "^[a-z0-9-]+$", message = "usar minusculas, numeros y guiones") String slug,
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 40) String categoria,
        @Size(max = 500) String imagenUrl,
        @Size(max = 500) String imagenHeroUrl,
        @Size(max = 200) String tagline,
        @Size(max = 1000) String descripcion,
        @Size(max = 500) String linkOficial,
        boolean destacadoHero,
        boolean mostrarFinanciacion,
        @Size(max = 200) String textoFinanciacion,
        int orden,
        boolean activo) {
}
