package com.kinormotors.service;

import com.kinormotors.dto.turno.TurnoRequest;
import com.kinormotors.dto.turno.TurnoResponse;
import com.kinormotors.entity.Lead;
import com.kinormotors.entity.TurnoPosventa;
import com.kinormotors.enums.EstadoTurno;
import com.kinormotors.enums.TipoAccion;
import com.kinormotors.exception.ResourceNotFoundException;
import com.kinormotors.repository.TurnoPosventaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurnoService {

    private final TurnoPosventaRepository repo;
    private final LeadService leads;

    public TurnoService(TurnoPosventaRepository repo, LeadService leads) {
        this.repo = repo;
        this.leads = leads;
    }

    @Transactional
    public TurnoResponse crear(TurnoRequest req, MetadatosDeVisita meta) {
        Lead lead = leads.guardar(Lead.builder()
                .tipoAccion(TipoAccion.TURNO).seccion(req.seccion()).pagina(req.pagina()).referrer(req.referrer())
                .utmSource(req.utmSource()).utmMedium(req.utmMedium()).utmCampaign(req.utmCampaign())
                .utmContent(req.utmContent()).utmTerm(req.utmTerm()).sessionId(req.sessionId())
                .nombre(req.nombre()).telefono(req.telefono()).email(req.email())
                .mensaje(req.servicio() + " - " + req.vehiculo())
                .userAgent(meta.userAgent()).ipHash(meta.ipHash())
                .build());
        TurnoPosventa turno = repo.save(TurnoPosventa.builder()
                .lead(lead).nombre(req.nombre()).telefono(req.telefono()).email(req.email())
                .vehiculo(req.vehiculo()).patente(req.patente() == null ? null : req.patente().toUpperCase())
                .servicio(req.servicio()).fechaDeseada(req.fechaDeseada())
                .franja(req.franja() == null || req.franja().isBlank() ? null : req.franja())
                .comentarios(req.comentarios()).estado(EstadoTurno.PENDIENTE).build());
        return TurnoResponse.desde(turno);
    }

    @Transactional(readOnly = true)
    public Page<TurnoResponse> listar(int pagina, int tamano) {
        return repo.findAll(PageRequest.of(Math.max(pagina, 0), Math.min(Math.max(tamano, 1), 200),
                Sort.by(Sort.Direction.DESC, "creadoEn"))).map(TurnoResponse::desde);
    }

    @Transactional
    public TurnoResponse cambiarEstado(Long id, EstadoTurno estado) {
        TurnoPosventa turno = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
        turno.setEstado(estado);
        return TurnoResponse.desde(repo.save(turno));
    }
}
