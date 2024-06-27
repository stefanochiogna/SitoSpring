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
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import io.github.bonigarcia.wdm.WebDriverManager;


@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrenotaAlloggioSeleniumTest {
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
    @Disabled
    public void testPrenotaAlloggio(){
        String url = sito.getNetworkAliases().iterator().next();
        loginUser(url);

        driver.findElement(By.id("ListaBasi")).click();

        WebElement form = driver.findElement(By.cssSelector("form[name='baseViewPisa']"));
        form.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement formBase = driver.findElement(By.cssSelector("form[name='prenotaAlloggioPisa']"));
        formBase.findElement(By.cssSelector("input[type='submit']")).click();

        assertEquals("http://"+ url.toLowerCase() +":8080/viewPrenotaAlloggi", driver.getCurrentUrl());

        WebElement formPrenota = driver.findElement(By.cssSelector("form[action='/confermaIscrizione']"));

        WebElement numPersone = driver.findElement(By.id("NumeroPersone"));
        if (numPersone.isDisplayed()) {
            numPersone.sendKeys("4");
        }
        WebElement numNotti = driver.findElement(By.id("NumeroNotti"));
        if (numNotti.isDisplayed()) {
            numNotti.sendKeys("4");
        }

        WebElement dataPrenotazione = driver.findElement(By.id("DataArrivo"));
        dataPrenotazione.sendKeys("01-01-2026");

        formPrenota.findElement(By.cssSelector("input[type='submit']")).click();

        System.out.println(driver.getPageSource());
        System.out.println(driver.getCurrentUrl());

        WebElement prenotazioneCorretta = driver.findElement(By.id("Conferma"));
        assertEquals(prenotazioneCorretta.isDisplayed(), true);
    }

    private void loginUser(String url){
        driver.get("http://" + url + ":8080/viewLogin");

        WebElement usernameInput = driver.findElement(By.id("Email"));
        WebElement passwordInput = driver.findElement(By.id("Password"));

        usernameInput.sendKeys("sara.tullini@edu.unife.it");
        passwordInput.sendKeys("password");

        // Invio del form
        WebElement submit = driver.findElement(By.id("login-button"));
        submit.click();

    }
}