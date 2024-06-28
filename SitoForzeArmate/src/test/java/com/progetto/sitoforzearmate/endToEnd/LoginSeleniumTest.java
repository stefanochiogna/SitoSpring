package com.progetto.sitoforzearmate.endToEnd;

import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
// import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

// import software.xdev.testcontainers.selenium.containers.browser.BrowserWebDriverContainer;
// import software.xdev.testcontainers.selenium.containers.browser.CapabilitiesBrowserWebDriverContainer;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginSeleniumTest {

    private RemoteWebDriver driver;
    @Container
    private static GenericContainer<?> mysql = new GenericContainer<>(DockerImageName.parse("stefanochiogna/db:latest"))
            .withExposedPorts(3306)
            .withNetwork(Network.SHARED)
            .withNetworkAliases("mysql");
    @ClassRule
    public static GenericContainer<?> sito;

    @ClassRule
    public static BrowserWebDriverContainer chrome;

    @BeforeAll
    public static void setUpAll() {

        mysql.start();

        sito = new GenericContainer<>(DockerImageName.parse("stefanochiogna/forze_armate:latest"))
                .withExposedPorts(8080)
                .withEnv("DB_HOST", mysql.getNetworkAliases().iterator().next())
                .withEnv("DB_PORT", String.valueOf(3306))
                .dependsOn(mysql)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("forze_armate");


        chrome = (BrowserWebDriverContainer) new BrowserWebDriverContainer()
                .withCapabilities(new ChromeOptions())
                .dependsOn(sito)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("chrome")
                .withExposedPorts(4444)
                .withFileSystemBind(Configuration.getDIRECTORY_FILE(), "/home/raccolta_file", BindMode.READ_ONLY);


    }
    @AfterAll
    public static void tearDownAll() {
        mysql.stop();
    }

    @BeforeEach
    public void setUp() {
        sito.start();
        chrome.start();
        driver = chrome.getWebDriver();

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        sito.stop();
        chrome.stop();

    }

    @Test
    public void testLoginSuccesso() {
        String url = sito.getNetworkAliases().iterator().next();

        driver.get("http://" + url + ":" + String.valueOf(8080) + "/viewLogin");


        // Compila il form
        WebElement usernameInput = driver.findElement(By.id("Email"));
        WebElement passwordInput = driver.findElement(By.id("Password"));

        usernameInput.sendKeys("sara.tullini@edu.unife.it");
        passwordInput.sendKeys("password");

        // Invio del form
        WebElement submit = driver.findElement(By.id("login-button"));
        submit.click();

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        // Verifica il risultato
        String expectedUrl = "http://" + url.toLowerCase() + ":8080/homepage";
        assertEquals(expectedUrl, driver.getCurrentUrl());

        // Altre asserzioni per verificare la registrazione riuscita
        //System.out.println(driver.getPageSource());
    }
    @Test
    public void testLoginFailure(){
        String url = sito.getNetworkAliases().iterator().next();

        driver.get("http://" + url + ":" + String.valueOf(8080) + "/viewLogin");

        WebElement usernameInput = driver.findElement(By.id("Email"));
        WebElement passwordInput = driver.findElement(By.id("Password"));

        usernameInput.sendKeys("testing@mail.com");
        passwordInput.sendKeys("password");

        // Invio del form
        WebElement submit = driver.findElement(By.id("login-button"));
        submit.click();

        // Verifica il risultato
        String expectedUrl = "http://" + url.toLowerCase() + ":8080/loginUser";
        assertEquals(expectedUrl, driver.getCurrentUrl());
        //System.out.println(driver.getPageSource());
    }


    @Test
    public void testRegistrazioneSuccesso() {
        String url = sito.getNetworkAliases().iterator().next();
        driver.get("http://" + url + ":" + String.valueOf(8080) + "/viewRegistrazione");

        // Compila il form
        driver.findElement(By.id("Nome")).sendKeys("Mario");
        driver.findElement(By.id("Cognome")).sendKeys("Rossi");
        driver.findElement(By.id("Sesso")).sendKeys("M");
        driver.findElement(By.id("DataNascita")).sendKeys("1990-01-01");
        driver.findElement(By.id("CF")).sendKeys("RSSMRA90A01H501U");
        driver.findElement(By.id("Indirizzo")).sendKeys("Via Roma, 1");
        driver.findElement(By.id("Telefono")).sendKeys("1234567890");
        driver.findElement(By.id("Email")).sendKeys("mario.rossi@example.com");
        driver.findElement(By.id("Password")).sendKeys("password");
        driver.findElement(By.id("IBAN")).sendKeys("IT60X0542811101000000123456");
        driver.findElement(By.id("Ruolo")).sendKeys("Ufficiale");

        // Seleziona locazione servizio se visibile
        WebElement locazione = driver.findElement(By.id("LocazioneServizio"));
        if (locazione.isDisplayed()) {
            locazione.sendKeys("base aerea di Pisa");
        }

        // Carica file foto e documento
        driver.findElement(By.id("Foto")).sendKeys("/home/raccolta_file/test/immagine_test.jpg");
        driver.findElement(By.id("Documento")).sendKeys("/home/raccolta_file/test/immagine_test.jpg");

        // Seleziona Newsletter
        driver.findElement(By.id("Newsletter")).click();

        // Invio del form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        String expectedUrl = "http://" + url.toLowerCase() + ":8080/homepage";
        assertEquals(expectedUrl, driver.getCurrentUrl());
    }

    @Test
    public void testLoginAdminSuccesso() {
        String url = sito.getNetworkAliases().iterator().next();

        driver.get("http://" + url + ":" + String.valueOf(8080) + "/viewLogin");

        driver.findElement(By.id("login-admin-button")).click();

        String expectedUrl = "http://" + url.toLowerCase() + ":8080/viewLoginAdmin";
        assertEquals(expectedUrl, driver.getCurrentUrl());

        WebElement usernameInput = driver.findElement(By.id("IdAdmin"));
        WebElement passwordInput = driver.findElement(By.id("Password"));

        usernameInput.sendKeys("1234567890");
        passwordInput.sendKeys("password1");

        driver.findElement(By.cssSelector("input[type='submit']")).click();

        expectedUrl = "http://" + url.toLowerCase() + ":8080/homepage";
        assertEquals(expectedUrl, driver.getCurrentUrl());
        //System.out.println(driver.getPageSource());
    }
    @Test
    public void testLoginAdminFailure() {
        String url = sito.getNetworkAliases().iterator().next();

        driver.get("http://" + url + ":" + String.valueOf(8080) + "/viewLogin");

        driver.findElement(By.id("login-admin-button")).click();

        String expectedUrl = "http://" + url.toLowerCase() + ":8080/viewLoginAdmin";
        assertEquals(expectedUrl, driver.getCurrentUrl());

        WebElement usernameInput = driver.findElement(By.id("IdAdmin"));
        WebElement passwordInput = driver.findElement(By.id("Password"));

        usernameInput.sendKeys("1234567890");
        passwordInput.sendKeys("password");

        driver.findElement(By.cssSelector("input[type='submit']")).click();

        expectedUrl = "http://" + url.toLowerCase() + ":8080/loginAdmin";
        assertEquals(expectedUrl, driver.getCurrentUrl());
        //System.out.println(driver.getPageSource());
    }

}

