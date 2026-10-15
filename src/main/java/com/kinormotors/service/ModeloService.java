package com.kinormotors.service;

import com.kinormotors.dto.modelo.ModeloRequest;
import com.kinormotors.dto.modelo.ModeloResponse;
import com.kinormotors.entity.Modelo;
import com.kinormotors.exception.ResourceNotFoundException;
import com.kinormotors.repository.ModeloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModeloService {

    private final ModeloRepository repo;
    private final AjusteService ajustes;

    public ModeloService(ModeloRepository repo, AjusteService ajustes) {
        this.repo = repo;
        this.ajustes = ajustes;
    }

    @Transactional(readOnly = true)
    public List<ModeloResponse> listarPublicos() {
        boolean financiacion = ajustes.mostrarFinanciacion();
        return repo.findByActivoTrueOrderByOrdenAsc().stream()
                .map(m -> ModeloResponse.publico(m, financiacion)).toList();
    }

    @Transactional(readOnly = true)
    public List<ModeloResponse> listarTodos() {
        return repo.findAllByOrderByOrdenAsc().stream().map(ModeloResponse::admin).toList();
    }

    @Transactional
    public ModeloResponse crear(ModeloRequest req) {
        if (repo.existsBySlug(req.slug())) {
            throw new IllegalArgumentException("Ya existe un modelo con ese slug");
        }
        return ModeloResponse.admin(repo.save(aplicar(new Modelo(), req)));
    }

    @Transactional
    public ModeloResponse actualizar(Long id, ModeloRequest req) {
        Modelo modelo = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Modelo no encontrado"));
        if (!modelo.getSlug().equals(req.slug()) && repo.existsBySlug(req.slug())) {
            throw new IllegalArgumentException("Ya existe un modelo con ese slug");
        }
        return ModeloResponse.admin(repo.save(aplicar(modelo, req)));
    }

    private Modelo aplicar(Modelo m, ModeloRequest r) {
        m.setSlug(r.slug());
        m.setNombre(r.nombre());
        m.setCategoria(r.categoria());
        m.setImagenUrl(vacioANulo(r.imagenUrl()));
        m.setImagenHeroUrl(vacioANulo(r.imagenHeroUrl()));
        m.setTagline(vacioANulo(r.tagline()));
        m.setDescripcion(vacioANulo(r.descripcion()));
        m.setLinkOficial(vacioANulo(r.linkOficial()));
        m.setDestacadoHero(r.destacadoHero());
        m.setMostrarFinanciacion(r.mostrarFinanciacion());
        m.setTextoFinanciacion(vacioANulo(r.textoFinanciacion()));
        m.setOrden(r.orden());
        m.setActivo(r.activo());
        return m;
    }

    private static String vacioANulo(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
