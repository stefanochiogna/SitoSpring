package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class RubricaTest {

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
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'A'"
    })
    void view(
            String cookieUser,
            String inizialeSelezionata
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("Rubrica/viewCSS");

        UtenteRegistrato user = new UtenteRegistrato();
        List<String> iniziali = new ArrayList<>();
        List<UtenteRegistrato> contatti = new ArrayList<>();

        UtenteRegistratoDAOmySQL userDAO = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null)).thenReturn(db_mock);
        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(userDAO);

        Mockito.when(userDAO.findByMatricola(anyString())).thenReturn(user);
        Mockito.when(userDAO.ElencoIniziali(user)).thenReturn(iniziali);

        Mockito.when(userDAO.cercaIniziale(anyString(), anyString())).thenReturn(contatti);

        if(cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new Rubrica().view(null, cookieUser, inizialeSelezionata));
        }
        else {
            ModelAndView page = new Rubrica().view(null, cookieUser, inizialeSelezionata);
            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
}