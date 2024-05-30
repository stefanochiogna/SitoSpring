package com.progetto.sitoforzearmate.model.dao.MySQL.Base;

import com.example.sitoforzaarmata.model.dao.Base.PastoDAO;
import com.example.sitoforzaarmata.model.dao.Data;
import com.example.sitoforzaarmata.model.mo.Base.Pasto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PastoDAOmySQL implements PastoDAO {
    Connection connection;
    public PastoDAOmySQL(Connection connection){
        this.connection = connection;
    }

    public Pasto create(
            String Turno,
            Data data,
            String Locazione
    ){
        PreparedStatement statement;

        Pasto pasto = new Pasto();
        pasto.setTurno(Turno);
        pasto.setData_prenotazioneString(data.toStringSQL());
        pasto.setLocazione(Locazione);
        System.out.println("create: " + pasto.getId());
        try{
            String sql
                    = " SELECT * "
                    + " FROM pasto "
                    + " WHERE "
                    + " Id = ? AND "
                    + " Locazione = ? ";

            statement = connection.prepareStatement(sql);

            statement.setString(1, pasto.getId());
            statement.setString(2, pasto.getLocazione());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("PastoDAOmySQL.create: Tentativo di inserimento di un pasto con stesso Id all'interno della stessa Locazione.");
            }

            String sqlPasto =
                    " INSERT INTO pasto " +
                            "     ( Id, " +
                            "       Locazione " +
                            "       )            " +
                            " VALUES (?, ?) ";
            statement = connection.prepareStatement(sqlPasto);
            int i = 1;

            statement.setString(i++, pasto.getId());
            statement.setString(i++, pasto.getLocazione());

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return  pasto;
    }

    public void delete ( Pasto pasto ) {
        PreparedStatement statement;

        try {
            String sqlPasto
                    = " DELETE "
                    + " FROM pasto "
                    + " WHERE Id = ? AND Locazione = ? ";
            statement = connection.prepareStatement(sqlPasto);
            statement.setString(1, pasto.getId());
            statement.setString(2, pasto.getLocazione());
            statement.executeUpdate();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public Pasto findbyId(String Id, String Locazione) {
        Pasto pasto = null;
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT * "
                    + " FROM pasto "
                    + " WHERE Id = ? AND Locazione = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Id);
            statement.setString(2, Locazione);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                pasto = read(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }

        return pasto;
    }

    public Pasto createPrenotazione( Pasto pasto ){
        PreparedStatement statement;

        Pasto prenotazione = new Pasto();
        prenotazione.setId(pasto.getId());
        prenotazione.setLocazione(pasto.getLocazione());
        prenotazione.setMatricola(pasto.getMatricola());
        prenotazione.setTurno(pasto.getTurno());
        prenotazione.setData_prenotazione(pasto.getData_prenotazione());

        System.out.println(prenotazione.getId());

        try{
            create(prenotazione.getTurno(), prenotazione.getData_prenotazione(), prenotazione.getLocazione());

            String sqlPrenotazione
                    = " SELECT * "
                    + " FROM prenota_pasto "
                    + " WHERE "
                    + " Matricola = ? AND "
                    + " Id_pasto = ? AND "
                    + " Locazione = ? AND "
                    + " Turno = ? AND "
                    + " Data = ? ";

            statement = connection.prepareStatement(sqlPrenotazione);

            statement.setString(1, prenotazione.getMatricola());
            statement.setString(2, prenotazione.getId());
            statement.setString(3, prenotazione.getLocazione());
            statement.setString(4, prenotazione.getTurno());
            statement.setDate(5, Date.valueOf(prenotazione.getData_prenotazione().toStringSQL()));


            ResultSet resultSet = statement.executeQuery();

            boolean exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("PastoDAOmySQL.create: Tentativo di inserimento di una prenotazione pasto già esistente.");
            }

            String sqlPasto =
                    " INSERT INTO prenota_pasto " +
                            "     ( Matricola, " +
                            "       Locazione, " +
                            "       Id_pasto," +
                            "       Turno, " +
                            "       Data  " +
                            "       )            " +
                            " VALUES (?, ?, ?, ?, ?) ";
            statement = connection.prepareStatement(sqlPasto);
            int i = 1;

            statement.setString(i++, prenotazione.getMatricola());
            statement.setString(i++, prenotazione.getLocazione());
            statement.setString(i++, prenotazione.getId());
            statement.setString(i++, prenotazione.getTurno());
            statement.setDate(i++, Date.valueOf(prenotazione.getData_prenotazione().toStringSQL()));

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return  prenotazione;
    }

    @Override
    public List<Pasto> findByMatricola(String Matricola){
        List<Pasto> pastoList = new ArrayList<>();

        PreparedStatement statement;
        try {
            String sql =
                    " SELECT * " +
                    " FROM pasto JOIN prenota_pasto ON Id = Id_pasto " +
                    " WHERE Matricola = ? ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Matricola);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                pastoList.add(read(resultSet));
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return pastoList;
    }

    private Pasto read(ResultSet resultSet){
        Pasto pasto = new Pasto();

        /* pasto */
        try{
            pasto.setId(resultSet.getString("Id"));
        }
        catch (SQLException sql){ }
        try{
            pasto.setLocazione(resultSet.getString("Locazione"));
        }
        catch (SQLException sql){ }

        /* prenota_pasto */
        try{
            pasto.setMatricola(resultSet.getString("Matricola"));
        }
        catch (SQLException sql){ }
        try{
            pasto.setTurno(resultSet.getString("Turno"));
        }
        catch (SQLException sql){ }
        try{
            pasto.setData_prenotazioneString(resultSet.getDate("Data").toString());
        }
        catch (SQLException sql){ }

        return pasto;
    }
}
