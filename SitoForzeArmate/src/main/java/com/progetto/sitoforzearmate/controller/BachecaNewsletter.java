package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Notizie.NewsletterDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Newsletter;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class BachecaNewsletter {

    @GetMapping("/viewBachecaNewsletter")
    public ModelAndView view(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;
            String applicationMessage = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;
            List<Newsletter> newsletterList = new ArrayList<>();

            try {
                
                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                if(loggedAdmin == null) {
                    NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();
                    newsletterList.addAll(newsletterDAO.stampaNewsletter(loggedUser.getMail()));
                }

                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("loggedAdminOn", loggedAdmin!=null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("Newsletter", newsletterList);
                page.setViewName("Bacheca/NewsletterCSS");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
                
                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

        @PostMapping(path = "/viewNewsletter", params = {"newsletterId"})
        public ModelAndView viewNewsletter(
            HttpServletResponse response,
            
            @CookieValue(value  = "loggedUser", defaultValue = "") String cookieUser,
            
            @RequestParam(value = "newsletterId") String newsletterId
            ) {
                ModelAndView page = new ModelAndView();
                DAOFactory sessionDAOFactory= null;
                DAOFactory daoFactory = null;

                UtenteRegistrato loggedUser = null;

                String applicationMessage = null;

                try {

                    sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                    sessionDAOFactory.beginTransaction();

                    UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                    if( ! cookieUser.equals("") )
                        loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                    sessionDAOFactory.commitTransaction();

                    daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                    daoFactory.beginTransaction();

                    NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();

                    Newsletter newsletter = newsletterDAO.findById(newsletterId);

                    daoFactory.commitTransaction();

                    page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                    page.addObject("loggedUser", loggedUser);
                    page.addObject("NewsletterSelezionata", newsletter);

                    page.setViewName("Bacheca/viewNewsletterCSS");

                } catch (Exception e) {
                    if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
                    
                    e.printStackTrace();
                    page.setViewName("Pagina_InizialeCSS");
                }

                return page;
            }

    @PostMapping(path = "/deleteNewsletter", params = {"newsletterId"})
    public ModelAndView deleteNewsletter(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
        
        @RequestParam(value = "newsletterId") String newsletterId
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;

            String applicationMessage = null;

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();

                Newsletter newsletter = newsletterDAO.findById(newsletterId);
                newsletterDAO.delete(newsletter);                     // lo passo poi al metodo delete per cancellarlo

                loggedUser.deleteIdAvviso(newsletterId);
                newsletter.removeMailDestinatario(loggedUser.getMail());

                // sessionUserDAO.update(loggedUser);          dopo aver modificato il logged user effettuo aggiornamento cookie
                sessionDAOFactory.commitTransaction();

                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);

                page.setViewName("Bacheca/reload");
                /* Da aggiungere pagina che ricarica la view di bacheca */

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }
            
            return page;
        }

    @PostMapping(path = "/inviaNewsletter")
    public ModelAndView inviaNewsletter(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "Oggetto") String oggetto,
        @RequestParam(value = "Testo") String testo
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                NewsletterDAO newsletterDAO = daoFactory.getNewsletterDAO();
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

                List<UtenteRegistrato> listUser = new ArrayList<>();

                Integer Id = Integer.parseInt(newsletterDAO.getID()) + 1;
                String NewsId = Id.toString();

                String DirectoryDest = "C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\";
                File file = new File(DirectoryDest + 'N' + NewsId);

                try{
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                    bw.write(testo);
                    bw.close();
                }
                catch(IOException e){
                    throw new RuntimeException(e);
                }

                String RiferimentoTesto = file.getAbsolutePath();

                listUser.addAll(userDAO.getUtentiNewsletter());
                for(int i=0; i<listUser.size(); i++){
                    newsletterDAO.create(NewsId, oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), listUser.get(i).getMail(), listUser.get(i).getMatricola());

                    Id = Integer.parseInt(newsletterDAO.getID()) + 1;
                    NewsId = Id.toString();
                }

                sessionDAOFactory.commitTransaction();

                daoFactory.commitTransaction();

                page.addObject("loggedAdminOn",loggedAdmin!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedAdmin", loggedAdmin);


                page.setViewName("Bacheca/reload");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }
}
