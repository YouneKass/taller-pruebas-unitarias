package cl.iplacex.automatizacion;

import com.sun.net.httpserver.HttpServer;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integracion (UI end-to-end) con Selenium WebDriver:
 * levanta el ApiServer real, abre la pagina en Chrome (headless),
 * completa el formulario de division y verifica el resultado
 * mostrado en pantalla, cubriendo todo el flujo HTTP + backend + UI.
 */
class DivisionUiTest {

  private static final int PUERTO = 8099;
  private static HttpServer servidor;
  private static WebDriver driver;

  @BeforeAll
  static void iniciarEntorno() throws IOException {
    servidor = ApiServer.iniciar(PUERTO);

    WebDriverManager.chromedriver().setup();
    ChromeOptions opciones = new ChromeOptions();
    opciones.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
    driver = new ChromeDriver(opciones);
  }

  @AfterAll
  static void detenerEntorno() {
    if (driver != null) driver.quit();
    if (servidor != null) servidor.stop(0);
  }

  @Test
  @DisplayName("La UI muestra el resultado correcto de una division exacta")
  void divisionExactaMuestraResultadoEnPantalla() {
    driver.get("http://localhost:" + PUERTO + "/");

    driver.findElement(By.id("a")).sendKeys("10");
    driver.findElement(By.id("b")).sendKeys("2");
    driver.findElement(By.id("btnDividir")).click();

    new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.textToBePresentInElementLocated(By.id("resultado"), "Resultado"));

    assertTrue(driver.findElement(By.id("resultado")).getText().contains("Resultado: 5"));
  }

  @Test
  @DisplayName("La UI muestra un error al intentar dividir por cero")
  void divisionPorCeroMuestraErrorEnPantalla() {
    driver.get("http://localhost:" + PUERTO + "/");

    driver.findElement(By.id("a")).sendKeys("10");
    driver.findElement(By.id("b")).sendKeys("0");
    driver.findElement(By.id("btnDividir")).click();

    new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.textToBePresentInElementLocated(By.id("resultado"), "Error"));

    assertTrue(driver.findElement(By.id("resultado")).getText().contains("No se puede dividir por cero"));
  }
}