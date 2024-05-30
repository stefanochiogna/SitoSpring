package com.progetto.sitoforzearmate.model.mo.Notizie;

import java.nio.file.Path;

public class Notizie {

    /* Attributi */
    private String ID;
    private Path RiferimentoTesto;
    private String Oggetto;
    private String IdAdministrator;

    /* Metodi */
    public void setID(String ID){
        this.ID = ID;
    }
    public void setRiferimentoTesto(Path testo){
        this.RiferimentoTesto = testo;
    }
    public void setOggetto(String Oggetto){
        this.Oggetto = Oggetto;
    }
    public void setIdAdministrator(String IdAdministrator){
        this.IdAdministrator = IdAdministrator;
    }
    public String getID(){
        return ID;
    }
    public Path getRiferimentoTesto(){
        return RiferimentoTesto;
    }
    public String getOggetto(){
        return Oggetto;
    }
    public String getIdAdministrator(){
        return IdAdministrator;
    }
}
