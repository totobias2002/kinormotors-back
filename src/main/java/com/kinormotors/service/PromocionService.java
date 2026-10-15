package com.kinormotors.service;

import com.kinormotors.dto.promocion.PromocionRequest;
import com.kinormotors.dto.promocion.PromocionResponse;
import com.kinormotors.entity.Promocion;
import com.kinormotors.exception.ResourceNotFoundException;
import com.kinormotors.repository.PromocionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromocionService {

    private final PromocionRepository repo;

    public PromocionService(PromocionRepository repo) {
        this.repo = repo;
    }

    /** Solo las activas y dentro de su ventana de vigencia (si la tienen). */
    @Transactional(readOnly = true)
    public List<PromocionResponse> listarVigentes() {
        LocalDate hoy = LocalDate.now();
        return repo.findByActivaTrueOrderByOrdenAsc().stream()
                .filter(p -> p.getVigenteDesde() == null || !p.getVigenteDesde().isAfter(hoy))
                .filter(p -> p.getVigenteHasta() == null || !p.getVigenteHasta().isBefore(hoy))
                .map(PromocionResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public List<PromocionResponse> listarTodas() {
        return repo.findAllByOrderByOrdenAsc().stream().map(PromocionResponse::desde).toList();
    }

    @Transactional
    public PromocionResponse crear(PromocionRequest req) {
        return PromocionResponse.desde(repo.save(aplicar(new Promocion(), req)));
    }

    @Transactional
    public PromocionResponse actualizar(Long id, PromocionRequest req) {
        Promocion p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Promocion no encontrada"));
        return PromocionResponse.desde(repo.save(aplicar(p, req)));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Promocion no encontrada");
        }
        repo.deleteById(id);
    }

    private Promocion aplicar(Promocion p, PromocionRequest r) {
        if (r.vigenteDesde() != null && r.vigenteHasta() != null && r.vigenteHasta().isBefore(r.vigenteDesde())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        p.setTitulo(r.titulo());
        p.setDescripcion(r.descripcion());
        p.setImagenUrl(r.imagenUrl());
        p.setModeloSlug(r.modeloSlug());
        p.setVigenteDesde(r.vigenteDesde());
        p.setVigenteHasta(r.vigenteHasta());
        p.setActiva(r.activa());
        p.setOrden(r.orden());
        return p;
    }
}
