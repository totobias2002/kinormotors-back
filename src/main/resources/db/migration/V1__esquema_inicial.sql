CREATE TABLE admins (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    nombre        VARCHAR(120) NOT NULL,
    creado_en     TIMESTAMP    NOT NULL
);

CREATE TABLE modelos (
    id                    BIGSERIAL PRIMARY KEY,
    slug                  VARCHAR(80)  NOT NULL UNIQUE,
    nombre                VARCHAR(120) NOT NULL,
    categoria             VARCHAR(40)  NOT NULL,
    imagen_url            VARCHAR(500),
    imagen_hero_url       VARCHAR(500),
    tagline               VARCHAR(200),
    descripcion           VARCHAR(1000),
    link_oficial          VARCHAR(500),
    destacado_hero        BOOLEAN      NOT NULL DEFAULT FALSE,
    mostrar_financiacion  BOOLEAN      NOT NULL DEFAULT FALSE,
    texto_financiacion    VARCHAR(200),
    orden                 INTEGER      NOT NULL DEFAULT 0,
    activo                BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE promociones (
    id          BIGSERIAL PRIMARY KEY,
    titulo      VARCHAR(160) NOT NULL,
    descripcion VARCHAR(1000),
    imagen_url  VARCHAR(500),
    modelo_slug VARCHAR(80),
    vigente_desde DATE,
    vigente_hasta DATE,
    activa      BOOLEAN      NOT NULL DEFAULT TRUE,
    orden       INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE ajustes (
    clave VARCHAR(80)  PRIMARY KEY,
    valor VARCHAR(500) NOT NULL
);

CREATE TABLE leads (
    id           BIGSERIAL PRIMARY KEY,
    tipo_accion  VARCHAR(30)  NOT NULL,
    modelo_slug  VARCHAR(80),
    seccion      VARCHAR(80),
    pagina       VARCHAR(300),
    referrer     VARCHAR(500),
    utm_source   VARCHAR(120),
    utm_medium   VARCHAR(120),
    utm_campaign VARCHAR(120),
    utm_content  VARCHAR(120),
    utm_term     VARCHAR(120),
    session_id   VARCHAR(64),
    user_agent   VARCHAR(300),
    ip_hash      VARCHAR(64),
    nombre       VARCHAR(120),
    telefono     VARCHAR(40),
    email        VARCHAR(160),
    mensaje      VARCHAR(1000),
    creado_en    TIMESTAMP    NOT NULL
);
CREATE INDEX idx_leads_creado_en ON leads (creado_en);
CREATE INDEX idx_leads_modelo ON leads (modelo_slug);
CREATE INDEX idx_leads_tipo ON leads (tipo_accion);

CREATE TABLE solicitudes_contacto (
    id          BIGSERIAL PRIMARY KEY,
    lead_id     BIGINT REFERENCES leads (id),
    nombre      VARCHAR(120) NOT NULL,
    telefono    VARCHAR(40),
    email       VARCHAR(160),
    modelo_slug VARCHAR(80),
    mensaje     VARCHAR(1000) NOT NULL,
    creado_en   TIMESTAMP    NOT NULL
);

CREATE TABLE turnos_posventa (
    id            BIGSERIAL PRIMARY KEY,
    lead_id       BIGINT REFERENCES leads (id),
    nombre        VARCHAR(120) NOT NULL,
    telefono      VARCHAR(40)  NOT NULL,
    email         VARCHAR(160),
    vehiculo      VARCHAR(160) NOT NULL,
    patente       VARCHAR(12),
    servicio      VARCHAR(160) NOT NULL,
    fecha_deseada DATE         NOT NULL,
    franja        VARCHAR(20),
    comentarios   VARCHAR(1000),
    estado        VARCHAR(20)  NOT NULL,
    creado_en     TIMESTAMP    NOT NULL
);
CREATE INDEX idx_turnos_fecha ON turnos_posventa (fecha_deseada);
