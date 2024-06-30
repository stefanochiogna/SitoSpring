package com.progetto.sitoforzearmate.endToEnd;

import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
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
import org.testcontainers.utility.MountableFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import io.github.bonigarcia.wdm.WebDriverManager;


@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrenotaPastoSeleniumTest {
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

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");

        // Create DesiredCapabilities and set chromeOptions as 'goog:chromeOptions'
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("goog:chromeOptions", chromeOptions);


        chrome = (BrowserWebDriverContainer) new BrowserWebDriverContainer()
                .withCapabilities(capabilities)
                .dependsOn(sito)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("chrome")
                .withExposedPorts(4444)
                //.withFileSystemBind(Configuration.getDIRECTORY_FILE(), "/home/raccolta_file", BindMode.READ_ONLY);
                .withCopyFileToContainer(
                        MountableFile.forHostPath(Configuration.getDIRECTORY_FILE()),
                        "/home/raccolta_file"
                );
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
    public void testPrenotaPasto(){
        String url = sito.getNetworkAliases().iterator().next();
        loginUser(url);

        driver.findElement(By.id("ListaBasi")).click();

        WebElement form = driver.findElement(By.cssSelector("form[name='baseViewPisa']"));
        form.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement formBase = driver.findElement(By.cssSelector("form[name='prenotaPastoPisa']"));
        formBase.findElement(By.cssSelector("input[type='submit']")).click();

        assertEquals("http://"+ url.toLowerCase() +":8080/viewPrenotaPasto", driver.getCurrentUrl());

        WebElement formPrenota = driver.findElement(By.cssSelector("form[action='/confermaPrenotaPasto']"));

        WebElement turno = driver.findElement(By.id("Turno"));
        if (turno.isDisplayed()) {
            turno.sendKeys("Pranzo");
        }
        WebElement dataPrenotazione = driver.findElement(By.id("DataPrenotazione"));
        dataPrenotazione.sendKeys("01-01-2026");

        formPrenota.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement prenotazioneCorretta = driver.findElement(By.id("Conferma"));
        assertTrue(prenotazioneCorretta.isDisplayed());
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