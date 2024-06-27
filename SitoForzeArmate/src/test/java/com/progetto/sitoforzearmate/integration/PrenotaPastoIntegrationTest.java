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

@Disabled
@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrenotaPastoIntegrationTest {
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

    @Test 
    public void integration_viewPasti() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String locazione = "Pisa";
        String cookieUser = "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&";

        this.mockMvc.perform(post("/viewPrenotaPasto")
                .param("locazioneBase", locazione)
                .cookie(new Cookie("loggedUser", cookieUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("ListaBasi/PrenotaPastoCSS"))
            .andExpect(model().attribute("loggedOn", true))
            .andExpect(model().attributeExists("locazionePasto"));
    }

    @Test 
    public void integration_confermaIscrizione() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser = "tllsra01m57a944b#2722674591#sara.tullini@edu.unife.it-0000000003&";
        String locazione = "Pisa";
        String turno = "A";
        String data = "2024-07-10";
        
        this.mockMvc.perform(post("/confermaPrenotaPasto")
                .param("locazionePasto", locazione)
                .param("Turno", turno)
                .param("DataPrenotazione", data)
                .cookie(new Cookie("loggedUser", cookieUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("ListaBasi/ConfermaCSS"))
            .andExpect(model().attribute("loggedOn", true))
            .andExpect(model().attributeExists("luogoBase"))
            .andExpect(model().attributeExists("Pasto"));
    }
}