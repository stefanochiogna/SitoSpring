package com.progetto.sitoforzearmate.model.dao.MySQL;

import com.example.sitoforzaarmata.model.dao.Bando.BandoDAO;
import com.example.sitoforzaarmata.model.dao.Base.BaseDAO;
import com.example.sitoforzaarmata.model.dao.Base.PastoDAO;
import com.example.sitoforzaarmata.model.dao.Base.PostoLettoDAO;
import com.example.sitoforzaarmata.model.dao.DAOFactory;
import com.example.sitoforzaarmata.model.dao.MySQL.Bando.BandoDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Base.BaseDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Base.PastoDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Base.PostoLettoDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Notizie.AvvisiDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Notizie.NewsletterDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Utente.AmministratoreDAOmySQL;
import com.example.sitoforzaarmata.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.example.sitoforzaarmata.model.dao.Notizie.AvvisoDAO;
import com.example.sitoforzaarmata.model.dao.Notizie.NewsletterDAO;
import com.example.sitoforzaarmata.model.dao.Notizie.NotizieDAO;
import com.example.sitoforzaarmata.model.dao.Utente.AmministratoreDAO;
import com.example.sitoforzaarmata.model.dao.Utente.UtenteRegistratoDAO;
import com.example.sitoforzaarmata.services.configuration.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class MySQLdao extends DAOFactory {
    private Map factoryParameters;
    /* oggetto che permette di mappare una coppia chiave-valore, con chiave univoca */

    private Connection connection;
    /* Connection: connessione ad uno specifico database */

    public MySQLdao(Map factoryParameters){
        this.factoryParameters = factoryParameters;
    }

    @Override /* Permette di sovrascrivere il metodo della superclasse */
    public void beginTransaction(){
        try{
            Class.forName(Configuration.DATABASE_DRIVER);


            /* restituisce una istanza della classe avente come nome quello passato come parametro */

            this.connection = DriverManager.getConnection(Configuration.DATABASE_URL);
            /* tramite getConnection si tenta di stabilire una connessione con l'url del database fornito. Restituisce una connessione all'url */

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
