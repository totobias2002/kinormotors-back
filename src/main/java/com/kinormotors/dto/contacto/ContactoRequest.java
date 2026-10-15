package com.kinormotors.dto.contacto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Formulario "Contactenos". Hace falta al menos un telefono o un email (se valida en el servicio). */
public record ContactoRequest(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 40) @Pattern(regexp = "^[0-9+()\\-\\s]*$", message = "telefono invalido") String telefono,
        @Email @Size(max = 160) String email,
        @Size(max = 80) @Pattern(regexp = "^[a-z0-9-]*$", message = "slug invalido") String modeloSlug,
        @NotBlank @Size(max = 1000) String mensaje,
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
