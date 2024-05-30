package com.progetto.sitoforzearmate.model.dao.Notizie;

import com.example.sitoforzaarmata.model.mo.Notizie.Newsletter;

import java.nio.file.Path;
import java.util.List;

public interface NewsletterDAO {
    public Newsletter create(
            String Id,
            String Oggetto,
            Path RiferimentoTesto,
            String IdAdministrator,
            String MailDestinatario,
            String MatricolaDestinatario);

    public void delete(Newsletter notizie);
    public Newsletter findById(String Id);
    public String getID();
    public List<Newsletter> stampaNewsletter(String MailDestinatario);
}
