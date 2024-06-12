package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.PostoLettoDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Base.PostoLetto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class PrenotaAlloggioTest {
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
            "'', ''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, Roma",
            "'', Roma",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, ''"
    })
    void view(
            String cookieUser,
            String locazione
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("ListaBasi/PrenotaAlloggioCSS");

        if(cookieUser.equals("") || locazione.equals("")) {
            assertThrows(RuntimeException.class, () -> {
                new PrenotaAlloggio().view(null, cookieUser, locazione);
            });
        }
        else {
            ModelAndView page = new PrenotaAlloggio().view(null, cookieUser, locazione);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }

    }

    @ParameterizedTest
    @CsvSource({
            "'', '','','', ''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, '','','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, Roma, 3, 5, '2023-06-12'"
    })
    void conferma(
            String cookieUser,
            String locazione,
            String NumPersone,
            String NumNotti,
            String data_arrivo
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("ListaBasi/ConfermaCSS");

        PostoLettoDAOmySQL alloggioDAO_mock = Mockito.mock(PostoLettoDAOmySQL.class);
        PostoLetto alloggioTest = new PostoLetto();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getPostoLettoDAO()).thenReturn(alloggioDAO_mock);
        Mockito.when(alloggioDAO_mock.create(anyString(), anyString(), any(), any(), any())).thenReturn(alloggioTest);

        if(cookieUser.equals("") || locazione.equals("") || NumNotti.equals("") || NumPersone.equals("") || data_arrivo.equals("")){
            assertThrows(RuntimeException.class, () -> {
                new PrenotaAlloggio().conferma(null, cookieUser, locazione, NumPersone, NumNotti, data_arrivo);
            });
        }
        else {
            ModelAndView page = new PrenotaAlloggio().conferma(null, cookieUser, locazione, NumPersone, NumNotti, data_arrivo);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
}