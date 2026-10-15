package com.kinormotors.dto.promocion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PromocionRequest(
        @NotBlank @Size(max = 160) String titulo,
        @Size(max = 1000) String descripcion,
        @Size(max = 500) String imagenUrl,
        @Size(max = 80) String modeloSlug,
        LocalDate vigenteDesde,
        LocalDate vigenteHasta,
        boolean activa,
        int orden) {
}
