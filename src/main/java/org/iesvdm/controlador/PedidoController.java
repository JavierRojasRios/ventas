package org.iesvdm.controlador;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.servicios.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/pedidos")
@Controller
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaPedidos", pedidoService.listAllDetalles());
        return "pedidos";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("nuevo", true);
        cargarPersonas(model);
        return "pedido-form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute Pedido pedido, Model model, RedirectAttributes redirectAttributes) {
        if (!relacionesValidas(pedido)) {
            prepararFormulario(model, pedido, true);
            return "pedido-form";
        }

        pedidoService.create(pedido);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido creado correctamente.");
        return "redirect:/pedidos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return pedidoService.findDetalle(id)
                .map(pedidoDetalle -> {
                    model.addAttribute("pedidoDetalle", pedidoDetalle);
                    return "pedido-detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "No se encontró el pedido.");
                    return "redirect:/pedidos";
                });
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return pedidoService.find(id)
                .map(pedido -> {
                    model.addAttribute("pedido", pedido);
                    model.addAttribute("nuevo", false);
                    cargarPersonas(model);
                    return "pedido-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "No se encontró el pedido.");
                    return "redirect:/pedidos";
                });
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable int id, @ModelAttribute Pedido pedido,
            Model model, RedirectAttributes redirectAttributes) {
        pedido.setId(id);
        if (!relacionesValidas(pedido)) {
            prepararFormulario(model, pedido, false);
            return "pedido-form";
        }

        pedidoService.update(pedido);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido actualizado correctamente.");
        return "redirect:/pedidos";
    }

    @PostMapping("/borrar/{id}")
    public String borrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        pedidoService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido eliminado correctamente.");
        return "redirect:/pedidos";
    }

    private void cargarPersonas(Model model) {
        model.addAttribute("listaClientes", clienteService.listAll());
        model.addAttribute("listaComerciales", comercialService.listAll());
    }

    private void prepararFormulario(Model model, Pedido pedido, boolean nuevo) {
        model.addAttribute("pedido", pedido);
        model.addAttribute("nuevo", nuevo);
        model.addAttribute("mensajeError", "Selecciona un cliente y un comercial existentes.");
        cargarPersonas(model);
    }

    private boolean relacionesValidas(Pedido pedido) {
        Integer clienteId = pedido.getClienteId();
        Integer comercialId = pedido.getComercialId();
        return clienteId != null
            && comercialId != null
            && clienteService.find(clienteId).isPresent()
            && comercialService.find(comercialId).isPresent();
    }
}