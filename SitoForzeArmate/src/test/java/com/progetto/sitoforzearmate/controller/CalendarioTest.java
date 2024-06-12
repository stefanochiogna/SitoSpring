package com.progetto.sitoforzearmate.controller;

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
import static org.mockito.ArgumentMatchers.anyString;

class CalendarioTest {

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
        bando.AddEsito("Accettato", "0000000001");
        boolean maxIscritti = false;


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
    }

    @Test
    void iscrizione() {
    }

    @Test
    void annullaIscrizione() {
    }

    @Test
    void esitoPartecipante() {
    }
}