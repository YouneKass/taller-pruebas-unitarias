# language: es
Característica: División de dos números
  Como usuario de la calculadora
  Quiero dividir dos números
  Para obtener el resultado de esa operación, incluyendo el manejo de casos borde

  Escenario: División exacta entre dos números
    Dado que tengo el número 10 y el número 2
    Cuando divido el primer número por el segundo
    Entonces el resultado debe ser 5.0

  Esquema del escenario: División con resultado decimal
    Dado que tengo el número <dividendo> y el número <divisor>
    Cuando divido el primer número por el segundo
    Entonces el resultado debe ser <resultado>

    Ejemplos:
      | dividendo | divisor | resultado |
      | 7         | 2       | 3.5       |
      | 5         | 4       | 1.25      |

  Escenario: División por cero lanza una excepción
    Dado que tengo el número 10 y el número 0
    Cuando intento dividir el primer número por el segundo
    Entonces debe lanzarse una excepción de tipo aritmética