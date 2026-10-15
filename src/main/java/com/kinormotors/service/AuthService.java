package com.kinormotors.service;

import com.kinormotors.dto.auth.AuthResponse;
import com.kinormotors.dto.auth.LoginRequest;
import com.kinormotors.entity.Admin;
import com.kinormotors.repository.AdminRepository;
import com.kinormotors.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    /** Hash valido de una clave al azar: se compara igual cuando el email no existe, para no filtrar por tiempo. */
    private final String hashFalso;

    private final AdminRepository admins;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(AdminRepository admins, PasswordEncoder encoder, JwtService jwt) {
        this.admins = admins;
        this.encoder = encoder;
        this.jwt = jwt;
        this.hashFalso = encoder.encode(java.util.UUID.randomUUID().toString());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Admin admin = admins.findByEmailIgnoreCase(req.email().trim()).orElse(null);
        boolean coincide = encoder.matches(req.password(), admin != null ? admin.getPasswordHash() : hashFalso);
        if (admin == null || !coincide) {
            throw new BadCredentialsException("Credenciales invalidas");
        }
        return new AuthResponse(jwt.generar(admin.getEmail()), admin.getEmail(), admin.getNombre());
    }
}
