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
public class PaginaInizialeIntegrationTest {
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
    public void integration_init() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(view().name("index"));
    }

    @Test
    public void integration_homepage() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(get("/homepage"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("Pagina_InizialeCSS"))
                .andExpect(model().attributeExists("notizia1"))
                .andExpect(model().attributeExists("notizia2"))
                .andExpect(model().attributeExists("notizia3"))
                .andExpect(model().attributeExists("notizia4"));
    }

    @Test
    public void integration_modifyArticolo() throws Exception {
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

        String cookieAdminValue = "nonlosoquanticar#1234567890#ciao1";  
        String id = "0000000001";
        String oggetto = "Nuovo Oggetto";
        String idAdmin = "1234567890";

        this.mockMvc.perform(multipart("/modifyArticolo")
            .part(testo)
            .param("Id", id)
            .param("Oggetto", oggetto)
            .param("IdAdministrator", idAdmin)
            .cookie(new Cookie("loggedAdmin", cookieAdminValue)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("index")) 
            .andExpect(model().attributeExists("notizia1"))
            .andExpect(model().attribute("loggedAdminOn", true)) 
            .andExpect(model().attribute("loggedAdmin", notNullValue()));
    }

    @Test 
    public void integration_viewArt() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String id = "0000000001";

        this.mockMvc.perform(post("/viewArt")
            .param("Id", id))
            .andExpect(status().isOk())
            .andExpect(view().name("viewArticoloCSS"))
            .andExpect(model().attributeExists("NotiziaSelezionata"));
    }
}