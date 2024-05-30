package com.progetto.sitoforzearmate.controller;

import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.Notizie.NewsletterDAO;
import com.example.sitoforzaarmata.model.dao.Utente.AmministratoreDAO;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import com.example.sitoforzaarmata.model.mo.Notizie.Newsletter;
import com.example.sitoforzaarmata.model.mo.Utente.Amministratore;
import com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato;
import com.example.sitoforzaarmata.services.configuration.Configuration;
import com.example.sitoforzaarmata.services.logservice.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BachecaNewsletter {
    private BachecaNewsletter(){}

    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        String applicationMessage = null;

        UtenteRegistrato loggedUser;
        Amministratore loggedAdmin;
        List<Newsletter> newsletterList = new ArrayList<>();

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

            if(loggedAdmin == null) {
                NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();
                newsletterList.addAll(newsletterDAO.stampaNewsletter(loggedUser.getMail()));
            }

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin!=null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("Newsletter", newsletterList);
            request.setAttribute("viewUrl", "Bacheca/NewsletterCSS");

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

    public static void viewNewsletter(HttpServletRequest request, HttpServletResponse response) {
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

            sessionDAOFactory.commitTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();

            String newsletterId = request.getParameter("newsletterId");

            Newsletter newsletter = newsletterDAO.findById(newsletterId);

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("NewsletterSelezionata", newsletter);

            request.setAttribute("viewUrl", "Bacheca/viewNewsletterCSS");

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

    public static void deleteNewsletter(HttpServletRequest request, HttpServletResponse response){
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

            NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();

            String newsletterId = request.getParameter("newsletterId");

            Newsletter newsletter = newsletterDAO.findById(newsletterId);
            newsletterDAO.delete(newsletter);                     // lo passo poi al metodo delete per cancellarlo

            loggedUser.deleteIdAvviso(newsletterId);
            newsletter.removeMailDestinatario(loggedUser.getMail());

            // sessionUserDAO.update(loggedUser);          dopo aver modificato il logged user effettuo aggiornamento cookie
            sessionDAOFactory.commitTransaction();

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);

            request.setAttribute("viewUrl", "Bacheca/reload");
            /* Da aggiungere pagina che ricarica la view di bacheca */

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

    public static void inviaNewsletter(HttpServletRequest request, HttpServletResponse response){
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

            NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();
            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

            List<UtenteRegistrato> listUser = new ArrayList<>();

            String Oggetto = request.getParameter("Oggetto");
            String Testo = request.getParameter("Testo");

            Integer Id = Integer.parseInt(newsletterDAO.getID()) + 1;
            String NewsId = Id.toString();

            String DirectoryDest = "C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\";
            File file = new File(DirectoryDest + 'N' + NewsId);

            try{
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                bw.write(Testo);
                bw.close();
            }
            catch(IOException e){
                throw new RuntimeException(e);
            }

            String RiferimentoTesto = file.getAbsolutePath();

            listUser.addAll(userDAO.getUtentiNewsletter());
            for(int i=0; i<listUser.size(); i++){
                newsletterDAO.create(NewsId, Oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), listUser.get(i).getMail(), listUser.get(i).getMatricola());

                Id = Integer.parseInt(newsletterDAO.getID()) + 1;
                NewsId = Id.toString();
            }

            sessionDAOFactory.commitTransaction();

            daoFactory.commitTransaction();

            request.setAttribute("loggedAdminOn",loggedAdmin!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedAdmin", loggedAdmin);


            request.setAttribute("viewUrl", "Bacheca/reload");

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
