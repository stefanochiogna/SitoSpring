package com.progetto.sitoforzearmate.model.mo.Utente;

public class Amministratore extends Utente {
    private String IdAdministrator;

    public void setIdAdministrator(String IdAdministrator){
        this.IdAdministrator = IdAdministrator;
    }
    public String getIdAdministrator(){
        return IdAdministrator;
    }
}
