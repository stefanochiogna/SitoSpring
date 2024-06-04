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
    @PostMapping("/viewPrenotaPasto")
    public ModelAndView view(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
            @RequestParam(value = "locazioneBase") String locazione
    ){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;
        Amministratore loggedAdmin = null;

        String applicationMessage = null;
        
        ModelAndView page = new ModelAndView();
        
        try {
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, response);
            sessionDAOFactory.beginTransaction();
            
            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            if(!cookieAdmin.equals(""))
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            sessionDAOFactory.commitTransaction();
            
            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("locazionePasto", locazione);
            page.setViewName("ListaBasi/PrenotaPastoCSS");

        } catch (Exception e) {
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

            e.printStackTrace();
            page.setViewName("ListaBasi/Lista");
        }
        return page;
    }

    @PostMapping("/confermaPrenotaPasto")
    public ModelAndView conferma (
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,

            @RequestParam(value = "locazionePasto") String locazione,
            @RequestParam(value = "Turno") String turno,
            @RequestParam(value = "DataPrenotazione") String data_prenotazione
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

            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            if(!cookieAdmin.equals(""))
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            sessionDAOFactory.commitTransaction();

            /* OPERAZIONI DATABASE */
            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            PastoDAO pastoDAO = daoFactory.getPastoDAO();

            Pasto pasto = new Pasto();
            pasto.setLocazione(locazione);
            pasto.setTurno(turno);
            pasto.setData_prenotazioneString(data_prenotazione);
            pasto.setId(pasto.getId());
            pasto.setMatricola(loggedUser.getMatricola());

            pastoDAO.createPrenotazione(pasto);

            daoFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("luogoBase", locazione);
            page.addObject("Pasto", pasto);
            page.setViewName("ListaBasi/ConfermaCSS");

        } catch (Exception e) {
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

            e.printStackTrace();
            page.setViewName("ListaBasi/Lista");

        }

        return page;
    }

    // TODO: metodo annulla prenotazione pasto

}
