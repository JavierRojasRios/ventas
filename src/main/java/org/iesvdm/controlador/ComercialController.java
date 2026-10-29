package org.iesvdm.controlador;

import java.util.List;

import org.iesvdm.modelo.Comercial;
import org.iesvdm.servicios.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ComercialController {

    @Autowired
    private ComercialService comercialService;

    @GetMapping("/comerciales")
    public String listar(Model model) {
        List<Comercial> comerciales = comercialService.listAll();
        model.addAttribute("listaComerciales", comerciales);
        return "comerciales";
    }
}
