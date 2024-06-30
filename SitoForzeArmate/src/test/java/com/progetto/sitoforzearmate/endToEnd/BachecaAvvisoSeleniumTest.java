package com.progetto.sitoforzearmate.endToEnd;

import com.progetto.sitoforzearmate.services.configuration.Configuration;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
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
import org.testcontainers.utility.MountableFile;

import static org.junit.jupiter.api.Assertions.*;
// import io.github.bonigarcia.wdm.WebDriverManager;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BachecaAvvisoSeleniumTest {
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


        ChromeOptions options = new ChromeOptions()
                .addArguments("--disable-dev-shm-usage");

        chrome = (BrowserWebDriverContainer) new BrowserWebDriverContainer()
                .withCapabilities(options)
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
    @Order(3)
    public void testInviaAvviso(){
        String url = sito.getNetworkAliases().iterator().next();
        loginAdmin(url);

        driver.findElement(By.id("Bacheca")).click();

        driver.findElement(By.cssSelector("a[href='/viewBachecaNewsletter']")).isDisplayed();

        assertEquals("http://"+ url.toLowerCase() +":8080/viewBachecaAvviso", driver.getCurrentUrl());

        WebElement formSend = driver.findElement(By.cssSelector("form[action='/inviaAvviso']"));

        driver.findElement(By.id("Tutti")).click();
        driver.findElement(By.id("Oggetto")).sendKeys("Avviso di test");
        driver.findElement(By.id("Testo")).sendKeys("Questo e' un avviso di test");
        
        formSend.findElement(By.cssSelector("input[type='submit']")).click();
        System.out.println(driver.getPageSource());
        assertEquals("http://"+ url.toLowerCase() +":8080/viewBachecaAvviso", driver.getCurrentUrl());
    }

    @Test 
    @Order(1)
    public void testViewAvviso(){
        String url = sito.getNetworkAliases().iterator().next();
        loginUser(url);

        driver.findElement(By.id("Bacheca")).click();

        driver.findElement(By.cssSelector("a[href='/viewBachecaNewsletter']")).isDisplayed();

        assertEquals("http://"+ url.toLowerCase() +":8080/viewBachecaAvviso", driver.getCurrentUrl());

        WebElement formView = driver.findElement(By.cssSelector("form[action='/viewAvviso']"));
        
        formView.findElement(By.cssSelector("input[type='submit']")).click();
        //System.out.println(driver.getPageSource());
        assertEquals("http://"+ url.toLowerCase() +":8080/viewAvviso", driver.getCurrentUrl());
        assertTrue(driver.findElement(By.cssSelector("a[href='/viewBachecaAvviso']")).isDisplayed());
    }

    @Test 
    @Order(2)
    public void testDeleteAvviso(){
        String url = sito.getNetworkAliases().iterator().next();
        loginUser(url);

        driver.findElement(By.id("Bacheca")).click();
        System.out.println(driver.getPageSource());
        driver.findElement(By.cssSelector("a[href='/viewBachecaNewsletter']")).isDisplayed();
        System.out.println(driver.getPageSource());
        WebElement formDelete = driver.findElement(By.cssSelector("form[name='avvisoDelete100000023']"));
        
        formDelete.findElement(By.cssSelector("input[type='submit']")).click();
        System.out.println(driver.getPageSource());
        assertEquals("http://"+ url.toLowerCase() +":8080/viewBachecaAvviso", driver.getCurrentUrl());
        assertTrue(driver.findElements(By.cssSelector("form[name='1000000023']")).isEmpty());
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