package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Bando.BandoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Notizie.AvvisoDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Base.Base;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import com.progetto.sitoforzearmate.services.logservice.LogService;
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

@Controller
public class Calendario {

    @GetMapping("/viewCalendario")
    public void view(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            List<String> dateList = new ArrayList<>();

            String applicationMessage = null;

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

                BandoDAO bandoDAO = daoFactory.getBandoDAO();

                List<Bando> bandoList = bandoDAO.show();
                dateList.addAll(bandoDAO.getDate());

                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("Date", dateList);
                page.addObject("Bandi", bandoList);
                page.setViewName("Calendario/CalendarioCSS");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
                
                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/viewBando", params = {"bandoId"})
    public void viewBando( 
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
        
        @RequestParam(value = "bandoId") String bandoId 
        ) {

            ModelAndView page = new ModelAndView();   
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;


            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if ( ! cookieAdmin.equals("") )
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                UtenteRegistratoDAO userDao = daoFactory.getUtenteRegistratoDAO();
                BandoDAO bandoDAO = daoFactory.getBandoDAO();

                //System.out.println("Controller: "+bandoId);
                Bando bando = bandoDAO.findbyId(bandoId);

                List<UtenteRegistrato> partecipanti = new ArrayList<>();
                for(int i = 0; i < bando.getEsitoLength(); i++) {
                    partecipanti.add(userDao.findByMatricola(bando.getEsitoKey().get(i)));
                }

                boolean maxIscritti = userDao.maxIscrittiRaggiunto(bando);

                daoFactory.commitTransaction();

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("BandoSelezionato", bando);
                page.addObject("maxIscrittiRaggiunto", maxIscritti);

                if(loggedAdmin != null){
                    page.addObject("partecipanti", partecipanti);
                }

                if (loggedUser != null){
                    page.addObject("Iscritto", loggedUser.trovaBando(bandoId));
                    page.addObject("inAttesa", bando.getEsito(loggedUser.getMatricola()));
                    // loggedUser.stampaBandi();
                    // System.out.println(loggedUser.getMatricola());
                    // System.out.println(loggedUser.trovaBando(bandoId));
                }

                page.setViewName("Calendario/viewBandoCSS");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
                
                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/deleteBando", params = {"bandoId"})
    public ModelAndView deleteBando(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "bandoId") String bandoId
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

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                BandoDAO bandoDAO = daoFactory.getBandoDAO();

                Bando bando = bandoDAO.findbyId(bandoId);   // vado a recuperare il bando selezionato tramite Id

                File file = new File(bando.getRiferimentoTesto().toString());
                if(file.exists()){
                    if(file.delete());
                    else    System.err.println("Errore nella cancellazione del file");
                }

                bandoDAO.delete(bando);                     // lo passo poi al metodo delete per cancellarlo

                daoFactory.commitTransaction();

                /*
                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                */
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("BandoSelezionato", bando);

                page.setViewName("Calendario/reload");
                /* Da aggiungere pagina che ricarica la view di calendario */

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/modificaBandoView", params = {"bandoId"})
    public ModelAndView modificaBandoView(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "")
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            Amministratore loggedAdmin = null;
            List<Base> listaBasi = new ArrayList<>();

            String applicationMessage = null;

            Logger logger = LogService.getApplicationLogger();

            try {
                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
                sessionDAOFactory.beginTransaction();

                AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )    
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                BandoDAO bandoDAO = daoFactory.getBandoDAO();

                Bando bando = null;
                if(bandoId != null) bando = bandoDAO.findbyId(bandoId);   // vado a modificare il bando selezionato tramite Id

                BaseDAO baseDAO = daoFactory.getBaseDAO();
                listaBasi.addAll(baseDAO.stampaBasi());

                daoFactory.commitTransaction();

                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("BandoSelezionato", bando);
                page.addObject("ListaBasi", listaBasi);
                page.setViewName("Calendario/modificaBandoCSS");

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/modificaBando", params = {"bandoId"})
    public ModelAndView modificaBando(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "bandoId") String bandoId,
        @RequestParam(value = "oggettoBando") String oggettoBando,
        @RequestParam(value = "numMaxIscritti") String numMaxIscritti,
        @RequestParam(value = "dataScadenza") String dataScadenza,
        @RequestParam(value = "dataBando") String dataBando,
        @RequestParam(value = "locazione") String locazione,
        @RequestParam(value = "testoBando") String testoBando
        ){
        DAOFactory sessionDAOFactory= null;
        DAOFactory daoFactory = null;

        Amministratore loggedAdmin = null;

        String applicationMessage = null;

        Logger logger = LogService.getApplicationLogger();

        try {
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            AmministratoreDAO sessionAdminDAO = sessionDAOFactory.getAmministratoreDAO();
            if( ! cookieAdmin.equals("") )    
                loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            sessionDAOFactory.commitTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            BandoDAO bandoDAO = daoFactory.getBandoDAO();

            Bando bando = bandoDAO.findbyId(bandoId);

            Data dataBando = new Data(data);
            Data dataScadenzaBando = new Data(dataScadenza);

            bandoDAO.update(dataBando, bandoId, oggetto, locazione, numMaxIscritti, dataScadenzaBando, loggedAdmin.getIdAdministrator());
                    // vado a modificare il bando selezionato tramite Id

            daoFactory.commitTransaction();

            /*
            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            */
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);

            page.setViewName("Calendario/reload");
        } catch (Exception e) {
            if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

            e.printStackTrace();
            page.setViewName("Pagina_InizialeCSS");
        } 
        
        return page;
    }

    @PostMapping(path = "/inserisciBando", params = {""})
    public ModelAndView inserisciBando(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "dataBando") String dataBando,
        @RequestParam(value = "oggettoBando") String oggettoBando,
        @RequestParam(value = "numMaxIscritti") String numMaxIscritti,
        @RequestParam(value = "dataScadenza") String dataScadenza,
        @RequestParam(value = "locazione") String locazione
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

                BandoDAO bandoDAO = daoFactory.getBandoDAO();

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
                Data dataScadenza = new Data(dataScadenza);


                Bando bando = bandoDAO.create(data, bandoId, oggetto, Paths.get(RiferimentoTesto),
                        Base, Integer.parseInt(numMaxIscritti), dataScadenza, loggedAdmin.getIdAdministrator());


                BaseDAO baseDAO = daoFactory.getBaseDAO();
                List<com.progetto.sitoforzearmate.model.mo.Base.Base> listaBasi = baseDAO.stampaBasi();

                daoFactory.commitTransaction();
                sessionDAOFactory.commitTransaction();

                page.addObject("loggedAdminOn",loggedAdmin!=null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("applicationMessage", applicationMessage);
                page.addObject("ListaBasi", listaBasi);

                if(bando != null){
                    page.setViewName("Calendario/reload");
                }
                else{
                    throw new RuntimeException("Errore nell'inserimento del Bando");
                }

            } catch (Exception e) {
                if (daoFactory != null) daoFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/iscrizione", params = {"bandoId"})
    public ModelAndView iscrizione(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "bandoId") String bandoId,
        @RequestParam(value = "iscritto") String iscrizione 
        ){
            ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            String applicationMessage = null;

            try {

                sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL,response);
                sessionDAOFactory.beginTransaction();

                UtenteRegistratoDAO sessionUserDAO = sessionDAOFactory.getUtenteRegistratoDAO();
                if( ! cookieUser.equals("") )    
                    loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

                AmministratoreDAO amministratoreDAO = sessionDAOFactory.getAmministratoreDAO();
                if( ! cookieAdmin.equals("") )    
                    loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                BandoDAO bandoDAO = daoFactory.getBandoDAO();
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

                Bando bando = bandoDAO.findbyId(bandoId);   // vado a recuperare il bando selezionato tramite Id

                bando.AddMatricola(loggedUser.getMatricola());
                bando.AddEsito("in attesa", loggedUser.getMatricola());

                userDAO.iscrizioneBando(loggedUser, bando);
                boolean maxIscritti = userDAO.maxIscrittiRaggiunto(bando);

                daoFactory.commitTransaction();

                loggedUser.AddIdBando(bando.getId());

                sessionUserDAO.update(loggedUser);

                boolean iscritto;
                if (iscrizione.equalsIgnoreCase("True")) iscritto = true;
                else if (iscrizione.equalsIgnoreCase("False")) iscritto = false;
                else throw new RuntimeException();


                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject( "loggedAdminOn", loggedAdmin != null);
                page.addObject( "loggedAdmin", loggedAdmin);
                page.addObject("Iscritto", loggedUser.trovaBando(bandoId) || iscritto);
                page.addObject("BandoSelezionato", bando);
                page.setViewName("Calendario/viewBandoCSS");
                page.addObject("maxIscrittiRaggiunto", maxIscritti);
                page.addObject("inAttesa", bando.getEsito(loggedUser.getMatricola()));

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_inizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/annullaIscrizione", params = {"bandoId"})
    public ModelAndView annullaIscrizione(
        HttpServletResponse response,
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String loggedAdmin,
        @CookieValue(value = "loggedUser", defaultValue = "") String loggedUser,
        
        @RequestParam(value = "bandoId") String bandoId
        ){
                ModelAndView page = new ModelAndView();
            DAOFactory sessionDAOFactory= null;
            DAOFactory daoFactory = null;

            UtenteRegistrato loggedUser = null;
            Amministratore loggedAdmin = null;

            String applicationMessage = null;

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

                BandoDAO bandoDAO = daoFactory.getBandoDAO();
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

                Bando bando = bandoDAO.findbyId(bandoId);   // vado a recuperare il bando selezionato tramite Id

                bando.deleteMatricola(loggedUser.getMatricola());

                userDAO.annullaIscrizione(loggedUser, bando);
                boolean maxIscritti = userDAO.maxIscrittiRaggiunto(bando);


                daoFactory.commitTransaction();

                loggedUser.deleteIdBando(bando.getId());
                bando.deleteMatricola(loggedUser.getMatricola());

                sessionUserDAO.update(loggedUser);

                page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
                page.addObject("loggedUser", loggedUser);
                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("Iscritto", false);
                page.addObject("BandoSelezionato", bando);
                page.setViewName("Calendario/viewBandoCSS");
                page.addObject("maxIscrittiRaggiunto", maxIscritti);

            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }

    @PostMapping(path = "/esitoPartecipante", params = {"bandoId"})
    public ModelAndView esitoPartecipante(
        HttpServletResponse response, 
        
        @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
        
        @RequestParam(value = "utenteSelezionato") String utenteSelezionato,
        @RequestParam(value = "inAttesa") String esito,
        @RequestParam(value = "bandoId") String bandoId
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

                sessionDAOFactory.commitTransaction();

                daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
                daoFactory.beginTransaction();

                UtenteRegistratoDAO userDao = daoFactory.getUtenteRegistratoDAO();

                System.out.println(Esito);

                BandoDAO bandoDAO = daoFactory.getBandoDAO();

                String bandoId = request.getParameter("bandoId");

                Bando bando = bandoDAO.findbyId(bandoId);
                bando.updateEsito(Esito, utenteSelezionato);
                bandoDAO.updateEsito(bandoId, utenteSelezionato, esito);

                /* INVIO AVVISO */
                AvvisoDAO avvisoDAO = daoFactory.getAvvisoDAO();
                UtenteRegistratoDAO userDAO = daoFactory.getUtenteRegistratoDAO();

                String Oggetto = "Esito bando: " + bandoId;
                String Testo = "Gentile utente,\nLa informiamo che la sua domanda per il bando " + bandoId + " ha avuto esito: " + esito + ".\nPer ulteriori informazioni contattare il numero della Segreteria.";

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

                avvisoDAO.create(avvisoId, Oggetto, Paths.get(RiferimentoTesto), loggedAdmin.getIdAdministrator(), utenteSelezionato);

                daoFactory.commitTransaction();

                page.addObject("loggedAdminOn", loggedAdmin != null);
                page.addObject("loggedAdmin", loggedAdmin);
                page.addObject("BandoSelezionato", bandoId);

                page.setViewName("Calendario/reload");


            } catch (Exception e) {
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();

                e.printStackTrace();
                page.setViewName("Pagina_InizialeCSS");
            }

            return page;
        }
    }
