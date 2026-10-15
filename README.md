# kinormotors-back

API del sitio de **Kinor Motors S.A.** (concesionaria oficial Kia). Sirve los modelos y promociones de la home,
y registra cada interaccion comercial (click en WhatsApp, "Contactenos", turno de posventa) para saber
**quien consulta, por que modelo, desde que seccion y de donde llego**. Incluye un panel admin con filtros y
exportacion a CSV.

Stack: Spring Boot 3.3 · Java 21 · PostgreSQL · JPA + Flyway · JWT. Deploy en Railway. El front vive en
[`kinormotors-front`](https://github.com/totobias2002/kinormotors-front).

## Estructura

```
src/main/java/com/kinormotors/
  config/       Seguridad (CORS, JWT, rutas) y carga inicial de datos
  controller/   Endpoints: PublicoController (web), AdminController y AdminAuthController (panel)
  dto/          Pedidos y respuestas, agrupados por funcionalidad
  entity/       Tablas: Lead, Modelo, Promocion, SolicitudContacto, TurnoPosventa, Admin, Ajuste
  enums/        TipoAccion (WHATSAPP, CONTACTO, TURNO, PROMOCION, LLAMADA) y EstadoTurno
  exception/    Errores y su traduccion a respuestas HTTP
  repository/   Acceso a datos (LeadSpecification arma los filtros del admin)
  security/     JWT y limite de solicitudes por IP
  service/      Logica de negocio
src/main/resources/
  db/migration/ Esquema (Flyway)
  seed/         modelos.json: modelos iniciales de la home
```

## Endpoints

Publicos (sin login, con limite de solicitudes por IP):

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/modelos` | Modelos activos. La financiacion solo sale si esta habilitada |
| GET | `/api/promociones` | Promociones vigentes |
| GET | `/api/config` | Configuracion publica (`mostrarFinanciacion`) |
| POST | `/api/leads` | Registra un click (tipo, modelo, seccion, UTM, referrer). Responde 204 |
| POST | `/api/contacto` | Formulario de contacto (guarda solicitud + lead) |
| POST | `/api/turnos` | Turno online de posventa (guarda turno + lead) |

Admin (`Authorization: Bearer <jwt>`, el token sale de `POST /api/admin/login`):

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/admin/leads` | Leads paginados. Filtros: `desde`, `hasta`, `modelo`, `tipo`, `origen` |
| GET | `/api/admin/leads/export.csv` | Mismos filtros, en CSV (hasta 50.000 filas) |
| GET | `/api/admin/leads/resumen` | Conteos por tipo, modelo, origen y seccion |
| GET | `/api/admin/contactos`, `/turnos` | Formularios recibidos |
| PATCH | `/api/admin/turnos/{id}/estado` | Pendiente, confirmado, cancelado, atendido |
| GET/POST/PUT | `/api/admin/modelos` | Administrar modelos (incluye el flag de financiacion por modelo) |
| GET/POST/PUT/DELETE | `/api/admin/promociones` | Administrar promociones |
| PUT | `/api/admin/config` | Interruptor general de financiacion |

### Financiacion oculta

Los precios todavia no estan definidos, asi que la financiacion **no se publica** salvo que esten prendidos los
dos interruptores: el general (`PUT /api/admin/config`) y el del modelo (`mostrarFinanciacion`). Cuando esta
apagada, la API ni siquiera manda el texto.

### Privacidad

De cada visita se guarda un **hash con sal de la IP** (no la IP), el user-agent y los datos que el visitante
cargo en un formulario. El CSV neutraliza formulas de Excel (`=`, `+`, `-`, `@`).

## Correr en local

```bash
cp .env.example .env     # completar JWT_SECRET, SPRING_DATASOURCE_PASSWORD, ADMIN_*
docker compose up -d     # Postgres en el puerto 5434
# cargar las variables del .env en el shell y:
mvn spring-boot:run
```

Tests (no necesitan Docker, usan H2): `mvn test`

## Deploy (Railway)

Variables del servicio: las de `.env.example`. `SPRING_DATASOURCE_URL` apunta al Postgres de Railway y
`APP_CORS_ALLOWED_ORIGINS` al dominio del front en Vercel. Railway detecta el `Dockerfile`.
Nada sensible se versiona.

## Pendientes (TODO)

- Imagenes de los modelos y promociones (hoy el front usa placeholders).
- Links oficiales de cada modelo y textos del carrusel: confirmar con Javier.
