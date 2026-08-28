package cl.iplacex.automatizacion.steps;

import cl.iplacex.automatizacion.Calculadora;
import io.cucumber.java.ParameterType;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DivisionStepDefinitions {

  private final Calculadora calculadora = new Calculadora();
  private double dividendo;
  private double divisor;
  private double resultado;
  private Exception excepcionCapturada;

  // Tipo de parámetro propio: evita el parseo "localizado" que rompe los decimales
  @ParameterType("-?\\d+(?:\\.\\d+)?")
  public Double numero(String valor) {
    return Double.parseDouble(valor);
  }

  @Dado("que tengo el número {numero} y el número {numero}")
  public void queTengoElNumeroYElNumero(Double a, Double b) {
    this.dividendo = a;
    this.divisor = b;
  }

  @Cuando("divido el primer número por el segundo")
  public void divideElPrimerNumeroPorElSegundo() {
    resultado = calculadora.dividir(dividendo, divisor);
  }

  @Cuando("intento dividir el primer número por el segundo")
  public void intentoDividirElPrimerNumeroPorElSegundo() {
    excepcionCapturada = null;
    try {
      calculadora.dividir(dividendo, divisor);
    } catch (Exception e) {
      excepcionCapturada = e;
    }
  }

  @Entonces("el resultado debe ser {numero}")
  public void elResultadoDebeSer(Double resultadoEsperado) {
    assertEquals(resultadoEsperado, resultado, 0.0001);
  }

  @Entonces("debe lanzarse una excepción de tipo aritmética")
  public void debeLanzarseUnaExcepcionDeTipoAritmetica() {
    assertNotNull(excepcionCapturada, "Se esperaba que se lanzara una excepción");
    assertTrue(excepcionCapturada instanceof ArithmeticException);
  }
}