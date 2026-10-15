package com.kinormotors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinormotors.repository.LeadRepository;
import com.kinormotors.service.LeadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
class FlujoCompletoTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired LeadRepository leadRepo;

    private String tokenAdmin() throws Exception {
        MvcResult r = mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.local\",\"password\":\"clave-de-test-123\"}"))
                .andExpect(status().isOk()).andReturn();
        return mapper.readTree(r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8)).get("token").asText();
    }

    @Test
    void laHomeListaLosModelosYOcultaLaFinanciacionPorDefecto() throws Exception {
        MvcResult r = mvc.perform(get("/api/modelos")).andExpect(status().isOk()).andReturn();
        JsonNode modelos = mapper.readTree(r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        assertEquals(10, modelos.size());
        for (JsonNode m : modelos) {
            assertFalse(m.get("mostrarFinanciacion").asBoolean());
            assertFalse(m.has("textoFinanciacion"), "la financiacion no debe salir mientras este oculta");
        }
        mvc.perform(get("/api/config")).andExpect(jsonPath("$.mostrarFinanciacion").value(false));
    }

    @Test
    void elAdminPuedeMostrarLaFinanciacionYSoloSaleSiElModeloTambienLaTieneActiva() throws Exception {
        String token = tokenAdmin();
        mvc.perform(put("/api/admin/config").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"mostrarFinanciacion\":true}"))
                .andExpect(status().isOk());

        MvcResult admin = mvc.perform(get("/api/admin/modelos").header("Authorization", "Bearer " + token))
                .andReturn();
        JsonNode tasman = null;
        for (JsonNode m : mapper.readTree(admin.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8))) {
            if (m.get("slug").asText().equals("tasman")) tasman = m;
        }
        assertNotNull(tasman);

        // global ON pero el modelo sigue OFF: no se muestra
        assertFalse(publico("tasman").get("mostrarFinanciacion").asBoolean());

        ((com.fasterxml.jackson.databind.node.ObjectNode) tasman).put("mostrarFinanciacion", true);
        mvc.perform(put("/api/admin/modelos/" + tasman.get("id").asLong())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(tasman.toString()))
                .andExpect(status().isOk());
        JsonNode ahora = publico("tasman");
        assertTrue(ahora.get("mostrarFinanciacion").asBoolean());
        assertEquals("Financiá hasta $27 millones", ahora.get("textoFinanciacion").asText());

        mvc.perform(put("/api/admin/config").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"mostrarFinanciacion\":false}"));
        assertFalse(publico("tasman").get("mostrarFinanciacion").asBoolean());
    }

    private JsonNode publico(String slug) throws Exception {
        MvcResult r = mvc.perform(get("/api/modelos")).andReturn();
        for (JsonNode m : mapper.readTree(r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8))) {
            if (m.get("slug").asText().equals(slug)) return m;
        }
        return fail("modelo no encontrado: " + slug);
    }

    @Test
    void unClickEnWhatsappQuedaRegistradoConSuOrigen() throws Exception {
        long antes = leadRepo.count();
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON)
                        .header("User-Agent", "JUnit").header("X-Forwarded-For", "203.0.113.9")
                        .content("""
                                {"tipoAccion":"WHATSAPP","modeloSlug":"sportage","seccion":"hero",
                                 "utmSource":"instagram","utmCampaign":"octubre","sessionId":"abc-123",
                                 "pagina":"/","referrer":"https://instagram.com"}"""))
                .andExpect(status().isNoContent());
        assertEquals(antes + 1, leadRepo.count());

        String token = tokenAdmin();
        MvcResult r = mvc.perform(get("/api/admin/leads").header("Authorization", "Bearer " + token)
                        .param("modelo", "sportage").param("origen", "instagram").param("tipo", "WHATSAPP"))
                .andExpect(status().isOk()).andReturn();
        JsonNode filas = mapper.readTree(r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8)).get("content");
        assertEquals(1, filas.size());
        assertEquals("hero", filas.get(0).get("seccion").asText());
        assertEquals("octubre", filas.get(0).get("utmCampaign").asText());
        // no se expone ni la IP ni su hash
        assertFalse(filas.get(0).has("ipHash"));
        assertFalse(filas.get(0).toString().contains("203.0.113.9"));
    }

    @Test
    void unModeloDesconocidoNoRompeElRegistroPeroSeGuardaSinModelo() throws Exception {
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoAccion\":\"WHATSAPP\",\"modeloSlug\":\"no-existe\"}"))
                .andExpect(status().isNoContent());
        assertTrue(leadRepo.findAll().stream().anyMatch(l -> l.getModeloSlug() == null));
    }

    @Test
    void validaLosDatosDeEntrada() throws Exception {
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoAccion\":\"INVENTADO\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoAccion\":\"WHATSAPP\",\"modeloSlug\":\"'; DROP TABLE leads;--\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void elContactoGuardaLaSolicitudYElLeadYExigeUnMedioDeContacto() throws Exception {
        mvc.perform(post("/api/contacto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"mensaje\":\"Hola\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/contacto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"telefono\":\"+54 11 5555-1234\",\"modeloSlug\":\"seltos\","
                                + "\"mensaje\":\"Quiero info\",\"utmSource\":\"google\"}"))
                .andExpect(status().isCreated());

        String token = tokenAdmin();
        mvc.perform(get("/api/admin/contactos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].nombre").value("Ana"));
        mvc.perform(get("/api/admin/leads").header("Authorization", "Bearer " + token).param("tipo", "CONTACTO"))
                .andExpect(jsonPath("$.content[0].utmSource").value("google"))
                .andExpect(jsonPath("$.content[0].modeloSlug").value("seltos"));
    }

    @Test
    void elTurnoSePersisteYSuEstadoSeCambiaDesdeElAdmin() throws Exception {
        String manana = LocalDate.now().plusDays(1).toString();
        mvc.perform(post("/api/turnos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Luis\",\"telefono\":\"1155551234\",\"vehiculo\":\"Kia Seltos 2024\","
                                + "\"patente\":\"ab123cd\",\"servicio\":\"Service 10.000\",\"fechaDeseada\":\""
                                + manana + "\",\"franja\":\"TARDE\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.patente").value("AB123CD"));

        mvc.perform(post("/api/turnos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Luis\",\"telefono\":\"1155551234\",\"vehiculo\":\"x\","
                                + "\"servicio\":\"y\",\"fechaDeseada\":\"2020-01-01\"}"))
                .andExpect(status().isBadRequest());

        String token = tokenAdmin();
        MvcResult r = mvc.perform(get("/api/admin/turnos").header("Authorization", "Bearer " + token)).andReturn();
        long id = mapper.readTree(r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8)).get("content").get(0).get("id").asLong();
        mvc.perform(patch("/api/admin/turnos/" + id + "/estado").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"CONFIRMADO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void elPanelAdminExigeLoginYValidaElToken() throws Exception {
        mvc.perform(get("/api/admin/leads")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/leads").header("Authorization", "Bearer token.falso.x"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/leads/export.csv")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/admin/config").contentType(MediaType.APPLICATION_JSON)
                .content("{\"mostrarFinanciacion\":true}")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/inexistente")).andExpect(status().is4xxClientError());
    }

    @Test
    void elLoginRechazaCredencialesMalasYSeBloqueaTrasVariosIntentos() throws Exception {
        String malo = "{\"email\":\"admin@test.local\",\"password\":\"incorrecta\"}";
        for (int i = 0; i < 3; i++) {
            mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON).content(malo)
                            .header("X-Forwarded-For", "198.51.100.77"))
                    .andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON).content(malo)
                        .header("X-Forwarded-For", "198.51.100.77"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void elCsvSeExportaYNeutralizaFormulas() throws Exception {
        mvc.perform(post("/api/contacto").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"=HYPERLINK(\\\"http://malo\\\")\",\"email\":\"a@b.com\",\"mensaje\":\"hola\"}"));
        String token = tokenAdmin();
        MvcResult r = mvc.perform(get("/api/admin/leads/export.csv").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
                .andReturn();
        String csv = r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(csv.contains("id,fecha,tipo,modelo"));
        assertFalse(csv.contains("\"=HYPERLINK"), "una celda que empieza con = debe ir prefijada");
        assertTrue(csv.contains("\"'=HYPERLINK"));
    }

    @Test
    void escapaCeldasDelCsv() {
        assertEquals("", LeadService.celda(null));
        assertEquals("\"a \"\"b\"\"\"", LeadService.celda("a \"b\""));
        assertEquals("\"'+54\"", LeadService.celda("+54"));
        assertEquals("\"'@x\"", LeadService.celda("@x"));
    }

    @Test
    void elResumenCuentaPorTipoModeloYOrigen() throws Exception {
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoAccion\":\"PROMOCION\",\"modeloSlug\":\"carnival\",\"utmSource\":\"facebook\"}"));
        String token = tokenAdmin();
        MvcResult r = mvc.perform(get("/api/admin/leads/resumen").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn();
        JsonNode j = mapper.readTree(r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        assertTrue(j.get("total").asLong() >= 1);
        assertTrue(j.get("porOrigen").toString().contains("facebook"));
        assertTrue(j.get("porModelo").toString().contains("carnival"));
    }
}
