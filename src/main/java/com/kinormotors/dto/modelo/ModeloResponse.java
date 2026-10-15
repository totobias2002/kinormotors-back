package com.kinormotors.dto.modelo;

import com.kinormotors.entity.Modelo;

public record ModeloResponse(
        Long id,
        String slug,
        String nombre,
        String categoria,
        String imagenUrl,
        String imagenHeroUrl,
        String tagline,
        String descripcion,
        String linkOficial,
        boolean destacadoHero,
        boolean mostrarFinanciacion,
        String textoFinanciacion,
        int orden,
        boolean activo) {

    /** Version publica: la financiacion solo se expone si esta habilitada, el resto queda oculto. */
    public static ModeloResponse publico(Modelo m, boolean financiacionGlobal) {
        boolean mostrar = financiacionGlobal && m.isMostrarFinanciacion();
        return new ModeloResponse(m.getId(), m.getSlug(), m.getNombre(), m.getCategoria(), m.getImagenUrl(),
                m.getImagenHeroUrl(), m.getTagline(), m.getDescripcion(), m.getLinkOficial(), m.isDestacadoHero(),
                mostrar, mostrar ? m.getTextoFinanciacion() : null, m.getOrden(), m.isActivo());
    }

    public static ModeloResponse admin(Modelo m) {
        return new ModeloResponse(m.getId(), m.getSlug(), m.getNombre(), m.getCategoria(), m.getImagenUrl(),
                m.getImagenHeroUrl(), m.getTagline(), m.getDescripcion(), m.getLinkOficial(), m.isDestacadoHero(),
                m.isMostrarFinanciacion(), m.getTextoFinanciacion(), m.getOrden(), m.isActivo());
    }
}
