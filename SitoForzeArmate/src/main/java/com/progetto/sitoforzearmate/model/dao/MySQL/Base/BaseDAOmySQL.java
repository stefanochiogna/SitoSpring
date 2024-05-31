package com.progetto.sitoforzearmate.model.dao.MySQL.Base;

import com.progetto.sitoforzearmate.model.dao.Base.BaseDAO;
import com.progetto.sitoforzearmate.model.mo.Base.Base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDAOmySQL implements BaseDAO {
    Connection connection;
    public BaseDAOmySQL(Connection connection){
        this.connection = connection;
    }

    public Base create(
            byte[] Foto,
            String longitudine,
            String latitudine,
            String Locazione,
            String Email,
            String Telefono,
            String Provincia,
            String CAP,
            String Via,
            String IdAdministrator
    ){
        PreparedStatement statement;

        Base base = new Base();
        base.setFotoByte(Foto);
        base.setLatitudine(Double.parseDouble(latitudine));
        base.setLongitudine(Double.parseDouble(longitudine));
        base.setLocazione(Locazione);
        base.setMail(Email);
        base.setTelefono(Telefono);
        base.setProvincia(Provincia);
        base.setCAP(CAP);
        base.setVia(Via);
        base.setIdAdministrator(IdAdministrator);

        try{
            String sql
                    = " SELECT Locazione "
                    + " FROM base "
                    + " WHERE "
                    + " Locazione = ?       ";

            statement = connection.prepareStatement(sql);

            statement.setString(1, base.getLocazione());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("BaseDAOmySQL.create: Tentativo di inserimento di una base con stessa Locazione.");
            }

            String sqlNotizie =
                    " INSERT INTO base " +
                            "     ( Locazione,        " +
                            "       Email,                " +
                            "       Telefono," +
                            "       Provincia," +
                            "       CAP," +
                            "       Via,   " +
                            "       IdAdministrator,        " +
                            "       Latitudine,     " +
                            "       Longitudine,     " +
                            "       FotoBase     " +
                            "       )            " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sqlNotizie);
            int i = 1;

            statement.setString(i++, base.getLocazione());
            statement.setString(i++, base.getMail());
            statement.setString(i++, base.getTelefono());
            statement.setString(i++, base.getProvincia());
            statement.setString(i++, base.getCAP());
            statement.setString(i++, base.getVia());
            statement.setString(i++, base.getIdAdministrator());
            statement.setDouble(i++, base.getLatitudine());
            statement.setDouble(i++, base.getLongitudine());
            statement.setBlob(i++, base.getFoto(), base.getFotoDim());

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return  base;
    }

    public void update(Base base){
        PreparedStatement statement;

        try {

            String sql
                    = " SELECT Locazione "
                    + " FROM base "
                    + " WHERE "
                    + " Locazione <> ? AND"
                    + " Email = ? AND"
                    + " Telefono = ? AND"
                    + " Provincia = ? AND "
                    + " CAP = ? AND "
                    + " Via = ? AND "
                    + " IdAdministrator = ? AND "
                    + " Latitudine = ? AND "
                    + " Longitudine = ?";

            statement = connection.prepareStatement(sql);
            int i = 1;
            statement.setString(i++, base.getLocazione());
            statement.setString(i++, base.getMail());
            statement.setString(i++, base.getTelefono());
            statement.setString(i++, base.getProvincia());
            statement.setString(i++, base.getCAP());
            statement.setString(i++, base.getVia());
            statement.setString(i++, base.getIdAdministrator());
            statement.setDouble(i++, base.getLatitudine());
            statement.setDouble(i++, base.getLongitudine());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                // throw new DuplicatedObjectException("ContactDAOJDBCImpl.create: Tentativo di aggiornamento in un contatto già esistente.");
                throw new RuntimeException("Aggiornamento base già esistente");
            }

            sql
                    = " UPDATE base "
                    + " SET "
                    + "   Email = ?, "
                    + "   Telefono = ?, "
                    + "   Provincia = ?, "
                    + "   CAP = ?, "
                    + "   Via = ?,"
                    + "   IdAdministrator = ?, "
                    + "   Latitudine = ?, "
                    + "   Longitudine = ?, "
                    + "   FotoBase = ? "
                    + " WHERE "
                    + "   Locazione = ? ";

            statement = connection.prepareStatement(sql);
            i = 1;
            statement.setString(i++, base.getMail());
            statement.setString(i++, base.getTelefono());
            statement.setString(i++, base.getProvincia());
            statement.setString(i++, base.getCAP());
            statement.setString(i++, base.getVia());
            statement.setString(i++, base.getIdAdministrator());
            statement.setString(i++, base.getLocazione());
            statement.setDouble(i++, base.getLatitudine());
            statement.setDouble(i++, base.getLongitudine());
            statement.setBlob(i++, base.getFoto(), base.getFotoDim());

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete ( Base base ) {
        PreparedStatement statement;

        try {
            String sql
                    = " DELETE "
                    + " FROM base "
                    + " WHERE Locazione = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, base.getLocazione());
            statement.executeUpdate();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public Base findbyLocazione(String Locazione) {
        Base base = null;
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT * "
                    + " FROM base "
                    + " WHERE Locazione = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, Locazione);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                base = read(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch ( SQLException e ) {
            throw new RuntimeException(e);
        }

        return base;
    }

    public List<Base> stampaBasi(){
        List<Base> basi = new ArrayList<>();

        PreparedStatement statement;

        try{
            String sqlBando
                    = " SELECT * "
                    + " FROM base ";

            statement = connection.prepareStatement(sqlBando);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                basi.add(read(resultSet));
            }

            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return basi;
    }

    private Base read(ResultSet resultSet){
        Base base = new Base();

        /* Base */
        try{
            base.setLocazione(resultSet.getString("Locazione"));
        }
        catch (SQLException sql){ }
        try{
            base.setMail(resultSet.getString("Email"));
        }
        catch (SQLException sql){ }
        try{
            base.setTelefono(resultSet.getString("Telefono"));
        }
        catch (SQLException sql){ }
        try{
            base.setProvincia(resultSet.getString("Provincia"));
        }
        catch (SQLException sql){ }
        try{
            base.setCAP(resultSet.getString("CAP"));
        }
        catch (SQLException sql){ }
        try{
            base.setVia(resultSet.getString("Via"));
        }
        catch (SQLException sql){ }
        try{
            base.setIdAdministrator(resultSet.getString("IdAdministrator"));
        }
        catch (SQLException sql){ }
        try{
            base.setLatitudine(Double.parseDouble(resultSet.getString("Latitudine")));
        }
        catch (SQLException sql){ }
        try{
            base.setLongitudine(Double.parseDouble(resultSet.getString("Longitudine")));
        }
        catch (SQLException sql){ }
        try{
            base.setFoto(resultSet.getBlob("FotoBase"));
        }
        catch (SQLException sql){ }

        return base ;
    }
}
