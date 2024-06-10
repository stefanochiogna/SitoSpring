package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class Rubrica {

    @GetMapping("/viewRubrica")
    public ModelAndView view(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @RequestParam(value = "inizialeSelezionata", defaultValue = "") String inizialeSelezionata
    ) {

        // DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        UtenteRegistrato loggedUser = null;
        String applicationMessage = null;

        ModelAndView page = new ModelAndView();

        try {
            // sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, response);

            if(!cookieUser.equals(""))
                loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);
            else
                throw new RuntimeException("Utente non loggato");

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            List<String> Iniziali;
            List<UtenteRegistrato> contatti = new ArrayList<>();

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            UtenteRegistrato user = userDAO.findByMatricola(loggedUser.getMatricola());

            Iniziali = userDAO.ElencoIniziali(user);

            if (inizialeSelezionata.equals("")) {
                inizialeSelezionata = "*";
            }

            contatti.addAll(userDAO.cercaIniziale(user.getMatricola(), inizialeSelezionata));

            page.addObject("inizialeSelezionata", inizialeSelezionata);
            page.addObject("ListaIniziali", Iniziali);
            page.addObject("Contatti", contatti);

            daoFactory.commitTransaction();

            page.addObject("loggedOn", true);
            page.addObject("loggedUser", loggedUser);
            page.addObject("applicationMessage", applicationMessage);
            page.setViewName("Rubrica/viewCSS");

        } catch (Exception e) {
            if (daoFactory != null) daoFactory.rollbackTransaction();
            // if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return page;

    }
}
