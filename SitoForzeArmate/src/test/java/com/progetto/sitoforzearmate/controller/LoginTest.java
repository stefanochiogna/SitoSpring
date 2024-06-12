package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.AmministratoreDAOcookie;
import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class LoginTest {
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

    @Test
    void view() {
    }

    @Test
    void login() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"False", "True"})
    void logout(String Eccezione) {

        Boolean eccezione = Boolean.parseBoolean(Eccezione);

        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("index");
        pageExpected.addObject("loggedUser", null);
        pageExpected.addObject("loggedOn", false);
        pageExpected.addObject("loggedAdmin", null);
        pageExpected.addObject("loggedAdminOn", false);

        UtenteRegistratoDAOcookie cookieUserMock = Mockito.mock(UtenteRegistratoDAOcookie.class);
        AmministratoreDAOcookie cookieAdminMock = Mockito.mock(AmministratoreDAOcookie.class);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        if(eccezione) {
            Mockito.doThrow(new NullPointerException()).when(session_mock).beginTransaction();
            assertThrows(RuntimeException.class, () -> new Login().logout(null));
        }
        else {
            Mockito.doNothing().when(session_mock).beginTransaction();
            Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(cookieUserMock);
            Mockito.when(session_mock.getAmministratoreDAO()).thenReturn(cookieAdminMock);
            Mockito.doNothing().when(cookieUserMock).delete(any());
            Mockito.doNothing().when(cookieAdminMock).delete(any());
            Mockito.doNothing().when(session_mock).commitTransaction();

            ModelAndView pageActual = new Login().logout(null);

            assertEquals(pageExpected.getViewName(), pageActual.getViewName());
            assertEquals(pageExpected.getModel(), pageActual.getModel());
        }
    }


    @Test
    void viewRegistrazione() {
    }

    @Test
    void registrazione() {
    }

    @Test
    void viewAmministratore() {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("LoginAmministratoreCSS");

        ModelAndView pageActual = new Login().viewAmministratore(null);
        assertEquals(pageExpected.getViewName(), pageActual.getViewName());
    }

    @Test
    void loginAmministratore() {
    }
}