package org.iesvdm.controlador;

import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.dto.ClienteComercialDTO;
import org.iesvdm.modelo.Comercial;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequestMapping("/clientes")
@Controller
public class ClienteController {
    
    @Autowired 
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @GetMapping
    public String listar(@RequestParam Map<String, String> parametros, Model model){
        String ciudad = parametros.get("ciudad");
        if (ciudad == null) {
            ciudad = parametros.entrySet().stream()
                    .filter(parametro -> parametro.getValue() == null || parametro.getValue().isBlank())
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
        }

        if (ciudad != null) {
            ciudad = ciudad.trim();
        }
        List<Cliente> clientes = ciudad == null || ciudad.isBlank()
                ? clienteService.listAll()
                : clienteService.findByCity(ciudad);
        model.addAttribute("listaClientes", clientes);
        model.addAttribute("ciudadFiltro", ciudad);
        return "clientes";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("nuevo", true);
        return "cliente-form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        clienteService.create(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return clienteService.find(id)
                .map(cliente -> {
                    List<PedidoDetalle> pedidos = clienteService.listPedidosByClienteId(id);
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("pedidos", pedidos);
                    return "cliente-detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "No se encontró el cliente.");
                    return "redirect:/clientes";
                });
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return clienteService.find(id)
                .map(cliente -> {
                    List<ClienteComercialDTO> comercialesVinculados =
                        clienteService.listComercialesByClienteId(id);
                    Set<Integer> idsVinculados = comercialesVinculados.stream()
                        .map(ClienteComercialDTO::getComercialId)
                        .collect(Collectors.toSet());
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("nuevo", false);
                    model.addAttribute("comercialesVinculados", comercialesVinculados);
                    model.addAttribute("comercialesDisponibles", comercialService.listAll().stream()
                        .filter(comercial -> !idsVinculados.contains(comercial.getId()))
                        .toList());
                    return "cliente-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "No se encontró el cliente.");
                    return "redirect:/clientes";
                });
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable int id, @ModelAttribute Cliente cliente,
            RedirectAttributes redirectAttributes) {
        cliente.setId(id);
        clienteService.update(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/editar/{id}/comerciales")
    public String anadirComercial(@PathVariable int id, @RequestParam int comercialId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam int prioridad, RedirectAttributes redirectAttributes) {
        if (clienteService.find(id).isEmpty() || comercialService.find(comercialId).isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el cliente o el comercial.");
            return "redirect:/clientes";
        }

        boolean yaVinculado = clienteService.listComercialesByClienteId(id).stream()
                .anyMatch(vinculo -> vinculo.getComercialId() == comercialId);
        if (yaVinculado) {
            redirectAttributes.addFlashAttribute("mensaje", "El comercial ya está vinculado a este cliente.");
        } else {
            clienteService.addComercial(id, comercialId, fecha, prioridad);
            redirectAttributes.addFlashAttribute("mensaje", "Comercial añadido al cliente.");
        }
        return "redirect:/clientes/editar/" + id;
    }

    @PostMapping("/editar/{id}/comerciales/{comercialId}/borrar")
    public String desvincularComercial(@PathVariable int id, @PathVariable int comercialId,
            RedirectAttributes redirectAttributes) {
        if (clienteService.find(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el cliente.");
            return "redirect:/clientes";
        }

        clienteService.removeComercial(id, comercialId);
        redirectAttributes.addFlashAttribute("mensaje", "Comercial desvinculado del cliente.");
        return "redirect:/clientes/editar/" + id;
    }

    @PostMapping("/borrar/{id}")
    public String borrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado correctamente.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("mensaje", "No se puede eliminar el cliente porque tiene pedidos asociados.");
        }
        return "redirect:/clientes";
    }
}
