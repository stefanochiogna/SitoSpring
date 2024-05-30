package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Base.Base;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class Login {
    @GetMapping("/viewLogin")
    public static void view(HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("viewUrl", "LoginCSS");
    }

    @PostMapping(path = "/login", params = {""})
    public static void login(
            @RequestParam(value = "", required = false, defaultValue = "") String prova
    ){
        //request.setAttribute("loggedOn", true);
        //request.setAttribute("viewUrl", "Pagina_Iniziale");

        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        UtenteRegistrato loggedUser;
        String applicationMessage = null;


        try {

            Map sessionFactoryParameters=new HashMap<String,Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            String Email = request.getParameter("Email");
            String Password = request.getParameter("Password");

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            UtenteRegistrato user = userDAO.findByMail(Email);

            if (user == null || !user.getPassword().equals(Password)) {
                sessionUserDAO.delete(null);
                applicationMessage = "Email e password errati";
                loggedUser=null;
            } else {
                loggedUser = sessionUserDAO.create(user.getNome(), user.getCognome(),user.getCF(), user.getMail(), user.getTelefono(),user.getPassword(),
                        user.getSesso(), user.getDataNascita(), user.getMatricola(), user.getIBAN(), user.getRuolo(), user.getFotoByte(), user.getDocumentoByte(),
                        user.getIndirizzo(), user.getLocazioneServizio(), user.getIscrittoNewsletter(), userDAO.recuperaBandi(user.getMatricola()));

                //loggedUser.stampaBandi();
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("applicationMessage", applicationMessage);

            if(loggedUser != null){
                RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
            }
            else request.setAttribute("viewUrl", "LoginCSS");

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

    public static void logout(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;

        Logger logger = LogService.getApplicationLogger();

        try {

            Map sessionFactoryParameters=new HashMap<String,Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            sessionUserDAO.delete(null);

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            sessionAdminDAO.delete(null);

            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",false);
            request.setAttribute("loggedUser", null);
            request.setAttribute("loggedAdmin", null);
            request.setAttribute("loggedAdminOn", false);

            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
            view.forward(request, response);

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

    /* REGISTRAZIONE */

    public static void viewRegistrazione(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        String applicationMessage = null;

        Logger logger = LogService.getApplicationLogger();

        try {

            Map sessionFactoryParameters=new HashMap<String,Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            BaseDAO baseDAO = daoFactory.getBaseDAO();

            List<Base> baseList = baseDAO.stampaBasi();

            daoFactory.commitTransaction();

            request.setAttribute("listaBasi", baseList);
            request.setAttribute("viewUrl", "RegistrazioneCSS");

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

    public static void Registrazione(HttpServletRequest request, HttpServletResponse response){
        //request.setAttribute("loggedOn", true);
        //request.setAttribute("viewUrl", "Pagina_Iniziale");

        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        UtenteRegistrato loggedUser;
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

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            // Recupero Parametri
            String Nome = request.getParameter("Nome");
            String Cognome = request.getParameter("Cognome");
            String CF = request.getParameter("CF");
            String Telefono = request.getParameter("Telefono");
            String Email = request.getParameter("Email");
            String Password = request.getParameter("Password");

            String sesso = request.getParameter("Sesso");
            String dataNascita = request.getParameter("DataNascita");

            Data DataNascita = new Data(dataNascita);

            String IBAN = request.getParameter("IBAN");
            String Ruolo = request.getParameter("Ruolo");

            Part Foto = request.getPart("Foto");
            Part Documento = request.getPart("Documento");
            String Indirizzo = request.getParameter("Indirizzo");
            String LocazioneServizio = request.getParameter("LocazioneServizio");
            boolean Newsletter = (request.getParameter("Newsletter") != null);

            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();
            UtenteRegistrato user = userDAO.findByMail(Email);

            String MatricolaUtente = userDAO.getLastMatricola();
            Long Matricola = Long.parseLong(MatricolaUtente) + 1;
            
            if (user != null) {
                sessionUserDAO.delete(null);
                applicationMessage = "È già presente un utente con la stessa mail";
                loggedUser=null;
            }
            else {
                user = userDAO.create(
                        Nome, Cognome, CF, Email, Telefono, Password, sesso, DataNascita, Matricola.toString(), IBAN, Ruolo, Foto.getInputStream().readAllBytes(),
                        Documento.getInputStream().readAllBytes(), Indirizzo, LocazioneServizio, Newsletter, null
                );


                loggedUser = sessionUserDAO.create(user.getNome(), user.getCognome(), user.getCF(), user.getMail(), user.getTelefono(), user.getPassword(),
                        user.getSesso(), user.getDataNascita(), user.getMatricola(), user.getIBAN(), user.getRuolo(), user.getFotoByte(), user.getDocumentoByte(),
                        user.getIndirizzo(), user.getLocazioneServizio(), Newsletter, userDAO.recuperaBandi(user.getMatricola()));
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("applicationMessage", applicationMessage);

            if(loggedUser != null){
                RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
            }
            else request.setAttribute("viewUrl", "LoginCSS");      // se ho inserito una mail già presente: loggedUser = null

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

    public static void viewAmministratore(HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("viewUrl", "LoginAmministratoreCSS");
    }


    public static void loginAmministratore(HttpServletRequest request, HttpServletResponse response){
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

            AmministratoreDAO sessionUserDAO = sessionDAOFactory.getAmministratoreDAO();
            loggedAdmin = sessionUserDAO.findLoggedAdmin();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            String IdAdministrator = request.getParameter("IdAdministrator");
            String Password = request.getParameter("Password");

            AmministratoreDAO adminDAO = daoFactory.getAmministratoreDAO();
            Amministratore admin = adminDAO.findById(IdAdministrator);

            if (admin == null || !admin.getPassword().equals(Password)) {
                sessionUserDAO.delete(null);
                applicationMessage = "Id e password errati";
                loggedAdmin=null;
            } else {
                loggedAdmin = sessionUserDAO.create(admin.getNome(), admin.getCognome(), admin.getCF(), admin.getMail(), admin.getTelefono(),
                        admin.getPassword(), admin.getSesso(), admin.getDataNascita(), admin.getIdAdministrator());

                //loggedUser.stampaBandi();
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedAdmin!=null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("applicationMessage", applicationMessage);

            if(loggedAdmin != null){
                RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
            }
            else request.setAttribute("viewUrl", "LoginAmministratoreCSS");

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
