package com.progetto.sitoforzearmate.model.dao.Cookie.Utente;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AmministratoreDAOcookie implements AmministratoreDAO {
    HttpServletResponse response;

    public AmministratoreDAOcookie(HttpServletResponse response){
        this.response = response;
    }

    @Override
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
            String IdAdministrator){

        Amministratore admin = new Amministratore();

        admin.setNome(Nome);
        admin.setCognome(Cognome);
        admin.setCF(CF);
        admin.setMail(mail);
        admin.setTelefono(telefono);
        admin.setPassword(password);
        admin.setSesso(sesso);
        admin.setDataNascita(dataNascita);

        admin.setIdAdministrator(IdAdministrator);

        /* Creazione Cookie */
        Cookie cookie;
        cookie = new Cookie("loggedAdmin", encode(admin));
        cookie.setPath("/");
            /*
            setPath: specifica il percorso per il quale il cookie è valido
            Con "/" il cookie viene impostato come valido in tutto il dominio del Server
            */

        response.addCookie(cookie);

        return admin;
    }

    @Override
    public void update(Amministratore Admin){
        Cookie cookie;
        cookie = new Cookie("loggedUser", encode(Admin));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public void delete(Amministratore Admin){
        Cookie cookie;
        cookie = new Cookie("loggedAdmin", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    @Override
    public Amministratore findByMail(String mail){
        throw new RuntimeException("Non supportato");
    }
    @Override
    public Amministratore findById(String IdAdmin){
        throw new RuntimeException("Non supportato");
    }

    /*
    @Override
    public Amministratore findLoggedAdmin(){
        Cookie[] cookies = request.getCookies();
        // Recupero lista cookies //

        Amministratore admin = null;

        if (cookies != null) {
            for (int i = 0; i < cookies.length && admin == null; i++) {
                if (cookies[i].getName().equals("loggedAdmin")) {
                    admin = decode(cookies[i].getValue());
                }
            }
        }

        return admin;
    }
    */

    private String encode(Amministratore admin){
        String encodeText;
        encodeText = admin.getCF() + "#" + admin.getIdAdministrator() + "#" + admin.getMail();

        return encodeText;
    }
    public static Amministratore decode(String encodeText){
        Amministratore admin = new Amministratore();

        String[] info = encodeText.split("#");
        admin.setCF(info[0]);
        admin.setIdAdministrator(info[1]);
        admin.setMail(info[2]);

        return admin;
    }
}
