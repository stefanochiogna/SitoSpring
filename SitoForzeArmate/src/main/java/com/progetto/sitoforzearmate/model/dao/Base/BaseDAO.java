package com.progetto.sitoforzearmate.model.dao.Base;

import com.progetto.sitoforzearmate.model.mo.Base.Base;

import java.util.List;

public interface BaseDAO {
    public Base create (
        byte[] Foto,
        String Latitudine,
        String Longitudine,
        String Locazione,
        String Email,
        String Telefono,
        String Provincia,
        String CAP,
        String Via,
        String IdAdministrator );

    public void delete(Base base);
    public Base findbyLocazione (String Locazione);
    public List<Base> stampaBasi();
}
