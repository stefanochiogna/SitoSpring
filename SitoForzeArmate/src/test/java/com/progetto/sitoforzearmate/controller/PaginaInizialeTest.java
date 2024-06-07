package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.model.mo.Utente.Amministratore;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

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
            "'',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,''",
            "'',nonlosoquanticar#1234567890#ciao1",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1"
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

        if(!cookieUtente.equals("") && !cookieAdmin.equals("")) {
            assertThrows(Exception.class, () -> new PaginaIniziale().view(null, cookieUtente, cookieAdmin));
        }
        else {
            ModelAndView page = new PaginaIniziale().view(null, cookieUtente, cookieAdmin);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }

    }

    /*
    static Stream<Arguments> sostituisciArticoloProvider() {
        return Stream.of(
                Arguments.of("", "", "", "", null),
                Arguments.of("", "0000000001", "", "", null),
                Arguments.of("", "", "Oggetto articolo", "", null),
                Arguments.of("", "", "", "1234567890", null),
                Arguments.of("", "", "", "", Mockito.mock(Part.class)),
                Arguments.of("nonlosoquanticar#1234567890#ciao1", "0000000001", "Oggetto articolo", "1234567890", Mockito.mock(Part.class))
        );
    }

    @ParameterizedTest
    @MethodSource("sostituisciArticoloProvider")
    void sostituisciArticolo(
            String cookieAdmin,
            String id,
            String oggetto,
            String idAdmin,
            Part testo
    ){

       ModelAndView pageExpected = new ModelAndView();
       pageExpected.setViewName("index");
       NotizieDAOmySQL notizie_dao = Mockito.mock(NotizieDAOmySQL.class);

       Notizie notizia = new Notizie();

       dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
       Mockito.when(db_mock.getNotizieDAO()).thenReturn(notizie_dao);
       Mockito.when(notizie_dao.findById(anyString())).thenReturn(notizia);

       if(id.equals("")) assertThrows(RuntimeException.class, () -> new PaginaIniziale().sostituisciArticolo(null, cookieAdmin, id, oggetto, idAdmin, testo));
       if(oggetto == null)  assertThrows(RuntimeException.class, () -> new PaginaIniziale().sostituisciArticolo(null, cookieAdmin, id, oggetto, idAdmin, testo));
       if(cookieAdmin.equals("")) assertThrows(RuntimeException.class, () -> new PaginaIniziale().sostituisciArticolo(null, cookieAdmin, id, oggetto, idAdmin, testo));
       if(idAdmin.equals("")) assertThrows(RuntimeException.class, () -> new PaginaIniziale().sostituisciArticolo(null, cookieAdmin, id, oggetto, idAdmin, testo));
       if(testo == null) assertThrows(RuntimeException.class, () -> new PaginaIniziale().sostituisciArticolo(null, cookieAdmin, id, oggetto, idAdmin, testo));

       ModelAndView page = new PaginaIniziale().sostituisciArticolo(null, cookieAdmin, id, oggetto, idAdmin, testo);
       assertEquals(pageExpected.getViewName(), page.getViewName());

    }
    */

    @ParameterizedTest
    @CsvSource({
            "'','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',''",
            "'',nonlosoquanticar#1234567890#ciao1,''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1,''",
            "'','','0000000001'",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'','0000000001'",
            "'',nonlosoquanticar#1234567890#ciao1,'0000000001'",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1,'0000000001'"
    })
    void viewArt(
            String cookieUtente,
            String cookieAdmin,
            String id
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("viewArticoloCSS");
        NotizieDAOmySQL notizie_dao = Mockito.mock(NotizieDAOmySQL.class);
        Notizie notizie = new Notizie();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        Mockito.when(db_mock.getNotizieDAO()).thenReturn(notizie_dao);
        Mockito.when(notizie_dao.findById(anyString())).thenReturn(notizie);

        if(!cookieUtente.equals("") && !cookieAdmin.equals(""))
            assertThrows(Exception.class, () -> new PaginaIniziale().viewArt(null, cookieAdmin, cookieUtente, id));
        else if(id.equals("")) {
            assertThrows(Exception.class, () -> new PaginaIniziale().viewArt(null, cookieAdmin, cookieUtente, id));
        }
        else{
            ModelAndView page = new PaginaIniziale().viewArt(null, cookieAdmin, cookieUtente, id);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

}