package com.progetto.sitoforzearmate.integration;

import com.progetto.sitoforzearmate.SitoForzeArmateApplication;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.dao.MySQL.Notizie.NotizieDAOmySQL;
import com.progetto.sitoforzearmate.model.mo.Notizie.Notizie;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class BachecaAvvisoIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Container
    private static GenericContainer<?> mysql = new GenericContainer<>(DockerImageName.parse("stefanochiogna/db:latest"))
            .withExposedPorts(3306);

    @BeforeAll
    public static void initContainer(){
        this.mysql.start();
    }
    @AfterAll
    public static void stopContainer(){
        this.mysql.stop();
    }

    @Test
    public void container_running() {
        assertTrue(this.mysql.isRunning());
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
    public void testViewBachecaAvviso(String cookieAdmin, String cookieUser, boolean loggedAdminOn, boolean loggedUserOn) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        /*if( !cookieAdmin.equals("") ){ 
            var requestBuilder = get("/viewBachecaAvviso")
                .cookie(new Cookie("loggedAdmin", cookieAdmin));
        }
        else if ( !cookieUser.equals("") ){ 
            var requestBuilder = get("/viewBachecaAvviso")
                .cookie(new Cookie("loggedUser", cookieUser)); // e tolgo queste righe vabb
        }*/

        this.mockMvc.perform(get("/viewBachecaAvviso")
                .cookie(new Cookie("loggedAdmin", cookieAdmin))
                .cookie(new Cookie("loggedUser", cookieUser))) 
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("Bacheca/AvvisiCSS"))
                .andExpect(model().attribute("loggedAdminOn", loggedAdminOn))
                .andExpect(model().attribute("loggedOn", loggedUserOn));
        
        if (loggedAdminOn) {
            this.mockMvc.perform(requestBuilder)
                .andExpect(model().attributeExists("listaUtenti"));
        } else if (loggedUserOn) { 
            this.mockMvc.perform(requestBuilder) 
                .andExpect(model().attributeExists("Avvisi"));
        }
    }


    @Test
    public void integration_viewAvviso() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser = "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&";
        String id = "100000012";

        this.mockMvc.perform(post("/viewAvviso")
                .param("avvisoId", id)
                .cookie(new Cookie("loggedUser", cookieUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("Bacheca/viewAvvisoCSS"))
                .andExpect(model().attributeExists("AvvisoSelezionato"))
                .andExpect(model().attribute("loggedOn", true));
    }

    @Test
    public void integration_deleteAvviso() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));
    
        String cookieUser = "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&";
        String id = "100000012";

        this.mockMvc.perform(post("/deleteAvviso")
            .param("avvisoId", id)
            .cookie(new Cookie("loggedUser", cookieUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Bacheca/reload")) 
            .andExpect(model().attribute("loggedOn", true));
    }

    @Test 
    public void integration_inviaAvviso() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", mysql.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String scelta = "Tutti";
        String oggetto = "Integration test";
        String testo = "prova per integration test";
        String[] ruolo = [];
        String[] matricola = [];

        this.mockMvc.perform(post("/inviaAvviso")
            .param("Scelta", scelta)
            .param("Oggetto", oggetto)
            .param("Testo", testo)
            .param("RuoloSelezionato", ruolo)
            .param("Matricola", matricola)
            .cookie(new Cookie("loggedAdmin", cookieAdmin)))
            .andExpect(status().isOk())
            .andExpect(view().name("Bacheca/reload"))
            .andExpect(model().attribute("loggedAdminOn", true));
    }
}