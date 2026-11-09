package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;
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

    public List<Cliente> findByCity(String ciudad) {
        return clienteDAO.findByCity(ciudad);
    }

    public Optional<Cliente> find(int id) {
        return clienteDAO.find(id);
    }

    public void create(Cliente cliente) {
        clienteDAO.create(cliente);
    }

    public void update(Cliente cliente) {
        clienteDAO.update(cliente);
    }

    public void delete(int id) {
        clienteDAO.delete(id);
    }
}
