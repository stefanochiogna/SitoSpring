import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrazioneTest {
    
    private WebDriver driver;

    @BeforeAll 
    public void setUpAll() {
        WebDriverManager.chromedriver().setup();
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
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        
        // Verifica il risultato
        String expectedUrl = "http://localhost:8080/path-to-expected-page";
        assertEquals(expectedUrl, driver.getCurrentUrl());
        
        // Altre asserzioni per verificare la registrazione riuscita
    }
}