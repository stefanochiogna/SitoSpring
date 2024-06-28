package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Bando.BandoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PastoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Base.Pasto;
import com.progetto.sitoforzearmate.model.mo.Base.PostoLetto;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.Badge;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class Profilo {
    
    @GetMapping("/viewProfilo")
    public ModelAndView view( 
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin
    ){
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;
        Amministratore loggedAdmin = null;
        
        ModelAndView page = new ModelAndView();
        
        UtenteRegistrato user = new UtenteRegistrato();
        Amministratore admin = new Amministratore();

        String applicationMessage = null;

        try {
            if(cookieUser.equals("") && cookieAdmin.equals("")) throw new RuntimeException("Utente non loggato");
            else if(!cookieUser.equals("") && !cookieAdmin.equals("")) throw new RuntimeException("Entrambi i cookie non possono essere settati contemporaneamente");

            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);
            if(!cookieAdmin.equals(""))
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            if(loggedUser != null) {
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
                user = userDAO.findByMail(loggedUser.getMail());

                if(user.getRuolo() != null){
                    Badge badge = userDAO.getBadge(loggedUser.getMatricola());
                    page.addObject("Badge", badge);
                }
            }
            else if(loggedAdmin != null){
                AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();
                admin = adminDAO.findByMail(loggedAdmin.getMail());
            }

            daoFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);

            page.addObject("UtenteCorrente", user);
            page.addObject("AdminCorrente", admin);

            page.setViewName("Profilo/ProfiloCSS");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return page;
    }

    @PostMapping(value = "/modificaProfiloView")
    public ModelAndView modificaProfiloView(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin
    ){
        DAOFactory daoFactory = null;

        Amministratore loggedAdmin = null;
        UtenteRegistrato loggedUser = null;

        ModelAndView page = new ModelAndView();

        UtenteRegistrato user = null;
        Amministratore admin = null;

        String applicationMessage = null;

        try {
            if(cookieUser.equals("") && cookieAdmin.equals("")) throw new RuntimeException("Utente non loggato");
            else if(!cookieUser.equals("") && !cookieAdmin.equals("")) throw new RuntimeException("Entrambi i cookie non possono essere settati contemporaneamente");

            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            if(!cookieAdmin.equals(""))
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            if(loggedUser != null) {
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
                user = userDAO.findByMatricola(loggedUser.getMatricola());
            }
            else if(loggedAdmin != null){
                AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();
                admin = adminDAO.findById(loggedAdmin.getIdAdministrator());
            }

            daoFactory.commitTransaction();

            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("loggedOn", loggedUser != null);
            page.addObject("loggedUser", loggedUser);
            page.addObject("UserSelezionato", user);
            page.addObject("AdminSelezionato", admin);
            page.setViewName("Profilo/modificaProfiloCSS");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return page;
    }

    @PostMapping(value = "/modificaProfilo", params = {"userMail"})
    public ModelAndView modificaProfilo(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,

            @RequestParam(value = "userMail") String Mail,
            @RequestParam(value = "userPassword") String password,
            @RequestParam(value = "userTelefono") String telefono,
            @RequestParam(value = "userIBAN", defaultValue = "", required = false) String IBAN,
            @RequestParam(value = "userFoto", required = false) Part foto,
            @RequestParam(value = "userDocumenti", required = false) Part documenti,
            @RequestParam(value = "userIndirizzo", defaultValue = "", required = false) String indirizzo,
            @RequestParam(value = "Newsletter", defaultValue = "", required = false) String Newsletter
    ){
        DAOFactory daoFactory = null;

        ModelAndView page = new ModelAndView();

        Amministratore loggedAdmin = null;
        UtenteRegistrato loggedUser = null;

        String applicationMessage = null;

        try {
            if(cookieUser.equals("") && cookieAdmin.equals("")) throw new RuntimeException("Utente non loggato");
            else if(!cookieUser.equals("") && !cookieAdmin.equals("")) throw new RuntimeException("Entrambi i cookie non possono essere settati contemporaneamente");

            System.out.println("cookieUtente: " + cookieUser);
            if(!cookieUser.equals("")){
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);
                System.out.println("decode utente profilo effettuata");
            }

            if(!cookieAdmin.equals(""))
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();

            if(Mail.equals("")) throw new RuntimeException("Mail non inserita");
            if(password.equals("") || telefono.equals("")) throw new RuntimeException("Campi obbligatori non compilati");

            UtenteRegistrato user = userDAO.findByMail(Mail);
            if(user != null){

                if(!IBAN.equals("")) user.setIBAN(IBAN);

                if(foto != null && foto.getSize() > 0){
                    user.setFotoByte(foto.getInputStream().readAllBytes());
                }
                if(documenti != null && documenti.getSize() > 0){
                    user.setDocumentoByte(documenti.getInputStream().readAllBytes());
                }
                if(!indirizzo.equals(""))   user.setIndirizzo(indirizzo);
                if(!Newsletter.equals("") ) user.setIscrittoNewsletter(Boolean.getBoolean(Newsletter));
                user.setTelefono(telefono);
                user.setPassword(password);

                userDAO.update(user);

            }
            Amministratore admin = adminDAO.findByMail(Mail);
            if(admin != null){
                admin.setPassword(password);
                admin.setTelefono(telefono);

                adminDAO.update(admin);
            }

            daoFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);

            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);

            page.setViewName("Profilo/reload");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
        return page;
    }

    @GetMapping("/prenotazioniView")
    public ModelAndView prenotazioniView(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser
    ){
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;

        List<Pasto> pastoList = new ArrayList<>();
        List<PostoLetto> alloggioList = new ArrayList<>();

        String applicationMessage = null;

        ModelAndView page = new ModelAndView();

        try {
            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            PastoDAO pastoDAO = daoFactory.getPastoDAO();
            PostoLettoDAO alloggioDAO = daoFactory.getPostoLettoDAO();

            pastoList.addAll(pastoDAO.findByMatricola(loggedUser.getMatricola()));
            alloggioList.addAll(alloggioDAO.findByMatricola(loggedUser.getMatricola()));

            daoFactory.commitTransaction();


            page.addObject("loggedOn",true);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("listaPasti", pastoList);
            page.addObject("listaAlloggi", alloggioList);

            page.setViewName("Profilo/prenotazioniCSS");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return page;
    }

    @GetMapping(value = "/viewDoc", params = {"bandoId", "UtenteSelezionato"})
    public ModelAndView viewDoc(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,

            @RequestParam(value = "bandoId", defaultValue = "") String bandoId,
            @RequestParam(value = "UtenteSelezionato", defaultValue = "") String matrSelezionata
    ) {
        DAOFactory daoFactory = null;

        ModelAndView page = new ModelAndView();

        UtenteRegistrato loggedUser = null;
        Amministratore loggedAdmin = null;

        String applicationMessage = null;

        try {
            if(cookieUser.equals("") && cookieAdmin.equals("")) throw new RuntimeException("Utente non loggato");
            if(!cookieUser.equals("") && !cookieAdmin.equals("")) throw new RuntimeException("Entrambi i cookie non possono essere settati contemporaneamente");

            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            if(!cookieAdmin.equals(""))
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            UtenteRegistratoDAO userDao = daoFactory.getUtenteRegistratoDAO();
            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            if(matrSelezionata.equals("")) throw new RuntimeException("Utente non selezionato");
            UtenteRegistrato utenteSelezionato = userDao.findByMatricola(matrSelezionata);

            if(bandoId.equals("")) throw new RuntimeException("Bando non selezionato");
            Bando bando = bandoDAO.findbyId(bandoId);

            daoFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("BandoSelezionato", bando);
            page.addObject("UtenteSelezionato", utenteSelezionato);

            page.setViewName("Profilo/viewDoc");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return page;
    }
}
