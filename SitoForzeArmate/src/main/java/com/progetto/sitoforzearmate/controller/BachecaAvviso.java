package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Notizie.AvvisoDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Avviso;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import com.progetto.sitoforzearmate.services.logservice.LogService;
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

@Controller
public class BachecaAvviso {

    @GetMapping("/viewBachecaAvviso")
    public ModelAndView view(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultvalue = "") String cookieAdmin,
        @CookieValue(value = "loggedUser", defaultvalue = "") String cookieUser
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;
            String applicationMessage = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            List<Avviso> avvisoList = new ArrayList<>();

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( !cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieUser)

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                if(loggedAdmin == null) {
                    AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();
                    avvisoList.addAll(avvisoDAO.stampaAvvisi(loggedUser.getMatricola()));
                }
                else{
                    UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

                    List<UtenteRegistrato> listaUtente = new ArrayList<>();
                    listaUtente.addAll(userDAO.getUtenti());
                    page.addObject("listaUtenti", listaUtente);
                }
                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("Avvisi", avvisoList);
                page.setViewName("Bacheca/AvvisiCSS");

            } catch (Exception e) {
                
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
                
                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS")
            }

            return page;
        }

    @PostMapping(path = "/viewAvviso", params = {"avvisoId"})
    public ModelAndView viewAvviso(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedUser", defaultvalue = ""), String cookieUser,
        
        @RequestParam(value = "avvisoId") String avvisoId
        ) {
            ModelAndView page = ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;

            String applicationMessage = null;

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser)

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();

                Avviso avviso = avvisoDAO.findById(avvisoId);

                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("AvvisoSelezionato", avviso);

                page.setViewName("Bacheca/viewAvvisoCSS");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/deleteAvviso", params = {"avvisoId"})
    public ModelAndView deleteAvviso(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedUser", defaultvalue = "") String cookieUser,
        
        @RequestParam(value = "avvisoId") String avvisoId
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
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser)

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();

                Avviso avviso = avvisoDAO.findById(avvisoId);
                avvisoDAO.delete(avviso);                     // lo passo poi al metodo delete per cancellarlo

                loggedUser.deleteIdAvviso(avvisoId);
                avviso.removeMatricolaDestinatario(loggedUser.getMatricola());

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

    @PostMapping(path = "/inviaAvviso", params = {""})
    public ModelAndView inviaAvviso(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultvalue = "") String cookieAdmin,
        
        @RequestParam(value = "Scelta") String scelta,
        @RequestParam(value = "Oggetto") String oggetto,
        @RequestParam(value = "Testo") String testo 
        ){
            ModelAndView page = ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieUser)

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

                List<UtenteRegistrato> listUser = new ArrayList<>();

                Integer Id = Integer.parseInt(avvisoDAO.getID()) + 1;
                String avvisoId = Id.toString();

                String DirectoryDest = "C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\";
                File file = new File(DirectoryDest + 'A' + avvisoId);

                try{
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                    bw.write(testo);
                    bw.close();
                }
                catch(IOException e){
                    throw new RuntimeException(e);
                }

                String RiferimentoTesto = file.getAbsolutePath();

                if(scelta == null){
                }
                else if(scelta.equals("Tutti")){
                    listUser.addAll(userDAO.getUtenti());

                    for(int i=0; i<listUser.size(); i++){
                        avvisoDAO.create(avvisoId, oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), listUser.get(i).getMatricola());

                        Id = Integer.parseInt(avvisoDAO.getID()) + 1;
                        avvisoId = Id.toString();
                    }

                }
                else if(scelta.equals("Ruolo")){

                    String[] Ruolo = request.getParameterValues("RuoloSelezionato");

                    for(int k=0; k<Ruolo.length; k++) {
                        listUser.addAll(userDAO.getUtentiRuolo(Ruolo[k]));
                        for (int i = 0; i < listUser.size(); i++) {
                            avvisoDAO.create(avvisoId, oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), listUser.get(i).getMatricola());

                            Id = Integer.parseInt(avvisoDAO.getID()) + 1;
                            avvisoId = Id.toString();
                        }
                    }
                }
                else if(scelta.equals("Utente")){
                    String[] Matricola = request.getParameterValues("Matricola");
                    for(int k=0; k<Matricola.length; k++){
                        avvisoDAO.create(avvisoId, oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), Matricola[k]);

                        Id = Integer.parseInt(avvisoDAO.getID()) + 1;
                        avvisoId = Id.toString();
                    }
                }

                sessionDAOFactory.commitTransaction();

                daoFactory.commitTransaction();

                page.addObject("loggedAdminOn",loggedAdmin!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedAdmin", loggedAdmin);


                page.setViewName("Bacheca/reload");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_inzialeCSS");
            } 
        
        return page;
    }
}
