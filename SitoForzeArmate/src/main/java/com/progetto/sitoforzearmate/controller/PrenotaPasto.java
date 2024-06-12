package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Base.PastoDAO;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Base.Pasto;
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
public class PrenotaPasto {
    @PostMapping(value = "/viewPrenotaPasto", params = {"locazioneBase"})
    public ModelAndView view(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @RequestParam(value = "locazioneBase", defaultValue = "") String locazione
    ){
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;

        String applicationMessage = null;
        
        ModelAndView page = new ModelAndView();
        
        try {
            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);
            else throw new RuntimeException("Utente non loggato");

            if(locazione.equals("")) throw new RuntimeException("locazione Base non settata");

            page.addObject("loggedOn",true);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("locazionePasto", locazione);
            page.setViewName("ListaBasi/PrenotaPastoCSS");

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }
        return page;
    }

    @PostMapping(value = "/confermaPrenotaPasto", params = {"locazionePasto"})
    public ModelAndView conferma (
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,

            @RequestParam(value = "locazionePasto", defaultValue = "") String locazione,
            @RequestParam(value = "Turno", defaultValue = "") String turno,
            @RequestParam(value = "DataPrenotazione", defaultValue = "") String data_prenotazione
    ) {
        DAOFactory sessionDAOFactory= null;
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

            PastoDAO pastoDAO = daoFactory.getPastoDAO();

            if(locazione.equals("") || turno.equals("") || data_prenotazione.equals(""))
                throw new RuntimeException("Parametri obbligatori non forniti");

            Pasto pasto = new Pasto();
            pasto.setLocazione(locazione);
            pasto.setTurno(turno);
            pasto.setData_prenotazioneString(data_prenotazione);
            pasto.setId(pasto.getId());
            pasto.setMatricola(loggedUser.getMatricola());

            pastoDAO.createPrenotazione(pasto);

            daoFactory.commitTransaction();

            page.addObject("loggedOn",true);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("luogoBase", locazione);
            page.addObject("Pasto", pasto);
            page.setViewName("ListaBasi/ConfermaCSS");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return page;
    }
}
