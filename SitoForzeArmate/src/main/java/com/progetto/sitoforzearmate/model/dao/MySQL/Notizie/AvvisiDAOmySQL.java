package com.progetto.sitoforzearmate.model.dao.MySQL.Notizie;

import com.progetto.sitoforzearmate.model.dao.Notizie.AvvisoDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Avviso;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvvisiDAOmySQL implements AvvisoDAO {
    Connection connection;
    public AvvisiDAOmySQL(Connection connection){
        this.connection = connection;
    }

    @Override
    public Avviso create(
            String Id,
            String Oggetto,
            Path RiferimentoTesto,
            String IdAdministrator,
            String MatricolaDestinatario
    ){
        PreparedStatement statement;

        Avviso avviso = new Avviso();
        avviso.setID(Id);
        avviso.setOggetto(Oggetto);
        avviso.setRiferimentoTesto(RiferimentoTesto);
        avviso.setIdAdministrator(IdAdministrator);
        avviso.addMatricolaDestinatario(MatricolaDestinatario);

        try{
            String sql
                    = " SELECT Id "
                    + " FROM avviso NATURAL JOIN notizia "
                    + " WHERE "
                    + " Id = ?       ";

            statement = connection.prepareStatement(sql);

            statement.setString(1, avviso.getID());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("AvvisiDAOmySQL.create: Tentativo di inserimento di una notizia con stesso Id.");
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

            statement.setString(i++, avviso.getID());
            statement.setString(i++, avviso.getOggetto());
            statement.setString(i++, avviso.getRiferimentoTesto().toString());
            statement.setString(i++, avviso.getIdAdministrator());

            statement.executeUpdate();

            String sqlAvvisi =
                    " INSERT INTO avviso " +
                            " ( Id," +
                            "   Matricola_destinatario " +
                            " ) " +
                            " VALUES (?,?) ";

            statement = connection.prepareStatement(sqlAvvisi);

            statement.setString(1, avviso.getID());
            statement.setString(2, MatricolaDestinatario);

            statement.executeUpdate();

            String sqlRiceve =
                    " INSERT INTO riceve_avviso " +
                            " ( Matricola," +
                            "   Id_avviso " +
                            " ) " +
                            " VALUES (?,?) ";

            statement = connection.prepareStatement(sqlRiceve);

            statement.setString(1, MatricolaDestinatario);
            statement.setString(2, avviso.getID());

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return  avviso;
    }

    @Override
    public void delete ( Avviso avviso ) {
        PreparedStatement statement;

        try {
            String sql
                    = " DELETE "
                    + " FROM riceve_avviso "
                    + " WHERE Id_avviso = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, avviso.getID());
            statement.executeUpdate();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getID(){
        String Id;
        PreparedStatement statement;

        try{
            String sql
                    = " SELECT Id "
                    + " FROM notizia "
                    + " WHERE Id LIKE '1%' AND Id >= ALL( SELECT Id "
                    + "                                   FROM notizia "
                    + "                                   WHERE Id LIKE '1%')";

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

    @Override
    public Avviso findById(String Id) {
        Avviso avviso = null;
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT * "
                    + " FROM avviso NATURAL JOIN notizia"
                    + " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                avviso = read(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }

        return avviso;
    }

    @Override
    public List<Avviso> stampaAvvisi(String MatricolaDestinatario){
        List<Avviso> avvisi = new ArrayList<>();

        PreparedStatement statement;

        try{
            String sqlAvvisi
                    = " SELECT * "
                    + " FROM avviso NATURAL JOIN notizia JOIN riceve_avviso ON avviso.Id = riceve_avviso.Id_avviso "
                    + " WHERE Matricola_destinatario = ? ";

            statement = connection.prepareStatement(sqlAvvisi);

            statement.setString(1, MatricolaDestinatario);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                avvisi.add(read(resultSet));
            }

            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return avvisi;
    }

    private Avviso read(ResultSet resultSet){
        Avviso avviso = new Avviso();

        /* Notizia */
        try{
            avviso.setID(resultSet.getString("Id"));
        }
        catch (SQLException sql){ }
        try{
            avviso.setOggetto(resultSet.getString("Oggetto"));
        }
        catch (SQLException sql){ }
        try{
            avviso.setRiferimentoTesto(Paths.get(resultSet.getString("Riferimento_al_testo")));
        }
        catch (SQLException sql){ }
        try{
            avviso.setIdAdministrator(resultSet.getString("IdAdministrator"));
        }
        catch (SQLException sql){ }

        /* Avviso */
        try {
            avviso.addMatricolaDestinatario(resultSet.getString("Matricola_destinatario"));
        }
        catch (SQLException sql){ }

        return avviso ;
    }
}
