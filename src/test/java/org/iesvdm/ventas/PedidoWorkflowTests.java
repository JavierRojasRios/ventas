package org.iesvdm.ventas;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.servicios.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoWorkflowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @Autowired
    private PedidoService pedidoService;

    @Test
    void createsEditsAndDisplaysPedidoWithFullNamesAndNullSecondSurnames() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Cliente clienteInicial = crearCliente("ClienteInicial" + suffix);
        Cliente clienteEditado = crearCliente("ClienteEditado" + suffix);
        Comercial comercialInicial = crearComercial("ComercialInicial" + suffix);
        Comercial comercialEditado = crearComercial("ComercialEditado" + suffix);

        mockMvc.perform(get("/pedidos/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido-form"))
                .andExpect(content().string(containsString("value=\"" + clienteInicial.getId() + "\"")))
                .andExpect(content().string(containsString(nombreCliente(clienteInicial))))
                .andExpect(content().string(containsString("value=\"" + comercialInicial.getId() + "\"")))
                .andExpect(content().string(containsString(nombreComercial(comercialInicial))));

        mockMvc.perform(post("/pedidos/crear")
                        .param("total", "125.75")
                        .param("fecha", "2025-03-14")
                        .param("clienteId", String.valueOf(clienteInicial.getId()))
                        .param("comercialId", String.valueOf(comercialInicial.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));

        Pedido pedido = pedidoService.listAll().stream()
                .filter(p -> p.getClienteId() == clienteInicial.getId()
                        && p.getComercialId() == comercialInicial.getId())
                .filter(p -> p.getTotal().compareTo(new BigDecimal("125.75")) == 0)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/pedidos/editar/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido-form"))
                .andExpect(content().string(containsString(
                        "value=\"" + clienteInicial.getId() + "\" selected=\"selected\"")))
                .andExpect(content().string(containsString(
                        "value=\"" + comercialInicial.getId() + "\" selected=\"selected\"")));

        mockMvc.perform(post("/pedidos/editar/{id}", pedido.getId())
                        .param("total", "200.50")
                        .param("fecha", "2025-04-15")
                        .param("clienteId", String.valueOf(clienteEditado.getId()))
                        .param("comercialId", String.valueOf(comercialEditado.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));

        Pedido actualizado = pedidoService.find(pedido.getId()).orElseThrow();
        assertEquals(clienteEditado.getId(), actualizado.getClienteId());
        assertEquals(comercialEditado.getId(), actualizado.getComercialId());
        assertEquals(LocalDate.of(2025, 4, 15), actualizado.getFecha());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">" + nombreCliente(clienteEditado) + "</td>")))
                .andExpect(content().string(containsString(">" + nombreComercial(comercialEditado) + "</td>")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("null"))));

        mockMvc.perform(get("/pedidos/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido-detalle"))
                .andExpect(content().string(containsString(nombreCliente(compruebaCliente(actualizado.getClienteId())))))
                .andExpect(content().string(containsString(nombreComercial(compruebaComercial(actualizado.getComercialId())))));
    }

    @Test
    void invalidForeignKeysReturnFormWithoutCreatingOrUpdatingPedido() throws Exception {
        Cliente cliente = crearCliente("ClienteValido" + UUID.randomUUID().toString().substring(0, 8));
        Comercial comercial = crearComercial("ComercialValido" + UUID.randomUUID().toString().substring(0, 8));
        int pedidosAntes = pedidoService.listAll().size();

        mockMvc.perform(post("/pedidos/crear")
                        .param("total", "10.00")
                        .param("clienteId", "2147483647")
                        .param("comercialId", String.valueOf(comercial.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido-form"))
                .andExpect(content().string(containsString("Selecciona un cliente y un comercial existentes.")));

        mockMvc.perform(post("/pedidos/crear")
                        .param("total", "10.00")
                        .param("clienteId", String.valueOf(cliente.getId()))
                        .param("comercialId", "2147483647"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido-form"))
                .andExpect(content().string(containsString("Selecciona un cliente y un comercial existentes.")));

        assertEquals(pedidosAntes, pedidoService.listAll().size());

        Pedido pedido = new Pedido(0, new BigDecimal("15.00"), LocalDate.of(2025, 1, 1),
                cliente.getId(), comercial.getId());
        pedidoService.create(pedido);

        mockMvc.perform(post("/pedidos/editar/{id}", pedido.getId())
                        .param("total", "99.00")
                        .param("clienteId", "2147483647")
                        .param("comercialId", String.valueOf(comercial.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido-form"))
                .andExpect(content().string(containsString("/pedidos/editar/" + pedido.getId())));

        Pedido sinCambios = pedidoService.find(pedido.getId()).orElseThrow();
        assertTrue(sinCambios.getTotal().compareTo(new BigDecimal("15.00")) == 0);
        assertEquals(cliente.getId(), sinCambios.getClienteId());
        assertEquals(comercial.getId(), sinCambios.getComercialId());
    }

    @Test
    void foreignKeyFailuresDoNotDeleteRelatedPeopleOrPedido() throws Exception {
        Cliente cliente = crearCliente("ClienteProtegido" + UUID.randomUUID().toString().substring(0, 8));
        Comercial comercial = crearComercial("ComercialProtegido" + UUID.randomUUID().toString().substring(0, 8));
        Pedido pedido = new Pedido(0, new BigDecimal("35.00"), LocalDate.of(2025, 2, 1),
                cliente.getId(), comercial.getId());
        pedidoService.create(pedido);

        mockMvc.perform(post("/clientes/borrar/{id}", cliente.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("mensaje",
                        "No se puede eliminar el cliente porque tiene pedidos asociados."));

        mockMvc.perform(post("/comerciales/borrar/{id}", comercial.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("mensaje",
                        "No se puede eliminar el comercial porque tiene pedidos asociados."));

        assertTrue(clienteService.find(cliente.getId()).isPresent());
        assertTrue(comercialService.find(comercial.getId()).isPresent());
        assertTrue(pedidoService.find(pedido.getId()).isPresent());
        assertFalse(pedidoService.find(pedido.getId()).isEmpty());
    }

        @Test
        void commercialDetailDisplaysAssociatedPedidos() throws Exception {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                Cliente cliente = crearCliente("ClienteDetalle" + suffix);
                Comercial comercial = crearComercial("ComercialDetalle" + suffix);
                Pedido pedido = new Pedido(0, new BigDecimal("45.50"), LocalDate.of(2025, 5, 20),
                                cliente.getId(), comercial.getId());
                pedidoService.create(pedido);

                mockMvc.perform(get("/comerciales/{id}", comercial.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("comercial-detalle"))
                                .andExpect(content().string(containsString(">" + pedido.getId() + "</td>")))
                                .andExpect(content().string(containsString("/clientes/" + cliente.getId())))
                                .andExpect(content().string(containsString(nombreCliente(cliente))))
                                .andExpect(content().string(containsString(">2025-05-20</td>")))
                                .andExpect(content().string(containsString(">45.50</td>")));
        }

        @Test
        void clientDetailDisplaysAssociatedPedidos() throws Exception {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                Cliente cliente = crearCliente("ClientePedidos" + suffix);
                Comercial comercial = crearComercial("ComercialPedidos" + suffix);
                Pedido pedido = new Pedido(0, new BigDecimal("68.25"), LocalDate.of(2025, 6, 10),
                                cliente.getId(), comercial.getId());
                pedidoService.create(pedido);

                mockMvc.perform(get("/clientes/{id}", cliente.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("cliente-detalle"))
                                .andExpect(content().string(containsString("/pedidos/" + pedido.getId())))
                                .andExpect(content().string(containsString(">2025-06-10</td>")))
                                .andExpect(content().string(containsString(">68.25</td>")));
        }

    private Cliente crearCliente(String nombre) {
        Cliente cliente = new Cliente(0, nombre, "Prueba", null, "Test", null);
        clienteService.create(cliente);
        return cliente;
    }

    private Comercial crearComercial(String nombre) {
        Comercial comercial = new Comercial(0, nombre, "Prueba", null, 0.1f);
        comercialService.create(comercial);
        return comercial;
    }

    private String nombreCliente(Cliente cliente) {
        return cliente.getNombre() + " " + cliente.getApellido1();
    }

    private String nombreComercial(Comercial comercial) {
        return comercial.getNombre() + " " + comercial.getApellido1();
    }

    private Cliente compruebaCliente(int id) {
        return clienteService.find(id).orElseThrow();
    }

    private Comercial compruebaComercial(int id) {
        return comercialService.find(id).orElseThrow();
    }
}