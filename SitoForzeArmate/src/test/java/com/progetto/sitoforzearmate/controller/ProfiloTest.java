package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Bando.BandoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.PastoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.PostoLettoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.AmministratoreDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Base.Pasto;
import com.progetto.sitoforzearmate.model.mo.Base.PostoLetto;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.Badge;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;

class ProfiloTest {

    @Mock
    private MockedStatic<DAOFactory> dao_factory_mock;
    @Mock
    private MockedStatic<Configuration> configuration_mock;
    @Mock
    private DAOFactory db_mock;
    @BeforeEach
    void setUp() {
        dao_factory_mock = Mockito.mockStatic(DAOFactory.class);
        configuration_mock = Mockito.mockStatic(Configuration.class);
        db_mock = Mockito.mock(DAOFactory.class);
    }

    @AfterEach
    void tearDown() {
        configuration_mock.close();
        dao_factory_mock.close();
    }

    @ParameterizedTest
    @CsvSource({
            "'','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',Generale",
            "'',nonlosoquanticar#1234567890#ciao1,''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1,''"
    })
    void view(
            String cookieUser,
            String cookieAdmin,
            String Ruolo
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Profilo/ProfiloCSS");

        UtenteRegistratoDAOmySQL userDAO = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        AmministratoreDAOmySQL adminDAO = Mockito.mock(AmministratoreDAOmySQL.class);
        UtenteRegistrato user = new UtenteRegistrato();
        Amministratore admin = new Amministratore();
        Badge badge = new Badge();
        user.setRuolo(Ruolo);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(userDAO);
        Mockito.when(userDAO.findByMail(anyString())).thenReturn(user);
        Mockito.when(userDAO.getBadge(anyString())).thenReturn(badge);

        Mockito.when(db_mock.getAmministratoreDAO()).thenReturn(adminDAO);
        Mockito.when(adminDAO.findByMail(anyString())).thenReturn(admin);

        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().view(null, cookieUser, cookieAdmin));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().view(null, cookieUser, cookieAdmin));
        }
        else {
            ModelAndView page = new Profilo().view(null, cookieUser, cookieAdmin);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,''",
            "'',nonlosoquanticar#1234567890#ciao1",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1"
    })
    void modificaProfiloView(
            String cookieUser,
            String cookieAdmin
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Profilo/modificaProfiloCSS");

        UtenteRegistratoDAOmySQL userDAO = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        AmministratoreDAOmySQL adminDAO = Mockito.mock(AmministratoreDAOmySQL.class);
        UtenteRegistrato user = new UtenteRegistrato();
        Amministratore admin = new Amministratore();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(userDAO);
        Mockito.when(userDAO.findByMatricola(anyString())).thenReturn(user);

        Mockito.when(db_mock.getAmministratoreDAO()).thenReturn(adminDAO);
        Mockito.when(adminDAO.findById(anyString())).thenReturn(admin);

        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().modificaProfiloView(null, cookieUser, cookieAdmin));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().modificaProfiloView(null, cookieUser, cookieAdmin));
        }
        else {
            ModelAndView page = new Profilo().modificaProfiloView(null, cookieUser, cookieAdmin);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','','','','','','','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','','','','','','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',sara.tullini@edu.unife.it,'','','','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','',passwordTest,'','','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','','',1234567890,'','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','','','',000000000000,'',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','','','','',via delle rose 1,''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','','','','','','true'",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',sara.tullini@edu.unife.it,passwordTest,1234567890,000000000000,via delle rose 1,true",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',sara.tullini@edu.unife.it,passwordTest,1234567890,000000000000,via delle rose 1,false",

            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',sara.tullini@edu.unife.it,passwordTest,1234567890,'',via delle rose 1,true",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',sara.tullini@edu.unife.it,passwordTest,1234567890,000000000000,'',true",

            "'',nonlosoquanticar#1234567890#ciao1,'','','','','','',''",
            "'',nonlosoquanticar#1234567890#ciao1,mailTest,'','','','',''",
            "'',nonlosoquanticar#1234567890#ciao1,'',passwordTest,'','','',''",
            "'',nonlosoquanticar#1234567890#ciao1,'','',1234567890,'','',''",
            "'',nonlosoquanticar#1234567890#ciao1,mailTest,passwordTest,1234567890,'','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1,'','','','','',''",
    })
    void modificaProfilo(
            String cookieUser,
            String cookieAdmin,
            String mail,
            String password,
            String telefono,
            String IBAN,
            String indirizzo,
            String newsletter
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Profilo/reload");

        Part foto = Mockito.mock(Part.class);
        Part documenti = Mockito.mock(Part.class);

        Boolean newsletterBool = Boolean.parseBoolean(newsletter);

        UtenteRegistratoDAOmySQL userDAO = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        AmministratoreDAOmySQL adminDAO = Mockito.mock(AmministratoreDAOmySQL.class);

        UtenteRegistrato userTest = new UtenteRegistrato();
        Amministratore adminTest = new Amministratore();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getAmministratoreDAO()).thenReturn(adminDAO);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(userDAO);

        if(!cookieUser.equals("")) {
            Mockito.when(userDAO.findByMail(anyString())).thenReturn(userTest);
            Mockito.when(adminDAO.findByMail(anyString())).thenReturn(null);
        }
        else if(!cookieAdmin.equals("")){
            Mockito.when(adminDAO.findByMail(anyString())).thenReturn(adminTest);
            Mockito.when(userDAO.findByMail(anyString())).thenReturn(null);
        }

        try {
            InputStream mockIO = Mockito.mock(InputStream.class);
            Mockito.when(foto.getSize()).thenReturn(1L);
            Mockito.when(foto.getInputStream()).thenReturn(mockIO);

            Mockito.when(documenti.getSize()).thenReturn(1L);
            Mockito.when(documenti.getInputStream()).thenReturn(mockIO);

            Mockito.when(mockIO.readAllBytes()).thenReturn(new byte[1]);
        }
        catch(Exception e){
            e.printStackTrace();
        }


        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().modificaProfilo(null, cookieUser, cookieAdmin, mail, password, telefono, IBAN, foto, documenti, indirizzo, newsletterBool));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().modificaProfilo(null, cookieUser, cookieAdmin, mail, password, telefono, IBAN, foto, documenti, indirizzo, newsletterBool));
        }
        else if(mail.equals("") || password.equals("") || telefono.equals("")){
            assertThrows(RuntimeException.class, () -> new Profilo().modificaProfilo(null, cookieUser, cookieAdmin, mail, password, telefono, IBAN, foto, documenti, indirizzo, newsletterBool));
        }
        else {
            if (cookieAdmin.equals("") && (IBAN.equals("") || indirizzo.equals("")))
                assertThrows(RuntimeException.class, () -> new Profilo().modificaProfilo(null, cookieUser, cookieAdmin, mail, password, telefono, IBAN, foto, documenti, indirizzo, newsletterBool));
            else {
                ModelAndView page = new Profilo().modificaProfilo(null, cookieUser, cookieAdmin, mail, password, telefono, IBAN, foto, documenti, indirizzo, newsletterBool);
                assertEquals(pageExpected.getViewName(), page.getViewName());

            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&"
    })
    void prenotazioniView(
            String cookieUser
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Profilo/prenotazioniCSS");

        List<Pasto> pasti = new ArrayList<>();
        Pasto pastoTest = new Pasto();
        pasti.add(pastoTest);

        List<PostoLetto> alloggi = new ArrayList<>();
        PostoLetto alloggioTest = new PostoLetto();
        alloggi.add(alloggioTest);

        PastoDAOmySQL pastoDao_mock = Mockito.mock(PastoDAOmySQL.class);
        PostoLettoDAOmySQL postoLettoDao_mock = Mockito.mock(PostoLettoDAOmySQL.class);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getPastoDAO()).thenReturn(pastoDao_mock);
        Mockito.when(db_mock.getPostoLettoDAO()).thenReturn(postoLettoDao_mock);

        Mockito.when(pastoDao_mock.findByMatricola(anyString())).thenReturn(pasti);
        Mockito.when(postoLettoDao_mock.findByMatricola(anyString())).thenReturn(alloggi);

        if( cookieUser.equals(""))
            assertThrows(RuntimeException.class, () -> new Profilo().prenotazioniView(null, cookieUser));
        else {
            ModelAndView page = new Profilo().prenotazioniView(null, cookieUser);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',0000000003,''",
            "'',nonlosoquanticar#1234567890#ciao1,'',''",
            "'',nonlosoquanticar#1234567890#ciao1,'',0000000003",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, nonlosoquanticar#1234567890#ciao1, '', ''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, '', 0000000003, tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&",
            "'', nonlosoquanticar#1234567890#ciao1, 0000000003,tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&"

    })
    void viewDoc(
            String cookieUser,
            String cookieAdmin,
            String bandoId,
            String matricolaSelected
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Profilo/viewDoc");

        UtenteRegistratoDAOmySQL userDAO = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistrato userTest = new UtenteRegistrato();

        BandoDAOmySQL bandoDao = Mockito.mock(BandoDAOmySQL.class);
        Bando bandoTest = new Bando();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(userDAO);
        Mockito.when(userDAO.findByMatricola(anyString())).thenReturn(userTest);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bandoDao);
        Mockito.when(bandoDao.findbyId(anyString())).thenReturn(bandoTest);

        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().viewDoc(null, cookieUser, cookieAdmin, bandoId, matricolaSelected));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Profilo().viewDoc(null, cookieUser, cookieAdmin, bandoId, matricolaSelected));
        }
        else if(bandoId.equals("") || matricolaSelected.equals("")){
            assertThrows(RuntimeException.class, () -> new Profilo().viewDoc(null, cookieUser, cookieAdmin, bandoId, matricolaSelected));
        }
        else {
            ModelAndView page = new Profilo().viewDoc(null, cookieUser, cookieAdmin, bandoId, matricolaSelected);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
}