package com.progetto.sitoforzearmate.controller;

import com.example.sitoforzaarmata.model.dao.Base.PastoDAO;
import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.Utente.AmministratoreDAO;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import com.example.sitoforzaarmata.model.mo.Base.Pasto;
import com.example.sitoforzaarmata.model.mo.Utente.Amministratore;
import com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato;
import com.example.sitoforzaarmata.services.configuration.Configuration;
import com.example.sitoforzaarmata.services.logservice.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrenotaPasto {
    private PrenotaPasto(){ }
    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser;
        Amministratore loggedAdmin;

        String applicationMessage = null;

        Logger logger = LogService.getApplicationLogger();

        try {

            Map sessionFactoryParameters=new HashMap<String,Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            loggedAdmin = sessionAdminDAO.findLoggedAdmin();

            sessionDAOFactory.commitTransaction();

            String locazione = request.getParameter("locazioneBase");

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("locazionePasto", locazione);
            request.setAttribute("viewUrl", "ListaBasi/PrenotaPastoCSS");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Controller Error", e);
            try {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
            }
            throw new RuntimeException(e);

        } finally {
            try {
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
            }
        }
    }

    public static void conferma (HttpServletRequest request, HttpServletResponse response) {
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser;
        Amministratore loggedAdmin;

        String applicationMessage = null;

        Logger logger = LogService.getApplicationLogger();

        try {

            Map sessionFactoryParameters=new HashMap<String,Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);

            /* SESSIONE COOKIE */
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            loggedAdmin = sessionAdminDAO.findLoggedAdmin();

            sessionDAOFactory.commitTransaction();

            /* OPERAZIONI DATABASE */
            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            PastoDAO pastoDAO = daoFactory.getPastoDAO();

            String locazione = request.getParameter("locazionePasto");
            String turno = request.getParameter("Turno");
            String data_prenotazione = request.getParameter("DataPrenotazione");

            Pasto pasto = new Pasto();
            pasto.setLocazione(locazione);
            pasto.setTurno(turno);
            pasto.setData_prenotazioneString(data_prenotazione);
            pasto.setId(pasto.getId());
            pasto.setMatricola(loggedUser.getMatricola());

            pastoDAO.createPrenotazione(pasto);

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("luogoBase", locazione);
            request.setAttribute("Pasto", pasto);
            request.setAttribute("viewUrl", "ListaBasi/ConfermaCSS");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Controller Error", e);
            try {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
            }
            throw new RuntimeException(e);

        } finally {
            try {
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
            }
        }
    }

    // TODO: metodo annulla prenotazione pasto

}
