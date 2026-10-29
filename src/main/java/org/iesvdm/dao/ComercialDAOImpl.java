package org.iesvdm.dao;

import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ComercialDAOImpl implements ComercialDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Comercial> getAll() {
        return jdbcTemplate.query(
                "SELECT id, nombre, apellido1, apellido2, comisión FROM comercial",
                (rs, rowNum) -> mapComercial(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("apellido1"), rs.getString("apellido2"),
                        rs.getObject("comisión", Float.class)));
    }

    @Override
    public Optional<Comercial> find(int id) {
        List<Comercial> comerciales = jdbcTemplate.query(
                "SELECT id, nombre, apellido1, apellido2, comisión FROM comercial WHERE id = ?",
                (rs, rowNum) -> mapComercial(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("apellido1"), rs.getString("apellido2"),
                        rs.getObject("comisión", Float.class)),
                id);
        return comerciales.stream().findFirst();
    }

    @Override
    public void update(Comercial comercial) {
        jdbcTemplate.update("""
                UPDATE comercial
                SET nombre = ?, apellido1 = ?, apellido2 = ?, comisión = ?
                WHERE id = ?
                """,
                comercial.getNombre(),
                comercial.getApellido1(),
                comercial.getApellido2(),
                comercial.getComision(),
                comercial.getId());
    }

    private Comercial mapComercial(int id, String nombre, String apellido1,
            String apellido2, Float comision) {
        return new Comercial(id, nombre, apellido1, apellido2, comision);
    }
}
