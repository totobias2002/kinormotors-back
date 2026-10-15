package com.kinormotors.controller;

import com.kinormotors.dto.admin.ConfigSitioRequest;
import com.kinormotors.dto.admin.ConfigSitioResponse;
import com.kinormotors.dto.contacto.ContactoResponse;
import com.kinormotors.dto.lead.LeadResponse;
import com.kinormotors.dto.lead.ResumenLeadsResponse;
import com.kinormotors.dto.modelo.ModeloRequest;
import com.kinormotors.dto.modelo.ModeloResponse;
import com.kinormotors.dto.promocion.PromocionRequest;
import com.kinormotors.dto.promocion.PromocionResponse;
import com.kinormotors.dto.turno.CambiarEstadoTurnoRequest;
import com.kinormotors.dto.turno.TurnoResponse;
import com.kinormotors.enums.TipoAccion;
import com.kinormotors.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/** Panel admin (todo requiere JWT de admin): leads, formularios, modelos, promociones y ajustes. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LeadService leads;
    private final ContactoService contactos;
    private final TurnoService turnos;
    private final ModeloService modelos;
    private final PromocionService promociones;
    private final AjusteService ajustes;

    public AdminController(LeadService leads, ContactoService contactos, TurnoService turnos,
                           ModeloService modelos, PromocionService promociones, AjusteService ajustes) {
        this.leads = leads;
        this.contactos = contactos;
        this.turnos = turnos;
        this.modelos = modelos;
        this.promociones = promociones;
        this.ajustes = ajustes;
    }

    // ---- Leads ----

    @GetMapping("/leads")
    public Page<LeadResponse> leads(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) TipoAccion tipo,
            @RequestParam(required = false) String origen,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamano) {
        return leads.buscar(desde, hasta, modelo, tipo, origen, pagina, tamano);
    }

    @GetMapping("/leads/export.csv")
    public ResponseEntity<byte[]> exportar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) TipoAccion tipo,
            @RequestParam(required = false) String origen) {
        // BOM para que Excel abra bien los acentos
        String csv = "﻿" + leads.exportarCsv(desde, hasta, modelo, tipo, origen);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"leads-kinor-motors.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/leads/resumen")
    public ResumenLeadsResponse resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return leads.resumen(desde, hasta);
    }

    // ---- Formularios ----

    @GetMapping("/contactos")
    public Page<ContactoResponse> contactos(@RequestParam(defaultValue = "0") int pagina,
                                            @RequestParam(defaultValue = "25") int tamano) {
        return contactos.listar(pagina, tamano);
    }

    @GetMapping("/turnos")
    public Page<TurnoResponse> turnos(@RequestParam(defaultValue = "0") int pagina,
                                      @RequestParam(defaultValue = "25") int tamano) {
        return turnos.listar(pagina, tamano);
    }

    @PatchMapping("/turnos/{id}/estado")
    public TurnoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoTurnoRequest req) {
        return turnos.cambiarEstado(id, req.estado());
    }

    // ---- Modelos ----

    @GetMapping("/modelos")
    public List<ModeloResponse> modelos() {
        return modelos.listarTodos();
    }

    @PostMapping("/modelos")
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloResponse crearModelo(@Valid @RequestBody ModeloRequest req) {
        return modelos.crear(req);
    }

    @PutMapping("/modelos/{id}")
    public ModeloResponse actualizarModelo(@PathVariable Long id, @Valid @RequestBody ModeloRequest req) {
        return modelos.actualizar(id, req);
    }

    // ---- Promociones ----

    @GetMapping("/promociones")
    public List<PromocionResponse> promociones() {
        return promociones.listarTodas();
    }

    @PostMapping("/promociones")
    @ResponseStatus(HttpStatus.CREATED)
    public PromocionResponse crearPromocion(@Valid @RequestBody PromocionRequest req) {
        return promociones.crear(req);
    }

    @PutMapping("/promociones/{id}")
    public PromocionResponse actualizarPromocion(@PathVariable Long id, @Valid @RequestBody PromocionRequest req) {
        return promociones.actualizar(id, req);
    }

    @DeleteMapping("/promociones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPromocion(@PathVariable Long id) {
        promociones.eliminar(id);
    }

    // ---- Ajustes ----

    @PutMapping("/config")
    public ConfigSitioResponse config(@RequestBody ConfigSitioRequest req) {
        ajustes.definirMostrarFinanciacion(req.mostrarFinanciacion());
        return new ConfigSitioResponse(req.mostrarFinanciacion());
    }
}
