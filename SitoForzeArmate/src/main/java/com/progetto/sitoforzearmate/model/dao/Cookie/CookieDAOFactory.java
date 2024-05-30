package com.progetto.sitoforzearmate.model.dao.Cookie;

import com.example.sitoforzaarmata.model.dao.*;
import com.example.sitoforzaarmata.model.dao.Bando.BandoDAO;
import com.example.sitoforzaarmata.model.dao.Base.BaseDAO;
import com.example.sitoforzaarmata.model.dao.Base.PastoDAO;
import com.example.sitoforzaarmata.model.dao.Base.PostoLettoDAO;
import com.example.sitoforzaarmata.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.example.sitoforzaarmata.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.Notizie.AvvisoDAO;
import com.example.sitoforzaarmata.model.dao.Notizie.NewsletterDAO;
import com.example.sitoforzaarmata.model.dao.Notizie.NotizieDAO;
import com.example.sitoforzaarmata.model.dao.Utente.AmministratoreDAO;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class CookieDAOFactory extends DAOFactory {
    private Map factoryParams;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public CookieDAOFactory(Map factoryParams){
        this.factoryParams = factoryParams;
    }

    @Override
    public void beginTransaction() {
        try {
            this.request = (HttpServletRequest) factoryParams.get("request");
            this.response = (HttpServletResponse) factoryParams.get("response");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
