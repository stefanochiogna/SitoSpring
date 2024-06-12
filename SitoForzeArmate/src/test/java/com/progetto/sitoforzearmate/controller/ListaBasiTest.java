package com.progetto.sitoforzearmate.controller;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Base.BaseDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Base.Base;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListaBasiTest {

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
        pageExpected.setViewName("ListaBasi/Lista");
        BaseDAOmySQL baseDao = Mockito.mock(BaseDAOmySQL.class);
        List<Base> listaBasi = new ArrayList<>();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        Mockito.when(db_mock.getBaseDAO()).thenReturn(baseDao);
        Mockito.when(baseDao.stampaBasi()).thenReturn(listaBasi);

        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new ListaBasi().view(null, cookieAdmin, cookieUser));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new ListaBasi().view(null, cookieAdmin, cookieUser));
        }
        else {
            ModelAndView page = new ListaBasi().view(null, cookieAdmin, cookieUser);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','',''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',''",
            "'',nonlosoquanticar#1234567890#ciao1,''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,nonlosoquanticar#1234567890#ciao1,''",
            "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,'',Roma",
            "'',nonlosoquanticar#1234567890#ciao1,Roma",
    })
    void viewBase(
            String cookieUser,
            String cookieAdmin,
            String locazione
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("ListaBasi/viewBase");
        BaseDAOmySQL baseDao = Mockito.mock(BaseDAOmySQL.class);
        Base baseTest = new Base();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        Mockito.when(db_mock.getBaseDAO()).thenReturn(baseDao);
        Mockito.when(baseDao.findbyLocazione(locazione)).thenReturn(baseTest);

        if( cookieAdmin.equals("") && cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new ListaBasi().viewBase(null, cookieAdmin, cookieUser, locazione));
        }
        else if( !cookieAdmin.equals("") && !cookieUser.equals("")) {
            assertThrows(RuntimeException.class, () -> new ListaBasi().viewBase(null, cookieAdmin, cookieUser, locazione));
        }
        else if(locazione.equals("")){
            assertThrows(RuntimeException.class, () -> new ListaBasi().viewBase(null, cookieAdmin, cookieUser, locazione));
        }
        else {
            ModelAndView page = new ListaBasi().viewBase(null, cookieAdmin, cookieUser, locazione);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "nonlosoquanticar#1234567890#ciao1,Roma",
            "nonlosoquanticar#1234567890#ciao1,''",
            "'',Roma",
            "'',''"
    })
    void deleteBase(
            String cookieAdmin,
            String locazione
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("ListaBasi/reload");
        BaseDAOmySQL baseDao = Mockito.mock(BaseDAOmySQL.class);
        Base baseTest = new Base();

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        Mockito.when(db_mock.getBaseDAO()).thenReturn(baseDao);
        Mockito.when(baseDao.findbyLocazione(locazione)).thenReturn(baseTest);

        Mockito.doNothing().when(baseDao).delete(baseTest);

        if(cookieAdmin.equals("") || locazione.equals(""))
            assertThrows(RuntimeException.class, () -> new ListaBasi().deleteBase(null, cookieAdmin, locazione));
        else {
            ModelAndView page = new ListaBasi().deleteBase(null, cookieAdmin, locazione);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
    @ParameterizedTest
    @ValueSource(strings = {"", "nonlosoquanticar#1234567890#ciao1"})
    void registraBase(String cookieAdmin) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("ListaBasi/NewBaseCSS");

        if(cookieAdmin.equals(""))
            assertThrows(RuntimeException.class, () -> new ListaBasi().registraBase(null, cookieAdmin));
        else {
            ModelAndView page = new ListaBasi().registraBase(null, cookieAdmin);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'','','','','','','','','',''",
            "nonlosoquanticar#1234567890#ciao1,'','','','','','','','',''",
            "nonlosoquanticar#1234567890#ciao1,email,1234567890,Roma,RO,12345,Via Roma,0,0",
            "nonlosoquanticar#1234567890#ciao1,email,1234567890,'',RO,12345,Via Roma,0,0"
    })
    void newBase(
            String cookieAdmin,
            String email,
            String telefono,
            String locazione,
            String provincia,
            String cap,
            String via,
            String latitudine,
            String longitudine
    ) {
        ModelAndView pageExpected = new ModelAndView();
        pageExpected.setViewName("ListaBasi/reload");
        Part foto = Mockito.mock(Part.class);
        BaseDAOmySQL baseDao = Mockito.mock(BaseDAOmySQL.class);

        dao_factory_mock.when(() -> DAOFactory.getDAOFactory("MySQLJDBCImpl", null)).thenReturn(db_mock);
        Mockito.when(db_mock.getBaseDAO()).thenReturn(baseDao);

        try {
            InputStream mockIO = Mockito.mock(InputStream.class);
            Mockito.when(foto.getInputStream()).thenReturn(mockIO);

            Mockito.when(mockIO.readAllBytes()).thenReturn(new byte[1]);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(cookieAdmin.equals("") || locazione.equals("") || provincia.equals("") || cap.equals("") || via.equals("") || latitudine.equals("") || longitudine.equals("") || email.equals("") || telefono.equals("") || foto == null)
            assertThrows(RuntimeException.class, () -> new ListaBasi().newBase(null, cookieAdmin, foto, email, telefono, locazione, provincia, cap, via, latitudine, longitudine));
        else {
            ModelAndView page = new ListaBasi().newBase(null, cookieAdmin, foto, email, telefono, locazione, provincia, cap, via, latitudine, longitudine);

            assertEquals(pageExpected.getViewName(), page.getViewName());
        }
    }
}