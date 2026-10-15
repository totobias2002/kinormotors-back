package com.kinormotors.repository;

import com.kinormotors.entity.Lead;
import com.kinormotors.enums.TipoAccion;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/** Filtros combinables del panel admin: fecha, modelo, tipo de accion y origen (utm_source). */
public final class LeadSpecification {

    private LeadSpecification() {
    }

    public static Specification<Lead> filtrar(LocalDate desde, LocalDate hasta, String modelo,
                                              TipoAccion tipo, String origen) {
        Specification<Lead> spec = Specification.where(null);
        if (desde != null) {
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("creadoEn"), desde.atStartOfDay()));
        }
        if (hasta != null) {
            spec = spec.and((r, q, cb) -> cb.lessThan(r.get("creadoEn"), hasta.plusDays(1).atStartOfDay()));
        }
        if (modelo != null && !modelo.isBlank()) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("modeloSlug"), modelo.trim()));
        }
        if (tipo != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("tipoAccion"), tipo));
        }
        if (origen != null && !origen.isBlank()) {
            String valor = origen.trim();
            spec = spec.and((r, q, cb) -> "directo".equalsIgnoreCase(valor)
                    ? cb.isNull(r.get("utmSource"))
                    : cb.equal(cb.lower(r.get("utmSource")), valor.toLowerCase()));
        }
        return spec;
    }
}
