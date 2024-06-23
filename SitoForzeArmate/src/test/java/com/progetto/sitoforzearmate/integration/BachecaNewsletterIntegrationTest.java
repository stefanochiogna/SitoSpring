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
public class BachecaNewsletterIntegrationTest {
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
        "'',chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&,false,true"    
    })
    public void testViewBachecaNewsletter(String cookieAdmin, String cookieUser, boolean loggedAdminOn, boolean loggedUserOn) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        /*if( !cookieAdmin.equals("") ){ 
            var requestBuilder = get("/viewBachecaNewsletter")
                .cookie(new Cookie("loggedAdmin", cookieAdmin));
        }
        else if ( !cookieUser.equals("") ){ 
            var requestBuilder = get("/viewBachecaNewsletter")
                .cookie(new Cookie("loggedUser", cookieUser)); // e tolgo queste righe vabb
        }*/

        this.mockMvc.perform(get("/viewBachecaNewsletter")
                .cookie(new Cookie("loggedAdmin", cookieAdmin))
                .cookie(new Cookie("loggedUser", cookieUser))) 
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("Bacheca/NewsletterCSS"))
                .andExpect(model().attribute("loggedAdminOn", loggedAdminOn))
                .andExpect(model().attribute("loggedOn", loggedUserOn));

        if (loggedUserOn) { 
            this.mockMvc.perform(get("/viewBachecaNewsletter")
                            .cookie(new Cookie("loggedAdmin", cookieAdmin))
                            .cookie(new Cookie("loggedUser", cookieUser)))
                .andExpect(model().attributeExists("Newsletter"));
        }
    }


    @Test
    public void integration_viewNewsletter() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieUser = "chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&";
        String id = "200000001";

        this.mockMvc.perform(post("/viewNewsletter")
                .param("newsletterId", id)
                .cookie(new Cookie("loggedUser", cookieUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("Bacheca/viewNewsletterCSS"))
                .andExpect(model().attributeExists("NewsletterSelezionata"))
                .andExpect(model().attribute("loggedOn", true));
    }

    @Test
    public void integration_deleteNewsletter() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));
    
        String cookieUser = "chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&";
        String id = "200000001";

        this.mockMvc.perform(post("/deleteNewsletter")
            .param("newsletterId", id)
            .cookie(new Cookie("loggedUser", cookieUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("Bacheca/reload")) 
            .andExpect(model().attribute("loggedOn", true));
    }

    @Test 
    public void integration_inviaNewsletter() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String oggetto = "Integration test";
        String testo = "prova per integration test";

        this.mockMvc.perform(post("/inviaNewsletter")
            .param("Oggetto", oggetto)
            .param("Testo", testo)
            .cookie(new Cookie("loggedAdmin", cookieAdmin)))
            .andExpect(status().isOk())
            .andExpect(view().name("Bacheca/reload"))
            .andExpect(model().attribute("loggedAdminOn", true));
    }
}