package org.iesvdm.controlador;

import org.iesvdm.servicios.ClienteService;
import java.util.List;
import java.util.Map;
import org.iesvdm.modelo.Cliente;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequestMapping("/clientes")
@Controller
public class ClienteController {
    
    @Autowired 
    private ClienteService clienteService;

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
                    model.addAttribute("cliente", cliente);
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
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("nuevo", false);
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
