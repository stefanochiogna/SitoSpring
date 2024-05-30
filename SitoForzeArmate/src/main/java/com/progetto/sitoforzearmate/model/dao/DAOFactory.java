package com.progetto.sitoforzearmate.model.dao;


import com.progetto.sitoforzearmate.model.dao.Bando.BandoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PastoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
import com.progetto.sitoforzearmate.model.dao.Cookie.CookieDAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.MySQLdao;
import com.progetto.sitoforzearmate.model.dao.Notizie.AvvisoDAO;
import com.progetto.sitoforzearmate.model.dao.Notizie.NewsletterDAO;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;

import java.util.Map;

public abstract class DAOFactory {

    // List of DAO types supported by the factory
    public static final String MYSQLJDBCIMPL = "MySQLJDBCImpl";
    public static final String COOKIEIMPL= "CookieImpl";

    public abstract void beginTransaction();
    public abstract void commitTransaction();
    public abstract void rollbackTransaction();
    public abstract void closeTransaction();

    /* Vanno aggiunti tanti DAO quante sono le entità */
    public abstract UtenteRegistratoDAO getUtenteRegistratoDAO();
    public abstract AmministratoreDAO getAmministratoreDAO();
    public abstract NotizieDAO getNotizieDAO();
    public abstract BandoDAO getBandoDAO();
    public abstract AvvisoDAO getAvvisoDAO();
    public abstract NewsletterDAO getNewsletterDAO();
    public abstract BaseDAO getBaseDAO();
    public abstract PastoDAO getPastoDAO();
    public abstract PostoLettoDAO getPostoLettoDAO();

    public static DAOFactory getDAOFactory(String whichFactory,Map factoryParameters) {
        /* A seconda della factory richiesta restituisce la factory mysql o cookie */
        /* Ritorna DAOFactory in quanto le factory sono figlie di DAOFactory */

        if (whichFactory.equals(MYSQLJDBCIMPL)) {
            return new MySQLdao(factoryParameters);
        } else if (whichFactory.equals(COOKIEIMPL)) {
            return new CookieDAOFactory(factoryParameters);
        } else {
            return null;
        }
    }

}
