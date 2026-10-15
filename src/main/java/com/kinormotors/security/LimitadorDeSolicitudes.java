package com.kinormotors.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kinormotors.exception.DemasiadasSolicitudesException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cuenta intentos por clave (IP + endpoint) con vencimiento. Frena el abuso de los endpoints publicos
 * y la fuerza bruta sobre el login del admin.
 */
@Component
public class LimitadorDeSolicitudes {

    private final Cache<String, AtomicInteger> publico = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1)).maximumSize(50_000).build();
    private final Cache<String, AtomicInteger> login = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(15)).maximumSize(50_000).build();

    private final int maxPublico;
    private final int maxLogin;

    public LimitadorDeSolicitudes(@Value("${app.rate-limit.publico-por-minuto}") int maxPublico,
                                  @Value("${app.rate-limit.login-por-15-min}") int maxLogin) {
        this.maxPublico = maxPublico;
        this.maxLogin = maxLogin;
    }

    public void controlarPublico(String ip) {
        controlar(publico, ip, maxPublico, "Demasiadas solicitudes, proba de nuevo en un minuto");
    }

    public void controlarLogin(String ip) {
        controlar(login, ip, maxLogin, "Demasiados intentos de ingreso, proba de nuevo en 15 minutos");
    }

    /** Un login exitoso borra el contador de esa IP. */
    public void loginExitoso(String ip) {
        login.invalidate(ip);
    }

    private void controlar(Cache<String, AtomicInteger> cache, String clave, int maximo, String mensaje) {
        int usados = cache.get(clave, k -> new AtomicInteger()).incrementAndGet();
        if (usados > maximo) {
            throw new DemasiadasSolicitudesException(mensaje);
        }
    }
}
