package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.iesvdm.dto.ClienteComercialDTO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class ClienteService {
    
    @Autowired 
    private ClienteDAO clienteDAO;

    @Autowired
    private PedidoDAO pedidoDAO;

    public List<Cliente> listAll(){
        return clienteDAO.getAll();
    }

    public List<Cliente> findByCity(String ciudad) {
        return clienteDAO.findByCity(ciudad);
    }

    public Optional<Cliente> find(int id) {
        return clienteDAO.find(id);
    }

    public List<PedidoDetalle> listPedidosByClienteId(int clienteId) {
        return pedidoDAO.findByClienteId(clienteId);
    }

    public List<ClienteComercialDTO> listComercialesByClienteId(int clienteId) {
        return clienteDAO.findComercialesByClienteId(clienteId);
    }

    public void addComercial(int clienteId, int comercialId, LocalDate fecha, int prioridad) {
        clienteDAO.addComercial(clienteId, comercialId, fecha, prioridad);
    }

    public void removeComercial(int clienteId, int comercialId) {
        clienteDAO.removeComercial(clienteId, comercialId);
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
