package com.kinormotors.controller;

import com.kinormotors.dto.admin.ConfigSitioResponse;
import com.kinormotors.dto.contacto.ContactoRequest;
import com.kinormotors.dto.contacto.ContactoResponse;
import com.kinormotors.dto.lead.LeadRequest;
import com.kinormotors.dto.modelo.ModeloResponse;
import com.kinormotors.dto.promocion.PromocionResponse;
import com.kinormotors.dto.turno.TurnoRequest;
import com.kinormotors.dto.turno.TurnoResponse;
import com.kinormotors.security.LimitadorDeSolicitudes;
import com.kinormotors.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints que consume la web publica: sin login, con limite de solicitudes por IP. */
@RestController
@RequestMapping("/api")
public class PublicoController {

    private final ModeloService modelos;
    private final PromocionService promociones;
    private final AjusteService ajustes;
    private final LeadService leads;
    private final ContactoService contactos;
    private final TurnoService turnos;
    private final LimitadorDeSolicitudes limitador;
    private final String sal;

    public PublicoController(ModeloService modelos, PromocionService promociones, AjusteService ajustes,
                             LeadService leads, ContactoService contactos, TurnoService turnos,
                             LimitadorDeSolicitudes limitador, @Value("${app.tracking.ip-salt}") String sal) {
        this.modelos = modelos;
        this.promociones = promociones;
        this.ajustes = ajustes;
        this.leads = leads;
        this.contactos = contactos;
        this.turnos = turnos;
        this.limitador = limitador;
        this.sal = sal;
    }

    @GetMapping("/modelos")
    public List<ModeloResponse> modelos() {
        return modelos.listarPublicos();
    }

    @GetMapping("/promociones")
    public List<PromocionResponse> promociones() {
        return promociones.listarVigentes();
    }

    @GetMapping("/config")
    public ConfigSitioResponse config() {
        return new ConfigSitioResponse(ajustes.mostrarFinanciacion());
    }

    /** Registra el click (WhatsApp, Contactenos, Promociones...). Responde 204: al front no le hace falta nada. */
    @PostMapping("/leads")
    public ResponseEntity<Void> lead(@Valid @RequestBody LeadRequest req, HttpServletRequest request) {
        MetadatosDeVisita meta = meta(request);
        limitador.controlarPublico(meta.ip());
        leads.registrar(req, meta);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/contacto")
    public ResponseEntity<ContactoResponse> contacto(@Valid @RequestBody ContactoRequest req,
                                                     HttpServletRequest request) {
        MetadatosDeVisita meta = meta(request);
        limitador.controlarPublico(meta.ip());
        return ResponseEntity.status(HttpStatus.CREATED).body(contactos.crear(req, meta));
    }

    @PostMapping("/turnos")
    public ResponseEntity<TurnoResponse> turno(@Valid @RequestBody TurnoRequest req, HttpServletRequest request) {
        MetadatosDeVisita meta = meta(request);
        limitador.controlarPublico(meta.ip());
        return ResponseEntity.status(HttpStatus.CREATED).body(turnos.crear(req, meta));
    }

    private MetadatosDeVisita meta(HttpServletRequest request) {
        return MetadatosDeVisita.de(request, sal);
    }
}
