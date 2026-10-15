package com.kinormotors.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinormotors.dto.modelo.ModeloRequest;
import com.kinormotors.entity.Admin;
import com.kinormotors.repository.AdminRepository;
import com.kinormotors.repository.ModeloRepository;
import com.kinormotors.service.AjusteService;
import com.kinormotors.service.ModeloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * Carga inicial: el admin (solo si ADMIN_EMAIL y ADMIN_PASSWORD estan definidos y no hay ninguno)
 * y los modelos de la home de Kia (solo si la tabla esta vacia).
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AdminRepository admins;
    private final ModeloRepository modeloRepo;
    private final ModeloService modeloService;
    private final AjusteService ajustes;
    private final PasswordEncoder encoder;
    private final ObjectMapper mapper;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminNombre;

    public DataSeeder(AdminRepository admins, ModeloRepository modeloRepo, ModeloService modeloService,
                      AjusteService ajustes, PasswordEncoder encoder, ObjectMapper mapper,
                      @Value("${app.admin.email}") String adminEmail,
                      @Value("${app.admin.password}") String adminPassword,
                      @Value("${app.admin.nombre}") String adminNombre) {
        this.admins = admins;
        this.modeloRepo = modeloRepo;
        this.modeloService = modeloService;
        this.ajustes = ajustes;
        this.encoder = encoder;
        this.mapper = mapper;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminNombre = adminNombre;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sembrarAdmin();
        sembrarModelos();
    }

    private void sembrarAdmin() {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.warn("ADMIN_EMAIL/ADMIN_PASSWORD no definidos: no se crea ningun admin");
            return;
        }
        if (adminPassword.length() < 10) {
            throw new IllegalStateException("ADMIN_PASSWORD debe tener al menos 10 caracteres");
        }
        if (admins.count() == 0) {
            admins.save(Admin.builder().email(adminEmail.trim().toLowerCase())
                    .passwordHash(encoder.encode(adminPassword)).nombre(adminNombre).build());
            log.info("Admin inicial creado");
        }
    }

    private void sembrarModelos() throws Exception {
        if (modeloRepo.count() > 0) return;
        try (InputStream in = new ClassPathResource("seed/modelos.json").getInputStream()) {
            List<ModeloRequest> modelos = mapper.readValue(in, new TypeReference<>() {
            });
            modelos.forEach(modeloService::crear);
            ajustes.definirMostrarFinanciacion(false);
            log.info("Se cargaron {} modelos iniciales", modelos.size());
        }
    }
}
