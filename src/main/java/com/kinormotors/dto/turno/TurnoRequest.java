package com.kinormotors.dto.turno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Formulario de turno online de posventa. */
public record TurnoRequest(
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 40) @Pattern(regexp = "^[0-9+()\\-\\s]+$", message = "telefono invalido") String telefono,
        @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 160) String vehiculo,
        @Size(max = 12) String patente,
        @NotBlank @Size(max = 160) String servicio,
        @NotNull @FutureOrPresent LocalDate fechaDeseada,
        @Pattern(regexp = "^(MANANA|TARDE)?$", message = "franja invalida") String franja,
        @Size(max = 1000) String comentarios,
        @Size(max = 80) String seccion,
        @Size(max = 300) String pagina,
        @Size(max = 500) String referrer,
        @Size(max = 120) String utmSource,
        @Size(max = 120) String utmMedium,
        @Size(max = 120) String utmCampaign,
        @Size(max = 120) String utmContent,
        @Size(max = 120) String utmTerm,
        @Size(max = 64) String sessionId) {
}
