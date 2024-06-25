package com.progetto.sitoforzearmate.endToEnd;

import com.progetto.sitoforzearmate.model.dao.Bando.BandoDAO;
import com.progetto.sitoforzearmate.model.dao.DAOFactory;
import com.progetto.sitoforzearmate.model.mo.Bando;
import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
// import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalendarioSeleniumTest {
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
                .withRecordingMode(
                        BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL,
                        new File(Configuration.getDIRECTORY_FILE() + "recordings" + File.separator),
                        VncRecordingContainer.VncRecordingFormat.MP4
                )
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
    @Order(1)
    public void testViewCalendario(){
        String url = sito.getNetworkAliases().iterator().next();
        driver.get("http://" + url + ":8080/homepage");

        driver.findElement(By.id("Calendario")).click();

        String expected = "http://" + url.toLowerCase() + ":8080/viewCalendario";
        assertEquals(expected, driver.getCurrentUrl());
    }

    @Test
    @Order(2)
    public void testCreateBando(){
        String url = sito.getNetworkAliases().iterator().next();
        loginAdmin(url);

        LocalDate oggi = LocalDate.now();

        driver.findElement(By.id("Calendario")).click();

        WebElement formNew = driver.findElement(By.cssSelector("form[name='newBando']"));
        formNew.findElement(By.cssSelector("input[type='submit']")).click();

        String expected = "http://" + url.toLowerCase() + ":8080/modificaBandoView";
        assertEquals(expected, driver.getCurrentUrl());

        WebElement formIns = driver.findElement(By.cssSelector("form[action='/inserisciBando']"));
        driver.findElement(By.id("oggettoBando")).sendKeys("Bando di prova");
        driver.findElement(By.id("numMaxIscritti")).sendKeys("1");
        driver.findElement(By.id("DataScadenza")).sendKeys(oggi.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        driver.findElement(By.id("DataBando")).sendKeys(oggi.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        WebElement selectForm = driver.findElement(By.id("base"));
        if(selectForm.isDisplayed()){
            selectForm.sendKeys("Pisa");
        }
        driver.findElement(By.id("insBando")).sendKeys("/home/raccolta_file/B3");

        formIns.findElement(By.cssSelector("input[type='submit']")).click();

        expected = "http://" + url.toLowerCase() + ":8080/inserisciBando";
        assertEquals(expected, driver.getCurrentUrl());
    }

    @Test
    @Order(3)
    public void testIscrizioneBando(){
        String url = sito.getNetworkAliases().iterator().next();
        loginUser(url);

        driver.findElement(By.id("Calendario")).click();

        String idString = getLastId();

        WebElement formBando = driver.findElement(By.cssSelector("form[name='bandoView" + idString + "']"));
        formBando.findElement(By.cssSelector("input[type='submit']")).click();

        String expected = "http://" + url.toLowerCase() + ":8080/viewBando";
        assertEquals(expected, driver.getCurrentUrl());

        WebElement formIscrizione = driver.findElement(By.cssSelector("form[action='/iscrizione']"));
        formIscrizione.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement formAnnullaIscrizione = driver.findElement(By.cssSelector("form[action='/annullaIscrizione']"));

        assertEquals(formAnnullaIscrizione.findElement(By.cssSelector("input[type='submit']")).isDisplayed(), true);
    }

    @Test
    @Order(4)
    public void testModificaBando(){

    }

    @Test
    @Order(5)
    public void testCancellazioneBando(){

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

    private void loginAdmin(String url){
        driver.get("http://" + url + ":" + String.valueOf(8080) + "/viewLogin");

        driver.findElement(By.id("login-admin-button")).click();

        String expectedUrl = "http://" + url.toLowerCase() + ":8080/viewLoginAdmin";
        assertEquals(expectedUrl, driver.getCurrentUrl());

        WebElement usernameInput = driver.findElement(By.id("IdAdmin"));
        WebElement passwordInput = driver.findElement(By.id("Password"));

        usernameInput.sendKeys("1234567890");
        passwordInput.sendKeys("password1");

        driver.findElement(By.cssSelector("input[type='submit']")).click();

    }

    private String getLastId(){
        DAOFactory daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL,null);

        daoFactory.beginTransaction();
        BandoDAO bandoDAO = daoFactory.getBandoDAO();
        Integer Id = Integer.parseInt(bandoDAO.getLastId());
        daoFactory.commitTransaction();

        String idString = String.valueOf(Id);

        while(idString.length() < 10){
            idString = "0" + idString;
        }

        return idString;
    }
}