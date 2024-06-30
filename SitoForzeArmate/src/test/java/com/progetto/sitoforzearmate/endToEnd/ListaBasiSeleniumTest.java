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

import static org.junit.jupiter.api.Assertions.*;
// import io.github.bonigarcia.wdm.WebDriverManager;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ListaBasiSeleniumTest {
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
    @Order(1)
    public void testViewListaBasi(){
        String url = sito.getNetworkAliases().iterator().next();
        loginUser(url);

        WebElement element = driver.findElement(By.id("ListaBasi"));
        element.click();

        assertEquals("http://"+ url.toLowerCase() +":8080/viewListaBasi", driver.getCurrentUrl());
        System.out.println(driver.getPageSource());
    }

    @Test
    @Order(2)
    public void testViewBase(){
        String url = sito.getNetworkAliases().iterator().next();
        loginUser(url);

        driver.findElement(By.id("ListaBasi")).click();

        WebElement form = driver.findElement(By.cssSelector("form[name='baseViewPisa']"));
        form.findElement(By.cssSelector("input[type='submit']")).click();

        assertEquals("http://"+ url.toLowerCase() +":8080/viewBase", driver.getCurrentUrl());
        assertTrue(driver.findElement(By.cssSelector("a[href='/viewListaBasi']")).isDisplayed());
        System.out.println(driver.getPageSource());
    }

    @Test
    @Order(3)
    public void testDeleteBase(){
        String url = sito.getNetworkAliases().iterator().next();
        loginAdmin(url);

        driver.findElement(By.id("ListaBasi")).click();

        WebElement form = driver.findElement(By.cssSelector("form[name='baseDeletePisa']"));
        form.findElement(By.cssSelector("input[type='submit']")).click();

        assertEquals("http://"+ url.toLowerCase() +":8080/viewListaBasi", driver.getCurrentUrl());
        assertTrue(driver.findElements(By.cssSelector("form[name='baseViewPisa']")).isEmpty());
        System.out.println(driver.getPageSource());
    }

    @Test
    @Order(4)
    public void testInsertBase(){
        String url = sito.getNetworkAliases().iterator().next();
        loginAdmin(url);

        driver.findElement(By.id("ListaBasi")).click();

        driver.findElement(By.cssSelector("a[href='/registraBase']")).click();

        assertEquals("http://"+ url.toLowerCase() +":8080/registraBase", driver.getCurrentUrl());

        WebElement formInsert = driver.findElement(By.cssSelector("form[action='/newBase']"));

        driver.findElement(By.id("Foto")).sendKeys("/home/raccolta_file/test/immagine_test.jpg");
        driver.findElement(By.id("Email")).sendKeys("email@test.com");
        driver.findElement(By.id("Telefono")).sendKeys("1234567890");
        driver.findElement(By.id("Locazione")).sendKeys("SummonerRift");
        driver.findElement(By.id("Provincia")).sendKeys("RIOT");
        driver.findElement(By.id("CAP")).sendKeys("12345");
        driver.findElement(By.id("Via")).sendKeys("Via di prova");
        driver.findElement(By.id("Latitudine")).sendKeys("0.0");
        driver.findElement(By.id("Longitudine")).sendKeys("0.0");

        formInsert.findElement(By.cssSelector("input[type='submit']")).click();
        assertTrue(driver.findElement(By.cssSelector("form[name='baseViewSummonerRift']")).isDisplayed());
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
        assertEquals("http://" + url.toLowerCase() + ":8080/homepage", driver.getCurrentUrl());
    }
}
