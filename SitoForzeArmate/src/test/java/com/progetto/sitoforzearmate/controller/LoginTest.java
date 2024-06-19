package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.Data;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.BaseDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.AmministratoreDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Base.Base;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class LoginTest {
    @Mock
    private MockedStatic<DAOFactory> dao_factory_mock;
    @Mock
    private MockedStatic<UtenteRegistratoDAOcookie> decode_mock;
    @Mock
    private MockedStatic<Configuration> configuration_mock;
    @Mock
    private DAOFactory db_mock;
    @Mock
    private DAOFactory session_mock;

    @BeforeEach
    void setUp() {
        dao_factory_mock = Mockito.mockStatic(DAOFactory.class);
        configuration_mock = Mockito.mockStatic(Configuration.class);
        db_mock = Mockito.mock(DAOFactory.class);
        session_mock = Mockito.mock(DAOFactory.class);
        decode_mock = Mockito.mockStatic(UtenteRegistratoDAOcookie.class);
    }

    @AfterEach
    void tearDown() {
        configuration_mock.close();
        dao_factory_mock.close();
        decode_mock.close();
    }

    @Test
    void view() {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("LoginCSS");

        ModelAndView pageActual = new Login().view();
        assertEquals(pageExpected.getViewName(), pageActual.getViewName());
    }

    @ParameterizedTest
    @CsvSource({
            "'','','',True",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','',True",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,sara.tullini@edu.unife.it,ciao1,True",
            "'',sara.tullini@edu.unife.it,Password,True",
            "'',sara.tullini@edu.unife.it,Password,False"
    })
    void login(
            String cookieUser,
            String email,
            String password,
            String casoIf
    ) {
        Boolean caso = Boolean.parseBoolean(casoIf);
        ModelAndView pageExpected = new ModelAndView();

        UtenteRegistratoDAOcookie utente_session_dao = Mockito.mock(UtenteRegistratoDAOcookie.class);

        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistrato utenteTest = Mockito.mock(UtenteRegistrato.class);
        UtenteRegistrato loggedUser = null;


        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        // Gestione sessionUserDAO
        Mockito.doNothing().when(session_mock).beginTransaction();
        Mockito.doNothing().when(session_mock).commitTransaction();
        Mockito.doNothing().when(session_mock).rollbackTransaction();
        Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(utente_session_dao);

        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);

        /* Setting valore ti ritorno adminTest */
        Mockito.when(utenteTest.getPassword()).thenReturn("Password");
        Mockito.when(utenteTest.getNome()).thenReturn("Nome");
        Mockito.when(utenteTest.getCognome()).thenReturn("Cognome");
        Mockito.when(utenteTest.getCF()).thenReturn("CF");
        Mockito.when(utenteTest.getMail()).thenReturn("Mail");
        Mockito.when(utenteTest.getTelefono()).thenReturn("Telefono");
        Mockito.when(utenteTest.getSesso()).thenReturn("Sesso");
        Mockito.when(utenteTest.getDataNascita()).thenReturn(new Data(2024, 12, 12));
        Mockito.when(utenteTest.getMatricola()).thenReturn("Matricola");
        Mockito.when(utenteTest.getIBAN()).thenReturn("IBAN");
        Mockito.when(utenteTest.getRuolo()).thenReturn("Graduato");
        Mockito.when(utenteTest.getFotoByte()).thenReturn(new byte[0]);
        Mockito.when(utenteTest.getDocumentoByte()).thenReturn(new byte[0]);
        Mockito.when(utenteTest.getIndirizzo()).thenReturn("Indirizzo");
        Mockito.when(utenteTest.getLocazioneServizio()).thenReturn("LocazioneServizio");
        Mockito.when(utenteTest.getIscrittoNewsletter()).thenReturn(true);

        Mockito.when(utente_dao.recuperaBandi(anyString())).thenReturn(new ArrayList<>());

        Mockito.when(utente_dao.findByMail(anyString())).thenReturn(utenteTest);

        if(caso){
            Mockito.doNothing().when(utente_session_dao).delete(any());
            Mockito.when(utenteTest.getPassword()).thenReturn("password a caso");
        }
        else{
            utenteTest.setPassword(password);
            loggedUser = new UtenteRegistrato();
            Mockito.when(utente_session_dao.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), Mockito.any(), anyString(),
                    anyString(), anyString(), Mockito.any(), Mockito.any(), anyString(), anyString(), anyBoolean(), Mockito.anyList())).thenReturn(loggedUser);
        }

        if(loggedUser != null) pageExpected.setViewName("index");
        else pageExpected.setViewName("LoginCSS");


        if(email.equals("") || password.equals("") || !cookieUser.equals("")){
            assertThrows(RuntimeException.class, () -> new Login().login(null, cookieUser, email, password));
        }
        else{
            ModelAndView pageActual = new Login().login(null, cookieUser, email, password);
            assertEquals(pageExpected.getViewName(), pageActual.getViewName());
        }


    }

    @ParameterizedTest
    @ValueSource(strings = {"False", "True"})
    void logout(String Eccezione) {

        Boolean eccezione = Boolean.parseBoolean(Eccezione);

        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("index");
        pageExpected.addObject("loggedUser", null);
        pageExpected.addObject("loggedOn", false);
        pageExpected.addObject("loggedAdmin", null);
        pageExpected.addObject("loggedAdminOn", false);

        UtenteRegistratoDAOcookie cookieUserMock = Mockito.mock(UtenteRegistratoDAOcookie.class);
        AmministratoreDAOcookie cookieAdminMock = Mockito.mock(AmministratoreDAOcookie.class);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        if(eccezione) {
            Mockito.doThrow(new NullPointerException()).when(session_mock).beginTransaction();
            assertThrows(RuntimeException.class, () -> new Login().logout(null));
        }
        else {
            Mockito.doNothing().when(session_mock).beginTransaction();
            Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(cookieUserMock);
            Mockito.when(session_mock.getAmministratoreDAO()).thenReturn(cookieAdminMock);
            Mockito.doNothing().when(cookieUserMock).delete(any());
            Mockito.doNothing().when(cookieAdminMock).delete(any());
            Mockito.doNothing().when(session_mock).commitTransaction();

            ModelAndView pageActual = new Login().logout(null);

            assertEquals(pageExpected.getViewName(), pageActual.getViewName());
            assertEquals(pageExpected.getModel(), pageActual.getModel());
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"False", "True"})
    void viewRegistrazione(
            String Eccezione
    )
    {
        Boolean eccezione = Boolean.parseBoolean(Eccezione);
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("RegistrazioneCSS");
        BaseDAOmySQL basi_dao = Mockito.mock(BaseDAOmySQL.class);
        List<Base> basi = new ArrayList<>();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        if(eccezione) {
            Mockito.doThrow(new NullPointerException()).when(db_mock).getBaseDAO();
            assertThrows(RuntimeException.class, () -> new Login().viewRegistrazione(null));
        }
        else {
            Mockito.when(db_mock.getBaseDAO()).thenReturn(basi_dao);
            Mockito.when(basi_dao.stampaBasi()).thenReturn(basi);

            ModelAndView page = new Login().viewRegistrazione(null);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource ({
            "'','','','','','','','','','','','','','','','',True",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','','','','','','','','','','','',True",
            "'',Nome,Cognome,CF,Mail,Telefono,Password,F,2024-12-12,IBAN,Ufficiale,via di test,'',True",
            "'',Nome,Cognome,CF,Mail,Telefono,Password,F,2024-12-12,IBAN,Ufficiale,via di test,LocazioneServizio,True"
            // "'',Nome,Cognome,CF,Mail,Telefono,Password,F,2024-12-12,IBAN,Ufficiale,via di test,LocazioneServizio,False"
    })

    void Registrazione(
            String cookieUser,
            String nome,
            String cognome,
            String cf,
            String mail,
            String telefono,
            String password,
            String sesso,
            String dataNascita,
            String IBAN,
            String ruolo,
            String indirizzo,
            String locazioneServizio,
            String condizione
    ) {
        ModelAndView pageExpected = new ModelAndView();

        UtenteRegistrato loggedUser = new UtenteRegistrato();
        Part foto = Mockito.mock(Part.class);
        Part documenti = Mockito.mock(Part.class);

        UtenteRegistrato utenteMock = Mockito.mock(UtenteRegistrato.class);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        Mockito.doNothing().when(session_mock).beginTransaction();
        Mockito.doNothing().when(session_mock).commitTransaction();
        Mockito.doNothing().when(session_mock).rollbackTransaction();

        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistratoDAOcookie utente_session_dao = Mockito.mock(UtenteRegistratoDAOcookie.class);

        Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(utente_session_dao);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);

        Mockito.when(utente_dao.getLastMatricola()).thenReturn("0");

        Mockito.when(utenteMock.getMatricola()).thenReturn("Matricola");
        Mockito.when(utenteMock.getNome()).thenReturn(nome);
        Mockito.when(utenteMock.getCognome()).thenReturn(cognome);
        Mockito.when(utenteMock.getCF()).thenReturn(cf);
        Mockito.when(utenteMock.getMail()).thenReturn(mail);
        Mockito.when(utenteMock.getTelefono()).thenReturn(telefono);
        Mockito.when(utenteMock.getPassword()).thenReturn(password);
        Mockito.when(utenteMock.getSesso()).thenReturn(sesso);
        Mockito.when(utenteMock.getDataNascita()).thenReturn(new Data(2024, 12, 12));
        Mockito.when(utenteMock.getIBAN()).thenReturn(IBAN);
        Mockito.when(utenteMock.getRuolo()).thenReturn(ruolo);
        Mockito.when(utenteMock.getIndirizzo()).thenReturn(indirizzo);
        Mockito.when(utenteMock.getLocazioneServizio()).thenReturn(locazioneServizio);
        Mockito.when(utenteMock.getIscrittoNewsletter()).thenReturn(Boolean.parseBoolean(condizione));
        Mockito.when(utenteMock.getFotoByte()).thenReturn(new byte[0]);
        Mockito.when(utenteMock.getDocumentoByte()).thenReturn(new byte[0]);


        if(Boolean.parseBoolean(condizione)){ // User != null
            Mockito.when(utente_dao.findByMail(anyString())).thenReturn(new UtenteRegistrato());
            pageExpected.setViewName("LoginCSS");
        }
        else{   // User == null
            Mockito.when(utente_dao.findByMail(anyString())).thenReturn(null);
            pageExpected.setViewName("index");

            try{
                InputStream inputMock = Mockito.mock(InputStream.class);
                Mockito.when(foto.getInputStream()).thenReturn(inputMock);
                Mockito.when(documenti.getInputStream()).thenReturn(inputMock);
                Mockito.when(inputMock.readAllBytes()).thenReturn(new byte[0]);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            Mockito.when(utente_dao.recuperaBandi(anyString())).thenReturn(new ArrayList<>());
            Mockito.when(utente_dao.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), any(Data.class), anyString(), anyString(), anyString(), any(byte[].class), any(byte[].class), anyString(),
                    anyString(), anyBoolean(), Mockito.anyList())
            ).thenReturn(utenteMock);

            Mockito.when(utente_session_dao.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), Mockito.any(), anyString(),
                    anyString(), anyString(), Mockito.any(), Mockito.any(), anyString(), anyString(), anyBoolean(), Mockito.anyList())).thenReturn(loggedUser);

        }

        if(!cookieUser.equals("") || nome.equals("") || cognome.equals("") || cf.equals("") || mail.equals("") || telefono.equals("") || password.equals("") || sesso.equals("") || dataNascita.equals("") || IBAN.equals("") || ruolo.equals("") || indirizzo.equals("") || locazioneServizio.equals("") || foto == null || documenti == null)
            assertThrows(RuntimeException.class, () -> new Login().Registrazione(null, cookieUser, nome, cognome, cf, telefono, mail, password, sesso, dataNascita, IBAN, ruolo, foto, documenti, indirizzo, locazioneServizio, Boolean.parseBoolean(condizione)));
        else {
            ModelAndView pageActual = new Login().Registrazione(null, cookieUser, nome, cognome, cf, telefono, mail, password, sesso, dataNascita, IBAN, ruolo, foto, documenti, indirizzo, locazioneServizio, Boolean.parseBoolean(condizione));
            assertEquals(pageExpected.getViewName(), pageActual.getViewName());
        }
    }

    @Test
    void viewAmministratore() {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("LoginAmministratoreCSS");

        ModelAndView pageActual = new Login().viewAmministratore(null);
        assertEquals(pageExpected.getViewName(), pageActual.getViewName());
    }

    @ParameterizedTest
    @CsvSource({
            "'','','',True",
            "'','',nonlosoquanticar#1234567890#ciao1,True",
            "1234567890,'',nonlosoquanticar#1234567890#ciao1,True",
            "1234567890,Password,'',True",
            "1234567890,Password,'',False"
    })
    void loginAmministratore(
            String idAdmin,
            String password,
            String cookieAdmin,
            String passwordEquals
    ) {
        Boolean casoPassword = Boolean.parseBoolean(passwordEquals);
        ModelAndView pageExpected = new ModelAndView();

        AmministratoreDAOmySQL adminDao = Mockito.mock(AmministratoreDAOmySQL.class);
        AmministratoreDAOcookie sessionAdmin = Mockito.mock(AmministratoreDAOcookie.class);

        Amministratore adminTest = Mockito.mock(Amministratore.class);
        Amministratore loggedAdmin = null;

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        Mockito.doNothing().when(session_mock).beginTransaction();
        Mockito.doNothing().when(session_mock).commitTransaction();
        Mockito.doNothing().when(session_mock).rollbackTransaction();

        Mockito.when(session_mock.getAmministratoreDAO()).thenReturn(sessionAdmin);

        Mockito.when(db_mock.getAmministratoreDAO()).thenReturn(adminDao);

        /* Setting valore ti ritorno adminTest */
        Mockito.when(adminTest.getPassword()).thenReturn("Password");
        Mockito.when(adminTest.getNome()).thenReturn("Nome");
        Mockito.when(adminTest.getCognome()).thenReturn("Cognome");
        Mockito.when(adminTest.getCF()).thenReturn("CF");
        Mockito.when(adminTest.getMail()).thenReturn("Mail");
        Mockito.when(adminTest.getTelefono()).thenReturn("Telefono");
        Mockito.when(adminTest.getSesso()).thenReturn("Sesso");
        Mockito.when(adminTest.getDataNascita()).thenReturn(new Data(2024, 12, 12));
        Mockito.when(adminTest.getIdAdministrator()).thenReturn("IdAdmin");

        if(casoPassword){
            Mockito.doNothing().when(sessionAdmin).delete(any());
            Mockito.when(adminTest.getPassword()).thenReturn("password a caso");
        }
        else{
            adminTest.setPassword(password);
            loggedAdmin = new Amministratore();
            Mockito.when(sessionAdmin.create(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(loggedAdmin);
        }

        Mockito.when(adminDao.findById(anyString())).thenReturn(adminTest);

        if(loggedAdmin != null) pageExpected.setViewName("index");
        else pageExpected.setViewName("LoginAmministratoreCSS");


        if(idAdmin.equals("") || password.equals("") || !cookieAdmin.equals("")){
            assertThrows(RuntimeException.class, () -> new Login().loginAmministratore(null, idAdmin, password, cookieAdmin));
        }
        else{
            ModelAndView pageActual = new Login().loginAmministratore(null, idAdmin, password, cookieAdmin);
            assertEquals(pageExpected.getViewName(), pageActual.getViewName());
        }
    }
}