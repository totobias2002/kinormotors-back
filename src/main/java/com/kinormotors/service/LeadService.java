package com.kinormotors.service;

import com.kinormotors.dto.lead.LeadRequest;
import com.kinormotors.dto.lead.LeadResponse;
import com.kinormotors.dto.lead.ResumenLeadsResponse;
import com.kinormotors.dto.lead.ResumenLeadsResponse.Conteo;
import com.kinormotors.entity.Lead;
import com.kinormotors.enums.TipoAccion;
import com.kinormotors.repository.LeadRepository;
import com.kinormotors.repository.LeadSpecification;
import com.kinormotors.repository.ModeloRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LeadService {

    /** Tope de filas del CSV, para que una exportacion no tumbe el servidor. */
    static final int MAX_FILAS_CSV = 50_000;

    private static final String ENCABEZADO_CSV = "id,fecha,tipo,modelo,seccion,utm_source,utm_medium,utm_campaign,"
            + "utm_content,utm_term,referrer,pagina,session_id,nombre,telefono,email,mensaje";

    private final LeadRepository leads;
    private final ModeloRepository modelos;

    public LeadService(LeadRepository leads, ModeloRepository modelos) {
        this.leads = leads;
        this.modelos = modelos;
    }

    /** Registra un click/interaccion. Un modelo desconocido no rompe el registro: se guarda sin modelo. */
    @Transactional
    public LeadResponse registrar(LeadRequest req, MetadatosDeVisita meta) {
        Lead lead = Lead.builder()
                .tipoAccion(req.tipoAccion())
                .modeloSlug(modeloValido(req.modeloSlug()))
                .seccion(req.seccion()).pagina(req.pagina()).referrer(req.referrer())
                .utmSource(req.utmSource()).utmMedium(req.utmMedium()).utmCampaign(req.utmCampaign())
                .utmContent(req.utmContent()).utmTerm(req.utmTerm())
                .sessionId(req.sessionId())
                .nombre(req.nombre()).telefono(req.telefono()).email(req.email()).mensaje(req.mensaje())
                .userAgent(meta.userAgent()).ipHash(meta.ipHash())
                .build();
        return LeadResponse.desde(leads.save(lead));
    }

    /** Usado por contacto y turnos para dejar el rastro comercial junto con el formulario. */
    @Transactional
    public Lead guardar(Lead lead) {
        lead.setModeloSlug(modeloValido(lead.getModeloSlug()));
        return leads.save(lead);
    }

    @Transactional(readOnly = true)
    public Page<LeadResponse> buscar(LocalDate desde, LocalDate hasta, String modelo, TipoAccion tipo,
                                     String origen, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(Math.max(pagina, 0), Math.min(Math.max(tamano, 1), 200),
                Sort.by(Sort.Direction.DESC, "creadoEn"));
        return leads.findAll(LeadSpecification.filtrar(desde, hasta, modelo, tipo, origen), pageable)
                .map(LeadResponse::desde);
    }

    @Transactional(readOnly = true)
    public String exportarCsv(LocalDate desde, LocalDate hasta, String modelo, TipoAccion tipo, String origen) {
        Pageable tope = PageRequest.of(0, MAX_FILAS_CSV, Sort.by(Sort.Direction.DESC, "creadoEn"));
        List<Lead> filas = leads.findAll(LeadSpecification.filtrar(desde, hasta, modelo, tipo, origen), tope)
                .getContent();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder csv = new StringBuilder(ENCABEZADO_CSV).append("\r\n");
        for (Lead l : filas) {
            csv.append(String.join(",",
                    String.valueOf(l.getId()), celda(l.getCreadoEn().format(fmt)), celda(l.getTipoAccion().name()),
                    celda(l.getModeloSlug()), celda(l.getSeccion()), celda(l.getUtmSource()),
                    celda(l.getUtmMedium()), celda(l.getUtmCampaign()), celda(l.getUtmContent()),
                    celda(l.getUtmTerm()), celda(l.getReferrer()), celda(l.getPagina()), celda(l.getSessionId()),
                    celda(l.getNombre()), celda(l.getTelefono()), celda(l.getEmail()), celda(l.getMensaje())))
                    .append("\r\n");
        }
        return csv.toString();
    }

    @Transactional(readOnly = true)
    public ResumenLeadsResponse resumen(LocalDate desde, LocalDate hasta) {
        LocalDateTime d = (desde != null ? desde : LocalDate.now().minusDays(30)).atStartOfDay();
        LocalDateTime h = (hasta != null ? hasta : LocalDate.now()).plusDays(1).atStartOfDay();
        List<Conteo> porTipo = conteos(leads.contarPorTipo(d, h));
        long total = porTipo.stream().mapToLong(Conteo::cantidad).sum();
        return new ResumenLeadsResponse(total, porTipo, conteos(leads.contarPorModelo(d, h)),
                conteos(leads.contarPorOrigen(d, h)), conteos(leads.contarPorSeccion(d, h)));
    }

    private List<Conteo> conteos(List<Object[]> filas) {
        return filas.stream().map(f -> new Conteo(String.valueOf(f[0]), ((Number) f[1]).longValue())).toList();
    }

    private String modeloValido(String slug) {
        if (slug == null || slug.isBlank()) return null;
        return modelos.existsBySlug(slug) ? slug : null;
    }

    /**
     * Escapa una celda de CSV y neutraliza las formulas de Excel (=, +, -, @) para que un dato
     * cargado por un visitante no se ejecute al abrir el archivo.
     */
    public static String celda(String valor) {
        if (valor == null) return "";
        String v = valor;
        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }
}
