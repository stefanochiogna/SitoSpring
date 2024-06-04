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
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {
                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )    
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )    
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                sessionDAOFactory.commitTransaction();

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
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/viewBase", params = {"luogoBase"})
    public ModelAndView viewBase(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "luogoBase") String locazione
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {
                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

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
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
                
                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/deleteBase", params = {"luogoBase"})
    public ModelAndView deleteBase(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "luogoBase") String locazione 
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                BaseDAO baseDAO = daoFactory.getBaseDAO();

                Base base = baseDAO.findbyLocazione(locazione);   // vado a recuperare la base selezionata tramite locazione

                baseDAO.delete(base);                                   // lo passo poi al metodo delete per cancellarlo

                daoFactory.commitTransaction();

                /*
                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                */
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("BaseSelezionata", base);

                page.setViewName("ListaBasi/reload");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
                
                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
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

            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.setViewName("ListaBasi/NewBaseCSS");

        }
        catch (Exception e) {
            e.printStackTrace();
            page.setViewName("Pagina_InizialeCSS");
        }
        return page;
    }

    @PostMapping(path = "/newBase")
    public ModelAndView newBase(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "Foto") Part foto,
        @RequestParam(value = "Email") String email,
        @RequestParam(value = "Telefono") String telefono,
        @RequestParam(value = "Locazione") String locazione,
        @RequestParam(value = "Provincia") String provincia,
        @RequestParam(value = "CAP") String CAP,
        @RequestParam(value = "Via") String via,
        @RequestParam(value = "Latitudine") String latitudine,
        @RequestParam(value = "Longitudine") String longitudine
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;
            Amministratore loggedAdmin = null;
            String applicationMessage = null;

            try {
                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                /* Recupero Parametri */

                BaseDAO baseDAO = daoFactory.getBaseDAO();
                baseDAO.create( foto.getInputStream().readAllBytes(), latitudine, longitudine, locazione, email, telefono, provincia, CAP,
                        via, loggedAdmin.getIdAdministrator());

                daoFactory.commitTransaction();
                sessionDAOFactory.commitTransaction();

                page.addObject("loggedAdminOn",loggedAdmin!=null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("applicationMessage", applicationMessage);
                page.setViewName("ListaBasi/reload");


            } catch (Exception e) {if (daoFactory != null) daoFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }
}
