package org.iesvdm.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PedidoDAOImpl implements PedidoDAO {

    private static final String SQL_PEDIDOS_DETALLE = """
         SELECT p.id, p.total, p.fecha, p.id_cliente, p.id_comercial,
             CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS cliente_nombre_completo,
             CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS comercial_nombre_completo
         FROM pedido p
         JOIN cliente c ON p.id_cliente = c.id
         JOIN comercial co ON p.id_comercial = co.id
         """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Inserta el pedido y actualiza su id con la clave generada.
     */
    @Override
    public synchronized void create(Pedido pedido) {
        String sqlInsert = """
                INSERT INTO pedido (total, fecha, id_cliente, id_comercial)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlInsert, new String[] { "id" });
            ps.setBigDecimal(1, pedido.getTotal());
            LocalDate fecha = pedido.getFecha();
            ps.setDate(2, fecha == null ? null : Date.valueOf(fecha));
            ps.setInt(3, pedido.getClienteId());
            ps.setInt(4, pedido.getComercialId());
            return ps;
        }, keyHolder);

        pedido.setId(keyHolder.getKey().intValue());
        log.info("Insertados {} registros.", rows);
    }

    /**
     * Devuelve todos los pedidos.
     */
    @Override
    public List<Pedido> getAll() {
        List<Pedido> pedidos = jdbcTemplate.query(
                "SELECT * FROM pedido",
                (rs, rowNum) -> mapPedido(rs));

        log.info("Devueltos {} registros.", pedidos.size());
        return pedidos;
    }

    /**
     * Devuelve el pedido con el id dado, si existe.
     */
    @Override
    public Optional<Pedido> find(int id) {
        List<Pedido> pedidos = jdbcTemplate.query(
                "SELECT * FROM pedido WHERE id = ?",
                (rs, rowNum) -> mapPedido(rs),
                id);

        return pedidos.stream().findFirst();
    }

    /**
     * Devuelve todos los pedidos con los nombres completos relacionados.
     */
    @Override
    public List<PedidoDetalle> getAllDetalles() {
        List<PedidoDetalle> pedidos = jdbcTemplate.query(
                SQL_PEDIDOS_DETALLE,
                (rs, rowNum) -> mapPedidoDetalle(rs));

        log.info("Devueltos {} detalles de pedido.", pedidos.size());
        return pedidos;
    }

    /**
     * Devuelve el pedido con los nombres completos relacionados.
     */
    @Override
    public Optional<PedidoDetalle> findDetalle(int id) {
        List<PedidoDetalle> pedidos = jdbcTemplate.query(
                SQL_PEDIDOS_DETALLE + " WHERE p.id = ?",
                (rs, rowNum) -> mapPedidoDetalle(rs),
                id);

        return pedidos.stream().findFirst();
    }

    /**
     * Actualiza los campos del pedido identificado por su id.
     */
    @Override
    public void update(Pedido pedido) {
        int rows = jdbcTemplate.update("""
                UPDATE pedido SET
                    total = ?,
                    fecha = ?,
                    id_cliente = ?,
                    id_comercial = ?
                WHERE id = ?
                """, pedido.getTotal(),
                pedido.getFecha() == null ? null : Date.valueOf(pedido.getFecha()),
                pedido.getClienteId(),
                pedido.getComercialId(),
                pedido.getId());

        log.info("Update de Pedido con {} registros actualizados.", rows);
    }

    /**
     * Borra el pedido con el id proporcionado.
     */
    @Override
    public void delete(int id) {
        int rows = jdbcTemplate.update("DELETE FROM pedido WHERE id = ?", id);
        log.info("Delete de Pedido con {} registros eliminados.", rows);
    }

    private Pedido mapPedido(ResultSet rs) throws SQLException {
        Date sqlFecha = rs.getDate("fecha");
        LocalDate fecha = sqlFecha == null ? null : sqlFecha.toLocalDate();

        return new Pedido(
                rs.getInt("id"),
                rs.getBigDecimal("total"),
                fecha,
                rs.getInt("id_cliente"),
                rs.getInt("id_comercial"));
    }

    private PedidoDetalle mapPedidoDetalle(ResultSet rs) throws SQLException {
        Date sqlFecha = rs.getDate("fecha");
        Pedido pedido = new Pedido(
                rs.getInt("id"),
                rs.getBigDecimal("total"),
            sqlFecha == null ? null : sqlFecha.toLocalDate(),
                rs.getInt("id_cliente"),
                rs.getInt("id_comercial"));

        return new PedidoDetalle(
                pedido,
                rs.getString("cliente_nombre_completo"),
                rs.getString("comercial_nombre_completo"));
    }

    @Override
    public List<PedidoDetalle> findByClienteId(int clienteId) {
        List<PedidoDetalle> pedidos = jdbcTemplate.query(
                SQL_PEDIDOS_DETALLE + " WHERE p.id_cliente = ?",
                (rs, rowNum) -> mapPedidoDetalle(rs),
                clienteId);

        log.info("Devueltos {} detalles de pedido para el cliente con ID {}.", pedidos.size(), clienteId);
        return pedidos;
    }

    @Override
    public List<PedidoDetalle> findByComercialId(int comercialId) {
        List<PedidoDetalle> pedidos = jdbcTemplate.query(
                SQL_PEDIDOS_DETALLE + " WHERE p.id_comercial = ?",
                (rs, rowNum) -> mapPedidoDetalle(rs),
                comercialId);

        log.info("Devueltos {} detalles de pedido para el comercial con ID {}.", pedidos.size(), comercialId);
        return pedidos;
    }
    
}