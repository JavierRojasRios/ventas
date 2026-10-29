package org.iesvdm.servicios;

import java.util.List;

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
}
