package com.kinormotors.service;

import com.kinormotors.entity.Ajuste;
import com.kinormotors.repository.AjusteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AjusteService {

    private final AjusteRepository repo;

    public AjusteService(AjusteRepository repo) {
        this.repo = repo;
    }

    /** Interruptor general de financiacion: apagado por defecto hasta que se definan los precios. */
    @Transactional(readOnly = true)
    public boolean mostrarFinanciacion() {
        return repo.findById(Ajuste.MOSTRAR_FINANCIACION).map(a -> Boolean.parseBoolean(a.getValor())).orElse(false);
    }

    @Transactional
    public void definirMostrarFinanciacion(boolean valor) {
        repo.save(new Ajuste(Ajuste.MOSTRAR_FINANCIACION, Boolean.toString(valor)));
    }
}
