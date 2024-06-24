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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class ListaBasiIntegrationTest {
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
    public void testViewListaBasi(String cookieAdmin, String cookieUser, boolean loggedAdminOn, boolean loggedUserOn) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(get("/viewListaBasi")
                .cookie(new Cookie("loggedAdmin", cookieAdmin))
                .cookie(new Cookie("loggedUser", cookieUser))) 
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ListaBasi/Lista"))
                .andExpect(model().attribute("loggedAdminOn", loggedAdminOn))
                .andExpect(model().attribute("loggedOn", loggedUserOn))
                .andExpect(model().attributeExists("Basi"));

        if (loggedUserOn) { 
            this.mockMvc.perform(get("/viewBachecaNewsletter")
                            .cookie(new Cookie("loggedAdmin", cookieAdmin))
                            .cookie(new Cookie("loggedUser", cookieUser)))
                .andExpect(model().attributeExists("Newsletter"));
        }
    }


    @ParameterizedTest
    @CsvSource({
        "nonlosoquanticar#1234567890#ciao1,'',true,false,Pisa",   
        "'',chslnz01l23h620q#0000163634#lorenzo.chesta@edu.unife.it-&,false,true,Pisa"    
    })
    public void integration_viewBase(String cookieAdmin, String cookieUser, boolean loggedAdmin, boolean loggedUser, String locazione) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(post("/viewBase")
                .cookie(new Cookie("loggedUser", cookieUser))
                .cookie(new Cookie("loggedAdmin", cookieAdmin))
                .param("luogoBase", locazione))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ListaBasi/viewBase"))
                .andExpect(model().attributeExists("BaseSelezionata"))
                .andExpect(model().attribute("loggedOn", loggedUser))
                .andExpect(model().attribute("loggedAdminOn", loggedAdmin));
    }

    @Test
    public void integration_deleteBase() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));
    
        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String locazione = "Pisa";

        this.mockMvc.perform(post("/deleteBase")
            .param("luogoBase", locazione)
            .cookie(new Cookie("loggedAdmin", cookieAdmin)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("ListaBasi/reload")) 
            .andExpect(model().attribute("loggedAdminOn", true))
            .andExpect(model().attributeExists("BaseSelezionata"));
    }

    @Test 
    public void integration_registraBase() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";

        this.mockMvc.perform(get("/registraBase")
            .cookie(new Cookie("loggedAdmin", cookieAdmin)))
            .andExpect(status().isOk())
            .andExpect(view().name("ListaBasi/NewBaseCSS"))
            .andExpect(model().attribute("loggedAdminOn", true));
    }

    @Test 
    public void integration_newBase() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        MockPart fotoFile = new MockPart(
            "Foto",
            "foto.jpg",
            "image/jpeg".getBytes()
        );

        MockPart foto = Mockito.mock(MockPart.class);
        Mockito.when(foto.getInputStream()).thenReturn(fotoFile.getInputStream());
        Mockito.when(foto.getSubmittedFileName()).thenReturn(fotoFile.getSubmittedFileName());
        Mockito.when(foto.getContentType()).thenReturn(fotoFile.getContentType());
        Mockito.when(foto.getSize()).thenReturn(fotoFile.getSize());
        Mockito.when(foto.getName()).thenReturn(fotoFile.getName());

        String cookieAdmin = "nonlosoquanticar#1234567890#ciao1";
        String email = "marsala@gmail.com";
        String telefono = "3245674311";
        String locazione = "Marsala";
        String provincia = "Trapani";
        String cap = "42041";
        String via = "via test";
        String latitudine = "0";
        String longitudine = "0";

        this.mockMvc.perform(multipart("/newBase")
            .part(foto)
            .cookie(new Cookie("loggedAdmin", cookieAdmin))
            .param("Email", email)
            .param("Telefono", telefono)
            .param("Locazione", locazione)
            .param("Provincia", provincia)
            .param("CAP", cap)
            .param("Via", via)
            .param("Latitudine", latitudine)
            .param("Longitudine", longitudine))
            .andExpect(status().isOk())
            .andExpect(view().name("ListaBasi/reload"))
            .andExpect(model().attribute("loggedAdminOn", true));
    }
}