package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Base.Base;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class Login {

    @GetMapping("/viewLogin")
    public ModelAndView view(){
        ModelAndView page = new ModelAndView();
        page.setViewName("LoginCSS");

        return page;
    }

    @PostMapping(path = "/loginUser", params = {"Email", "Password"})
    public ModelAndView login(
            HttpServletResponse response,

            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,

            @RequestParam(value = "Email", defaultValue = "") String Email,
            @RequestParam(value = "Password", defaultValue = "") String Password
    ){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        UtenteRegistrato loggedUser = null;
        String applicationMessage = null;

        ModelAndView page = new ModelAndView();

        try {

            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            if(!cookieUser.equals("")) loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            UtenteRegistrato user = userDAO.findByMail(Email);

            if (user == null || !user.getPassword().equals(Password)) {
                sessionUserDAO.delete(null);
                applicationMessage = "Email o password errati";
                loggedUser=null;
            } else {
                loggedUser = sessionUserDAO.create(user.getNome(), user.getCognome(),user.getCF(), user.getMail(), user.getTelefono(),user.getPassword(),
                        user.getSesso(), user.getDataNascita(), user.getMatricola(), user.getIBAN(), user.getRuolo(), user.getFotoByte(), user.getDocumentoByte(),
                        user.getIndirizzo(), user.getLocazioneServizio(), user.getIscrittoNewsletter(), userDAO.recuperaBandi(user.getMatricola()));

                //loggedUser.stampaBandi();
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            page.addObject("loggedOn", loggedUser);
            page.addObject("loggedUser", loggedUser);
            page.addObject("applicationMessage", applicationMessage);

            if(loggedUser != null){
                page.setViewName("index");
            }
            else page.setViewName("LoginCSS");

        } catch (Exception e) {
            if (daoFactory != null) daoFactory.rollbackTransaction();
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

            System.err.println("Errore Login");
            e.printStackTrace();
        }

        return page;

    }

    @GetMapping(path = "/logout")
    public ModelAndView logout(
            HttpServletResponse response
    ){
        DAOFactory sessionDAOFactory= null;
        ModelAndView page = new ModelAndView();

        try {

            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            sessionUserDAO.delete(null);

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            sessionAdminDAO.delete(null);

            sessionDAOFactory.commitTransaction();

            page.addObject("loggedOn",false);
            page.addObject("loggedUser", null);
            page.addObject("loggedAdmin", null);
            page.addObject("loggedAdminOn", false);

            page.setViewName("index");

        } catch (Exception e) {
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            System.err.println("Errore Logout");
            e.printStackTrace();
        }

        return page;
    }

    /* REGISTRAZIONE */

    @GetMapping("/viewRegistrazione")
    public ModelAndView viewRegistrazione(
            HttpServletResponse response
    ){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        ModelAndView page = new ModelAndView();

        try {
            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            BaseDAO baseDAO = daoFactory.getBaseDAO();

            List<Base> baseList = baseDAO.stampaBasi();

            daoFactory.commitTransaction();

            page.addObject("listaBasi", baseList);
            page.setViewName("RegistrazioneCSS");

        } catch (Exception e) {
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            e.printStackTrace();

        }
        return page;
    }

    @PostMapping(value = "/registrazione", params = {})
    public ModelAndView Registrazione(
            HttpServletResponse response,

            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,

            @RequestParam(value = "Nome") String Nome,
            @RequestParam(value = "Cognome") String Cognome,
            @RequestParam(value = "CF") String CF,
            @RequestParam(value = "Telefono") String Telefono,
            @RequestParam(value = "Email") String Email,
            @RequestParam(value = "Password") String Password,
            @RequestParam(value = "Sesso") String sesso,
            @RequestParam(value = "DataNascita") String dataNascita,
            @RequestParam(value = "IBAN") String IBAN,
            @RequestParam(value = "Ruolo") String Ruolo,
            @RequestParam(value = "Foto") Part Foto,
            @RequestParam(value = "Documento") Part Documento,
            @RequestParam(value = "Indirizzo") String Indirizzo,
            @RequestParam(value = "LocazioneServizio") String LocazioneServizio,
            @RequestParam(value = "Newsletter", required = false) boolean Newsletter
    ){
        ModelAndView page = new ModelAndView();

        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        UtenteRegistrato loggedUser = null;
        String applicationMessage = null;

        try {
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            if(!cookieUser.equals("")) loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            // Recupero Parametri
            Data DataNascita = new Data(dataNascita);

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            UtenteRegistrato user = userDAO.findByMail(Email);

            String MatricolaUtente = userDAO.getLastMatricola();
            Long Matricola = Long.parseLong(MatricolaUtente) + 1;
            
            if (user != null) {
                sessionUserDAO.delete(null);
                applicationMessage = "È già presente un utente con la stessa mail";
                loggedUser = null;
            }
            else {
                user = userDAO.create(
                        Nome, Cognome, CF, Email, Telefono, Password, sesso, DataNascita, Matricola.toString(), IBAN, Ruolo, Foto.getInputStream().readAllBytes(),
                        Documento.getInputStream().readAllBytes(), Indirizzo, LocazioneServizio, Newsletter, null
                );

                loggedUser = sessionUserDAO.create(user.getNome(), user.getCognome(), user.getCF(), user.getMail(), user.getTelefono(), user.getPassword(),
                        user.getSesso(), user.getDataNascita(), user.getMatricola(), user.getIBAN(), user.getRuolo(), user.getFotoByte(), user.getDocumentoByte(),
                        user.getIndirizzo(), user.getLocazioneServizio(), Newsletter, userDAO.recuperaBandi(user.getMatricola()));
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);
            page.addObject("loggedUser", loggedUser);
            page.addObject("applicationMessage", applicationMessage);

            if(loggedUser != null){
                page.setViewName("index");
            }
            else page.setViewName("LoginCSS");      // se ho inserito una mail già presente: loggedUser = null

        } catch (Exception e) {
            if (daoFactory != null) daoFactory.rollbackTransaction();
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            System.err.println("Errore Registrazione");
            e.printStackTrace();

        }
        return page;
    }

    @GetMapping("/viewLoginAdmin")
    public ModelAndView viewAmministratore(
            HttpServletResponse response
    ){
        ModelAndView page = new ModelAndView();
        page.setViewName("LoginAmministratoreCSS");

        return page;
    }

    @PostMapping(value = "/loginAdmin", params = {"IdAdministrator", "Password"})
    public ModelAndView loginAmministratore(
            HttpServletResponse response,

            @RequestParam(value = "IdAdministrator") String IdAdministrator,
            @RequestParam(value = "Password") String Password,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin
    ){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        Amministratore loggedAdmin;
        String applicationMessage = null;

        ModelAndView page = new ModelAndView();

        try {
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
            sessionDAOFactory.beginTransaction();

            AmministratoreDAO sessionUserDAO = sessionDAOFactory.getAmministratoreDAO();

            if(!cookieAdmin.equals("")) loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();
            Amministratore admin = adminDAO.findById(IdAdministrator);

            if (admin == null || !admin.getPassword().equals(Password)) {
                sessionUserDAO.delete(null);
                applicationMessage = "Id e password errati";
                loggedAdmin=null;
            } else {
                loggedAdmin = sessionUserDAO.create(admin.getNome(), admin.getCognome(), admin.getCF(), admin.getMail(), admin.getTelefono(),
                        admin.getPassword(), admin.getSesso(), admin.getDataNascita(), admin.getIdAdministrator());

                //loggedUser.stampaBandi();
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            page.addObject("loggedOn",loggedAdmin!=null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("applicationMessage", applicationMessage);

            if(loggedAdmin != null){
                page.setViewName("index");
            }
            else page.setViewName("LoginAmministratoreCSS");

        } catch (Exception e) {
            if (daoFactory != null) daoFactory.rollbackTransaction();
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

            System.err.println("Errore login amministratore");
            e.printStackTrace();

        }
        return page;
    }

}
