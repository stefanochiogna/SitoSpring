package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
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

import static org.junit.jupiter.api.Assertions.*;

class BachecaAvvisoTest {

    @Mock
    private MockedStatic<DAOFactory> dao_factory_mock;
    @Mock
    private DAOFactory db_mock;

    @BeforeEach
    void setUp() {
        dao_factory_mock = Mockito.mockStatic(DAOFactory.class);
        db_mock = Mockito.mock(DAOFactory.class);
    }

    @AfterEach
    void tearDown() {
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
        Avviso avvisi = new Avviso();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getAvvisoDAO()).thenReturn(avvisi_dao);
        Mockito.when(avvisi_dao.stampaAvvisi(anyString())).thenReturn(avvisi);

        if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new BachecaAvviso().view(null, cookieUser, cookieAdmin));
        }
        if( cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new BachecaAvviso().view(null, cookieUser, cookieAdmin));

        }

        ModelAndView page = new BachecaAvviso().view(null, cookieUtente, cookieAdmin);

        assertEquals(pageExpected.getViewName(), page.getViewName());

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
        Mockito.when(avvisi_dao.findById(anyString())).thenReturn(avvisi);

        if(cookieUser.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().viewAvviso(null, cookieUser, avvisoId));

        ModelAndView page = new BachecaAvviso().viewAvviso(null, cookieUtente, avvisoId);

        assertEquals(pageExpected.getViewName(), page.getViewName());

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

        Mockito.when(db_mock.getAvvisoDAO()).thenReturn(avvisi_dao);
        Mockito.when(avvisi_dao.findById(anyString())).thenReturn(avvisi);

        if(cookieUser.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().deleteAvviso(null, cookieUser, avvisoId));

        ModelAndView page = new BachecaAvviso().deleteAvviso(null, cookieUtente, avvisoId);

        assertEquals(pageExpected.getViewName(), page.getViewName());
    }

    @ParameterizedTest
    @CsvSource({
            "'',Tutti,ProvaTest, Stiamo provando il test,,", 
            "nonlosoquanticar#1234567890#ciao1,Tutti,ProvaTest,Stiamo provando il test,,",
            "nonlosoquanticar#1234567890#ciao1,Ruolo,ProvaTest,Stiamo provando il test,,",
            "nonlosoquanticar#1234567890#ciao1,Utente,ProvaTest,Stiamo provando il test,,",
            "nonlosoquanticar#1234567890#ciao1,Tutti,'','',,"
    })
    void inviaAvviso(
        String cookieAdmin,
        String scelta,
        String oggetto,
        String testo,
        String[] Ruolo,
        String[] Matricola
    ) {
        
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Bacheca/reload");

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        if(cookieAdmin.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, Ruolo, Matricola));
        if(scelta.equals("") || oggetto.equals("") || testo.equals("")) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, Ruolo, Matricola));
        if(scelta.equals("Ruolo") && Ruolo.length() == 0) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, Ruolo, Matricola));
        if(scelta.equals("Utente" && Matricola.length() == 0)) assertThrows(RuntimeException.class, () -> new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, Ruolo, Matricola));

        ModelAndView page = new BachecaAvviso().inviaAvviso(null, cookieAdmin, scelta, oggetto, testo, Ruolo, Matricola);

        assertEquals(pageExpected.getViewName(), page.getViewName());
    }
}