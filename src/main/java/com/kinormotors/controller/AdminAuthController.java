package com.kinormotors.controller;

import com.kinormotors.dto.auth.AuthResponse;
import com.kinormotors.dto.auth.LoginRequest;
import com.kinormotors.security.LimitadorDeSolicitudes;
import com.kinormotors.service.AuthService;
import com.kinormotors.service.MetadatosDeVisita;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AuthService auth;
    private final LimitadorDeSolicitudes limitador;

    public AdminAuthController(AuthService auth, LimitadorDeSolicitudes limitador) {
        this.auth = auth;
        this.limitador = limitador;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        String ip = MetadatosDeVisita.de(request, "").ip();
        limitador.controlarLogin(ip);
        AuthResponse respuesta = auth.login(req);
        limitador.loginExitoso(ip);
        return respuesta;
    }
}
