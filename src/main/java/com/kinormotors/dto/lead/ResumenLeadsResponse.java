package com.kinormotors.dto.lead;

import java.util.List;

/** Conteos para el tablero del admin: cuantas consultas hubo y de donde vinieron. */
public record ResumenLeadsResponse(
        long total,
        List<Conteo> porTipo,
        List<Conteo> porModelo,
        List<Conteo> porOrigen,
        List<Conteo> porSeccion) {

    public record Conteo(String clave, long cantidad) {
    }
}
