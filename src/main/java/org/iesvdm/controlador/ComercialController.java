package org.iesvdm.controlador;

import java.util.List;

import org.iesvdm.modelo.Comercial;
import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.servicios.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/comerciales")
@Controller
public class ComercialController {

    @Autowired
    private ComercialService comercialService;

    @GetMapping
    public String listar(Model model) {
        List<Comercial> comerciales = comercialService.listAll();
        model.addAttribute("listaComerciales", comerciales);
        return "comerciales";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("comercial", new Comercial());
        model.addAttribute("nuevo", true);
        return "comercial-form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute Comercial comercial, RedirectAttributes redirectAttributes) {
        comercialService.create(comercial);
        redirectAttributes.addFlashAttribute("mensaje", "Comercial creado correctamente.");
        return "redirect:/comerciales";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return comercialService.find(id)
                .map(comercial -> {
                    List<PedidoDetalle> pedidos = comercialService.listPedidosByComercialId(id);
                    model.addAttribute("comercial", comercial);
                    model.addAttribute("pedidos", pedidos);
                    return "comercial-detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "No se encontró el comercial.");
                    return "redirect:/comerciales";
                });
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return comercialService.find(id)
                .map(comercial -> {
                    model.addAttribute("comercial", comercial);
                    model.addAttribute("nuevo", false);
                    return "comercial-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "No se encontró el comercial.");
                    return "redirect:/comerciales";
                });
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable int id, @ModelAttribute Comercial comercial,
            RedirectAttributes redirectAttributes) {
        comercial.setId(id);
        comercialService.update(comercial);
        redirectAttributes.addFlashAttribute("mensaje", "Comercial actualizado correctamente.");
        return "redirect:/comerciales";
    }

    @PostMapping("/borrar/{id}")
    public String borrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            comercialService.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Comercial eliminado correctamente.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("mensaje", "No se puede eliminar el comercial porque tiene pedidos asociados.");
        }
        return "redirect:/comerciales";
    }


}
