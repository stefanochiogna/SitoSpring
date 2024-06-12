package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Base.Base;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ListaBasi {

    @GetMapping("/viewListaBasi")
    public ModelAndView view(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {
                if(cookieAdmin.equals("") && cookieUser.equals(""))
                    throw new RuntimeException("Utente non loggato");
                if(!cookieAdmin.equals("") && !cookieUser.equals(""))
                    throw new RuntimeException("Entrambi gli utenti non possono essere loggati");

                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                if( ! cookieAdmin.equals("") )    
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                BaseDAO baseDAO = daoFactory.getBaseDAO();

                List<Base> baseList = baseDAO.stampaBasi();

                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("Basi", baseList);
                page.setViewName("ListaBasi/Lista");

            } catch (Exception e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }

            return page;
        }

    @PostMapping(path = "/viewBase", params = {"luogoBase"})
    public ModelAndView viewBase(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "luogoBase", defaultValue = "") String locazione
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {
                if(cookieAdmin.equals("") && cookieUser.equals(""))
                    throw new RuntimeException("Utente non loggato");
                if(!cookieAdmin.equals("") && !cookieUser.equals(""))
                    throw new RuntimeException("Entrambi gli utenti non possono essere loggati");

                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                if(locazione.equals("")) throw new RuntimeException("Base non selezionata");
                BaseDAO baseDAO = daoFactory.getBaseDAO();
                Base BaseSelezionata = baseDAO.findbyLocazione(locazione);

                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("BaseSelezionata", BaseSelezionata);
                page.setViewName("ListaBasi/viewBase");

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return page;
        }

    @PostMapping(path = "/deleteBase", params = {"luogoBase"})
    public ModelAndView deleteBase(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "luogoBase", defaultValue = "") String locazione
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory daoFactory = null;

            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);
                else throw new RuntimeException("Utente non loggato");

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                BaseDAO baseDAO = daoFactory.getBaseDAO();

                if(locazione.equals("")) throw new RuntimeException("Base non selezionata");

                Base base = baseDAO.findbyLocazione(locazione);         // vado a recuperare la base selezionata tramite locazione

                baseDAO.delete(base);                                   // lo passo poi al metodo delete per cancellarlo

                daoFactory.commitTransaction();

                page.addObject("loggedAdminOn", true);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("BaseSelezionata", base);

                page.setViewName("ListaBasi/reload");

            } catch (Exception e) {

                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return page;
        }

    @GetMapping("/registraBase")
    public ModelAndView registraBase(
            HttpServletResponse response,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin
    ){
        ModelAndView page = new ModelAndView();

        Amministratore loggedAdmin = null;

        try {
            if( ! cookieAdmin.equals("") )
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);
            else throw new RuntimeException("Utente non loggato");

            page.addObject("loggedAdminOn", true);
            page.addObject("loggedAdmin", loggedAdmin);
            page.setViewName("ListaBasi/NewBaseCSS");

        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return page;
    }

    @PostMapping(path = "/newBase")
    public ModelAndView newBase(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "Foto") Part foto,
        @RequestParam(value = "Email", defaultValue = "") String email,
        @RequestParam(value = "Telefono", defaultValue = "") String telefono,
        @RequestParam(value = "Locazione", defaultValue = "") String locazione,
        @RequestParam(value = "Provincia", defaultValue = "") String provincia,
        @RequestParam(value = "CAP", defaultValue = "") String CAP,
        @RequestParam(value = "Via", defaultValue = "") String via,
        @RequestParam(value = "Latitudine", defaultValue = "") String latitudine,
        @RequestParam(value = "Longitudine", defaultValue = "") String longitudine
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory daoFactory = null;
            Amministratore loggedAdmin = null;
            String applicationMessage = null;

            try {
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);
                else throw new RuntimeException("Utente non loggato");

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                if(foto == null || email.equals("") || telefono.equals("") || locazione.equals("") ||
                    provincia.equals("") || CAP.equals("") || via.equals("") || latitudine.equals("") || longitudine.equals(""))
                    throw new RuntimeException("Parametri obbligatori per la creazinoe base non forniti");

                BaseDAO baseDAO = daoFactory.getBaseDAO();
                baseDAO.create( foto.getInputStream().readAllBytes(), latitudine, longitudine, locazione, email, telefono, provincia, CAP,
                        via, loggedAdmin.getIdAdministrator());

                daoFactory.commitTransaction();

                page.addObject("loggedAdminOn",true);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("applicationMessage", applicationMessage);
                page.setViewName("ListaBasi/reload");


            } catch (Exception e) {
                if (daoFactory != null) daoFactory.rollbackTransaction();

                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return page;
        }
}
