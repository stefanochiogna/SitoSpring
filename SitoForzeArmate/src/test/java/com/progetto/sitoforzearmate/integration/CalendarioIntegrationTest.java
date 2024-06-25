package com.progetto.sitoforzearmate.integration;

import com.progetto.sitoforzearmate.SitoForzeArmateApplication;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.junit.jupiter.params.ParameterizedTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalendarioIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Container
    private static GenericContainer<?> mysql = new GenericContainer<>(DockerImageName.parse("stefanochiogna/db:latest"))
            .withExposedPorts(3306);

    @BeforeAll
    public static void initContainer(){
        mysql.start();
    }
    @AfterAll
    public static void stopContainer(){
        mysql.stop();
    }

    @Test
    public void container_running() {
        assertTrue(mysql.isRunning());
    }


    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @ParameterizedTest
    @CsvSource({
        "'','',false,false",
        "nonlosoquanticar#1234567890#ciao1,'',true,false",   
        "'',chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&,false,true"    
    })
    public void testViewCalendario(String cookieAdmin, String cookieUser, boolean loggedAdminOn, boolean loggedUserOn) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(get("/viewCalendario")
                .cookie(new Cookie("loggedAdmin", cookieAdmin))
                .cookie(new Cookie("loggedUser", cookieUser))) 
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("Calendario/CalendarioCSS"))
                .andExpect(model().attribute("loggedAdminOn", loggedAdminOn))
                .andExpect(model().attribute("loggedOn", loggedUserOn))
                .andExpect(model().attributeExists("Date"))
                .andExpect(model().attributeExists("Bandi"));
    }

    @ParameterizedTest
    @CsvSource({
        "'','',false,false,0000000003",
        "nonlosoquanticar#1234567890#ciao1,'',true,false,0000000003",   
        "'',chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&,false,true,0000000003"    
    })
    public void testViewBando(String cookieAdmin, String cookieUser, boolean loggedAdminOn, boolean loggedUserOn, String id) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(get("/viewBando")
                .param("bandoId", id)
                .cookie(new Cookie("loggedAdmin", cookieAdmin))
                .cookie(new Cookie("loggedUser", cookieUser))) 
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("Calendario/viewBandoCSS"))
                .andExpect(model().attribute("loggedAdminOn", loggedAdminOn))
                .andExpect(model().attribute("loggedOn", loggedUserOn))
                .andExpect(model().attributeExists("Date"))
                .andExpect(model().attributeExists("BandoSelezionato"))
                .andExpect(model().attributeExists("maxIscrittiRaggiunto"));
    }

    @ParameterizedTest
    @CsvSource({
        "'','',false,false,0000000003",
        "nonlosoquanticar#1234567890#ciao1,'',true,false,0000000003",   
        "'',chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&,false,true,0000000003"    
    })
    public void testViewBando(String cookieAdmin, String cookieUser, boolean loggedAdminOn, boolean loggedUserOn, String id) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        var requestBuilder = post("/viewBando")
            .param("bandoId", id)
            .cookie(new Cookie("loggedAdmin", cookieAdmin))
            .cookie(new Cookie("loggedUser", cookieUser))

        this.mockMvc.perform(requestBuilder) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/viewBandoCSS"))
            .andExpect(model().attribute("loggedAdminOn", loggedAdminOn))
            .andExpect(model().attribute("loggedOn", loggedUserOn))
            .andExpect(model().attributeExists("Date"))
            .andExpect(model().attributeExists("BandoSelezionato"))
            .andExpect(model().attributeExists("maxIscrittiRaggiunto"));

        if( loggedUserOn ) {
            this.mockMvc.perform(requestBuilder)
                .andExpect(model().attributeExists("Iscritto"))
                .andExpect(model().attributeExists("inAttesa"));

        }
        else if( loggedAdminOn ) {
            this.mockMvc.perform(requestBuilder)
                .andExpect(model().attributeExists("partecipanti"));
        }
    }

    @Test
    public void testDeleteBando() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String id = "0000000003";

        this.mockMvc.perform(post("/deleteBando")
                .param("bandoId", id)
                .cookie(new Cookie("loggedAdmin", cookieAdmin))) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/reload"))
            .andExpect(model().attribute("loggedAdminOn", true))
            .andExpect(model().attributeExists("BandoSelezionato"));
    }

    @Test
    public void testModificaBandoView() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String id = "0000000003";

        this.mockMvc.perform(post("/modificaBandoView")
                .param("bandoId", id)
                .cookie(new Cookie("loggedAdmin", cookieAdmin))) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/modificaBandoCSS"))
            .andExpect(model().attribute("loggedAdminOn", true))
            .andExpect(model().attributeExists("BandoSelezionato"))
            .andExpect(model().attributeExists("ListaBasi"));
    }

    @Test
    public void testDeleteBando() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String id = "0000000003";
        String oggetto = "test";
        String numMaxIscritti = "23";
        String dataScadenza = "2025-12-21";
        String data = "2026-01-21";
        String locazione = "Pisa";
        String testoBando = "Bando di prova";

        this.mockMvc.perform(post("/modificaBando")
                .param("bandoId", id)
                .param("oggettoBando", oggetto)
                .param("numMaxIscritti", numMaxIscritti)
                .param("DataScadenza", dataScadenza)
                .param("DataBando", data)
                .param("Locazione", locazione)
                .param("testoBando", testoBando)
                .cookie(new Cookie("loggedAdmin", cookieAdmin))) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/reload"))
            .andExpect(model().attribute("loggedAdminOn", true));
    }

    @Test
    public void testInserisciBando() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        MockPart testoFile = new MockPart(
            "Testo",
            "testo.txt",
            "text/plain Questo è il contenuto del file di testo.".getBytes()
        );

        MockPart testo = Mockito.mock(MockPart.class);
        Mockito.when(testo.getInputStream()).thenReturn(testoFile.getInputStream());
        Mockito.when(testo.getSubmittedFileName()).thenReturn(testoFile.getSubmittedFileName());
        Mockito.when(testo.getContentType()).thenReturn(testoFile.getContentType());
        Mockito.when(testo.getSize()).thenReturn(testoFile.getSize());
        Mockito.when(testo.getName()).thenReturn(testoFile.getName());
        Mockito.doNothing().when(testo).write(anyString());

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String oggetto = "test";
        String numMaxIscritti = "23";
        String dataScadenza = "2025-12-21";
        String data = "2026-01-21";
        String locazione = "Pisa";

        this.mockMvc.perform(post("/inserisciBando")
                .part(testo)
                .param("DataBando", data)
                .param("oggettobando", oggetto)
                .param("numMaxIscritti", numMaxIscritti)
                .param("DataScadenza", dataScadenza)
                .param("Locazione", locazione)
                .cookie(new Cookie("loggedAdmin", cookieAdmin))) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/reload"))
            .andExpect(model().attribute("loggedAdminOn", true))
            .andExpect(model().attributeExists("ListaBasi"));
    }

    @Test
    public void testIscrizione() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser = "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&";
        String id = "0000000005";

        this.mockMvc.perform(post("/iscrizione")
                .param("bandoId", id)
                .param("Iscritto", "True")
                .cookie(new Cookie("loggedUser", cookieUser))) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/viewBandoCSS"))
            .andExpect(model().attribute("loggedOn", true))
            .andExpect(model().attributeExists("BandoSelezionato"))
            .andExpect(model().attribute("Iscritto",true))
            .andExpect(model().attributeExists("maxIscrittiRaggiunto"))
            .andExpect(model().attributeExists("inAttesa"));
    }

    @Test
    public void testAnnullaIscrizione() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser = "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&";
        String id = "0000000004";

        this.mockMvc.perform(post("/annullaIscrizione")
                .param("bandoId", id)
                .cookie(new Cookie("loggedUser", cookieUser))) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/viewBandoCSS"))
            .andExpect(model().attribute("loggedOn", true))
            .andExpect(model().attributeExists("BandoSelezionato"))
            .andExpect(model().attribute("Iscritto",false))
            .andExpect(model().attributeExists("maxIscrittiRaggiunto"));
    }

    @Test
    public void testEsitoPartecipante() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String inAttesa = "in attesa"
        String id = "0000000004";

        this.mockMvc.perform(post("/esitoPartecipante")
                .param("inAttesa", inAttesa)
                .param("bandoId", id)
                .cookie(new Cookie("loggedAdmin", cookieAdmin))) 
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Calendario/reload"))
            .andExpect(model().attribute("loggedAdminOn", true))
            .andExpect(model().attributeExists("BandoSelezionato"));
    }
}