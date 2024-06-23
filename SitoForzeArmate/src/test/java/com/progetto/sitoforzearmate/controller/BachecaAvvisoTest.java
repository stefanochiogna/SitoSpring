package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.AvvisiDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Avviso;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;

class BachecaAvvisoTest {

    @Mock
    private MockedStatic<DAOFactory> dao_factory_mock;
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
    }

    @AfterEach
    void tearDown() {
        configuration_mock.close();
        dao_factory_mock.close();
    }

    @ParameterizedTest
    @CsvSource({
            "'',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,''",
            "'',nonlosoquanticar#1234567890#ciao1",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1"
    })
    void view(
            String cookieUser,
            String cookieAdmin
    ) {

        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Bacheca/AvvisiCSS");
        AvvisiDAOmySQL avvisi_dao = Mockito.mock(AvvisiDAOmySQL.class);
        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        ArrayList<Avviso> avvisi = new ArrayList<>();
        ArrayList<UtenteRegistrato> utenti = new ArrayList<>();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);


        Mockito.when(db_mock.getAvvisoDAO()).thenReturn(avvisi_dao);
        Mockito.when(avvisi_dao.stampaAvvisi(anyString())).thenReturn(avvisi);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);
        Mockito.when(utente_dao.getUtenti()).thenReturn(utenti);

        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new BachecaAvviso().view(null, cookieAdmin, cookieUser));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new BachecaAvviso().view(null, cookieAdmin, cookieUser));
        }
        else {
            ModelAndView page = new BachecaAvviso().view(null, cookieAdmin, cookieUser);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'',100000006", 
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,100000006",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,''"
    })
    void viewAvviso(
        String cookieUser,
        String avvisoId
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Bacheca/viewAvvisoCSS");
        AvvisiDAOmySQL avvisi_dao = Mockito.mock(AvvisiDAOmySQL.class);
        Avviso avvisi = new Avviso();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getAvvisoDAO()).thenReturn(avvisi_dao);
        Mockito.when(avvisi_dao.findById(avvisoId)).thenReturn(avvisi);

        if(cookieUser.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().viewAvviso(null, cookieUser, avvisoId));
        else if(avvisoId.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().viewAvviso(null, cookieUser, avvisoId));
        else {
            ModelAndView page = new BachecaAvviso().viewAvviso(null, cookieUser, avvisoId);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'',100000006", 
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,100000006",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,''"
    })
    void deleteAvviso(
        String cookieUser,
        String avvisoId
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Bacheca/reload");
        AvvisiDAOmySQL avvisi_dao = Mockito.mock(AvvisiDAOmySQL.class);
        Avviso avvisi = new Avviso();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        UtenteRegistratoDAOcookie utente_cookie_dao = Mockito.mock(UtenteRegistratoDAOcookie.class);

        Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(utente_cookie_dao);
        Mockito.doNothing().when(session_mock).beginTransaction();
        Mockito.doNothing().when(session_mock).commitTransaction();
        Mockito.doNothing().when(session_mock).rollbackTransaction();
        Mockito.doNothing().when(utente_cookie_dao).update(new UtenteRegistrato());

        Mockito.when(db_mock.getAvvisoDAO()).thenReturn(avvisi_dao);
        Mockito.when(avvisi_dao.findById(avvisoId)).thenReturn(avvisi);

        if (cookieUser.equals(""))
            assertThrows(RuntimeException.class, () -> new BachecaAvviso().deleteAvviso(null, cookieUser, avvisoId));
        else if (avvisoId.equals("")) {
            assertThrows(RuntimeException.class, () -> new BachecaAvviso().deleteAvviso(null, cookieUser, avvisoId));
        }
        else {
            ModelAndView page = new BachecaAvviso().deleteAvviso(null, cookieUser, avvisoId);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    private String[] parseString(String stringa){
        if(stringa == null)
            return null;
        else
            return stringa.split(",");
    }

    @ParameterizedTest
    @CsvSource({
            "'',Tutti,ProvaTest, Stiamo provando il test,,", 
            "nonlosoquanticar#1234567890#ciao1,Tutti,ProvaTest,Stiamo provando il test,,",
            "nonlosoquanticar#1234567890#ciao1,Ruolo,ProvaTest,Stiamo provando il test,,",
            "nonlosoquanticar#1234567890#ciao1,Utente,ProvaTest,Stiamo provando il test,,",
            "nonlosoquanticar#1234567890#ciao1,Tutti,'','',,",
            "nonlosoquanticar#1234567890#ciao1,Ruolo,ProvaTest,Stiamo provando il test,'Graduato',",
            "nonlosoquanticar#1234567890#ciao1,Utente,ProvaTest,Stiamo provando il test,,'tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&'",
            "nonlosoquanticar#1234567890#ciao1,Altro,ProvaTest,Stiamo provando il test,,"

    })
    void inviaAvviso(
        String cookieAdmin,
        String scelta,
        String oggetto,
        String testo,
        String Ruolo,
        String Matricola
    ) {

        String[] RuoloArray = parseString(Ruolo);
        String[] MatricolaArray = parseString(Matricola);

        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        AvvisiDAOmySQL avviso_dao = Mockito.mock(AvvisiDAOmySQL.class);

        File file = Mockito.mock(File.class);

        ArrayList<UtenteRegistrato> utenteRuolo = new ArrayList<>();
        ArrayList<UtenteRegistrato> utenti = new ArrayList<>();

        UtenteRegistrato utenteTest = new UtenteRegistrato();

        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Bacheca/reload");

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);
        Mockito.when(utente_dao.getUtenti()).thenReturn(utenti);
        Mockito.when(utente_dao.getUtentiRuolo(anyString())).thenReturn(utenteRuolo);
        Mockito.when(db_mock.getAvvisoDAO()).thenReturn(avviso_dao);
        Mockito.when(avviso_dao.getID()).thenReturn("00000001");

        String relativePath = ".." + File.separator +"raccolta_file"+ File.separator +"test"+ File.separator;
        String fullPath = Paths.get(relativePath).toString() + File.separator;

        configuration_mock.when(() -> Configuration.getDIRECTORY_FILE()).thenReturn(relativePath);
        configuration_mock.when(() -> Configuration.getPATH(anyString())).thenReturn(fullPath);


        if(cookieAdmin.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, RuoloArray, MatricolaArray));
        else if(scelta.equals("") || oggetto.equals("") || testo.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, RuoloArray, MatricolaArray));
        else if(scelta.equals("Ruolo") && Ruolo == null) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, RuoloArray, MatricolaArray));
        else if(scelta.equals("Utente") && Matricola == null) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, RuoloArray, MatricolaArray));
        else if(!scelta.equals("Tutti") && !scelta.equals("Ruolo") && !scelta.equals("Utente")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, RuoloArray, MatricolaArray));
        else {
            utenteTest = UtenteRegistratoDAOcookie.decode("tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&");
            utenti.add(utenteTest);
            utenteRuolo.add(utenteTest);

            ModelAndView page = new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, RuoloArray, MatricolaArray);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
}