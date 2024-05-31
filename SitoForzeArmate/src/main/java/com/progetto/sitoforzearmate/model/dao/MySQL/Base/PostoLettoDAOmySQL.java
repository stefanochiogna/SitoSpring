package com.progetto.sitoforzearmate.model.dao.MySQL.Base;

import com.progetto.sitoforzearmate.model.dao.Base.PostoLettoDAO;
import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.mo.Base.PostoLetto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PostoLettoDAOmySQL implements PostoLettoDAO {
    Connection connection;

    public PostoLettoDAOmySQL(Connection connection) {
        this.connection = connection;
    }

    public PostoLetto create(
            String Locazione,
            String Matricola,
            Data dataArrivo,
            Integer numNotti,
            Integer numPersone
    ) {

        PreparedStatement statement;

        PostoLetto alloggio = new PostoLetto();
        alloggio.setLocazione(Locazione);
        alloggio.setMatricola(Matricola);
        alloggio.setData_arrivo(dataArrivo);
        alloggio.setNum_notti(numNotti);
        alloggio.setNum_persone(numPersone);
        alloggio.setId(alloggio.getId());

        try {
            String sql
                    = " SELECT * "
                    + " FROM effettua "
                    + " WHERE "
                    + " Id = ? AND"
                    + " Locazione = ? ";

            statement = connection.prepareStatement(sql);

            statement.setString(1, alloggio.getId());
            statement.setString(2, alloggio.getLocazione());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("PostoLettoDAOmySQL.create: Tentativo di inserimento di un posto letto con stesso Id all'interno della stessa Locazione.");
            }
            // insert in prenotazione_alloggio
            String sqlPrenotaAlloggio =
                    " INSERT INTO prenotazione_alloggio  " +
                            "     ( Locazione, " +
                            "       Id    " +
                            "     )       " +
                            "VALUES (?, ?)";
            statement = connection.prepareStatement(sqlPrenotaAlloggio);
            int i = 1;

            statement.setString(i++, alloggio.getLocazione());
            statement.setString(i++, alloggio.getId());
            statement.executeUpdate();

            // insert in Effettua
            String sqlPostoLetto =
                    " INSERT INTO effettua  " +
                            "     ( Locazione, " +
                            "       Id,    " +
                            "       Matricola, " +
                            "       Data_arrivo, " +
                            "       Num_notti, " +
                            "       Num_persone" +
                            "     )            " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sqlPostoLetto);
            i = 1;

            statement.setString(i++, alloggio.getLocazione());
            statement.setString(i++, alloggio.getId());
            statement.setString(i++, alloggio.getMatricola());
            statement.setDate(i++, Date.valueOf(alloggio.getData_arrivo().toStringSQL()));
            statement.setInt(i++, alloggio.getNum_notti());
            statement.setInt(i++, alloggio.getNum_persone());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return alloggio;

    }

    public void delete(PostoLetto alloggio) {
        PreparedStatement statement;

        try {
            String sqlPrenotaAlloggio
                    = " DELETE "
                    + " FROM prenotazione_alloggio "
                    + " WHERE Locazione = ? AND Id = ? ";
            statement = connection.prepareStatement(sqlPrenotaAlloggio);
            statement.setString(1, alloggio.getLocazione());
            statement.setString(2, alloggio.getId());
            statement.executeUpdate();

            String sqlPostoLetto
                    = " DELETE "
                    + " FROM effettua "
                    + " WHERE Locazione = ? AND Id = ? ";
            statement = connection.prepareStatement(sqlPostoLetto);
            statement.setString(1, alloggio.getLocazione());
            statement.setString(2, alloggio.getId());
            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PostoLetto findbyId(String Locazione, String Id) {

        PostoLetto alloggio = null;
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT * "
                    + " FROM effettua NATURAL JOIN prenotazione_alloggio "
                    + " WHERE Locazione = ? AND Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Locazione);
            statement.setString(2, Id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                alloggio = read(resultSet);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return alloggio;
    }

    public List<PostoLetto> findByMatricola(String Matricola){
        List<PostoLetto> alloggioList = new ArrayList<>();

        PreparedStatement statement;
        try {
            String sql =
                    " SELECT * " +
                    " FROM prenotazione_alloggio NATURAL JOIN effettua " +
                    " WHERE Matricola = ? ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Matricola);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                alloggioList.add(read(resultSet));
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return alloggioList;
    }

    private PostoLetto read(ResultSet resultSet){
        PostoLetto alloggio = new PostoLetto();

        // posto_letto1
        try{
            alloggio.setMatricola(resultSet.getString("Matricola"));
        }
        catch (SQLException sql){ }
        try{
            alloggio.setId(resultSet.getString("Id"));
        }
        catch (SQLException sql){ }
        try{
            alloggio.setLocazione(resultSet.getString("Locazione"));
        }
        catch (SQLException sql){ }

        try{
            alloggio.setNum_persone(resultSet.getInt("Num_persone"));
        }
        catch (SQLException sql){ }
        try{
            alloggio.setNum_notti(resultSet.getInt("Num_notti"));
        }
        catch (SQLException sql){ }
        try{
            alloggio.setData_arrivoString(resultSet.getDate("Data_arrivo").toString());
        }
        catch (SQLException sql){ }

        return alloggio;
    }
}


