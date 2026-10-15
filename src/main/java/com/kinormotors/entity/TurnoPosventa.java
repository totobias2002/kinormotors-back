package com.kinormotors.entity;

import com.kinormotors.enums.EstadoTurno;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos_posventa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoPosventa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String telefono;

    @Column(length = 160)
    private String email;

    @Column(nullable = false, length = 160)
    private String vehiculo;

    @Column(length = 12)
    private String patente;

    @Column(nullable = false, length = 160)
    private String servicio;

    @Column(name = "fecha_deseada", nullable = false)
    private LocalDate fechaDeseada;

    @Column(length = 20)
    private String franja;

    @Column(length = 1000)
    private String comentarios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTurno estado;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    void alCrear() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
        if (estado == null) estado = EstadoTurno.PENDIENTE;
    }
}
