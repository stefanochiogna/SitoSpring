package com.progetto.sitoforzearmate.model.dao.Bando;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.mo.Bando;

import java.nio.file.Path;
import java.util.List;

public interface BandoDAO {
    public Bando create (
        Data data,
        String Id,
        String Oggetto,
        Path RiferimentoTesto,
        String Locazione,
        Integer N_max_iscritti,
        Data dataScadenza,
        String IdAdmin
    );

    public void delete(Bando bando);
    public void update(Data data, String Id, String Oggetto, String Locazione, Integer MaxIscritti, Data dataScadenza, String IdAdmin);
    public List<Bando> show();
    public List<String> getDate();
    public List<Bando> getBandiScaduti();
    public Bando findbyId(String Id);
    public String getLastId();
    public void updateEsito(String IdBando, String IdPartecipante, String Esito );
}
