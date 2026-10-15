package com.kinormotors.dto.promocion;

import com.kinormotors.entity.Promocion;

import java.time.LocalDate;

public record PromocionResponse(
        Long id,
        String titulo,
        String descripcion,
        String imagenUrl,
        String modeloSlug,
        LocalDate vigenteDesde,
        LocalDate vigenteHasta,
        boolean activa,
        int orden) {

    public static PromocionResponse desde(Promocion p) {
        return new PromocionResponse(p.getId(), p.getTitulo(), p.getDescripcion(), p.getImagenUrl(),
                p.getModeloSlug(), p.getVigenteDesde(), p.getVigenteHasta(), p.isActiva(), p.getOrden());
    }
}
