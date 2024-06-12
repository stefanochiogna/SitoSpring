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

    @PostMapping(value = "/viewPrenotaAlloggi", params = {"locazioneBase"})
    public ModelAndView view(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,

            @RequestParam(value = "locazioneBase", defaultValue = "") String locazione
    ){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        ModelAndView page = new ModelAndView();
        UtenteRegistrato loggedUser = null;

        String applicationMessage = null;

        try {
            if(!cookieUser.equals("")) loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);
            else throw new RuntimeException("Utente non loggato");

            if(locazione.equals("")) throw new RuntimeException("Errore nella selezione della locazione");

            page.addObject("loggedOn",true);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("locazioneAlloggio", locazione);
            page.setViewName("ListaBasi/PrenotaAlloggioCSS");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return page;
    }

    @PostMapping(value = "/confermaIscrizione", params = {"locazioneAlloggio"})
    public ModelAndView conferma (
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,

            @RequestParam(value = "logazioneAlloggio", defaultValue = "") String locazione,
            @RequestParam(value = "NumeroPersone", defaultValue = "") String NumPersone,
            @RequestParam(value = "NumeroNotti", defaultValue = "") String NumNotti,
            @RequestParam(value = "DataArrivo", defaultValue = "") String data_arrivo
            ) {
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;

        String applicationMessage = null;

        ModelAndView page = new ModelAndView();

        try {

            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);
            else throw new RuntimeException("Utente non loggato");

            /* OPERAZIONI DATABASE */
            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            PostoLettoDAO alloggioDAO = daoFactory.getPostoLettoDAO();

            PostoLetto alloggio;

            if(locazione.equals("") || NumPersone.equals("") || NumNotti.equals("") || data_arrivo.equals(""))
                throw new RuntimeException("Errore nella selezione della locazione");

            Data dataArrivo = new Data(data_arrivo);
            alloggio = alloggioDAO.create(locazione, loggedUser.getMatricola(), dataArrivo, Integer.parseInt(NumNotti), Integer.parseInt(NumPersone));


            daoFactory.commitTransaction();

            page.addObject("loggedOn",true);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("luogoBase", locazione);
            page.addObject("PostoLetto", alloggio);
            page.setViewName("ListaBasi/ConfermaCSS");

        } catch (Exception e) {
            if(daoFactory != null) daoFactory.rollbackTransaction();
            e.printStackTrace();

            throw new RuntimeException(e);
        }
        return page;
    }
}
