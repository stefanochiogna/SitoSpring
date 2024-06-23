package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
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
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/")
    public ModelAndView init(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUtente,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin
    ){
        ModelAndView page = new ModelAndView();
        page.setViewName("index");
        return page;
    }

    @GetMapping("/homepage")
    public ModelAndView view(
            HttpServletResponse response,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUtente,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin
    ){

        ModelAndView page = new ModelAndView();
        page.setViewName("Pagina_InizialeCSS");

        // page.addObject("nome", oggetto) == request.addAttribute()

        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;
        Amministratore loggedAdmin = null;

        ArrayList<Notizie> notizie = new ArrayList<>();

        try {
            if(!cookieUtente.equals("") && !cookieAdmin.equals("")) throw new Exception("Errore: entrambi i cookie sono settati");
            if(!cookieUtente.equals("")) loggedUser = UtenteRegistratoDAOcookie.decode(cookieUtente);
            if(!cookieAdmin.equals(""))  loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            NotizieDAO notizieDAO = daoFactory.getNotizieDAO();
            notizie.add(0, notizieDAO.findById("0000000001"));
            notizie.add(1, notizieDAO.findById("0000000002"));
            notizie.add(2, notizieDAO.findById("0000000003"));
            notizie.add(3, notizieDAO.findById("0000000004"));

            page.addObject("notizia1", notizie.get(0));
            page.addObject("notizia2", notizie.get(1));
            page.addObject("notizia3", notizie.get(2));
            page.addObject("notizia4", notizie.get(3));


            System.out.println(cookieUtente);
            System.out.println(cookieAdmin);

            daoFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn", loggedAdmin != null);
            page.addObject("loggedAdmin", loggedAdmin);
            page.setViewName("Pagina_InizialeCSS");

        } catch (Exception e) {
            if (daoFactory != null) daoFactory.rollbackTransaction();

            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return page;

    }

    @PostMapping( path = "/modifyArticolo", params = {"Id"})
    public ModelAndView sostituisciArticolo(
            HttpServletResponse response,

            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,

            @RequestParam(value = "Id") String Id,
            @RequestParam(value = "Oggetto") String Oggetto,
            @RequestParam(value = "IdAdministrator") String IdAdmin,
            @RequestParam(value = "Testo") Part Testo
        ){

        ModelAndView page = new ModelAndView();

        DAOFactory daoFactory = null;

        Amministratore loggedAdmin = null;

        Notizie notizie;

        try {
            if(!cookieAdmin.equals("")) loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);
            else throw new RuntimeException("Errore: cookie non settato");

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();


            NotizieDAO notizieDAO = daoFactory.getNotizieDAO();
            if(Id.equals("")) throw new RuntimeException("Errore: Id non settato");
            notizie = notizieDAO.findById(Id);

            /* Recupero Parametri */
            if(Testo == null) throw new RuntimeException("Errore: Testo non settato");
            int IdArt = Integer.parseInt(Id);

            String DirectoryDest = Configuration.getDIRECTORY_FILE();                   // directory dove salvo i file
            String DirectoryDestPath = Configuration.getPATH(DirectoryDest);           // path della directory
            File file = new File(DirectoryDestPath + 'A' + IdArt);                 // vado a creare un file in quella directory con il nome 'B' + Id
            Testo.write(file.getAbsolutePath());                                        // vado a scriverci il contenuto del file

            if(Oggetto.equals("")) throw new RuntimeException("Errore: Oggetto non settato");
            notizie.setOggetto(Oggetto);

            if(IdAdmin.equals("")) throw new RuntimeException("Errore: IdAdmin non settato");
            notizie.setIdAdministrator(IdAdmin);

            notizie.setRiferimentoTesto(Paths.get(DirectoryDest));

            notizieDAO.update(notizie);


            daoFactory.commitTransaction();

            String notizia = "notizia" + Integer.parseInt(Id);
            page.addObject(notizia, notizie);

            page.addObject("loggedAdminOn", true);
            page.addObject("loggedAdmin", loggedAdmin);

            page.setViewName("index");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return page;
    }

    @PostMapping("/viewArt")
    public ModelAndView viewArt(
            HttpServletResponse response,
            @CookieValue(value = "loggedAdmin", defaultValue = "") String cookieAdmin,
            @CookieValue(value = "loggedUser", defaultValue = "") String cookieUser,
            @RequestParam(value = "Id", defaultValue = "")  String NotiziaId
    ) {
        DAOFactory daoFactory = null;

        UtenteRegistrato loggedUser = null;
        Amministratore loggedAdmin = null;

        ModelAndView page = new ModelAndView();

        try {
            if(!cookieUser.equals("") && !cookieAdmin.equals("")) throw new Exception("Errore: entrambi i cookie sono settati");
            if(!cookieAdmin.equals("")) loggedAdmin = AmministratoreDAOcookie.decode(cookieAdmin);
            if(!cookieUser.equals(""))  loggedUser = UtenteRegistratoDAOcookie.decode(cookieUser);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);
            daoFactory.beginTransaction();

            NotizieDAO notizieDAO = daoFactory.getNotizieDAO();

            if(NotiziaId.equals("")) throw new Exception("Errore: Id non settato");
            Notizie notizie = notizieDAO.findById(NotiziaId);

            daoFactory.commitTransaction();

            page.addObject("loggedOn",loggedUser!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedUser", loggedUser);
            page.addObject("loggedAdminOn",loggedAdmin!=null);  // loggedUser != null: attribuisce valore true o false
            page.addObject("loggedAdmin", loggedAdmin);
            page.addObject("NotiziaSelezionata", notizie);
            page.setViewName("viewArticoloCSS");

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return page;
    }
}
