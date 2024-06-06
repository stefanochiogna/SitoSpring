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

class PaginaInizialeTest {

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

    @Test
    void init() {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("index");

        ModelAndView page = new PaginaIniziale().init(null, "", "");
        assertEquals(pageExpected.getViewName(), page.getViewName());
    }

    @ParameterizedTest
    @CsvSource({
            ",",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,",
            ",nonlosoquanticar#1234567890#ciao1"
    })
    void view(
            String cookieUtente,
            String cookieAdmin
    ) {

        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Pagina_InizialeCSS");
        NotizieDAOmySQL notizie_dao = Mockito.mock(NotizieDAOmySQL.class);
        Notizie notizie = new Notizie();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getNotizieDAO()).thenReturn(notizie_dao);
        Mockito.when(notizie_dao.findById("0000000001")).thenReturn(notizie);
        Mockito.when(notizie_dao.findById("0000000002")).thenReturn(notizie);
        Mockito.when(notizie_dao.findById("0000000003")).thenReturn(notizie);
        Mockito.when(notizie_dao.findById("0000000004")).thenReturn(notizie);

        /*
        if(cookieUtente != null && cookieAdmin != null) {
            assertThrows(RuntimeException.class, () -> new PaginaIniziale().view(null, cookieUtente, cookieAdmin));
        }
        */
        if(cookieAdmin == null) cookieAdmin = "";
        if(cookieUtente == null) cookieUtente = "";

        ModelAndView page = new PaginaIniziale().view(null, cookieUtente, cookieAdmin);

        assertEquals(pageExpected.getViewName(), page.getViewName());

    }

    @ParameterizedTest
    @CsvSource({
            "view"
    })
    void ExceptionCookie(String metodo) {
        switch (metodo) {
            case "view":
                assertThrows(RuntimeException.class, () -> new PaginaIniziale().view(null, "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&", "nonlosoquanticar#1234567890#ciao1"));

        }
    }

    @Test
    void sostituisciArticolo() {
    }

    @Test
    void viewArt() {
    }
}