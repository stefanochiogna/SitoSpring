package com.progetto.sitoforzearmate.model.dao.Cookie.Utente;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Utente.Badge;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

public class UtenteRegistratoDAOcookie implements UtenteRegistratoDAO {
    HttpServletResponse response;

    private Long Matricola = (long)0;
    private long StipendioBase = (long)1000;

    public UtenteRegistratoDAOcookie(HttpServletResponse response){
        this.response = response;
    }

    @Override
    public UtenteRegistrato create(
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
            String IBAN,
            /* Long Stipendio, */
            String Ruolo,
            byte[] Foto,
            byte[] Documento,
            String Indirizzo,
            String Locazione_servizio,
            boolean Newsletter,
            List<String> Bandi){

        /* Inizializzazione Utente */
        UtenteRegistrato user = new UtenteRegistrato();

        user.setNome(Nome);
        user.setCognome(Cognome);
        user.setCF(CF);
        user.setMail(mail);
        user.setTelefono(telefono);
        user.setPassword(password);
        user.setSesso(sesso);
        user.setDataNascita(dataNascita);

        Optional<String> Role = Optional.ofNullable(Ruolo);

        user.setMatricola(Matricola);
        user.setRuolo(Ruolo);
        user.setIBAN(IBAN);
        user.setFotoByte(Foto);
        user.setDocumentoByte(Documento);
        user.setIndirizzo(Indirizzo);
        user.setLocazioneServizio(Locazione_servizio);
        user.setIscrittoNewsletter(Newsletter);

        Optional<List<String>> bandi = Optional.ofNullable(Bandi);
        if(bandi.isPresent()) user.AddAllBando(Bandi);

        //user.stampaBandi();

        if(Role.isPresent()){
            switch(Ruolo){
                case "Ufficiale":
                    user.setStipendio((long)2*StipendioBase);
                    break;
                case "Sottufficiale":
                    user.setStipendio((long)1.5*StipendioBase);
                    break;
                case "Graduato":
                    user.setStipendio(StipendioBase);
                    break;
            }
        }
        else{
            user.setStipendio((long)0);
        }

        /* Creazione Cookie */
        Cookie cookie;
        cookie = new Cookie("loggedUser", encode(user));
        cookie.setPath("/");
            /*
            setPath: specifica il percorso per il quale il cookie è valido
            Con "/" il cookie viene impostato come valido in tutto il dominio del Server
            */

        response.addCookie(cookie);

        return user;
    }

    @Override
    public void update(UtenteRegistrato utente){
        Cookie cookie;
        cookie = new Cookie("loggedUser", encode(utente));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public void delete(UtenteRegistrato utente){
        Cookie cookie;
        cookie = new Cookie("loggedUser", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    /*
    @Override
    public UtenteRegistrato findLoggedUser() {

        Cookie[] cookies = request.getCookies();
        // Recupero lista cookies //

        UtenteRegistrato user = null;

        if (cookies != null) {
            for (int i = 0; i < cookies.length && user == null; i++) {
                if (cookies[i].getName().equals("loggedUser")) {
                    user = decode(cookies[i].getValue());
                }
            }
        }

        return user;
    }
    */

    @Override
    public boolean isDeleted(UtenteRegistrato user){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public UtenteRegistrato findByMatricola(String Matricola){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public UtenteRegistrato findByMail(String mail){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public void iscrizioneBando(UtenteRegistrato user, Bando bando){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public void annullaIscrizione(UtenteRegistrato user, Bando bando){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public List<String> recuperaBandi(String Matricola){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public void iscrizioneNewsletter(String MailUtente){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public void annullaIscrizione(String MailUtente){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public boolean isIscritto(String MailUtente){throw new RuntimeException("Non supportato"); }
    @Override
    public List<UtenteRegistrato> getUtentiNewsletter(){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public String getLastMatricola(){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public List<UtenteRegistrato> cercaIniziale(String Matricola, String Iniziale){throw new RuntimeException("Non supportato");}
    @Override
    public List<String> ElencoIniziali(UtenteRegistrato user){throw new RuntimeException("Non supportato");}
    @Override
    public Badge getBadge(String Matricola){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public List<UtenteRegistrato> getUtenti(){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public List<UtenteRegistrato> getUtentiRuolo(String Ruolo){
        throw new RuntimeException("Non supportato");
    }
    public boolean maxIscrittiRaggiunto(Bando bando){
        throw new RuntimeException("Non supportato");
    }
    private String encode(UtenteRegistrato user){
        String encodeInfo;
        String encodeBandi ="";
        String encodeText;

        encodeInfo = user.getCF() + "#" + user.getMatricola() + "#" + user.getMail();

        // CF#matricola#mail-bando1%bando2%bando3...
        for(int i=0; i<user.getLunghezzaBando(); i++){
            encodeBandi = encodeBandi + user.getElementoBando(i) + "%";
        }

        encodeText = encodeInfo + "-" + encodeBandi;
        return encodeText;
    }
    public static UtenteRegistrato decode(String encodeText){
        UtenteRegistrato user = new UtenteRegistrato();
        String[] campi = encodeText.split("-");

        String[] info = campi[0].split("#");
        user.setCF(info[0]);
        user.setMatricola(info[1]);
        user.setMail(info[2]);

        if(campi.length > 1) {
            if (campi[1] != "") {
                String[] idBandi = campi[1].split("%");
                for (int i = 0; i < idBandi.length; i++) user.AddIdBando(idBandi[i]);
            }
        }

        return user;
    }
}
