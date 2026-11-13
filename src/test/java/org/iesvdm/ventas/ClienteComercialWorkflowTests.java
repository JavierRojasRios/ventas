package org.iesvdm.ventas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;

import java.time.LocalDate;
import java.util.UUID;

import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteComercialWorkflowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @Test
    void addsCommercialWithDateAndPriorityAndReturnsToClientEditForm() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Cliente cliente = new Cliente(0, "Cliente" + suffix, "Prueba", null, "Test", null);
        clienteService.create(cliente);
        Comercial comercial = new Comercial(0, "Comercial" + suffix, "Prueba", null, 0.1f);
        comercialService.create(comercial);
        LocalDate fecha = LocalDate.of(2026, 11, 13);

        mockMvc.perform(get("/clientes/editar/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente-form"))
                .andExpect(content().string(containsString("name=\"comercialId\"")))
                .andExpect(content().string(containsString("name=\"fecha\"")))
                .andExpect(content().string(containsString("name=\"prioridad\"")));

        mockMvc.perform(post("/clientes/editar/{id}/comerciales", cliente.getId())
                        .param("comercialId", String.valueOf(comercial.getId()))
                        .param("fecha", fecha.toString())
                        .param("prioridad", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/editar/" + cliente.getId()));

        mockMvc.perform(get("/clientes/editar/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente-form"))
                .andExpect(content().string(containsString("Comercial" + suffix + " Prueba")))
                .andExpect(content().string(containsString(fecha.toString())))
                .andExpect(content().string(containsString(">2</td>")));

        var vinculos = clienteService.listComercialesByClienteId(cliente.getId());
        assertEquals(1, vinculos.size());
        assertEquals(fecha, vinculos.get(0).getFecha());
        assertEquals(2, vinculos.get(0).getPrioridad());

        mockMvc.perform(post("/clientes/editar/{id}/comerciales", cliente.getId())
                        .param("comercialId", String.valueOf(comercial.getId()))
                        .param("fecha", fecha.toString())
                        .param("prioridad", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/editar/" + cliente.getId()))
                .andExpect(flash().attribute("mensaje",
                        "El comercial ya está vinculado a este cliente."));
        assertEquals(1, clienteService.listComercialesByClienteId(cliente.getId()).size());
    }

        @Test
        void removesOnlySelectedCommercialLinkAndReturnsToClientEditForm() throws Exception {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                Cliente cliente = new Cliente(0, "Cliente" + suffix, "Prueba", null, "Test", null);
                clienteService.create(cliente);
                Comercial comercialBorrado = new Comercial(0, "Borrado" + suffix, "Prueba", null, 0.1f);
                comercialService.create(comercialBorrado);
                Comercial comercialConservado = new Comercial(0, "Conservado" + suffix, "Prueba", null, 0.1f);
                comercialService.create(comercialConservado);
                clienteService.addComercial(cliente.getId(), comercialBorrado.getId(), LocalDate.of(2026, 11, 13), 1);
                clienteService.addComercial(cliente.getId(), comercialConservado.getId(), LocalDate.of(2026, 11, 14), 2);

                mockMvc.perform(get("/clientes/editar/{id}", cliente.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("Borrar")))
                                .andExpect(content().string(containsString("Borrado" + suffix + " Prueba")))
                                .andExpect(content().string(containsString("Conservado" + suffix + " Prueba")));

                mockMvc.perform(post("/clientes/editar/{id}/comerciales/{comercialId}/borrar",
                                                cliente.getId(), comercialBorrado.getId()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/clientes/editar/" + cliente.getId()));

                String html = mockMvc.perform(get("/clientes/editar/{id}", cliente.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("cliente-form"))
                                .andReturn().getResponse().getContentAsString();
                assertFalse(html.contains("href=\"/comerciales/" + comercialBorrado.getId() + "\""));
                assertTrue(html.contains("href=\"/comerciales/" + comercialConservado.getId() + "\""));
                assertEquals(1, clienteService.listComercialesByClienteId(cliente.getId()).size());
        }
}
