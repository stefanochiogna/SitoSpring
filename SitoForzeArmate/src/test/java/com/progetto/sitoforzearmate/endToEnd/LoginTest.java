package com.progetto.sitoforzearmate.endToEnd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
// import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest{

    private WebDriver driver;


    @Container
    private static GenericContainer<?> mysql = new GenericContainer<>(DockerImageName.parse("stefanochiogna/db:latest"))
            .withExposedPorts(3306);

    @Container
    private static GenericContainer<?> sito = new GenericContainer<>(DockerImageName.parse("stefanochiogna/forze_armate:latest"))
            .withExposedPorts(8080)
            .withEnv("DB_HOST", mysql.getHost())
            .withEnv("DB_PORT", String.valueOf(mysql.getMappedPort(3306)))
            .dependsOn(mysql);

    @Container
    private static GenericContainer<?> selenium = new GenericContainer<>(DockerImageName.parse("selenium/standalone-chrome:latest"))
            .withExposedPorts(4444)
            .withReuse(true)
            .dependsOn(sito);


    @BeforeAll
    public static void setUpAll() {
        mysql.start();
        sito.start();
        selenium.start();
    }
    @AfterAll
    public static void tearDownAll() {
        mysql.stop();
        sito.stop();
        selenium.stop();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        try {
            URL url = new URL("http://" + selenium.getHost() + ":" + selenium.getMappedPort(4444) + "/wd/hub");
            driver = new RemoteWebDriver(url, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginSuccesso() {

        System.setProperty("host", mysql.getHost());
        System.setProperty("porta", String.valueOf(mysql.getMappedPort(3306)));

        System.out.println("http://" + sito.getHost() + ":" + sito.getMappedPort(8080) + "/viewLogin");

        ChromeOptions options = new ChromeOptions();
        try {
            URL url = new URL("http://" + selenium.getHost() + ":" + selenium.getMappedPort(4444) + "/wd/hub");
            driver = new RemoteWebDriver(url, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        driver.get("http://" + sito.getHost() + ":" + sito.getMappedPort(8080) + "/viewLogin");

        // Compila il form
        try {
            WebElement usernameInput = driver.findElement(By.id("Email"));
            WebElement passwordInput = driver.findElement(By.id("Password"));

            usernameInput.sendKeys("sara.tullini@edu.unife.it");
            Thread.sleep(1000);
            passwordInput.sendKeys("password");
            Thread.sleep(1000);
            // Invio del form
            WebElement submit = driver.findElement(By.id("login-button"));
            submit.click();

            // Verifica il risultato
            String expectedUrl = "http://localhost:8080/homepage";
            assertEquals(expectedUrl, driver.getCurrentUrl());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Altre asserzioni per verificare la registrazione riuscita
    }
}