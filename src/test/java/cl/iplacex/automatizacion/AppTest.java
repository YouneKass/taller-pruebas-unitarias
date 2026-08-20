package cl.iplacex.automatizacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    
    private final Calculadora calculadora = new Calculadora();

    @Test
    @DisplayName("Sumar 2 + 3 debe ser igual a 5")
    void sumaDeDosEnterosPositivos() {
        int resultado = calculadora.sumar(2, 3);
        assertEquals(5, resultado);
    }

    @Test
    @DisplayName("Restar 20 - 10 debe ser igual a 10")
    void restaDeDosEnterosPositivos() {
        int resultado = calculadora.restar(20, 10);
        assertEquals(10, resultado);
    }
}
