package com.kinormotors.service;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Datos del request que alimentan la trazabilidad: IP (solo su hash) y navegador. */
public record MetadatosDeVisita(String ipHash, String userAgent, String ip) {

    public static MetadatosDeVisita de(HttpServletRequest request, String sal) {
        String ip = ipDe(request);
        String ua = request.getHeader("User-Agent");
        if (ua != null && ua.length() > 300) ua = ua.substring(0, 300);
        return new MetadatosDeVisita(hash(ip + sal), ua, ip);
    }

    /** Detras de Railway la IP real llega en X-Forwarded-For (el primer valor). */
    static String ipDe(HttpServletRequest request) {
        String reenviada = request.getHeader("X-Forwarded-For");
        if (reenviada != null && !reenviada.isBlank()) {
            return reenviada.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String hash(String valor) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
