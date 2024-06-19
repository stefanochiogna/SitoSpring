package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Bando.BandoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.BaseDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.AvvisiDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.AmministratoreDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.model.mo.Base.Base;
import com.progetto.sitoforzearmate.model.mo.Notizie.Avviso;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class CalendarioTest {

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

    @ParameterizedTest
    @CsvSource({
            "'',''",
            "nonlosoquanticar#1234567890#ciao1,''",
            "'',tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&",
            "nonlosoquanticar#1234567890#ciao1,tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&"
    })
    void view(
            String cookieUser,
            String cookieAdmin
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/CalendarioCSS");
        BandoDAOmySQL bandi_dao = Mockito.mock(BandoDAOmySQL.class);
        ArrayList<Bando> bandi = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bandi_dao);
        Mockito.when(bandi_dao.show()).thenReturn(bandi);
        Mockito.when(bandi_dao.getDate()).thenReturn(dateList);

        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Calendario().view(null, cookieAdmin, cookieUser));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Calendario().view(null, cookieAdmin, cookieUser));
        }
        else {
            ModelAndView page = new Calendario().view(null, cookieAdmin, cookieUser);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
    @ParameterizedTest
    @CsvSource({
            "'','',''",
            "nonlosoquanticar#1234567890#ciao1,'',''",
            "nonlosoquanticar#1234567890#ciao1,'',0000000001",
            "'',tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,0000000001",
            "nonlosoquanticar#1234567890#ciao1,tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,0000000001"
    })
    void viewBando(
            String cookieAdmin,
            String cookieUser,
            String bandoId
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/viewBandoCSS");
        BandoDAOmySQL bandi_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();
        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistrato utente = new UtenteRegistrato();
        String matricola = "0000000001";
        bando.AddEsito("Accettato", matricola);
        boolean maxIscritti = false;

        utente.setMatricola(matricola);
        utente.AddIdBando(bandoId);

        decode_mock.when(() -> UtenteRegistratoDAOcookie.decode(cookieUser)).thenReturn(utente);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bandi_dao);
        Mockito.when(bandi_dao.findbyId(bandoId)).thenReturn(bando);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);
        Mockito.when(utente_dao.findByMatricola(anyString())).thenReturn(utente);
        Mockito.when(utente_dao.maxIscrittiRaggiunto(bando)).thenReturn(maxIscritti);

        if( cookieAdmin.equals("") && cookieUser.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().viewBando(null, cookieAdmin, cookieUser, bandoId));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().viewBando(null, cookieAdmin, cookieUser, bandoId));
        }
        else if( bandoId.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().viewBando(null, cookieAdmin, cookieUser, bandoId));
        }
        else {
            ModelAndView page = new Calendario().viewBando(null, cookieAdmin, cookieUser, bandoId);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'',''",
            "nonlosoquanticar#1234567890#ciao1,''",
            "nonlosoquanticar#1234567890#ciao1,0000000001",
            "'',0000000001"
    })
    void deleteBando(
            String cookieAdmin,
            String bandoId
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/reload");
        BandoDAOmySQL bandi_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();
        Path riferimentoTesto = Mockito.mock(Path.class);
        bando.setRiferimentoTesto(riferimentoTesto);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bandi_dao);
        Mockito.when(bandi_dao.findbyId(bandoId)).thenReturn(bando);

        if( cookieAdmin.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().deleteBando(null, cookieAdmin, bandoId));
        }
        else if( bandoId.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().deleteBando(null, cookieAdmin, bandoId));
        }
        else {
            ModelAndView page = new Calendario().deleteBando(null, cookieAdmin, bandoId);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'',''",
            "nonlosoquanticar#1234567890#ciao1,''",
            "nonlosoquanticar#1234567890#ciao1,0000000001",
            "'',0000000001"

    })
    void modificaBandoView(
            String cookieAdmin,
            String bandoId
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/modificaBandoCSS");
        BandoDAOmySQL bandi_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();
        BaseDAOmySQL base_dao = Mockito.mock(BaseDAOmySQL.class);
        ArrayList<Base> basi = new ArrayList<>();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bandi_dao);
        Mockito.when(bandi_dao.findbyId(bandoId)).thenReturn(bando);
        Mockito.when(db_mock.getBaseDAO()).thenReturn(base_dao);
        Mockito.when(base_dao.stampaBasi()).thenReturn(basi);

        if( cookieAdmin.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().modificaBandoView(null, cookieAdmin, bandoId));
        }
        else if( bandoId.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().modificaBandoView(null, cookieAdmin, bandoId));
        }
        else {
            ModelAndView page = new Calendario().modificaBandoView(null, cookieAdmin, bandoId);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','','','','','','',''",
            "nonlosoquanticar#1234567890#ciao1,'',BandoProva,24,2024-12-01,2024-12-27,Pisa,Bando di prova per i test",
            "'',0000000001,BandoProva,24,2024-12-01,2024-12-27,Pisa,Bando di prova per i test",
            "nonlosoquanticar#1234567890#ciao1,0000000001,BandoProva,24,2024-12-01,2024-12-27,Pisa,''",
            "nonlosoquanticar#1234567890#ciao1,0000000001,BandoProva,24,2024-12-01,2024-12-27,Pisa,Bando di prova per i test"
    })
    void modificaBando(
            String cookieAdmin,
            String bandoId,
            String oggetto,
            String numMaxIscritti,
            String dataScadenza,
            String data,
            String locazione,
            String testo
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/reload");
        BandoDAOmySQL bandi_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bandi_dao);
        Mockito.when(bandi_dao.findbyId(bandoId)).thenReturn(bando);

        if( cookieAdmin.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().modificaBando(null, cookieAdmin, bandoId, oggetto, numMaxIscritti, dataScadenza, data, locazione, testo));
        }
        else if( bandoId.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().modificaBando(null, cookieAdmin, bandoId, oggetto, numMaxIscritti, dataScadenza, data, locazione, testo));
        }
        else if( oggetto.equals("") || numMaxIscritti.equals("") || dataScadenza.equals("") || data.equals("") || locazione.equals("") || testo.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().modificaBando(null, cookieAdmin, bandoId, oggetto, numMaxIscritti, dataScadenza, data, locazione, testo));
        }
        else {
            ModelAndView page = new Calendario().modificaBando(null, cookieAdmin, bandoId, oggetto, numMaxIscritti, dataScadenza, data, locazione, testo);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','','','','','',''",
            "nonlosoquanticar#1234567890#ciao1,'',BandoProva,24,2024-12-27,Pisa",
            "'',2024-12-01,BandoProva,24,2024-12-27,Pisa",
            "nonlosoquanticar#1234567890#ciao1,2024-12-01,BandoProva,24,2024-12-27,Pisa"
    })
    void inserisciBando(
            String cookieAdmin,
            String dataBando,
            String oggetto,
            String numMaxIscritti,
            String dataScadenza,
            String locazione
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/reload");
        Part insBando = Mockito.mock(Part.class);
        BandoDAOmySQL bando_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();
        BaseDAOmySQL basi_dao = Mockito.mock(BaseDAOmySQL.class);
        List<Base> basi = new ArrayList<>();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bando_dao);
        Mockito.when(bando_dao.create(any(), anyString(), anyString(), any(), anyString(), any(), any(), any())).thenReturn(bando);
        Mockito.when(bando_dao.getLastId()).thenReturn("0000000001");
        Mockito.when(db_mock.getBaseDAO()).thenReturn(basi_dao);
        Mockito.when(basi_dao.stampaBasi()).thenReturn(basi);

        configuration_mock.when(() -> Configuration.getDIRECTORY_FILE()).thenReturn("C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\Test\\");

        if( cookieAdmin.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().inserisciBando(null, cookieAdmin, dataBando, oggetto, numMaxIscritti, dataScadenza, locazione, insBando));
        }
        else if( dataBando.equals("") || oggetto.equals("") || numMaxIscritti.equals("") || dataScadenza.equals("") || locazione.equals("") || insBando == null ) {
            assertThrows(RuntimeException.class, () -> new Calendario().inserisciBando(null, cookieAdmin, dataBando, oggetto, numMaxIscritti, dataScadenza, locazione, insBando));
        }
        else {
            ModelAndView page = new Calendario().inserisciBando(null, cookieAdmin, dataBando, oggetto, numMaxIscritti, dataScadenza, locazione, insBando);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','',''",
            "'',0000000001,True",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,0000000001,True",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,0000000001,False",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,0000000001,Accettato",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',''"
    })
    void iscrizione(
        String cookieUser,
        String bandoId, 
        String iscritto
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/viewBandoCSS");
        BandoDAOmySQL bando_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();
        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistratoDAOcookie utente_cookie_dao = Mockito.mock(UtenteRegistratoDAOcookie.class);
        UtenteRegistrato utente = new UtenteRegistrato();
        boolean maxIscritti = false;
        utente.AddIdBando(bandoId);

        decode_mock.when(() -> UtenteRegistratoDAOcookie.decode(cookieUser)).thenReturn(utente);
        
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(utente_cookie_dao);
        Mockito.doNothing().when(session_mock).beginTransaction();
        Mockito.doNothing().when(session_mock).commitTransaction();
        Mockito.doNothing().when(session_mock).rollbackTransaction();
        Mockito.doNothing().when(utente_cookie_dao).update(new UtenteRegistrato());

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bando_dao);
        Mockito.when(bando_dao.findbyId(anyString())).thenReturn(bando);
        Mockito.when(utente_dao.maxIscrittiRaggiunto(bando)).thenReturn(maxIscritti);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);


        if( cookieUser.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().iscrizione(null, cookieUser, bandoId, iscritto));
        }
        else if( bandoId.equals("") || iscritto.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().iscrizione(null, cookieUser, bandoId, iscritto));
        }
        else if( !iscritto.equals("True") && !iscritto.equals("False")) {
            assertThrows(RuntimeException.class, () -> new Calendario().iscrizione(null, cookieUser, bandoId, iscritto));
        }
        else {
            ModelAndView page = new Calendario().iscrizione(null, cookieUser, bandoId, iscritto);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
        "'',''",
        "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,''",
        "'',0000000001",
        "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,0000000001"
    })
    void annullaIscrizione(
        String cookieUser,
        String bandoId
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/viewBandoCSS");
        BandoDAOmySQL bando_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();
        UtenteRegistratoDAOcookie utente_cookie_dao = Mockito.mock(UtenteRegistratoDAOcookie.class);
        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistrato utente = new UtenteRegistrato();
        boolean maxIscritti = false;

        decode_mock.when(() -> UtenteRegistratoDAOcookie.decode(cookieUser)).thenReturn(utente);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(utente_cookie_dao);
        Mockito.doNothing().when(session_mock).beginTransaction();
        Mockito.doNothing().when(session_mock).commitTransaction();
        Mockito.doNothing().when(session_mock).rollbackTransaction();
        Mockito.doNothing().when(utente_cookie_dao).update(new UtenteRegistrato());
        
        Mockito.when(db_mock.getBandoDAO()).thenReturn(bando_dao);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);
        Mockito.when(bando_dao.findbyId(anyString())).thenReturn(bando);
        Mockito.when(utente_dao.maxIscrittiRaggiunto(bando)).thenReturn(maxIscritti);

        if( cookieUser.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().annullaIscrizione(null, cookieUser, bandoId));
        }
        else if( bandoId.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().annullaIscrizione(null, cookieUser, bandoId));
        }
        else {
            ModelAndView page = new Calendario().annullaIscrizione(null, cookieUser, bandoId);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','','','',''",
            "nonlosoquanticar#1234567890#ciao1,'','','',''",
            "nonlosoquanticar#1234567890#ciao1,matricola,Accettato,0000000001",
            "nonlosoquanticar#1234567890#ciao1,matricola,Accettato,''",
    })
    void esitoPartecipante(
        String cookieAdmin,
        String utenteSelezionato,
        String esito,
        String bandoId
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Calendario/reload");
        BandoDAOmySQL bando_dao = Mockito.mock(BandoDAOmySQL.class);
        Bando bando = new Bando();
        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        AvvisiDAOmySQL avviso_dao = Mockito.mock(AvvisiDAOmySQL.class);
        Avviso avviso = new Avviso();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getBandoDAO()).thenReturn(bando_dao);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);
        Mockito.when(db_mock.getAvvisoDAO()).thenReturn(avviso_dao);
        Mockito.when(bando_dao.findbyId(anyString())).thenReturn(bando);
        Mockito.when(avviso_dao.create(anyString(), anyString(), any(), anyString(), anyString())).thenReturn(avviso);
        Mockito.when(avviso_dao.getID()).thenReturn("0000000001");

        configuration_mock.when(() -> Configuration.getDIRECTORY_FILE()).thenReturn("C:\\Users\\stefa\\Desktop\\Sito_SistemiWeb\\File\\Test\\");

        if( cookieAdmin.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().esitoPartecipante(null, cookieAdmin, utenteSelezionato, esito, bandoId));
        }
        else if( bandoId.equals("") || utenteSelezionato.equals("") || esito.equals("") ) {
            assertThrows(RuntimeException.class, () -> new Calendario().esitoPartecipante(null, cookieAdmin, utenteSelezionato, esito, bandoId));
        }
        else {
            ModelAndView page = new Calendario().esitoPartecipante(null, cookieAdmin, utenteSelezionato, esito, bandoId);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
}