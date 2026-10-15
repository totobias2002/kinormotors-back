package com.kinormotors.entity;

import com.kinormotors.enums.TipoAccion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Una interaccion comercial: quien consulta, por que modelo, desde que seccion y de donde llego. */
@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false, length = 30)
    private TipoAccion tipoAccion;

    @Column(name = "modelo_slug", length = 80)
    private String modeloSlug;

    @Column(length = 80)
    private String seccion;

    @Column(length = 300)
    private String pagina;

    @Column(length = 500)
    private String referrer;

    @Column(name = "utm_source", length = 120)
    private String utmSource;

    @Column(name = "utm_medium", length = 120)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 120)
    private String utmCampaign;

    @Column(name = "utm_content", length = 120)
    private String utmContent;

    @Column(name = "utm_term", length = 120)
    private String utmTerm;

    @Column(name = "session_id", length = 64)
    private String sessionId;

    @Column(name = "user_agent", length = 300)
    private String userAgent;

    /** Hash de la IP con sal: sirve para agrupar visitas sin guardar el dato personal. */
    @Column(name = "ip_hash", length = 64)
    private String ipHash;

    @Column(length = 120)
    private String nombre;

    @Column(length = 40)
    private String telefono;

    @Column(length = 160)
    private String email;

    @Column(length = 1000)
    private String mensaje;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    void alCrear() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
    }
}
