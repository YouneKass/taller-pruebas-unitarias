# Sesión Three Amigos — Funcionalidad: División

**Proyecto:** taller-pruebas-unitarias
**Funcionalidad discutida:** Nuevo método `dividir(a, b)` en la clase `Calculadora`
**Roles simulados:** Negocio (PO), Desarrollo, QA

---

## Contexto

Antes de implementar la operación de división en `Calculadora`, se simula una sesión
Three Amigos para alinear expectativas entre negocio, desarrollo y QA, y detectar
casos borde antes de escribir código.

## Participantes y aportes

**Negocio (Product Owner):**
Pide agregar la operación de división a la calculadora, igual que ya existen suma y resta.
Espera que reciba dos números y retorne el resultado de dividir el primero por el segundo.

**Desarrollo:**
Plantea la pregunta clave: ¿qué pasa si el divisor es 0? Matemáticamente no está definido,
y en Java una división entera por cero lanza `ArithmeticException`, mientras que con
`double` retorna `Infinity` o `NaN` sin lanzar excepción. Hay que decidir el comportamiento
esperado explícitamente, no dejarlo al comportamiento por defecto del lenguaje.

**QA:**
Señala que este es exactamente el tipo de caso que se debe cubrir con una prueba dedicada,
y no solo probar el "camino feliz" (división de dos números normales). Propone además
probar división con resultado decimal, para verificar precisión.

## Preguntas y resolución

| Pregunta | Resolución acordada |
|---|---|
| ¿Qué pasa si el divisor es 0? | Se lanza una excepción controlada (`ArithmeticException`) con un mensaje claro, en vez de dejar que el programa retorne un valor ambiguo. |
| ¿La división debe soportar decimales? | Sí, el resultado se retorna como `double` para no perder precisión. |
| ¿Se valida el tipo de dato de entrada? | No es necesario, ya que los parámetros son tipados (`double`) a nivel de firma del método. |

## Casos de prueba identificados

A partir de esta discusión, se acordaron los siguientes escenarios a cubrir con Gherkin/BDD:

1. **Camino feliz:** dividir dos números donde el resultado es exacto (ej. 10 / 2 = 5).
2. **Resultado decimal:** dividir dos números donde el resultado no es un entero (ej. 7 / 2 = 3.5).
3. **Caso borde — división por cero:** dividir cualquier número por 0 debe lanzar una excepción,
   no retornar un valor numérico.

Estos tres escenarios se implementan en el siguiente paso como archivo `.feature` en Gherkin.