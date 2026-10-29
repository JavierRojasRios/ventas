package org.iesvdm.controlador;

import org.iesvdm.servicios.ClienteService;
import java.util.List;
import org.iesvdm.modelo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ClienteController {
    
    @Autowired 
    private ClienteService clienteService;

    @GetMapping("/clientes")
    public String listar(Model model){
        List<Cliente> clientes = clienteService.listAll();
        model.addAttribute("listaClientes", clientes);
        return "clientes";
    }
}
