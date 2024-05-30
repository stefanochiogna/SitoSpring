package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrenotaAlloggio {
    private PrenotaAlloggio(){ }
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
            request.setAttribute("locazioneAlloggio", locazione);
            request.setAttribute("viewUrl", "ListaBasi/PrenotaAlloggioCSS");

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

            PostoLettoDAO alloggioDAO = daoFactory.getPostoLettoDAO();

            String locazione = request.getParameter("locazioneAlloggio");
            String NumPersone = request.getParameter("NumeroPersone");
            String NumNotti = request.getParameter("NumeroNotti");
            String data_arrivo = request.getParameter("DataArrivo");

            PostoLetto alloggio;

            Data dataArrivo = new Data(data_arrivo);
            alloggio = alloggioDAO.create(locazione, loggedUser.getMatricola(), dataArrivo, Integer.parseInt(NumNotti), Integer.parseInt(NumPersone));


            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("luogoBase", locazione);
            request.setAttribute("PostoLetto", alloggio);
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
    // TODO: quando utente prenota un alloggio viene considerato in trasferta -> aggiornare tabella "inTrasferta"
    // TODO: Aggiungere metodo annulla prenotazione alloggio

}
