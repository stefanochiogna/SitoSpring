package com.progetto.sitoforzearmate.model.dao.MySQL;

import com.progetto.sitoforzearmate.model.dao.Bando.BandoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PastoDAO;
import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Bando.BandoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.BaseDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.PastoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.PostoLettoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.AvvisiDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NewsletterDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.AmministratoreDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.Notizie.AvvisoDAO;
import com.progetto.sitoforzearmate.model.dao.Notizie.NewsletterDAO;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class MySQLdao extends DAOFactory {
    private Connection connection;
    /* Connection: connessione ad uno specifico database */
    

    @Override /* Permette di sovrascrivere il metodo della superclasse */
    public void beginTransaction(){
        try{
            Class.forName(Configuration.DATABASE_DRIVER);


            /* restituisce una istanza della classe avente come nome quello passato come parametro */
            String url = Configuration.DATABASE_URL;
            System.out.println(Configuration.DATABASE_HOSTNAME);

            String port = System.getProperty("porta");
            String host = System.getProperty("host");
            if(port != null){
                url = url.replace("3306", port);
            }
            if(host != null){
                url = url.replace("localhost", host);
            }

            if(Configuration.DATABASE_HOSTNAME != null) {
                String DATABASE_URL = url.replace("localhost", Configuration.DATABASE_HOSTNAME);
                if(Configuration.DATABASE_PORT != null) DATABASE_URL = DATABASE_URL.replace("3306", Configuration.DATABASE_PORT);
                System.out.println(DATABASE_URL);
                this.connection = DriverManager.getConnection(DATABASE_URL);
            }
            else
                this.connection = DriverManager.getConnection(url);
            /* tramite getConnection si tenta di stabilire una connessione con l'url del database fornito. Restituisce una connessione all'url */

            System.out.println("Connessione al database: " + url);
            System.out.println("Connessione stabilita: " + this.connection);

            this.connection.setAutoCommit(false);
            /* se si avesse autocommit(true): tutte le transazioni vengono eseguite ed effettuato commit individualmente */
        }
        catch(ClassNotFoundException e){
            // Eccezione rilanciata se la classe che si tenta di caricare con il metodo Class.forName()
            e.printStackTrace();
            throw new RuntimeException("Problemi con la classe del database driver\n");
            // throw new RuntimeException(e);
        }

        catch(SQLException e){
            /* Eccezione legata ad un accesso errato al database o altri tipologie di errori sql */

            throw new RuntimeException(e);
        }
    }

    @Override
    public void commitTransaction() {
        try {
            this.connection.commit();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            this.connection.rollback();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeTransaction() {
        try {
            this.connection.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UtenteRegistratoDAO getUtenteRegistratoDAO(){
        return new UtenteRegistratoDAOmySQL(connection);
    }
    @Override
    public NotizieDAO getNotizieDAO(){
        return new NotizieDAOmySQL(connection);
    }
    @Override
    public BandoDAO getBandoDAO(){
        return new BandoDAOmySQL(connection);
    }
    @Override
    public AmministratoreDAO getAmministratoreDAO(){
        return new AmministratoreDAOmySQL(connection);
    }
    @Override
    public AvvisoDAO getAvvisoDAO() {
        return new AvvisiDAOmySQL(connection);
    }
    @Override
    public NewsletterDAO getNewsletterDAO() {
        return new NewsletterDAOmySQL(connection);
    }
    @Override
    public BaseDAO getBaseDAO() {
        return new BaseDAOmySQL(connection);
    }
    @Override
    public PastoDAO getPastoDAO(){
        return new PastoDAOmySQL(connection);
    }
    public PostoLettoDAO getPostoLettoDAO() {
        return new PostoLettoDAOmySQL(connection);
    }
}
