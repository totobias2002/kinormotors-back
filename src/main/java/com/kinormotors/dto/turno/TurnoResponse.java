package com.kinormotors.dto.turno;

import com.kinormotors.entity.TurnoPosventa;
import com.kinormotors.enums.EstadoTurno;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        String nombre,
        String telefono,
        String email,
        String vehiculo,
        String patente,
        String servicio,
        LocalDate fechaDeseada,
        String franja,
        String comentarios,
        EstadoTurno estado,
        LocalDateTime creadoEn) {

    public static TurnoResponse desde(TurnoPosventa t) {
        return new TurnoResponse(t.getId(), t.getNombre(), t.getTelefono(), t.getEmail(), t.getVehiculo(),
                t.getPatente(), t.getServicio(), t.getFechaDeseada(), t.getFranja(), t.getComentarios(),
                t.getEstado(), t.getCreadoEn());
    }
}
