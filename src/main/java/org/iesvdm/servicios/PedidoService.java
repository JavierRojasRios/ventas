package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private ClienteDAO ClienteDAO;

    public List<Pedido> listAll() {
        return pedidoDAO.getAll();
    }

    public Optional<Pedido> find(int id) {
        return pedidoDAO.find(id);
    }

    public List<PedidoDetalle> listAllDetalles() {
        return pedidoDAO.getAllDetalles();
    }

    public Optional<PedidoDetalle> findDetalle(int id) {
        return pedidoDAO.findDetalle(id);
    }
    
    public List<Cliente> findByPedidoId(int pedidoId) {
        return ClienteDAO.findByPedidoId(pedidoId);
    }

    public void create(Pedido pedido) {
        pedidoDAO.create(pedido);
    }

    public void update(Pedido pedido) {
        pedidoDAO.update(pedido);
    }

    public void delete(int id) {
        pedidoDAO.delete(id);
    }
}