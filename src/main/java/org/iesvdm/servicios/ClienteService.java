package org.iesvdm.servicios;

import java.util.List;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.dao.ClienteDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class ClienteService {
    
    @Autowired 
    private ClienteDAO clienteDAO;

    public List<Cliente> listAll(){
        return clienteDAO.getAll();
    }
}
