package com.progetto.sitoforzearmate.model.dao.Notizie;

import com.example.sitoforzaarmata.model.mo.Notizie.Avviso;

import java.nio.file.Path;
import java.util.List;

public interface AvvisoDAO {
    public Avviso create(
            String Id,
            String Oggetto,
            Path RiferimentoTesto,
            String IdAdministrator,
            String MatricolaDestinatario);

    public void delete(Avviso notizie);
    public Avviso findById(String Id);
    public String getID();
    public List<Avviso> stampaAvvisi(String MatricolaDestinatario);
}
