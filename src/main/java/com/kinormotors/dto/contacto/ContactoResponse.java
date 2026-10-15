package com.kinormotors.dto.contacto;

import com.kinormotors.entity.SolicitudContacto;

import java.time.LocalDateTime;

public record ContactoResponse(
        Long id,
        String nombre,
        String telefono,
        String email,
        String modeloSlug,
        String mensaje,
        LocalDateTime creadoEn) {

    public static ContactoResponse desde(SolicitudContacto c) {
        return new ContactoResponse(c.getId(), c.getNombre(), c.getTelefono(), c.getEmail(), c.getModeloSlug(),
                c.getMensaje(), c.getCreadoEn());
    }
}
