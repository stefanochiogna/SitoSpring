package com.progetto.sitoforzearmate.model.dao.Base;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.mo.Base.Pasto;

import java.util.List;

public interface PastoDAO {
    public Pasto create(
            String Turno,
            Data data,
            String locazione
    );

    public void delete(Pasto pasto);
    public Pasto findbyId (String id, String locazione);
    public Pasto createPrenotazione ( Pasto pasto );
    public List<Pasto> findByMatricola(String Matricola);
}
