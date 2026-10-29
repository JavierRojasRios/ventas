package org.iesvdm.dao;

import java.util.List;
import java.util.Optional;
import org.iesvdm.modelo.Comercial;

public interface ComercialDAO {

    List<Comercial> getAll();
    Optional<Comercial> find(int id);
    void update(Comercial comercial);
}
