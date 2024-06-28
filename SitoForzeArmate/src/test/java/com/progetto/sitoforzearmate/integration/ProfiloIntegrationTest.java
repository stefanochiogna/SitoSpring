package com.progetto.sitoforzearmate.integration;

import com.progetto.sitoforzearmate.SitoForzeArmateApplication;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
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
import org.testcontainers.shaded.org.hamcrest.Matchers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.ArrayList;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfiloIntegrationTest {
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
        "nonlosoquanticar#1234567890#ciao1,'',true,false",
        "'',tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&,false,true"
    })
    public void integration_viewProfilo(String cookieAdmin, String cookieUser, boolean loggedAdminOn, boolean loggedOn) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(get("/viewProfilo")
                .cookie(new Cookie("loggedUser", cookieUser))
                .cookie(new Cookie("loggedAdmin", cookieAdmin)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Profilo/ProfiloCSS"))
            .andExpect(model().attribute("loggedOn", loggedOn))
            .andExpect(model().attribute("loggedAdminOn", loggedAdminOn))
            .andExpect(model().attributeExists("UtenteCorrente"))
            .andExpect(model().attributeExists("AdminCorrente"));

        if(loggedOn == true) {
            this.mockMvc.perform(get("/viewProfilo")
                    .cookie(new Cookie("loggedUser", cookieUser))
                    .cookie(new Cookie("loggedAdmin", cookieAdmin)))
                .andExpect(model().attributeExists("Badge"));
        }
    }

    @Test
    public void integration_modificaProfiloView() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser =  "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&";
        
        this.mockMvc.perform(post("/modificaProfiloView")
                .cookie(new Cookie("loggedUser", cookieUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Profilo/modificaProfiloCSS"))
            .andExpect(model().attribute("loggedAdminOn", false))
            .andExpect(model().attribute("loggedOn", true))
            .andExpect(model().attributeExists("UserSelezionato"));
    }

    @Test
    public void integration_modificaProfilo() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser = "chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&";
        String password = "passwordDiLorenzo";

        this.mockMvc.perform(post("/modificaProfilo")
                .param("userPassword", password)
                .param("userMail", "lorenzo.chesta@edu.unife.it")
                .param("userTelefono", "0000163634")
                .cookie(new Cookie("loggedUser", cookieUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Profilo/reload"))
            .andExpect(model().attribute("loggedOn", true));
    }

    @Test 
    public void integration_prenotazioniView() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser = "chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&";

        this.mockMvc.perform(get("/prenotazioniView")
                .cookie(new Cookie("loggedUser", cookieUser)))
            .andDo(print())
            .andExpect(view().name("Profilo/prenotazioniCSS"))
            .andExpect(model().attributeExists("listaPasti"))
            .andExpect(model().attributeExists("listaAlloggi"));
    }

    @Test 
    public void integration_viewDoc() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String id = "";
    }
}