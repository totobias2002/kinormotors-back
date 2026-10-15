package com.kinormotors.service;

import com.kinormotors.dto.contacto.ContactoRequest;
import com.kinormotors.dto.contacto.ContactoResponse;
import com.kinormotors.entity.Lead;
import com.kinormotors.entity.SolicitudContacto;
import com.kinormotors.enums.TipoAccion;
import com.kinormotors.repository.SolicitudContactoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactoService {

    private final SolicitudContactoRepository repo;
    private final LeadService leads;

    public ContactoService(SolicitudContactoRepository repo, LeadService leads) {
        this.repo = repo;
        this.leads = leads;
    }

    @Transactional
    public ContactoResponse crear(ContactoRequest req, MetadatosDeVisita meta) {
        boolean sinTelefono = req.telefono() == null || req.telefono().isBlank();
        boolean sinEmail = req.email() == null || req.email().isBlank();
        if (sinTelefono && sinEmail) {
            throw new IllegalArgumentException("Dejanos un telefono o un email para poder responderte");
        }
        Lead lead = leads.guardar(Lead.builder()
                .tipoAccion(TipoAccion.CONTACTO).modeloSlug(req.modeloSlug())
                .seccion(req.seccion()).pagina(req.pagina()).referrer(req.referrer())
                .utmSource(req.utmSource()).utmMedium(req.utmMedium()).utmCampaign(req.utmCampaign())
                .utmContent(req.utmContent()).utmTerm(req.utmTerm()).sessionId(req.sessionId())
                .nombre(req.nombre()).telefono(req.telefono()).email(req.email()).mensaje(req.mensaje())
                .userAgent(meta.userAgent()).ipHash(meta.ipHash())
                .build());
        SolicitudContacto solicitud = repo.save(SolicitudContacto.builder()
                .lead(lead).nombre(req.nombre()).telefono(req.telefono()).email(req.email())
                .modeloSlug(lead.getModeloSlug()).mensaje(req.mensaje()).build());
        return ContactoResponse.desde(solicitud);
    }

    @Transactional(readOnly = true)
    public Page<ContactoResponse> listar(int pagina, int tamano) {
        return repo.findAll(PageRequest.of(Math.max(pagina, 0), Math.min(Math.max(tamano, 1), 200),
                Sort.by(Sort.Direction.DESC, "creadoEn"))).map(ContactoResponse::desde);
    }
}
