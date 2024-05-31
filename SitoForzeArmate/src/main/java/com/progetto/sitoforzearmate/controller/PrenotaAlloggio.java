package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Base.PostoLetto;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class PrenotaAlloggio {

    @GetMapping("/viewAlloggi")
    public ModelAndView view(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,

            @RequestParam(value = "locazioneBase") String locazione
    ){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        ModelAndView page = new ModelAndView();
        UtenteRegistrato loggedUser = null;
        Amministratore loggedAdmin = null;

        String applicationMessage = null;

        try {
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
            if(!cookieUser.equals("")) loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("locazioneAlloggio", locazione);
            page.setViewName("ListaBasi/PrenotaAlloggioCSS");

        } catch (Exception e) {

            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            e.printStackTrace();
            page.setViewName("PaginaInizialeCSS");
        }

        return page;
    }

    @PostMapping(value = "/confermaIscrizione", params = {"locazioneAlloggio"})
    public ModelAndView conferma (
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,

            @RequestParam(value = "logazioneAlloggio") String locazione,
            @RequestParam(value = "NumeroPersone") String NumPersone,
            @RequestParam(value = "NumeroNotti") String NumNotti,
            @RequestParam(value = "DataArrivo") String data_arrivo
            ) {
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;
        Amministratore loggedAdmin = null;

        String applicationMessage = null;

        ModelAndView page = new ModelAndView();

        try {

            /* SESSIONE COOKIE */
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, response);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            if(!cookieAdmin.equals(""))
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            sessionDAOFactory.commitTransaction();

            /* OPERAZIONI DATABASE */
            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            PostoLettoDAO alloggioDAO = daoFactory.getPostoLettoDAO();

            PostoLetto alloggio;

            Data dataArrivo = new Data(data_arrivo);
            alloggio = alloggioDAO.create(locazione, loggedUser.getMatricola(), dataArrivo, Integer.parseInt(NumNotti), Integer.parseInt(NumPersone));


            daoFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("luogoBase", locazione);
            page.addObject("PostoLetto", alloggio);
            page.setViewName("ListaBasi/ConfermaCSS");

        } catch (Exception e) {
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

            e.printStackTrace();
            page.setViewName("PaginaInizialeCSS");
        }
        return page;
    }
    // TODO: quando utente prenota un alloggio viene considerato in trasferta -> aggiornare tabella "inTrasferta"
    // TODO: Aggiungere metodo annulla prenotazione alloggio

}
