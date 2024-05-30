package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.*;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.model.mo.Utente.*;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
@Controller
public class PaginaIniziale {
    private PaginaIniziale(){}

    @GetMapping("/homepage")
    public ModelAndView view(){

        ModelAndView page = new ModelAndView();
        page.setViewName("PaginaInizialeCSS");

        // page.addObject("nome", oggetto) == request.addAttribute()

        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser;
        Amministratore loggedAdmin;

        ArrayList<Notizie> notizie = new ArrayList<>();

        String applicationMessage = null;

        try {

            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,null);
            sessionDAOFactory.beginTransaction();

            UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            loggedAdmin = sessionAdminDAO.findLoggedAdmin();

            sessionDAOFactory.commitTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            NotizieDAO notizieDAO = daoFactory.getNotizieDAO();
            notizie.add(0, notizieDAO.findById("0000000001"));
            notizie.add(1, notizieDAO.findById("0000000002"));
            notizie.add(2, notizieDAO.findById("0000000003"));
            notizie.add(3, notizieDAO.findById("0000000004"));



            request.setAttribute("notizia1", notizie.get(0));
            request.setAttribute("notizia2", notizie.get(1));
            request.setAttribute("notizia3", notizie.get(2));
            request.setAttribute("notizia4", notizie.get(3));



            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("viewUrl", "Pagina_InizialeCSS");

        } catch (Exception e) {

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
        return page
    }

    public static void sostituisciArticolo(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        Amministratore loggedAdmin;

        Notizie notizie;

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

            String Id = request.getParameter("Id");

            NotizieDAO notizieDAO = daoFactory.getNotizieDAO();
            notizie = notizieDAO.findById(Id);

            /* Recupero Parametri */
            String Oggetto = request.getParameter("Oggetto");
            String IdAdministrator = request.getParameter("IdAdministrator");

            Integer IdArt = Integer.parseInt(Id);

            Part Testo = request.getPart("Testo");                                   // recupero il file
            String DirectoryDest = "C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\";// directory dove salvo i file
            File file = new File(DirectoryDest + 'A' + IdArt);                 // vado a creare un file in quella directory con il nome 'B' + Id
            Testo.write(file.getAbsolutePath());                                        // vado a scriverci il contenuto del file
            String RiferimentoTesto = file.getAbsolutePath();                           // recupero il riferimento al testo

            notizie.setOggetto(Oggetto);
            notizie.setIdAdministrator(IdAdministrator);
            notizie.setRiferimentoTesto(Paths.get(RiferimentoTesto));

            notizieDAO.update(notizie);


            daoFactory.commitTransaction();

            String notizia = "notizia" + Integer.parseInt(Id);
            request.setAttribute(notizia, notizie);

            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);

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

    public static void viewArt(HttpServletRequest request, HttpServletResponse response) {
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

            NotizieDAO notizieDAO = daoFactory.getNotizieDAO();

            String NotiziaId = request.getParameter("Id");

            Notizie notizie = notizieDAO.findById(NotiziaId);

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn",loggedAdmin!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("NotiziaSelezionata", notizie);
            request.setAttribute("viewUrl", "viewArticoloCSS");

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
