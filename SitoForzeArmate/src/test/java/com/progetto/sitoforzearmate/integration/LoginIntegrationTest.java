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
public class LoginIntegrationTest {
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
    public void integration_viewLogin() throws Exception {
        this.mockMvc.perform(get("/viewLogin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("LoginCSS"));
    }

    @ParameterizedTest
    @CsvSource({
        "sara.tullini@edu.unife.it, ciao1",
        "sara.tullini@edu.unife.it, ciao"
    }) 
    public void integration_login(String email, String password) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        var requestBuilder = post("/loginUser")
            .param("Email", email)
            .param("Password", password);

        String emailCorretta = "sara.tullini@edu.unife.it";
        String passwordCorretta = "ciao1";

        if( (email == emailCorretta) && (password == passwordCorretta) ) {
            this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("loggedOn", true));
        }
        else if( (email != emailCorretta) || (password != passwordCorretta)) {
            this.mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("LoginCSS"))
            .andExpect(model().attribute("loggedOn", false));
        }
    }

    @Test
    public void integration_logout() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(get("/logout"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("loggedOn", false))
            .andExpect(model().attribute("loggedUser", null))
            .andExpect(model().attribute("loggedAdminOn", false))
            .andExpect(model().attribute("loggedAdmin", null));
    }

    @Test
    public void integration_viewRegistrazione() throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        this.mockMvc.perform(get("/viewRegistrazione"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("RegistrazioneCSS"))
            .andExpect(model().attributeExists("listaBasi"));
    }

    @ParameterizedTest
    @CsvSource({
        "Matilda,Toma,RSSMTL90H55F205,3245081441,matilda@gmail.com,passwordDiMatilda,F,2000-08-10,IT60A1234567890123456789012,Sottoufficiale,via la spezia 12,Pisa"
        "Matilda,Toma,RSSMTL90H55F205,3245081441,matilda@gmail.com,passwordDiMatilda,F,2000-08-10,'',Sottoufficiale,via la spezia 12,Pisa",
    }) 
    public void integration_registrazione(String nome, String cognome, String cf, String telefono, String email, String password, String sesso, String dataNascita, String iban, String ruolo, String indirizzo, String locazione) throws Exception {
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

        MockPart documentoFile = new MockPart(
            "Documento",
            "documento.jpg",
            "image/jpeg".getBytes()
        );

        MockPart documento = Mockito.mock(MockPart.class);
        Mockito.when(documento.getInputStream()).thenReturn(documentoFile.getInputStream());
        Mockito.when(documento.getSubmittedFileName()).thenReturn(documentoFile.getSubmittedFileName());
        Mockito.when(documento.getContentType()).thenReturn(documentoFile.getContentType());
        Mockito.when(documento.getSize()).thenReturn(documentoFile.getSize());
        Mockito.when(documento.getName()).thenReturn(documentoFile.getName());

        var requestBuilder = multipart("/registrazione")
            .part(foto)
            .part(documento)
            .param("Nome", nome)
            .param("Cognome", cognome)
            .param("CF", cf)
            .param("Telefono", telefono)
            .param("Email", email)
            .param("Password", password)
            .param("Sesso", sesso)
            .param("DataNascita", dataNascita)
            .param("IBAN", iban)
            .param("Ruolo", ruolo)
            .param("Indirizzo", indirizzo)
            .param("LocazioneServizio", locazione)
            .param("Newsletter", true)

        if(nome.equals("") || cognome.equals("") || cf.equals("") || telefono.equals("") || email.equals("") || password.equals("") || sesso.equals("") || dataNascita.equals("") || iban.equals("") || foto == null || documento == null || indirizzo.equals("") || locazione.equals("")){
            this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("LoginCSS"))
                .andExpect(model().attribute("loggedOn", false));
        }
        else {
            this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model()attribute("loggedOn", true));
        }
    }

    @Test
    public void integration_viewAmministratore() throws Exception {
        this.mockMvc.perform(get("/viewLoginAdmin"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("LoginAmministratoreCSS"));
    }

    @ParameterizedTest
    @CsvSource({
        "1234567890, password1",
        "1234567890, password"
    }) 
    public void integration_loginAdmin(String id, String password) throws Exception {
        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        var requestBuilder = post("/loginAdmin")
            .param("IdAdministrator", id)
            .param("Password", password);

        String idCorretto = "1234567890";
        String passwordCorretta = "password1";

        if( (id == idCorretto) && (password == passwordCorretta) ) {
            this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("loggedOn", true));
        }
        else if( (id != idCorretto) || (password != passwordCorretta) ) {
            this.mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("LoginAmministratoreCSS"))
            .andExpect(model().attribute("loggedOn", false));
        }
    }
}