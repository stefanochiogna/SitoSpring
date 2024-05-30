package com.progetto.sitoforzearmate.model.dao.MySQL.Notizie;

import com.example.sitoforzaarmata.model.dao.Notizie.NotizieDAO;
import com.example.sitoforzaarmata.model.mo.Notizie.Notizie;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotizieDAOmySQL implements NotizieDAO {

    Connection connection;
    public NotizieDAOmySQL(Connection connection){
        this.connection = connection;
    }

    @Override
    public Notizie create(
            String Id,
            String Oggetto,
            Path RiferimentoTesto,
            String IdAdministrator
        ){
        PreparedStatement statement;

        Notizie notizie = new Notizie();
        notizie.setID(Id);
        notizie.setOggetto(Oggetto);
        notizie.setRiferimentoTesto(RiferimentoTesto);
        notizie.setIdAdministrator(IdAdministrator);

        try{
            String sql
                    = " SELECT Id "
                    + " FROM notizia "
                    + " WHERE "
                    + " Id = ?       ";

            statement = connection.prepareStatement(sql);
            int i = 1;

            statement.setString(i++, notizie.getID());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("NotizieDAOmySQL.create: Tentativo di inserimento di una notizia con stesso Id.");
            }

            String sqlNotizie =
                    " INSERT INTO notizia " +
                            "     ( Id,        " +
                            "       Oggetto,                " +
                            "       Riferimento_al_testo,   " +
                            "       IdAdministrator,        " +
                            "       )            " +
                            "VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sqlNotizie);
            i = 1;

            statement.setString(i++, notizie.getID());
            statement.setString(i++, notizie.getOggetto());
            statement.setString(i++, notizie.getRiferimentoTesto().toString());
            statement.setString(i++, notizie.getIdAdministrator());

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return  notizie;
    }

    @Override
    public void update(Notizie notizie){
        PreparedStatement statement;
        try {
            String sql
                    = " SELECT Id           "
                    + " FROM notizia        "
                    + " WHERE               "
                    + "     Id <> ? AND     "
                    + "     Oggetto = ?     ";


            statement = connection.prepareStatement(sql);
            int i = 1;
            statement.setString(i++, notizie.getID());
            statement.setString(i++, notizie.getOggetto());

            ResultSet resultSet = statement.executeQuery();
            boolean exist;
            exist = resultSet.next();
            resultSet.close();
            if (exist) {
                throw new RuntimeException("NotizieDAOmySQL.create: Tentativo di aggiornamento di una notizia già presente.");
            }

            String sqlNotizie =
                    " UPDATE notizia " +
                    " SET   Oggetto = ?,                " +
                    "       Riferimento_al_testo = ?,   " +
                    "       IdAdministrator = ?        " +
                    "WHERE Id = ?";
            statement = connection.prepareStatement(sqlNotizie);
            i = 1;

            statement.setString(i++, notizie.getOggetto());
            statement.setString(i++, notizie.getRiferimentoTesto().toString());
            statement.setString(i++, notizie.getIdAdministrator());
            statement.setString(i++, notizie.getID());

            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete ( Notizie notizie ) {
        PreparedStatement statement;

        try {
            String sql
                    = " DELETE "
                    + " FROM notizia "
                    + " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, notizie.getID());
            statement.executeUpdate();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Notizie findById(String Id) {
        Notizie notizia = null;
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT * "
                    + " FROM notizia "
                    + " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                notizia = read(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }

        return notizia;
    }

    private Notizie read(ResultSet resultSet){
        Notizie notizia = new Notizie();

        /* Notizia */
        try{
            notizia.setID(resultSet.getString("Id"));
        }
        catch (SQLException sql){ }
        try{
            notizia.setOggetto(resultSet.getString("Oggetto"));
        }
        catch (SQLException sql){ }
        try{
            notizia.setRiferimentoTesto(Paths.get(resultSet.getString("Riferimento_al_testo")));
        }
        catch (SQLException sql){ }
        try{
            notizia.setIdAdministrator(resultSet.getString("IdAdministrator"));
        }
        catch (SQLException sql){ }

        return notizia ;
    }
}
