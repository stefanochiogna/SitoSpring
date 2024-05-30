package com.progetto.sitoforzearmate.controller;

import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.Notizie.AvvisoDAO;
import com.example.sitoforzaarmata.model.dao.Utente.AmministratoreDAO;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import com.example.sitoforzaarmata.model.mo.Notizie.Avviso;
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

public class BachecaAvviso {
    private BachecaAvviso(){}

    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;
        String applicationMessage = null;

        UtenteRegistrato loggedUser;
        Amministratore loggedAdmin;

        List<Avviso> avvisoList = new ArrayList<>();

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
                AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();
                avvisoList.addAll(avvisoDAO.stampaAvvisi(loggedUser.getMatricola()));
            }
            else{
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

                List<UtenteRegistrato> listaUtente = new ArrayList<>();
                listaUtente.addAll(userDAO.getUtenti());
                request.setAttribute("listaUtenti", listaUtente);
            }
            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("Avvisi", avvisoList);
            request.setAttribute("viewUrl", "Bacheca/AvvisiCSS");

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

    public static void viewAvviso(HttpServletRequest request, HttpServletResponse response) {
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

            AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();

            String avvisoId = request.getParameter("avvisoId");

            Avviso avviso = avvisoDAO.findById(avvisoId);

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("AvvisoSelezionato", avviso);

            request.setAttribute("viewUrl", "Bacheca/viewAvvisoCSS");

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

    public static void deleteAvviso(HttpServletRequest request, HttpServletResponse response){
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

            AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();

            String avvisoId = request.getParameter("avvisoId");

            Avviso avviso = avvisoDAO.findById(avvisoId);
            avvisoDAO.delete(avviso);                     // lo passo poi al metodo delete per cancellarlo

            loggedUser.deleteIdAvviso(avvisoId);
            avviso.removeMatricolaDestinatario(loggedUser.getMatricola());

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

    public static void inviaAvviso(HttpServletRequest request, HttpServletResponse response){
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

            AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();
            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

            List<UtenteRegistrato> listUser = new ArrayList<>();

            String scelta = request.getParameter("Scelta");

            String Oggetto = request.getParameter("Oggetto");
            String Testo = request.getParameter("Testo");

            Integer Id = Integer.parseInt(avvisoDAO.getID()) + 1;
            String avvisoId = Id.toString();

            String DirectoryDest = "C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\";
            File file = new File(DirectoryDest + 'A' + avvisoId);

            try{
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                bw.write(Testo);
                bw.close();
            }
            catch(IOException e){
                throw new RuntimeException(e);
            }

            String RiferimentoTesto = file.getAbsolutePath();

            if(scelta == null){
                logger.log(Level.WARNING, "Nessuna opzione selezionata");
            }
            else if(scelta.equals("Tutti")){
                listUser.addAll(userDAO.getUtenti());

                for(int i=0; i<listUser.size(); i++){
                    avvisoDAO.create(avvisoId, Oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), listUser.get(i).getMatricola());

                    Id = Integer.parseInt(avvisoDAO.getID()) + 1;
                    avvisoId = Id.toString();
                }

            }
            else if(scelta.equals("Ruolo")){

                String[] Ruolo = request.getParameterValues("RuoloSelezionato");

                for(int k=0; k<Ruolo.length; k++) {
                    listUser.addAll(userDAO.getUtentiRuolo(Ruolo[k]));
                    for (int i = 0; i < listUser.size(); i++) {
                        avvisoDAO.create(avvisoId, Oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), listUser.get(i).getMatricola());

                        Id = Integer.parseInt(avvisoDAO.getID()) + 1;
                        avvisoId = Id.toString();
                    }
                }
            }
            else if(scelta.equals("Utente")){
                String[] Matricola = request.getParameterValues("Matricola");
                for(int k=0; k<Matricola.length; k++){
                    avvisoDAO.create(avvisoId, Oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), Matricola[k]);

                    Id = Integer.parseInt(avvisoDAO.getID()) + 1;
                    avvisoId = Id.toString();
                }
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
