package com.progetto.sitoforzearmate.model.dao.Cookie;

import com.progetto.sitoforzearmate.model.dao.*;
import com.progetto.sitoforzearmate.model.dao.Bando.BandoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PastoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Notizie.AvvisoDAO;
import com.progetto.sitoforzearmate.model.dao.Notizie.NewsletterDAO;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class CookieDAOFactory extends DAOFactory {
    private HttpServletResponse response;

    public CookieDAOFactory(HttpServletResponse response){
        this.response = response;
    }

    @Override
    public void beginTransaction() {
            this.response = response;
    }

    @Override
    public void commitTransaction(){    }

    @Override
    public void rollbackTransaction(){    }

    @Override
    public void closeTransaction(){    }

    @Override
    public UtenteRegistratoDAO getUtenteRegistratoDAO(){
        return new UtenteRegistratoDAOcookie(request, response);
    }
    @Override
    public AmministratoreDAO getAmministratoreDAO(){
        return new AmministratoreDAOcookie(request, response);
    }
    public NotizieDAO getNotizieDAO(){ throw new RuntimeException(); }
    public BandoDAO getBandoDAO(){
        throw new RuntimeException();
    }
    public AvvisoDAO getAvvisoDAO() { throw new RuntimeException(); }
    public NewsletterDAO getNewsletterDAO() { throw new RuntimeException(); }
    public BaseDAO getBaseDAO() { throw new RuntimeException(); }
    public PastoDAO getPastoDAO(){
        throw new RuntimeException();
    }
    public PostoLettoDAO getPostoLettoDAO(){
        throw new RuntimeException();
    }

}
