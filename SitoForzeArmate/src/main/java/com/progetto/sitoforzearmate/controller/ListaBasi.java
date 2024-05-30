package com.progetto.sitoforzearmate.controller;

import com.example.sitoforzaarmata.model.dao.Base.BaseDAO;
import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.Utente.AmministratoreDAO;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import com.example.sitoforzaarmata.model.mo.Base.Base;
import com.example.sitoforzaarmata.model.mo.Utente.Amministratore;
import com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato;
import com.example.sitoforzaarmata.services.configuration.Configuration;
import com.example.sitoforzaarmata.services.logservice.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListaBasi {
    private ListaBasi(){}
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

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            BaseDAO baseDAO = daoFactory.getBaseDAO();

            List<Base> baseList = baseDAO.stampaBasi();

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("Basi", baseList);
            request.setAttribute("viewUrl", "ListaBasi/Lista");

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
    public static void viewBase(HttpServletRequest request, HttpServletResponse response){
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

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            BaseDAO baseDAO = daoFactory.getBaseDAO();

            String Locazione = request.getParameter("luogoBase");
            Base BaseSelezionata = baseDAO.findbyLocazione(Locazione);

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("BaseSelezionata", BaseSelezionata);
            request.setAttribute("viewUrl", "ListaBasi/viewBase");

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

    public static void deleteBase(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        Amministratore loggedAdmin;

        String applicationMessage = null;

        Logger logger = LogService.getApplicationLogger();

        try {

            Map sessionFactoryParameters=new HashMap<String,Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);

            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            loggedAdmin = sessionAdminDAO.findLoggedAdmin();

            sessionDAOFactory.commitTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            BaseDAO baseDAO = daoFactory.getBaseDAO();

            String baseLocazione = request.getParameter("luogoBase");

            Base base = baseDAO.findbyLocazione(baseLocazione);   // vado a recuperare la base selezionata tramite locazione

            baseDAO.delete(base);                                   // lo passo poi al metodo delete per cancellarlo

            daoFactory.commitTransaction();

            /*
            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            */
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("BaseSelezionata", base);

            request.setAttribute("viewUrl", "ListaBasi/reload");

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
    public static void registraBase(HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("viewUrl", "ListaBasi/NewBaseCSS");
    }

    public static void newBase(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        Amministratore loggedAdmin;
        String applicationMessage = null;

        Logger logger = LogService.getApplicationLogger();

        try {

            Map sessionFactoryParameters=new HashMap<String,Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            loggedAdmin = sessionAdminDAO.findLoggedAdmin();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            /* Recupero Parametri */
            Part Foto = request.getPart("Foto");
            String Email = request.getParameter("Email");
            String Telefono = request.getParameter("Telefono");
            String Locazione = request.getParameter("Locazione");
            String Provincia = request.getParameter("Provincia");
            String CAP = request.getParameter("CAP");
            String Via = request.getParameter("Via");
            String Latitudine = request.getParameter("Latitudine");
            String Longitudine = request.getParameter("Longitudine");

            BaseDAO baseDAO = daoFactory.getBaseDAO();
            baseDAO.create( Foto.getInputStream().readAllBytes(), Latitudine, Longitudine, Locazione, Email, Telefono, Provincia, CAP,
                    Via, loggedAdmin.getIdAdministrator());

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedAdminOn",loggedAdmin!=null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("applicationMessage", applicationMessage);
            request.setAttribute("viewUrl", "ListaBasi/reload");


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
