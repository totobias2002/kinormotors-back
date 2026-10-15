package com.kinormotors.dto.lead;

import com.kinormotors.entity.Lead;
import com.kinormotors.enums.TipoAccion;

import java.time.LocalDateTime;

public record LeadResponse(
        Long id,
        TipoAccion tipoAccion,
        String modeloSlug,
        String seccion,
        String pagina,
        String referrer,
        String utmSource,
        String utmMedium,
        String utmCampaign,
        String utmContent,
        String utmTerm,
        String sessionId,
        String nombre,
        String telefono,
        String email,
        String mensaje,
        LocalDateTime creadoEn) {

    public static LeadResponse desde(Lead l) {
        return new LeadResponse(l.getId(), l.getTipoAccion(), l.getModeloSlug(), l.getSeccion(), l.getPagina(),
                l.getReferrer(), l.getUtmSource(), l.getUtmMedium(), l.getUtmCampaign(), l.getUtmContent(),
                l.getUtmTerm(), l.getSessionId(), l.getNombre(), l.getTelefono(), l.getEmail(), l.getMensaje(),
                l.getCreadoEn());
    }
}
