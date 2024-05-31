package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Bando.BandoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PastoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Base.Pasto;
import com.progetto.sitoforzearmate.model.mo.Base.PostoLetto;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.Badge;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Profilo {
    private Profilo(){}

    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser;
        Amministratore loggedAdmin;

        UtenteRegistrato user = new UtenteRegistrato();
        Amministratore admin = new Amministratore();

        Notizie[] notizie = new Notizie[4];

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

            if(loggedUser != null) {
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
                user = userDAO.findByMail(loggedUser.getMail());

                if(user.getRuolo() != null){
                    Badge badge = userDAO.getBadge(loggedUser.getMatricola());
                    request.setAttribute("Badge", badge);
                }
            }
            else if(loggedAdmin != null){
                AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();
                admin = adminDAO.findByMail(loggedAdmin.getMail());
            }

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);

            request.setAttribute("UtenteCorrente", user);
            request.setAttribute("AdminCorrente", admin);

            request.setAttribute("viewUrl", "Profilo/ProfiloCSS");

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

    public static void modificaProfiloView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        Amministratore loggedAdmin;
        UtenteRegistrato loggedUser;

        UtenteRegistrato user = null;
        Amministratore admin = null;

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

            if(loggedUser != null) {
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
                user = userDAO.findByMatricola(loggedUser.getMatricola());
            }
            else if(loggedAdmin != null){
                AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();
                admin = adminDAO.findById(loggedAdmin.getIdAdministrator());
            }

            daoFactory.commitTransaction();

            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("UserSelezionato", user);
            request.setAttribute("AdminSelezionato", admin);
            request.setAttribute("viewUrl", "Profilo/modificaProfiloCSS");

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

    public static void modificaProfilo(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        Amministratore loggedAdmin;
        UtenteRegistrato loggedUser;

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

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            sessionDAOFactory.commitTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();
            String Mail = request.getParameter("userMail");
            String password = request.getParameter("userPassword");
            String telefono = request.getParameter("userTelefono");

            UtenteRegistrato user = userDAO.findByMail(Mail);
            if(user != null){
                String IBAN = request.getParameter("userIBAN");
                Part foto = request.getPart("userFoto");
                Part documenti = request.getPart("userDocumenti");
                String indirizzo = request.getParameter("userIndirizzo");
                boolean Newsletter = (request.getParameter("Newsletter") != null);

                user.setIBAN(IBAN);

                if(foto != null && foto.getSize() > 0){
                    user.setFotoByte(foto.getInputStream().readAllBytes());
                }
                if(documenti != null && documenti.getSize() > 0){
                    user.setDocumentoByte(documenti.getInputStream().readAllBytes());
                }
                user.setIndirizzo(indirizzo);
                user.setIscrittoNewsletter(Newsletter);
                user.setTelefono(telefono);
                user.setPassword(password);

                userDAO.update(user);

            }
            Amministratore admin = adminDAO.findByMail(Mail);
            if(admin != null){
                admin.setPassword(password);
                admin.setTelefono(telefono);

                adminDAO.update(admin);
            }

            daoFactory.commitTransaction();


            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);

            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);

            request.setAttribute("viewUrl", "Profilo/reload");

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

    public static void prenotazioniView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser;

        List<Pasto> pastoList = new ArrayList<>();
        List<PostoLetto> alloggioList = new ArrayList<>();

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

            sessionDAOFactory.commitTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            PastoDAO pastoDAO = daoFactory.getPastoDAO();
            PostoLettoDAO alloggioDAO = daoFactory.getPostoLettoDAO();

            pastoList.addAll(pastoDAO.findByMatricola(loggedUser.getMatricola()));
            alloggioList.addAll(alloggioDAO.findByMatricola(loggedUser.getMatricola()));

            daoFactory.commitTransaction();


            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("listaPasti", pastoList);
            request.setAttribute("listaAlloggi", alloggioList);

            request.setAttribute("viewUrl", "Profilo/prenotazioniCSS");

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

    public static void viewDoc(HttpServletRequest request, HttpServletResponse response) {
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

            UtenteRegistratoDAO userDao = daoFactory.getUtenteRegistratoDAO();
            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            String matrSelezionata = request.getParameter("UtenteSelezionato");
            UtenteRegistrato utenteSelezionato = userDao.findByMatricola(matrSelezionata);

            String bandoId = request.getParameter("bandoId");
            //System.out.println("Controller: "+bandoId);
            Bando bando = bandoDAO.findbyId(bandoId);

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("BandoSelezionato", bando);
            request.setAttribute("UtenteSelezionato", utenteSelezionato);

            request.setAttribute("viewUrl", "Profilo/viewDoc");

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
}
