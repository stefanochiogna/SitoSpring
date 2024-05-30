package com.progetto.sitoforzearmate.controller;

import com.example.sitoforzaarmata.model.dao.Bando.BandoDAO;
import com.example.sitoforzaarmata.model.dao.Base.BaseDAO;
import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.Data;
import com.example.sitoforzaarmata.model.dao.Notizie.AvvisoDAO;
import com.example.sitoforzaarmata.model.dao.Utente.AmministratoreDAO;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import com.example.sitoforzaarmata.model.mo.Bando;
import com.example.sitoforzaarmata.model.mo.Base.Base;
import com.example.sitoforzaarmata.model.mo.Utente.Amministratore;
import com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato;
import com.example.sitoforzaarmata.services.configuration.Configuration;
import com.example.sitoforzaarmata.services.logservice.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Calendario {
    private Calendario(){}

    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser;
        Amministratore loggedAdmin;

        List<String> dateList = new ArrayList<>();

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

            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            List<Bando> bandoList = bandoDAO.show();
            dateList.addAll(bandoDAO.getDate());

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("Date", dateList);
            request.setAttribute("Bandi", bandoList);
            request.setAttribute("viewUrl", "Calendario/CalendarioCSS");

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

    public static void viewBando(HttpServletRequest request, HttpServletResponse response) {
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

            String bandoId = request.getParameter("bandoId");
            //System.out.println("Controller: "+bandoId);
            Bando bando = bandoDAO.findbyId(bandoId);

            List<UtenteRegistrato> partecipanti = new ArrayList<>();
            for(int i = 0; i < bando.getEsitoLength(); i++) {
                partecipanti.add(userDao.findByMatricola(bando.getEsitoKey().get(i)));
            }

            boolean maxIscritti = userDao.maxIscrittiRaggiunto(bando);

            daoFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("BandoSelezionato", bando);
            request.setAttribute("maxIscrittiRaggiunto", maxIscritti);

            if(loggedAdmin != null){
                request.setAttribute("partecipanti", partecipanti);
            }

            if (loggedUser != null){
                request.setAttribute("Iscritto", loggedUser.trovaBando(bandoId));
                request.setAttribute("inAttesa", bando.getEsito(loggedUser.getMatricola()));
                // loggedUser.stampaBandi();
                // System.out.println(loggedUser.getMatricola());
                // System.out.println(loggedUser.trovaBando(bandoId));
            }

            request.setAttribute("viewUrl", "Calendario/viewBandoCSS");

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

    public static void deleteBando(HttpServletRequest request, HttpServletResponse response){
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

            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            String bandoId = request.getParameter("bandoId");

            Bando bando = bandoDAO.findbyId(bandoId);   // vado a recuperare il bando selezionato tramite Id

            File file = new File(bando.getRiferimentoTesto().toString());
            if(file.exists()){
                if(file.delete());
                else    System.err.println("Errore nella cancellazione del file");
            }

            bandoDAO.delete(bando);                     // lo passo poi al metodo delete per cancellarlo

            daoFactory.commitTransaction();

            /*
            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            */
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("BandoSelezionato", bando);

            request.setAttribute("viewUrl", "Calendario/reload");
            /* Da aggiungere pagina che ricarica la view di calendario */

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

    public static void modificaBandoView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        Amministratore loggedAdmin;
        List<Base> listaBasi = new ArrayList<>();

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

            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            String bandoId = request.getParameter("bandoId");

            Bando bando = null;
            if(bandoId != null) bando = bandoDAO.findbyId(bandoId);   // vado a modificare il bando selezionato tramite Id

            BaseDAO baseDAO = daoFactory.getBaseDAO();
            listaBasi.addAll(baseDAO.stampaBasi());

            daoFactory.commitTransaction();

            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("BandoSelezionato", bando);
            request.setAttribute("ListaBasi", listaBasi);
            request.setAttribute("viewUrl", "Calendario/modificaBandoCSS");

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
    public static void modificaBando(HttpServletRequest request, HttpServletResponse response){
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

            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            String bandoId = request.getParameter("bandoId");
            String Oggetto = request.getParameter("oggettoBando");
            Integer numMaxIscritti = Integer.parseInt(request.getParameter("numMaxIscritti"));
            String dataScadenza = request.getParameter("DataScadenza");
            String data = request.getParameter("DataBando");
            String Locazione = request.getParameter("Locazione");

            Bando bando = bandoDAO.findbyId(bandoId);
            bando.updateTesto(request.getParameter("testoBando"));

            Data dataBando = new Data(data);
            Data dataScadenzaBando = new Data(dataScadenza);

            bandoDAO.update(dataBando, bandoId, Oggetto, Locazione, numMaxIscritti, dataScadenzaBando, loggedAdmin.getIdAdministrator());
                    // vado a modificare il bando selezionato tramite Id

            daoFactory.commitTransaction();

            /*
            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            */
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);

            request.setAttribute("viewUrl", "Calendario/reload");

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

    public static void inserisciBando(HttpServletRequest request, HttpServletResponse response){
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

            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            /* Recupero Parametri */
            String dataBando = request.getParameter("DataBando");
            String Oggetto = request.getParameter("oggettoBando");
            Integer numMaxIscritti = Integer.parseInt(request.getParameter("numMaxIscritti"));
            String DataScadenza = request.getParameter("DataScadenza");
            String Base = request.getParameter("Locazione");

            Integer Id = Integer.parseInt(bandoDAO.getLastId()) + 1;

            String bandoId = Id.toString();
            String caratteriRestanti = "";
            while((caratteriRestanti.length() + bandoId.length()) < 10){
                caratteriRestanti += "0";
            }
            bandoId = caratteriRestanti + bandoId;

            Part insBando = request.getPart("insBando");                               // recupero il file
            String DirectoryDest = "C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\";   // directory dove salvo i file
            File file = new File(DirectoryDest + 'B' + bandoId);                  // vado a creare un file in quella directory con il nome 'B' + Id
            insBando.write(file.getAbsolutePath());                                        // vado a scriverci il contenuto del file
            String RiferimentoTesto = file.getAbsolutePath();                               // recupero il riferimento al testo

            Data data = new Data(dataBando);
            Data dataScadenza = new Data(DataScadenza);


            Bando bando = bandoDAO.create(data, bandoId, Oggetto, Paths.get(RiferimentoTesto),
                    Base, numMaxIscritti, dataScadenza, loggedAdmin.getIdAdministrator());


            BaseDAO baseDAO = daoFactory.getBaseDAO();
            List<com.example.sitoforzaarmata.model.mo.Base.Base> listaBasi = baseDAO.stampaBasi();

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedAdminOn",loggedAdmin!=null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("applicationMessage", applicationMessage);
            request.setAttribute("ListaBasi", listaBasi);

            if(bando != null){
                request.setAttribute("viewUrl", "Calendario/reload");
            }
            else{
                throw new RuntimeException("Errore nell'inserimento del Bando");
            }

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
    public static void iscrizione(HttpServletRequest request, HttpServletResponse response){
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

            AmministratoreDAO amministratoreDAO = sessionDAOFactory.getAmministratoreDAO();
            loggedAdmin = amministratoreDAO.findLoggedAdmin();

            sessionDAOFactory.commitTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            BandoDAO bandoDAO = daoFactory.getBandoDAO();
            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

            String bandoId = request.getParameter("bandoId");
            Bando bando = bandoDAO.findbyId(bandoId);   // vado a recuperare il bando selezionato tramite Id

            bando.AddMatricola(loggedUser.getMatricola());
            bando.AddEsito("in attesa", loggedUser.getMatricola());

            userDAO.iscrizioneBando(loggedUser, bando);
            boolean maxIscritti = userDAO.maxIscrittiRaggiunto(bando);

            daoFactory.commitTransaction();

            loggedUser.AddIdBando(bando.getId());

            sessionUserDAO.update(loggedUser);

            String iscrizione = (String) request.getParameter("Iscritto");
            boolean iscritto;
            if (iscrizione.equalsIgnoreCase("True")) iscritto = true;
            else if (iscrizione.equalsIgnoreCase("False")) iscritto = false;
            else throw new RuntimeException();


            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute( "loggedAdminOn", loggedAdmin != null);
            request.setAttribute( "loggedAdmin", loggedAdmin);
            request.setAttribute("Iscritto", loggedUser.trovaBando(bandoId) || iscritto);
            request.setAttribute("BandoSelezionato", bando);
            request.setAttribute("viewUrl", "Calendario/viewBandoCSS");
            request.setAttribute("maxIscrittiRaggiunto", maxIscritti);
            request.setAttribute("inAttesa", bando.getEsito(loggedUser.getMatricola()));

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

    public static void annullaIscrizione(HttpServletRequest request, HttpServletResponse response){
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

            BandoDAO bandoDAO = daoFactory.getBandoDAO();
            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

            String bandoId = request.getParameter("bandoId");
            Bando bando = bandoDAO.findbyId(bandoId);   // vado a recuperare il bando selezionato tramite Id

            bando.deleteMatricola(loggedUser.getMatricola());

            userDAO.annullaIscrizione(loggedUser, bando);
            boolean maxIscritti = userDAO.maxIscrittiRaggiunto(bando);


            daoFactory.commitTransaction();

            loggedUser.deleteIdBando(bando.getId());
            bando.deleteMatricola(loggedUser.getMatricola());

            sessionUserDAO.update(loggedUser);

            request.setAttribute("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("Iscritto", false);
            request.setAttribute("BandoSelezionato", bando);
            request.setAttribute("viewUrl", "Calendario/viewBandoCSS");
            request.setAttribute("maxIscrittiRaggiunto", maxIscritti);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Controller Error", e);
            try {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
                t.getStackTrace();
            }
            throw new RuntimeException(e);

        } finally {
            try {
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
                t.getStackTrace();
            }
        }
    }

    public static void esitoPartecipante(HttpServletRequest request, HttpServletResponse response){
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

            UtenteRegistratoDAO userDao = daoFactory.getUtenteRegistratoDAO();

            String matrSelezionata = request.getParameter("UtenteSelezionato");

            String Esito = request.getParameter("inAttesa");

            System.out.println(Esito);

            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            String bandoId = request.getParameter("bandoId");

            Bando bando = bandoDAO.findbyId(bandoId);
            bando.updateEsito(Esito, matrSelezionata);
            bandoDAO.updateEsito(bandoId, matrSelezionata, Esito);

            /* INVIO AVVISO */
            AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();
            UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

            String Oggetto = "Esito bando: " + bandoId;
            String Testo = "Gentile utente,\nLa informiamo che la sua domanda per il bando " + bandoId + " ha avuto esito: " + Esito + ".\nPer ulteriori informazioni contattare il numero della Segreteria.";

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

            avvisoDAO.create(avvisoId, Oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), matrSelezionata);

            daoFactory.commitTransaction();



            request.setAttribute("loggedAdminOn", loggedAdmin != null);
            request.setAttribute("loggedAdmin", loggedAdmin);
            request.setAttribute("BandoSelezionato", bandoId);

            request.setAttribute("viewUrl", "Calendario/reload");
            // TODO OPZIONALE: mettere reload che ricarica il bando selezionato


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
