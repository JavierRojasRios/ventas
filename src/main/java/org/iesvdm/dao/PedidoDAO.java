package org.iesvdm.dao;

import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.modelo.Pedido;

public interface PedidoDAO {

    void create(Pedido pedido);
    List<Pedido> getAll();
    Optional<Pedido> find(int id);
    List<PedidoDetalle> getAllDetalles();
    Optional<PedidoDetalle> findDetalle(int id);
    void update(Pedido pedido);
    void delete(int id);
}