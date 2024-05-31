package com.progetto.sitoforzearmate.model.dao.MySQL.Notizie;

import com.progetto.sitoforzearmate.model.dao.Notizie.NewsletterDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Newsletter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class NewsletterDAOmySQL implements NewsletterDAO {
    Connection connection;
    public NewsletterDAOmySQL(Connection connection){
        this.connection = connection;
    }

    public Newsletter create(
            String Id,
            String Oggetto,
            Path RiferimentoTesto,
            String IdAdministrator,
            String MailDestinatario,
            String Matricola
    ){
        PreparedStatement statement;

        Newsletter newsletter = new Newsletter();
        newsletter.setID(Id);
        newsletter.setOggetto(Oggetto);
        newsletter.setRiferimentoTesto(RiferimentoTesto);
        newsletter.setIdAdministrator(IdAdministrator);
        newsletter.addMailDestinatario(MailDestinatario);
        newsletter.addMatricolaDestinatario(Matricola);

        try{
            String sql
                    = " SELECT Id "
                    + " FROM newsletter NATURAL JOIN notizia "
                    + " WHERE "
                    + " Id = ?       ";

            statement = connection.prepareStatement(sql);

            statement.setString(1, newsletter.getID());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("NewsletterDAOmySQL.create: Tentativo di inserimento di una notizia con stesso Id.");
            }

            String sqlNotizie =
                    " INSERT INTO notizia " +
                            "     ( Id,        " +
                            "       Oggetto,                " +
                            "       Riferimento_al_testo,   " +
                            "       IdAdministrator        " +
                            "       )            " +
                            "VALUES (?, ?, ?, ?)";

            statement = connection.prepareStatement(sqlNotizie);
            int i = 1;

            statement.setString(i++, newsletter.getID());
            statement.setString(i++, newsletter.getOggetto());
            statement.setString(i++, newsletter.getRiferimentoTesto().toString());
            statement.setString(i++, newsletter.getIdAdministrator());

            statement.executeUpdate();

            String sqlAvvisi =
                    " INSERT INTO newsletter " +
                            " ( Id," +
                            "   Mail_destinatario " +
                            " ) " +
                            " VALUES (?,?) ";

            statement = connection.prepareStatement(sqlAvvisi);

            statement.setString(1, newsletter.getID());
            statement.setString(2, MailDestinatario);

            statement.executeUpdate();

            String sqlRiceve =
                    " INSERT INTO riceve_news " +
                            " ( Matricola," +
                            "   Id_news " +
                            " ) " +
                            " VALUES (?,?) ";

            statement = connection.prepareStatement(sqlRiceve);

            statement.setString(1, Matricola);
            statement.setString(2, newsletter.getID());

            statement.executeUpdate();

            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return newsletter;
    }

    public void delete ( Newsletter newsletter ) {
        PreparedStatement statement;

        try {
            String sql
                    = " DELETE "
                    + " FROM riceve_news "
                    + " WHERE Id_news = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, newsletter.getID());
            statement.executeUpdate();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public Newsletter findById(String Id) {
        Newsletter newsletter = null;
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT * "
                    + " FROM newsletter NATURAL JOIN notizia"
                    + " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                newsletter = read(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }

        return newsletter;
    }

    public List<Newsletter> stampaNewsletter(String MailDestinatario){
        List<Newsletter> newsletters = new ArrayList<>();

        PreparedStatement statement;

        try{
            String sqlAvvisi
                    = " SELECT * " +
                      " FROM riceve_news " +
                      " JOIN newsletter ON riceve_news.Id_news = newsletter.Id " +
                      " JOIN notizia ON newsletter.Id = notizia.Id " +
                      " JOIN utente_registrato ON newsletter.Mail_destinatario = utente_registrato.Email " +
                      " WHERE utente_registrato.Email = ? AND utente_registrato.Iscritto_newsletter = 1 ";

            statement = connection.prepareStatement(sqlAvvisi);

            statement.setString(1, MailDestinatario);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                newsletters.add(read(resultSet));
            }

            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return newsletters;
    }

    @Override
    public String getID(){
        String Id;
        PreparedStatement statement;

        try{
            String sql
                    = " SELECT Id "
                    + " FROM notizia "
                    + " WHERE Id LIKE '2%' AND Id >= ALL( SELECT Id "
                    + "                                   FROM notizia "
                    + "                                   WHERE Id LIKE '2%')";

            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            Id = resultSet.getString("Id");

            if(Id == null){
                throw new RuntimeException("Errore nell creazione dell'ID");
            }
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }

        return Id;
    }

    private Newsletter read(ResultSet resultSet){
        Newsletter newsletter = new Newsletter();

        /* Notizia */
        try{
            newsletter.setID(resultSet.getString("Id"));
        }
        catch (SQLException sql){ }
        try{
            newsletter.setOggetto(resultSet.getString("Oggetto"));
        }
        catch (SQLException sql){ }
        try{
            newsletter.setRiferimentoTesto(Paths.get(resultSet.getString("Riferimento_al_testo")));
        }
        catch (SQLException sql){ }
        try{
            newsletter.setIdAdministrator(resultSet.getString("IdAdministrator"));
        }
        catch (SQLException sql){ }

        /* Newsletter */
        try {
            newsletter.addMailDestinatario(resultSet.getString("Mail_destinatario"));
        }
        catch (SQLException sql){ }

        return newsletter ;
    }
}
