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
import org.testcontainers.utility.DockerImageName;
// import io.github.bonigarcia.wdm.WebDriverManager;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(SpringExtension.class)
// @Testcontainers
@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegistrazioneTest {
    
    private WebDriver driver;

    @Container
    GenericContainer<?> mysql = new GenericContainer<>(DockerImageName.parse("stefanochiogna/db:latest"))
            .withExposedPorts(3306);

    @Container
    GenericContainer<?> selenium = new GenericContainer<>(DockerImageName.parse("selenium/standalone-chrome:latest"))
            .withExposedPorts(4444);

    @BeforeAll
    public void setUpAll() {

    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/viewRegistrazione");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testRegistrazioneSuccesso() {
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
            locazione.sendKeys("Base di Pisa");
        }
        
        // Carica file foto e documento
        driver.findElement(By.id("Foto")).sendKeys("path/to/foto.jpg"); 
        driver.findElement(By.id("Documento")).sendKeys("path/to/documento.pdf");
        
        // Seleziona Newsletter
        driver.findElement(By.id("Newsletter")).click();
        
        // Invio del form
        //driver.findElement(By.cssSelector("input[type='submit']")).click();
        
        // Verifica il risultato
        String expectedUrl = "http://localhost:8080/homepage";
        assertEquals(expectedUrl, driver.getCurrentUrl());
        
        // Altre asserzioni per verificare la registrazione riuscita
    }
}