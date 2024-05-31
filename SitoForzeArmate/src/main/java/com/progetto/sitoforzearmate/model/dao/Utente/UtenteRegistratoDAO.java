package com.progetto.sitoforzearmate.model.dao.Utente;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Utente.Badge;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;

import java.util.List;

public interface UtenteRegistratoDAO {

    public UtenteRegistrato create(
            /* Attributi Utente */
            String Nome,
            String Cognome,
            String CF,
            String mail,
            String telefono,
            String password,
            String sesso,
            Data dataNascita,

            /* Attributi UtenteRegistrato */
            String Matricola,
            /* Long Stipendio, */
            String IBAN,
            String Ruolo,
            byte[] Foto,
            byte[] Documento,
            String Indirizzo,
            String Locazione_servizio,
            boolean Newsletter,

            List<String> Bandi);

    public void update(UtenteRegistrato utente);
    public void delete(UtenteRegistrato utente);
    public void iscrizioneBando(UtenteRegistrato user, Bando bando);
    public void annullaIscrizione(UtenteRegistrato user, Bando bando);
    public List<String> recuperaBandi(String Matricola);
    public boolean isDeleted(UtenteRegistrato user);
    public UtenteRegistrato findByMatricola(String Matricola);
    public UtenteRegistrato findByMail(String mail);
    public String getLastMatricola();
    public List<UtenteRegistrato> getUtenti();
    public List<UtenteRegistrato> getUtentiRuolo(String Ruolo);
    public boolean maxIscrittiRaggiunto(Bando bando);

    /* CONTATTI */
    public List<String> ElencoIniziali(UtenteRegistrato user);
    public List<UtenteRegistrato> cercaIniziale(String Matricola, String Iniziale);

    /* NEWSLETTER */
    public void iscrizioneNewsletter(String MailUtente);
    public void annullaIscrizione(String MailUtente);
    public boolean isIscritto(String MailUtente);
    public List<UtenteRegistrato> getUtentiNewsletter();


    /* BADGE */
    public Badge getBadge(String Matricola);
}