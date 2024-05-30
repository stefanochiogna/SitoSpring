package com.progetto.sitoforzearmate.model.dao.Notizie;

import com.example.sitoforzaarmata.model.mo.Notizie.Notizie;

import java.nio.file.Path;


public interface NotizieDAO {
    public Notizie create(
            String Id,
            String Oggetto,
            Path RiferimentoTesto,
            String IdAdministrator);

    public void update(Notizie notizie);
    public void delete(Notizie notizie);
    public Notizie findById(String Id);

}
