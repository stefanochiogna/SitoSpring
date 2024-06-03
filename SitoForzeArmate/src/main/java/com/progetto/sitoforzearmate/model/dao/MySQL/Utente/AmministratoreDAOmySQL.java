package com.progetto.sitoforzearmate.model.dao.MySQL.Utente;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Utente.AmministratoreDAO;
import com.progetto.sitoforzearmate.model.mo.Utente.*;

import java.sql.*;

public class AmministratoreDAOmySQL implements AmministratoreDAO{
    Connection connection;
    public AmministratoreDAOmySQL(Connection connection){
        this.connection = connection;
    }

    @Override
    public Amministratore create(
            /* Attributi Utente */
            String Nome,
            String Cognome,
            String CF,
            String mail,
            String telefono,
            String password,
            String sesso,
            Data dataNascita,

            /* Attributi Amministratore */
            String IdAdministrator){

        PreparedStatement statement;
        Amministratore admin = new Amministratore();

        admin.setNome(Nome);
        admin.setCognome(Cognome);
        admin.setCF(CF);
        admin.setMail(mail);
        admin.setTelefono(telefono);
        admin.setPassword(password);
        admin.setSesso(sesso);
        admin.setDataNascita(dataNascita);

        admin.setIdAdministrator(IdAdministrator);

        try {

            String sql
                    = " SELECT Email "
                    + " FROM utente "
                    + " WHERE  Email = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, admin.getMail());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();
            /* .next restituisce false se la select restituisce dei dati */
            /* si ha quindi che se restituisce true, allora si hanno più utenti con stessa chiave */

            if (exist) {
                throw new RuntimeException("AmministratoreDAOmySQL.create: Tentativo di inserimento di un amministratore già esistente.");
            }

            String sqlUtente =
                    " INSERT INTO utente " +
                            "     ( Nome,        " +
                            "       Cognome,     " +
                            "       CF,          " +
                            "       Telefono,    " +
                            "       Email,       " +
                            "       Password,    " +
                            "       sesso,       " +
                            "       DataNascita  " +
                            "       )            " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sqlUtente);
            int i = 1;
            statement.setString(i++, admin.getNome());
            statement.setString(i++, admin.getCognome());
            statement.setString(i++, admin.getCF());
            statement.setString(i++, admin.getTelefono());
            statement.setString(i++, admin.getMail());
            statement.setString(i++, admin.getPassword());
            statement.setString(i++, admin.getSesso());
            statement.setDate(i++, Date.valueOf(dataNascita.toStringSQL()));
            statement.setString(i++, admin.getPassword());

            statement.executeUpdate();

            String sqlUtenteRegistrato1 =
                    " INSERT INTO utente_registrato " +
                            "       ( Email,                " +
                            "         IdAdministrator       " +
                            "       )                       " +
                            " VALUES (?,?)";
            statement = connection.prepareStatement(sqlUtenteRegistrato1);

            statement.setString(1, admin.getMail());
            statement.setString(2, admin.getIdAdministrator());

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }

        return admin;
    }

    @Override
    public void update(Amministratore Admin){
        PreparedStatement statement;

        try {

            String sql
                    = " SELECT Email "
                    + " FROM utente "
                    + " WHERE "
                    + " Nome = ? AND"
                    + " Cognome = ? AND"
                    + " CF = ? AND"
                    + " Telefono = ? AND "
                    + " Email <> ? AND "
                    + " Password = ? AND "
                    + " sesso = ? AND "
                    + " DataNascita = ?";
            /* Si controlla se si ha uno stesso utente con due mail differenti. In caso eccezione */

            statement = connection.prepareStatement(sql);
            int i = 1;
            statement.setString(i++, Admin.getNome());
            statement.setString(i++, Admin.getCognome());
            statement.setString(i++, Admin.getCF());
            statement.setString(i++, Admin.getTelefono());
            statement.setString(i++, Admin.getMail());
            statement.setString(i++, Admin.getPassword());
            statement.setString(i++, Admin.getSesso());
            statement.setDate(i++, Date.valueOf(Admin.getDataNascita().toStringSQL()));

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                // throw new DuplicatedObjectException("ContactDAOJDBCImpl.create: Tentativo di aggiornamento in un contatto già esistente.");
                throw new RuntimeException("Aggiornamento amministratore già esistente");
            }

            sql
                    = " UPDATE utente "
                    + " SET "
                    + "   Nome = ?, "
                    + "   Cognome = ?, "
                    + "   CF = ?, "
                    + "   Telefono = ?, "
                    + "   Password = ?, "
                    + "   sesso = ?, "
                    + "   DataNascita = ? "
                    + " WHERE "
                    + "   Email = ? ";

            statement = connection.prepareStatement(sql);
            i = 1;
            statement.setString(i++, Admin.getNome());
            statement.setString(i++, Admin.getCognome());
            statement.setString(i++, Admin.getCF());
            statement.setString(i++, Admin.getTelefono());
            statement.setString(i++, Admin.getPassword());
            statement.setString(i++, Admin.getSesso());
            statement.setDate(i++, Date.valueOf(Admin.getDataNascita().toStringSQL()));
            statement.setString(i++, Admin.getMail());

            statement.executeUpdate();

            String sqlUtenteRegistrato =
                    " UPDATE amministratore " +
                            "       SET                        " +
                            "         Email = ?,               " +
                            "         IdAdministrator = ?      " +
                            "       WHERE                      " +
                            "         IdAdministrator = ?      ";
            statement = connection.prepareStatement(sqlUtenteRegistrato);
            i = 1;
            statement.setString(i++, Admin.getMail());
            statement.setString(i++, Admin.getIdAdministrator());
            statement.setString(i++, Admin.getIdAdministrator());

            statement.executeUpdate();
            statement.close();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Amministratore Admin){
        PreparedStatement statement;

        try{
            String sql
                    = "DELETE "
                    + "FROM utente NATURAL JOIN amministratore "
                    + "WHERE IdAdministrator = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Admin.getIdAdministrator());
            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Amministratore findByMail(String mail){
        Amministratore admin = null;
        PreparedStatement statement;

        try{
            String sql
                    = " SELECT * "
                    + " FROM utente NATURAL JOIN amministratore "
                    + " WHERE Email = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, mail);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                admin = readAdmin(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return admin;
    }

    @Override
    public Amministratore findById(String IdAdmin){
        Amministratore admin = null;
        PreparedStatement statement;

        try{
            String sql
                    = " SELECT * "
                    + " FROM utente NATURAL JOIN amministratore "
                    + " WHERE IdAdministrator = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, IdAdmin);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                admin = readAdmin(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return admin;
    }

    private Amministratore readAdmin(ResultSet resultSet) {
        Amministratore admin = new Amministratore();

        /* Utente */
        try{
            admin.setNome(resultSet.getString("Nome"));
        }
        catch (SQLException sql){ }
        try{
            admin.setCognome(resultSet.getString("Cognome"));
        }
        catch (SQLException sql){ }
        try{
            admin.setCF(resultSet.getString("CF"));
        }
        catch (SQLException sql){ }
        try{
            admin.setTelefono(resultSet.getString("Telefono"));
        }
        catch (SQLException sql){ }
        try{
            admin.setMail(resultSet.getString("Email"));
        }
        catch (SQLException sql){ }
        try{
            admin.setPassword(resultSet.getString("Password"));
        }
        catch (SQLException sql){ }
        try{
            admin.setSesso(resultSet.getString("sesso"));
        }
        catch (SQLException sql){ }
        try{
            admin.setDataNascitaString(resultSet.getDate("DataNascita").toString());
        }
        catch (SQLException sql){ }

        /* Amministratore */
        try{
            admin.setIdAdministrator(resultSet.getString("IdAdministrator"));
        }
        catch (SQLException sql){ }

        return admin;
    }
}
