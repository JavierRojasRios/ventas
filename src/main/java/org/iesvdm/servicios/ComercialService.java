package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComercialService {

    @Autowired
    private ComercialDAO comercialDAO;

    public List<Comercial> listAll() {
        return comercialDAO.getAll();
    }

    public Optional<Comercial> find(int id) {
        return comercialDAO.find(id);
    }

    public void create(Comercial comercial) {
        comercialDAO.create(comercial);
    }

    public void update(Comercial comercial) {
        comercialDAO.update(comercial);
    }

    public void delete(int id) {
        comercialDAO.delete(id);
    }
}
