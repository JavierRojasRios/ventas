package org.iesvdm.dao;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.iesvdm.dto.ClienteComercialDTO;
import org.iesvdm.modelo.Cliente;

public interface ClienteDAO {

	public void create(Cliente cliente);
	public List<Cliente> getAll();
	public List<Cliente> findByCity(String ciudad);
	public Optional<Cliente>  find(int id);
	public void update(Cliente cliente);
	public void delete(int id);
	public List<Cliente> findByPedidoId(int pedidoId);
	public List<ClienteComercialDTO> findComercialesByClienteId(int clienteId);
	public void addComercial(int clienteId, int comercialId, LocalDate fecha, int prioridad);
	public void removeComercial(int clienteId, int comercialId);
}