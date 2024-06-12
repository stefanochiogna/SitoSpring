package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.Cookie.Utente.UtenteRegistratoDAOcookie;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NewsletterDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.MySQL.Utente.UtenteRegistratoDAOmySQL;
import com.progetto.sitoforzearmate.model.dao.Notizie.NotizieDAO;
import com.progetto.sitoforzearmate.model.mo.Notizie.Avviso;
import com.progetto.sitoforzearmate.model.mo.Notizie.Newsletter;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class LoginTest {

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

    @Test
    void view() {

        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("LoginCSS");
        
        assertEquals(pageExpected.getViewName(), page.getViewName());
    }

    @ParameterizedTest
    @CsvSource({
        "'','',''",
        "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',''"
        "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,sara.tullini@edu.unife.it,ciao1"
    })
    void login(
        String cookieUser,
        String email,
        String password
    ) {
        UtenteRegistratoDAOcookie utente_cookie_dao = Mockito.mock(UtenteRegistratoDAOcookie.class);
        UtenteRegistratoDAOmySQL utente_session_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistratoDAOmySQL utente_dao = Mockito.mock(UtenteRegistratoDAOmySQL.class);
        UtenteRegistrato utente = new UtenteRegistrato();
        
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        dao_factory_mock.when(() -> DAOFactory.getDAOFactory(eq("CookieImpl"), any())).thenReturn(session_mock);

        Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(utente_cookie_dao);
        Mockito.doNothing().when(session_mock).beginTransaction();
        Mockito.doNothing().when(session_mock).commitTransaction();
        Mockito.doNothing().when(session_mock).rollbackTransaction();
        Mockito.when(session_mock.getUtenteRegistratoDAO()).thenReturn(utente_session_dao);

        Mockito.when(db_mock.getUtenteRegistratoDAO()).thenReturn(utente_dao);
        Mockito.when(utente_dao.findByMail(anyString())).thenReturn(utente);

    }

    @Test
    void logout(){

    }

    @Test 
    void viewRegistrazione()
    {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("RegistrazioneCSS");
        BaseDAOmySQL basi_dao = Mockito.mock(BaseDAOmySQL.class);
        List<Base> basi = new ArrayList<>();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        
        Mockito.when(db_mock.getBaseDAO()).thenReturn(basi_dao);
        Mockito.when(basi_dao.stampaBasi()).thenReturn(basi);

        ModelAndView page = new Login().viewRegistrazione(null);
        assertEquals(pageExpected.getViewName(), page.getViewName());
        
    }

    @Test 
    void Registrazione(){

    }

    @Test
    void viewAmministratore(){

    }

}