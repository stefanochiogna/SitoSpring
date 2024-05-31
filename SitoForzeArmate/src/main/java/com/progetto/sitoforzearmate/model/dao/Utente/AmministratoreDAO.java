package com.progetto.sitoforzearmate.model.dao.Utente;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;

public interface AmministratoreDAO {
    public Amministratore create(
            /* Attributi Utente */
            String Nome,
            String Cognome,
            String CF,
            String mail,
            String telefono,
            String password,
            String sesso,
            Data dataNascita,

            /* Attributi Amministratore */
            String IdAdministrator);

    public void update(Amministratore Admin);
    public void delete(Amministratore Admin);
    public Amministratore findByMail(String mail);
    public Amministratore findById(String IdAdmin);
}