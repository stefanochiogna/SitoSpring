package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.PastoDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Base.Pasto;
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

class PrenotaPastoTest {
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
        pageExpected.setViewName("ListaBasi/PrenotaPastoCSS");

        if(cookieUser.equals("") || locazione.equals("")) {
            assertThrows(RuntimeException.class, () -> {
                new PrenotaPasto().view(null, cookieUser, locazione);
            });
        }
        else {
            ModelAndView page = new PrenotaPasto().view(null, cookieUser, locazione);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }

    }

    @ParameterizedTest
    @CsvSource({
            "'', '','','', ''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, '','','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&, Roma, A, '2023-06-12'"
    })
    void conferma(
            String cookieUser,
            String locazione,
            String turno,
            String data_arrivo
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("ListaBasi/ConfermaCSS");

        PastoDAOmySQL pastoDao_mock = Mockito.mock(PastoDAOmySQL.class);
        Pasto pastoTest = new Pasto();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);

        Mockito.when(db_mock.getPastoDAO()).thenReturn(pastoDao_mock);
        Mockito.when(pastoDao_mock.createPrenotazione(any())).thenReturn(pastoTest);

        if(cookieUser.equals("") || locazione.equals("") || turno.equals("") || data_arrivo.equals("")){
            assertThrows(RuntimeException.class, () -> {
                new PrenotaPasto().conferma(null, cookieUser, locazione, turno, data_arrivo);
            });
        }
        else {
            ModelAndView page = new PrenotaPasto().conferma(null, cookieUser, locazione, turno, data_arrivo);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
}