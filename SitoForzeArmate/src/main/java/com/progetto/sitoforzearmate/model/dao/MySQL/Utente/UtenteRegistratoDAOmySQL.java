package com.progetto.sitoforzearmate.model.dao.MySQL.Utente;

import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.Utente.UtenteRegistratoDAO;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Utente.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class UtenteRegistratoDAOmySQL implements UtenteRegistratoDAO {
    private Long Matricola = (long)0;
    private Long StipendioBase = (long)1000;

    Connection connection;
    public UtenteRegistratoDAOmySQL(Connection connection){
        this.connection = connection;
    }
    @Override
    public UtenteRegistrato create(
                /* Attributi Utente */
                String Nome,
                String Cognome,
                String CF,
                String Mail,
                String Telefono,
                String Password,
                String sesso,
                Data dataNascita,

                /* Attributi UtenteRegistrato */
                String Matricola,
                /* Long Stipendio, */
                String IBAN,
                String Ruolo,
                byte[] Foto,
                byte[] Documento,
                String Indirizzo,
                String Locazione_servizio,
                boolean Newsletter,

                List<String> Bandi){

        PreparedStatement statement;
        UtenteRegistrato user = new UtenteRegistrato();

        user.setNome(Nome);
        user.setCognome(Cognome);
        user.setCF(CF);
        user.setMail(Mail);
        user.setTelefono(Telefono);
        user.setPassword(Password);
        user.setSesso(sesso);
        user.setDataNascita(dataNascita);

        Optional<String> Role = Optional.ofNullable(Ruolo);
        user.setMatricola(Matricola);
        user.setRuolo(Ruolo);
        //user.setStipendio(Stipendio);
        user.setIBAN(IBAN);
        user.setFotoByte(Foto);
        user.setDocumentoByte(Documento);
        user.setIndirizzo(Indirizzo);
        user.setIscrittoNewsletter(Newsletter);

        Optional<List<String>> bandi = Optional.ofNullable(Bandi);

        user.setLocazioneServizio(Locazione_servizio);

        if(Role.isPresent() && !Ruolo.equals("")){
            switch(Ruolo){
                case "Ufficiale":
                    user.setStipendio((long)2*StipendioBase);
                    break;
                case "Sottufficiale":
                    user.setStipendio((long)1.5*StipendioBase);
                    break;
                case "Graduato":
                    user.setStipendio(StipendioBase);
                    break;
            }
        }
        else{
            user.setStipendio((long)0);
        }
        try {

            String sql
                    = " SELECT Email "
                    + " FROM utente "
                    + " WHERE  Email = ?";

            statement = connection.prepareStatement(sql);
            int i = 1;
            /*
            statement.setString(i++, user.getNome());
            statement.setString(i++, user.getCognome());
            statement.setString(i++, user.getCF());
            statement.setString(i++, user.getTelefono());
             */
            statement.setString(i++, user.getMail());
            /* statement.setString(i++, user.getPassword()); */

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();
            /* .next restituisce false se la select restituisce dei dati */
            /* si ha quindi che se restituisce true, allora si hanno più utenti con stessa chiave */

            if (exist) {
                throw new RuntimeException("UtenteRegistratoDAOmySQL.create: Tentativo di inserimento di un utente già esistente.");
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
            i = 1;
            statement.setString(i++, user.getNome());
            statement.setString(i++, user.getCognome());
            statement.setString(i++, user.getCF());
            statement.setString(i++, user.getTelefono());
            statement.setString(i++, user.getMail());
            statement.setString(i++, user.getPassword());
            statement.setString(i++, user.getSesso());
            statement.setDate(i++, Date.valueOf(user.getDataNascita().toStringSQL()));

            statement.executeUpdate();

            String sqlUtenteRegistrato1 =
                    " INSERT INTO utente_registrato " +
                    "       ( Email,                 " +
                    "         Matricola,            " +
                    "         IBAN,                 " +
                    "         Ruolo,                " +
                    "         Foto,                 " +
                    "         Documenti,            " +
                    "         Indirizzo,            " +
                    "         Locazione_servizio,   " +
                    "         Iscritto_Newsletter   " +
                    "       )                       " +
                    " VALUES (?,?,?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(sqlUtenteRegistrato1);
            i = 1;
            statement.setString(i++, user.getMail());
            statement.setString(i++, user.getMatricola());
            statement.setString(i++, user.getIBAN());
            statement.setString(i++, user.getRuolo());
            statement.setBlob(i++, user.getFoto(), user.getFotoDim());
            statement.setBlob(i++, user.getDocumento(), user.getDocumentoDim());
            statement.setString(i++, user.getIndirizzo());
            statement.setString(i++, user.getLocazioneServizio());
            statement.setInt(i++, user.getIscrittoNewsletterInt());

            statement.executeUpdate();
            statement.close();

            if(user.getRuolo() != null){
                addContatto(user.getMatricola());
                createBadge(user.getMatricola());
            }
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void update(UtenteRegistrato user){
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
                    + " Password = ? AND"
                    + " sesso = ? AND"
                    + " DataNascita = ?";
            /* Si controlla se si ha uno stesso utente con due mail differenti. In caso eccezione */

            statement = connection.prepareStatement(sql);
            int i = 1;
            statement.setString(i++, user.getNome());
            statement.setString(i++, user.getCognome());
            statement.setString(i++, user.getCF());
            statement.setString(i++, user.getTelefono());
            statement.setString(i++, user.getMail());
            statement.setString(i++, user.getPassword());
            statement.setString(i++, user.getSesso());
            statement.setDate(i++, Date.valueOf(user.getDataNascita().toStringSQL()));

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                // throw new DuplicatedObjectException("ContactDAOJDBCImpl.create: Tentativo di aggiornamento in un contatto già esistente.");
                throw new RuntimeException("Aggiornamento contatto già esistente");
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
            statement.setString(i++, user.getNome());
            statement.setString(i++, user.getCognome());
            statement.setString(i++, user.getCF());
            statement.setString(i++, user.getTelefono());
            statement.setString(i++, user.getPassword());
            statement.setString(i++, user.getSesso());
            statement.setDate(i++, Date.valueOf(user.getDataNascita().toStringSQL()));
            statement.setString(i++, user.getMail());

            statement.executeUpdate();

            String sqlUtenteRegistrato =
                    " UPDATE utente_registrato " +
                            "       SET                         " +
                            "         Email = ?,                " +
                            "         IBAN = ?,                 " +
                            "         Ruolo = ?,                " +
                            "         Foto = ?,                 " +
                            "         Documenti = ?,            " +
                            "         Indirizzo = ?,            " +
                            "         Locazione_servizio = ?,   " +
                            "         Iscritto_Newsletter = ?   " +
                            "       WHERE                       " +
                            "         Matricola = ?                  ";
            statement = connection.prepareStatement(sqlUtenteRegistrato);
            i = 1;
            statement.setString(i++, user.getMail());
            statement.setString(i++, user.getIBAN());
            statement.setString(i++, user.getRuolo());
            statement.setBlob(i++, user.getFoto(), user.getFotoDim());
            statement.setBlob(i++, user.getDocumento(), user.getDocumentoDim());
            statement.setString(i++, user.getIndirizzo());
            statement.setString(i++, user.getLocazioneServizio());
            statement.setInt(i++, user.getIscrittoNewsletterInt());
            statement.setString(i++, user.getMatricola());

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UtenteRegistrato user){
        PreparedStatement statement;

        try{
            String sql
                    = "DELETE "
                    + "FROM utente NATURAL JOIN utente_registrato "
                    + "WHERE Matricola = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getMatricola());
            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public UtenteRegistrato findByMatricola(String Matricola){
        UtenteRegistrato user = null;
        PreparedStatement statement;

        try{
            String sql
                    = " SELECT * "
                    + " FROM utente AS u JOIN utente_registrato AS ur ON u.Email = ur.Email"
                    + " WHERE Matricola = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Matricola);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                user = readUtente(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public UtenteRegistrato findByMail(String mail){
        UtenteRegistrato user = null;
        PreparedStatement statement;

        try{
            String sql
                    = " SELECT * "
                    + " FROM utente NATURAL JOIN utente_registrato "
                    + " WHERE Email = ? ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, mail);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                user = readUtente(resultSet);
            }

            resultSet.close();
            statement.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public boolean isDeleted(UtenteRegistrato user){
        PreparedStatement statement;

        try {
            String sql
                    = " SELECT Matricola"
                    + " FROM utente NATURAL JOIN utente_registrato "
                    + " WHERE "
                    + " Matricola = ?";

            statement = connection.prepareStatement(sql);

            statement.setString(1, user.getMatricola());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                user = readUtente(resultSet);
            }

            resultSet.close();
            statement.close();

            if(user == null) {
                return true;
            }
            else return false;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void iscrizioneBando(UtenteRegistrato user, Bando bando) {
        PreparedStatement statement;
        try {

            String sql
                    = " SELECT Matricola, Id_bando "
                    + " FROM partecipa "
                    + " WHERE "
                    + " Matricola = ? AND     "
                    + " Id_bando = ?          ";

            statement = connection.prepareStatement(sql);

            statement.setString(1, user.getMatricola());
            statement.setString(2, bando.getId());

            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                // throw new DuplicatedObjectException("ContactDAOJDBCImpl.create: Tentativo di aggiornamento in un contatto già esistente.");
                throw new RuntimeException("Aggiornamento contatto già esistente");
            }

            String sqlPartecipa =
                    " INSERT INTO partecipa " +
                            "       (Matricola,                 " +
                            "        Id_bando            " +
                            "       )                       " +
                            " VALUES (?,?)";
            statement = connection.prepareStatement(sqlPartecipa);

            statement.setString(1, user.getMatricola());
            statement.setString(2, bando.getId());

            statement.executeUpdate();

            String sqlAggiorna =
                    " UPDATE bando " +
                            " SET Numero_partecipanti = ? " +
                            " WHERE Id = ? ";
            statement = connection.prepareStatement(sqlAggiorna);

            statement.setInt(1, (bando.getNumPartecipanti()));
            statement.setString(2, bando.getId());

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean maxIscrittiRaggiunto(Bando bando){
        PreparedStatement statement;
        boolean maxRaggiunto = false;

        try {
            String controllo
                    = " SELECT Numero_partecipanti, N_max_iscritti "
                    + " FROM bando "
                    + " WHERE Id = ? ";

            statement = connection.prepareStatement(controllo);
            statement.setString(1, bando.getId());

            ResultSet resultSetControllo = statement.executeQuery();
            resultSetControllo.next();

            if (resultSetControllo.getInt("Numero_partecipanti") == resultSetControllo.getInt("N_max_iscritti")) {
                maxRaggiunto = true;
            }
            resultSetControllo.close();
            statement.close();

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return maxRaggiunto;
    }

    @Override
    public List<String> recuperaBandi(String Matricola){
        List<String> bandi = new ArrayList<>();

        PreparedStatement statement;

        try{
            String sqlBando
                    = " SELECT Id_bando "
                    + " FROM partecipa "
                    + " WHERE Matricola = ?";

            statement = connection.prepareStatement(sqlBando);

            statement.setString(1, Matricola);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                bandi.add(resultSet.getString("Id_bando"));
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
    public void annullaIscrizione(UtenteRegistrato user, Bando bando){
        PreparedStatement statement;

        try{
            String sql
                    = "DELETE "
                    + "FROM partecipa "
                    + "WHERE Matricola = ? AND"
                    + "      Id_bando = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getMatricola());
            statement.setString(2, bando.getId());

            statement.executeUpdate();

            String sqlAggiorna =
                    " UPDATE bando " +
                            " SET Numero_partecipanti = ? " +
                            " WHERE Id = ? ";
            statement = connection.prepareStatement(sqlAggiorna);
            statement.setInt(1, (bando.getNumPartecipanti()));
            statement.setString(2, bando.getId());

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void iscrizioneNewsletter(String MailUtente){
        PreparedStatement statement;

        try{
            String sqlAvvisi
                    = " SELECT Iscritto_Newsletter "
                    + " FROM utente NATURAL JOIN utente_registrato "
                    + " WHERE Email = ? AND Iscritto_Newsletter != 1";

            statement = connection.prepareStatement(sqlAvvisi);

            statement.setString(1, MailUtente);
            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("Già iscritto alla newsletter");
            }

            String sql
                    = " UPDATE utente_registrato "
                    + " SET "
                    + "   Iscritto_newsletter = ? "
                    + " WHERE "
                    + "   Email = ? ";

            statement = connection.prepareStatement(sql);

            statement.setInt(1, '1');
            statement.setString(2, MailUtente);

            statement.executeUpdate();

            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void annullaIscrizione(String MailUtente){
        PreparedStatement statement;

        try{
            String sqlAvvisi
                    = " SELECT Iscritto_Newsletter "
                    + " FROM utente NATURAL JOIN utente_registrato "
                    + " WHERE Email = ? AND Iscritto_Newsletter != 0";

            statement = connection.prepareStatement(sqlAvvisi);

            statement.setString(1, MailUtente);
            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new RuntimeException("Non è iscritto alla newsletter");
            }

            String sql
                    = " UPDATE utente_registrato "
                    + " SET "
                    + "   Iscritto_newsletter = ? "
                    + " WHERE "
                    + "   Email = ? ";

            statement = connection.prepareStatement(sql);

            statement.setInt(1, '0');
            statement.setString(2, MailUtente);

            statement.executeUpdate();

            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isIscritto(String MailUtente){
        PreparedStatement statement;
        try {
            String sqlAvvisi
                    = " SELECT Iscritto_Newsletter "
                    + " FROM utente NATURAL JOIN utente_registrato "
                    + " WHERE Email = ? AND Iscritto_Newsletter == 1";

            statement = connection.prepareStatement(sqlAvvisi);

            statement.setString(1, MailUtente);
            ResultSet resultSet = statement.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();
            return exist;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<UtenteRegistrato> getUtentiNewsletter(){
        List<UtenteRegistrato> MatricolaUtenti = new ArrayList<>();

        PreparedStatement statement;
        try {
            String sql =
                    " SELECT * " +
                    " FROM utente_registrato" +
                    " WHERE Iscritto_Newsletter = 1 ";

            statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                MatricolaUtenti.add(readUtente(resultSet));
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return MatricolaUtenti;
    }

    @Override
    public String getLastMatricola(){
        PreparedStatement statement;
        try {
            String sql
                    =   " SELECT Matricola " +
                        " FROM utente_registrato" +
                        " WHERE Matricola >= ALL(SELECT Matricola " +
                                                "FROM utente_registrato)";

            statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getString("Matricola");
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    private void addContatto(String MatricolaContatto){
        List<String> UtentiRegistrati = new ArrayList<>();
        UtentiRegistrati.addAll(getUtentiArruolati());

        PreparedStatement statement;

        for(int k=0; k<UtentiRegistrati.size(); k++){
            try {
                String Matricola_utente = UtentiRegistrati.get(k);

                String sql
                        = " SELECT Matricola_contatto "
                        + " FROM rubrica "
                        + " WHERE  Matricola_contatto = ? AND"
                        + "        Matricola_utente = ?";

                statement = connection.prepareStatement(sql);
                statement.setString(1, MatricolaContatto);
                statement.setString(2, Matricola_utente);

                ResultSet resultSet = statement.executeQuery();

                boolean exist;
                exist = resultSet.next();
                resultSet.close();
                /* .next restituisce false se la select restituisce dei dati */
                /* si ha quindi che se restituisce true, allora si hanno più utenti con stessa chiave */

                if (exist) {
                    throw new RuntimeException("UtenteRegistratoDAOmySQL.create: Tentativo di inserimento di un contatto già esistente.");
                }
                if(!Matricola_utente.equals(MatricolaContatto)) {
                    String sqlUtente =
                            " INSERT INTO rubrica " +
                                    "     ( Matricola_utente,  " +
                                    "       Matricola_contatto " +
                                    "       )            " +
                                    "VALUES (?, ?)";
                    statement = connection.prepareStatement(sqlUtente);

                    statement.setString(1, Matricola_utente);
                    statement.setString(2, MatricolaContatto);

                    statement.executeUpdate();

                    String sqlContatto =
                            " INSERT INTO rubrica " +
                                    "     ( Matricola_utente,  " +
                                    "       Matricola_contatto " +
                                    "       )            " +
                                    "VALUES (?, ?)";
                    statement = connection.prepareStatement(sqlContatto);

                    statement.setString(1, MatricolaContatto);
                    statement.setString(2, Matricola_utente);

                    statement.executeUpdate();
                    statement.close();
                }
            }
            catch(SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private List<String> getUtentiArruolati(){
        List<String> MatricolaUtenti = new ArrayList<>();

        PreparedStatement statement;
        try {
            String sql
                =   " SELECT Matricola " +
                    " FROM utente_registrato" +
                    " WHERE Ruolo <> 'NULL' ";

            statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                MatricolaUtenti.add(resultSet.getString("Matricola"));
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return MatricolaUtenti;

    }

    @Override
    public List<String> ElencoIniziali(UtenteRegistrato user){
        PreparedStatement statement;
        List<String> listaIniziali = new ArrayList<>();
        try{
            String sql =
                    " SELECT DISTINCT UPPER(LEFT(utente.Cognome, 1)) AS iniziale " +
                    " FROM rubrica JOIN utente_registrato ON Matricola_contatto = Matricola " +
                    " NATURAL JOIN utente " +
                    " WHERE Matricola_utente = ? " +
                    " ORDER BY iniziale ASC ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getMatricola());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                listaIniziali.add(resultSet.getString("iniziale"));
            }

            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return listaIniziali;
    }

    @Override
    public List<UtenteRegistrato> cercaIniziale(String Matricola, String Iniziale){
        List<UtenteRegistrato> contatti = new ArrayList<>();
        PreparedStatement statement;
        try{
            String sql =
                      " SELECT *  "
                    + " FROM rubrica JOIN utente_registrato ON Matricola_contatto = Matricola "
                    + " NATURAL JOIN utente "
                    + " WHERE Matricola_utente = ? ";

            if((Iniziale == null) || !(Iniziale.equals("*"))){
                sql += " AND UPPER(LEFT(utente.Cognome, 1)) = ? ";
            }

            sql += " ORDER BY utente.Nome, utente.Cognome";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Matricola);
            if(Iniziale == null || !(Iniziale.equals("*"))) statement.setString(2, Iniziale);

            ResultSet resultSet = statement.executeQuery();

            int i=0;
            while (resultSet.next()) {
                contatti.add(readUtente(resultSet));
                i++;
            }

            resultSet.close();
            statement.close();


        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return contatti;
    }

    @Override
    public Badge getBadge(String Matricola){
        Badge badge = new Badge();
        PreparedStatement statement;
        try {
            String sql =
                    " SELECT * " +
                    " FROM badge " +
                    " WHERE Matricola = ? ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Matricola);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            badge.setId(resultSet.getString("Id"));
            badge.setOreLavorative(resultSet.getLong("Ore_di_lavoro"));
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return badge;
    }

    private void createBadge(String Matricola){
        PreparedStatement statement;
        try {
            String sql =
                    " INSERT INTO badge(Matricola, Id, Ore_di_lavoro) " +
                    " VALUES(?, ?, ?) ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Matricola);

            Random random = new Random();
            Integer IdRandom = random.nextInt(900) + 100;   // random.nextInt(900) genera un numero tra 0 e 899, poi si aggiunge 100
            String IdBadge = IdRandom.toString();

            statement.setString(2, IdBadge);
            statement.setLong(3, 0);

            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }
    @Override
    public List<UtenteRegistrato> getUtenti(){
        List<UtenteRegistrato> MatricolaUtenti = new ArrayList<>();

        PreparedStatement statement;
        try {
            String sql =
                    " SELECT * " +
                    " FROM utente_registrato NATURAL JOIN utente ";

            statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                MatricolaUtenti.add(readUtente(resultSet));
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return MatricolaUtenti;
    }

    @Override
    public List<UtenteRegistrato> getUtentiRuolo(String Ruolo){
        List<UtenteRegistrato> MatricolaUtenti = new ArrayList<>();

        PreparedStatement statement;
        try {
            String sql =
                    " SELECT * " +
                    " FROM utente_registrato NATURAL JOIN utente " +
                    " WHERE Ruolo = ? ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, Ruolo);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                MatricolaUtenti.add(readUtente(resultSet));
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return MatricolaUtenti;
    }

    private UtenteRegistrato contattiRead(ResultSet resultSet){
        UtenteRegistrato contatto = new UtenteRegistrato();
        try{
            contatto.setNome(resultSet.getString("Nome"));
        }
        catch(SQLException sql){ }
        try{
            contatto.setCognome(resultSet.getString("Cognome"));
        }
        catch (SQLException sql){ }
        try{
            contatto.setCognome(resultSet.getString("Telefono"));
        }
        catch (SQLException sql){ }
        try{
            contatto.setCognome(resultSet.getString("Email"));
        }
        catch (SQLException sql){ }
        try{
            contatto.setCognome(resultSet.getString("Matricola"));
        }
        catch (SQLException sql){ }
        try{
            contatto.setLocazioneServizio(resultSet.getString("Locazione_servizio"));
        }
        catch (SQLException sql){ }


        return contatto;
    }

    private UtenteRegistrato readUtente(ResultSet resultSet){
        UtenteRegistrato user = new UtenteRegistrato();

        /* Utente */
        try{
            user.setNome(resultSet.getString("Nome"));
        }
        catch (SQLException sql){ }
        try{
            user.setCognome(resultSet.getString("Cognome"));
        }
        catch (SQLException sql){ }
        try{
            user.setCF(resultSet.getString("CF"));
        }
        catch (SQLException sql){ }
        try{
            user.setTelefono(resultSet.getString("Telefono"));
        }
        catch (SQLException sql){ }
        try{
            user.setMail(resultSet.getString("Email"));
        }
        catch (SQLException sql){ }
        try{
            user.setPassword(resultSet.getString("Password"));
        }
        catch (SQLException sql){ }
        try{
            user.setSesso(resultSet.getString("sesso"));
        }
        catch (SQLException sql){ }
        try{
            user.setDataNascitaString(resultSet.getDate("DataNascita").toString());
        }
        catch (SQLException sql){ sql.printStackTrace(); }

        /* Utente registrato */
        try{
            user.setMatricola(resultSet.getString("Matricola"));
        }
        catch (SQLException sql){ }
        try{
            user.setIBAN(resultSet.getString("IBAN"));
        }
        catch (SQLException sql){ }
        try{
            user.setRuolo(resultSet.getString("Ruolo"));
        }
        catch (SQLException sql){ }
        try{
            user.setFoto(resultSet.getBlob("Foto"));
        }
        catch (SQLException sql){ }
        try{
            user.setDocumento(resultSet.getBlob("Documenti"));

        }
        catch (SQLException sql){ }
        try{
            user.setIndirizzo(resultSet.getString("Indirizzo"));
        }
        catch (SQLException sql){ }
        try{
            user.setLocazioneServizio(resultSet.getString("Locazione_servizio"));
        }
        catch (SQLException sql){ }

        try{
            user.setStipendio(resultSet.getLong("Stipendio"));
        }
        catch (SQLException sql){ }
        try{
            user.setIscrittoNewsletterInt(resultSet.getInt("Iscritto_Newsletter"));
        }
        catch (SQLException sql){ }


        return user;
    }
}