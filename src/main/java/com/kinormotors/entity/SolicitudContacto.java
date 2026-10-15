package com.kinormotors.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_contacto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 40)
    private String telefono;

    @Column(length = 160)
    private String email;

    @Column(name = "modelo_slug", length = 80)
    private String modeloSlug;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    void alCrear() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
    }
}
