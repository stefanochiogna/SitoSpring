package com.progetto.sitoforzearmate.controller;

import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato;
import com.example.sitoforzaarmata.services.configuration.Configuration;
import com.example.sitoforzaarmata.services.logservice.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rubrica {
    private Rubrica() {
    }

    public static void view(HttpServletRequest request, HttpServletResponse response) {

        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        UtenteRegistrato loggedUser;
        String applicationMessage = null;

        Logger logger = LogService.getApplicationLogger();

        try {

            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request", request);
            sessionFactoryParameters.put("response", response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            List<String> Iniziali;
            List<UtenteRegistrato> contatti = new ArrayList<>();

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            UtenteRegistrato user = userDAO.findByMatricola(loggedUser.getMatricola());

            Iniziali = userDAO.ElencoIniziali(user);

            String inizialeSelezionata = request.getParameter("inizialeSelezionata");

            if (inizialeSelezionata == null) {
                inizialeSelezionata = "*";
            }

            contatti.addAll(userDAO.cercaIniziale(user.getMatricola(), inizialeSelezionata));

            request.setAttribute("inizialeSelezionata", inizialeSelezionata);
            request.setAttribute("ListaIniziali", Iniziali);
            request.setAttribute("Contatti", contatti);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn", loggedUser != null);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("applicationMessage", applicationMessage);
            request.setAttribute("viewUrl", "Rubrica/viewCSS");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Controller Error", e);
            try {
                if (daoFactory != null) daoFactory.rollbackTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
            }
            throw new RuntimeException(e);

        } finally {
            try {
                if (daoFactory != null) daoFactory.closeTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
            }

        }

    }
}
