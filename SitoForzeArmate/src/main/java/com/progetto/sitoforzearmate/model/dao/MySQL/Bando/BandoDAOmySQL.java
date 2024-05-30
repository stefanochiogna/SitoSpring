package com.progetto.sitoforzearmate.model.dao.MySQL.Bando;

import com.example.sitoforzaarmata.model.dao.Bando.BandoDAO;
import com.example.sitoforzaarmata.model.dao.Data;
import com.example.sitoforzaarmata.model.mo.Bando;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BandoDAOmySQL implements BandoDAO {
    Connection connection;
    public BandoDAOmySQL(Connection connection){
        this.connection = connection;
    }

    @Override
    public Bando create(
            Data data,
            String Id,
            String Oggetto,
            Path RiferimentoTesto,
            String Locazione,
            Integer N_max_iscritti,
            Data dataScadenza,
            String IdAdmin
    ) {
        PreparedStatement statement;

        Bando bando = new Bando();
        bando.setData(data);
        bando.setId(Id);
        bando.setOggetto(Oggetto);
        bando.setRiferimentoTesto(RiferimentoTesto);
        bando.setLocazione(Locazione);
        bando.setMaxNumPartecipanti(N_max_iscritti);
        bando.setDataScadenzaIscrizione(dataScadenza);
        bando.setIdAdmin(IdAdmin);

        try {
            String sql
                    = " SELECT Id "
                    + " FROM bando "
                    + " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);

            statement.setString(1, bando.getId());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if(exist) {
                throw new RuntimeException("BandoDAOmySQL.create: Tentativo di inserimento di un bando con stesso Id.");
            }

            String sqlBando
                    = " INSERT INTO bando "
                    + " ( Data, "
                    + "   Id, "
                    + "   Oggetto, "
                    + "   Riferimento_al_testo, "
                    + "   Locazione, "
                    + "   N_max_iscritti, "
                    + "   Data_scadenza_iscrizione, "
                    + "   IdAdministrator "
                    + "  ) "
                    + " VALUES (?,?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(sqlBando);
            int i = 1;

            statement.setDate(i++, Date.valueOf(bando.getDataSQL(data)));
            statement.setString(i++, bando.getId());
            statement.setString(i++, bando.getOggetto());
            statement.setString(i++, bando.getRiferimentoTesto().toString());
            statement.setString(i++, bando.getLocazione());
            statement.setInt(i++, bando.getMaxNumPartecipanti());
            statement.setDate(i++, Date.valueOf(bando.getDataSQL(dataScadenza)));
            statement.setString(i++, bando.getIdAdmin());

            statement.executeUpdate();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }

        return bando;
    }

    @Override
    public List<Bando> show(){
        List<Bando> bandi = new ArrayList<>();

        PreparedStatement statement;

        try{
            String sqlBando
                    = " SELECT * "
                    + " FROM bando ";
                    //+ " WHERE Data_scadenza_iscrizione > CURRENT_DATE()";

            statement = connection.prepareStatement(sqlBando);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                bandi.add(read(resultSet));
            }

            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return bandi;
    }

    @Override
    public void update(
            Data data,
            String Id,
            String Oggetto,
            String Locazione,
            Integer MaxIscritti,
            Data dataScadenza,
            String IdAdministrator
    ){
        PreparedStatement statement;

        try {
            String sql
                    = " UPDATE bando "
                    + " SET "
                    + "   Data = ?,    "
                    + "   Oggetto = ?, "
                    + "   Locazione = ?, "
                    + "   N_max_iscritti = ?, "
                    + "   Data_scadenza_iscrizione = ?, "
                    + "   IdAdministrator = ? "
                    + " WHERE "
                    + "   Id = ? ";

            statement = connection.prepareStatement(sql);
            int i = 1;
            statement.setDate(i++, Date.valueOf(data.toStringSQL()));
            statement.setString(i++, Oggetto);
            statement.setString(i++, Locazione);
            statement.setInt(i++, MaxIscritti);
            statement.setDate(i++,  Date.valueOf(dataScadenza.toStringSQL()));
            statement.setString(i++, IdAdministrator);
            statement.setString(i++, Id);
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete( Bando bando ) {
        PreparedStatement statement;

        try {
            String sql
                    = " DELETE "
                    + " FROM bando "
                    + " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, bando.getId());
            statement.executeUpdate();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Bando findbyId(String Id) {
        Bando bando = new Bando();
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT * "
                    + " FROM bando "
                    + " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                bando = read(resultSet);
            }

            sql
                = " SELECT Matricola , Esito"
                + " FROM bando JOIN partecipa ON bando.Id = partecipa.Id_bando"
                + " WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Id);

            resultSet = statement.executeQuery();
            while(resultSet.next()){
                bando.AddMatricola(resultSet.getString("Matricola"));
                bando.AddEsito(resultSet.getString("Esito"), resultSet.getString("Matricola"));
            }

            resultSet.close();
            statement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bando;
    }

    @Override
    public List<String> getDate(){
        List<String> dataList = new ArrayList<>();

        PreparedStatement statement;

        try{
            String sqlBando
                    = " SELECT DISTINCT Data "
                    + " FROM bando "
                    + " ORDER BY Data ASC";

            statement = connection.prepareStatement(sqlBando);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                dataList.add(resultSet.getString("Data"));
            }

            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return dataList;
    }

    @Override
    public List<Bando> getBandiScaduti(){
        List<Bando> bandi = new ArrayList<>();

        PreparedStatement statement;

        try{
            String sqlBando
                    = " SELECT * "
                    + " FROM bando "
                    + " WHERE Data_scadenza_iscrizione <= CURRENT_DATE()";

            statement = connection.prepareStatement(sqlBando);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                bandi.add(read(resultSet));
            }

            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return bandi;
    }

    @Override
    public String getLastId(){
        PreparedStatement statement;
        try {
            String sql
                    =   " SELECT Id " +
                        " FROM bando" +
                        " WHERE Id >= ALL(SELECT Id " +
                                        " FROM bando)";

            statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getString("Id");
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public void updateEsito(
            String IdBando,
            String IdPartecipante,
            String Esito
    ){
        PreparedStatement statement;

        try {
            String sql
                    = " UPDATE partecipa "
                    + " SET "
                    + "   Esito = ?    "
                    + " WHERE "
                    + "   Id_bando = ? AND"
                    + "   Matricola = ? ";

            statement = connection.prepareStatement(sql);
            int i = 1;
            statement.setString(i++, Esito);
            statement.setString(i++, IdBando);
            statement.setString(i++, IdPartecipante);
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Bando read(ResultSet resultSet){
        Bando bando = new Bando();

        /* Bando */
        try{
            bando.setDataStringa(resultSet.getDate("Data").toString());
        }
        catch (SQLException sql){ }
        try{
            bando.setId(resultSet.getString("Id"));
        }
        catch (SQLException sql){ }
        try {
            bando.setOggetto(resultSet.getString("Oggetto"));
        }
        catch (SQLException sql){ }
        try{
            bando.setRiferimentoTesto(Paths.get(resultSet.getString("Riferimento_al_testo")));
        }
        catch (SQLException sql){ }
        try {
            bando.setLocazione(resultSet.getString("Locazione"));
        }
        catch (SQLException sql){ }
        try{
            bando.setMaxNumPartecipanti(resultSet.getInt("N_max_iscritti"));
        }
        catch (SQLException sql){ }
        try {
            bando.setDataScadenzaIscrizioneStringa(resultSet.getDate("Data_scadenza_iscrizione").toString());
        }
        catch ( SQLException sql ) { }
        try {
            bando.setIdAdmin(resultSet.getString("IdAdministrator"));
        }
        catch ( SQLException sql ) { }

        return bando ;
    }
}
