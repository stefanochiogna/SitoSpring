package com.progetto.sitoforzearmate.model.dao.Base;

import com.example.sitoforzaarmata.model.dao.Data;
import com.example.sitoforzaarmata.model.mo.Base.PostoLetto;

import java.util.List;

public interface PostoLettoDAO {
    public PostoLetto create (
            String Locazione,
            String Matricola,
            Data dataArrivo,
            Integer numNotti,
            Integer numPersone
    );

    public void delete(PostoLetto postoLetto);
    public List<PostoLetto> findByMatricola(String Matricola);
    public PostoLetto findbyId(String locazione, String Id);
}
