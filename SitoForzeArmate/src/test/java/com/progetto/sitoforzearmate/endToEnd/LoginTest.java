package com.progetto.sitoforzearmate.endToEnd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
// import io.github.bonigarcia.wdm.WebDriverManager;

import static org.junit.jupiter.api.Assertions.*;
@Disabled
@ExtendWith(SpringExtension.class)
// @Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest{

    private WebDriver driver;

    /*
    @Container
    GenericContainer<?> mysql = new GenericContainer<>(DockerImageName.parse("stefanochiogna/db:latest"))
            .withExposedPorts(3306);

    @Container
    GenericContainer<?> selenium = new GenericContainer<>(DockerImageName.parse("selenium/standalone-chrome:latest"))
            .withExposedPorts(4444);
    */

    @BeforeAll
    public void setUpAll() {

    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/viewLogin");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginSuccesso() {
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