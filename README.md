# Taller 1 - Actividad 1: Pruebas unitarias, Maven y CI con gitHub actions

Proyecto desarrollado para el Taller 1 (Evaluación Unidad N°II) de automatización de pruebas — IPLACEX.

## Objetivo del proyecto

Implementar un flujo básico de integración continua para un proyecto Java, incluyendo gestión
de versiones con Git, configuración de dependencias con Maven, pruebas unitarias atómicas con
JUnit 5, y un pipeline de integración continua con GitHub Actions.

## Estructura del proyecto

```
taller-pruebas-unitarias/
├── .github/
│   └── workflows/
│       └── ci.yml                        # Pipeline de integración continua (gitHub actions)
├── docs/
│   └── three-amigos-session.md           # Sesión three amigos simulada
├── performance/
│   ├── division-test-plan.jmx            # Test plan de JMeter
│   ├── resultados.jtl                    # Resultados crudos de la última ejecución
│   └── reporte-html/                     # Dashboard HTML generado por JMeter
├── src/
│   ├── main/java/cl/iplacex/automatizacion/
│   │   ├── App.java
│   │   ├── ApiServer.java                # Servidor HTTP minimo para pruebas de performance
│   │   └── Calculadora.java              # Clase con las operaciones a probar (sumar, restar, dividir)
│   └── test/
│       ├── java/cl/iplacex/automatizacion/
│       │   ├── AppTest.java              # Pruebas unitarias (JUnit 5)
│       │   ├── RunCucumberTest.java      # Runner que conecta JUnit platform con cucumber
│       │   └── steps/
│       │       └── DivisionStepDefinitions.java  # Step definitions (dado/cuando/entonces)
│       └── resources/features/
│           └── division.feature          # Escenarios Gherkin en español
├── .gitignore
└── pom.xml                               # Configuración de Maven y dependencias
```

## Tecnologías utilizadas

| Herramienta | versión | uso |
|---|---|---|
| Java (temurin) | 17 | lenguaje / runtime |
| Maven | 3.9.x | gestión de dependencias y build |
| JUnit | 5.10.2 (jupiter) | Framework de pruebas unitarias |
| Cucumber | 7.18.1 | Pruebas BDD basadas en Gherkin |
| Git | 2.55.x | Control de versiones |
| GitHub Actions | — | Integración continua (CI) |
| Apache JMeter | 5.6.3 | Pruebas de performance / carga |

## Cómo ejecutar el proyecto localmente

Clonar el repositorio y ejecutar las pruebas con Maven:

```bash
git clone https://github.com/YouneKass/taller-pruebas-unitarias.git
cd taller-pruebas-unitarias
mvn test
```

Resultado esperado: `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS`.

Para ver el árbol de dependencias resueltas por Maven:

```bash
mvn dependency:tree
```

## Pruebas unitarias implementadas

Se implementaron 2 pruebas unitarias atómicas e independientes sobre la clase `Calculadora`,
siguiendo el patrón Arrange-Act-Assert:

- `sumaDeDosEnterosPositivos()` — valida que `sumar(2, 3)` retorne `5`.
- `restaDeDosEnterosPositivos()` — valida que `restar(20, 10)` retorne `10`.

Cada prueba verifica un único comportamiento y no depende del resultado de otra prueba.

## Pruebas BDD con Cucumber (Gherkin)

Como parte de la Actividad 2, se incorporó una nueva funcionalidad (división) desarrollada
bajo un enfoque BDD (Behavior Driven Development), partiendo de una sesión simulada
**Three Amigos** (Negocio, Desarrollo y QA) documentada en [`docs/three-amigos-session.md`](docs/three-amigos-session.md).

De esa sesión se identificaron 3 escenarios clave, incluyendo un caso borde (división por cero),
que se implementaron como pruebas Gherkin ejecutadas con Cucumber:

- **Camino feliz:** división exacta entre dos números.
- **Resultado decimal:** división parametrizada (Scenario Outline) con distintos pares de valores.
- **Caso borde:** división por cero, que debe lanzar una excepción controlada (`ArithmeticException`).

### Estructura BDD

```
src/test/
├── java/cl/iplacex/automatizacion/
│   ├── RunCucumberTest.java              # Runner que conecta JUnit Platform con Cucumber
│   └── steps/
│       └── DivisionStepDefinitions.java  # Step definitions (Dado/Cuando/Entonces)
└── resources/features/
    └── division.feature                  # Escenarios Gherkin en español
```

Al usar `cucumber-junit-platform-engine`, los escenarios Gherkin corren sobre el mismo motor
que los tests JUnit tradicionales, por lo que se ejecutan automáticamente con `mvn test`
—tanto en local como en el pipeline de CI— sin necesidad de configuración adicional en `ci.yml`.

### Ejecutar solo las pruebas BDD

```bash
mvn test -Dtest=RunCucumberTest
```

## Flujo de trabajo con Git

El proyecto sigue un flujo de ramas + Pull Request + merge:

1. Cada funcionalidad se desarrolla en una rama descriptiva (`feature/...`, `ci/...`, `fix/...`).
2. Los commits usan prefijos claros (`feat:`, `chore:`, `ci:`, `fix:`) describiendo el tipo de cambio.
3. Al terminar, se abre un Pull Request hacia `main`.
4. Tras revisar que no haya conflictos, se fusiona (merge) el PR.
5. La rama de feature se elimina una vez fusionada, para mantener el repositorio ordenado.

## Pipeline de integración Continua (`.github/workflows/ci.yml`)

El pipeline se dispara automáticamente en cada `push` o `pull request` dirigido a `main`, y ejecuta:

1. **Checkout** del código del repositorio.
2. **Configuración de JDK 17** (distribución Temurin), con caché de dependencias Maven para
   acelerar builds sucesivos.
3. **Compilación y ejecución de pruebas** (`mvn -B test`), incluyendo tanto los tests JUnit
   como los escenarios BDD de Cucumber (ambos corren sobre el mismo motor JUnit Platform).
4. **Publicación de un reporte navegable** de resultados de pruebas (a partir del XML que genera
   el plugin Surefire), visible directamente en la interfaz de GitHub Actions.
5. **Publicación del reporte HTML de Cucumber** como artefacto descargable (`reporte-cucumber-bdd`),
   con el detalle navegable de cada escenario BDD ejecutado.

Cada ejecución corre en un agente efímero (máquina virtual que se crea y destruye en cada corrida),
garantizando que las pruebas siempre se ejecuten en un ambiente limpio y reproducible.

## Evidencia de ejecución

### Ejecución local

```bash
$ mvn test
...
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------
[INFO] Total time:  6.282 s
```

### Ejecución en CI (GitHub Actions)

El pipeline se ejecutó exitosamente sobre `main` tras fusionar el fix de indentación del
workflow (run *"Merge pull request #3 from YouneKass/fix/pipeline-yaml-indentacion" #4*),
con una duración total de 20s y estado **Success**. El job `build-and-test` publica un
resumen navegable directamente en la pestaña *Summary* de Actions, confirmando
**tests: 2 passed**.

Las capturas de pantalla correspondientes a ambas ejecuciones (local y CI) se incluyen
como evidencia visual en el informe entregado junto a este repositorio.

## Prueba de Performance (JMeter)

### Funcionalidad probada

Se diseñó una prueba de carga básica sobre la funcionalidad clave `dividir()`, expuesta a
través de un servidor HTTP minimalista (`ApiServer.java`, usando solo la librería estándar
de Java, sin frameworks adicionales) que corre en `http://localhost:8080/dividir`.

### Diseño del test plan

Herramienta: **Apache JMeter 5.6.3**. Archivo: `performance/division-test-plan.jmx`.

- **20 usuarios virtuales concurrentes**, con una rampa de arranque (*ramp-up*) de 5 segundos.
- Cada usuario ejecuta **20 iteraciones**, generando 400 requests por endpoint (800 en total).
- Dos escenarios (*samplers*) evaluados en paralelo:
  - **Camino feliz:** `GET /dividir?a=10&b=2`
  - **Caso borde:** `GET /dividir?a=10&b=0` (división por cero, debe responder `400`)

### Ejecución

```bash
# Terminal 1: levantar el servidor
mvn exec:java

# Terminal 2: correr JMeter en modo consola con reporte HTML
jmeter -n -t performance/division-test-plan.jmx -l performance/resultados.jtl -e -o performance/reporte-html
```

### Indicadores monitoreados

- **Throughput (TPS):** transacciones por segundo que el servidor sostiene bajo esta carga.
- **Latencia:** tiempo de respuesta, medido en promedio, mediana y percentiles 90/95/99
  (los percentiles son más representativos que el promedio porque no se distorsionan con outliers).
- **Tasa de error:** porcentaje de respuestas fuera del rango HTTP 2xx/3xx.

### Resultados obtenidos

| Sampler | Samples | Error % | Avg (ms) | Mediana | 90 pct | 95 pct | 99 pct | Throughput (tx/s) |
|---|---|---|---|---|---|---|---|---|
| Camino Feliz | 400 | 0.00% | 1.10 | 1.00 | 2.00 | 2.00 | 3.00 | 85.02 |
| División por Cero | 400 | 100.00%* | 0.89 | 1.00 | 1.00 | 2.00 | 3.00 | 86.15 |
| **Total** | 800 | 50.00% | 0.99 | 1.00 | 1.00 | 2.00 | 3.00 | 170.00 |

*El 100% de error en este sampler es esperado: JMeter marca como fallo cualquier respuesta
no-2xx/3xx por defecto, y este endpoint responde intencionalmente `400` para rechazar la
división por cero. Confirma que la validación funciona de forma consistente bajo carga, no
indica una falla del sistema.

### Interpretación

Con 20 usuarios concurrentes, el servidor sostiene un throughput combinado de ~170 tx/s con
latencias muy bajas (mediana de 1 ms, percentil 99 de solo 3 ms), sin degradación observable
ni errores reales de conexión. El caso borde responde incluso levemente más rápido que el
camino feliz, ya que la validación corta el flujo antes de ejecutar la división.

Para un análisis de performance más realista, sería necesario aumentar significativamente la
concurrencia (cientos o miles de usuarios) y agregar un `Response Assertion` en JMeter que
distinga entre "falla real" (timeout, error 5xx, conexión rechazada) y "respuesta de negocio
esperada" (400 por validación), ya que actualmente ambas se contabilizan igual como "Error %".

## Dashboard de métricas (simulado)

Actualmente el pipeline expone sus resultados de dos formas independientes: el resumen de
tests unitarios/BDD generado por `dorny/test-reporter` (visible en la pestaña Summary de cada
run de Actions) y el reporte HTML de Cucumber, descargable como artefacto. Para consolidar
ambas fuentes —además de las métricas de performance— en un dashboard único y navegable,
la propuesta es la siguiente:

1. **Fuente de datos:** cada corrida del pipeline ya genera datos estructurados aprovechables:
   - `target/surefire-reports/*.xml` (resultados JUnit + Cucumber, vía JUnit Platform).
   - `performance/reporte-html/statistics.json` (métricas de JMeter: throughput, latencia, errores).

2. **Herramienta de consolidación:** un dashboard tipo **Allure Report** permitiría combinar
   resultados funcionales y de performance en una sola vista, con historial entre ejecuciones
   (tendencia de tiempos de respuesta y tasa de éxito a lo largo del tiempo). Alternativas más
   livianas: publicar los archivos HTML/JSON generados en **GitHub Pages** en cada push a `main`,
   o centralizarlos en una herramienta externa como **Grafana** (leyendo el JSON de JMeter vía
   un job programado) si el proyecto creciera a un contexto real de equipo.

3. **Paso adicional al pipeline (`ci.yml`)** que se agregaría para publicar automáticamente:

```yaml
   - name: Publicar dashboard en GitHub Pages
     uses: peaceiris/actions-gh-pages@v3
     if: github.ref == 'refs/heads/main'
     with:
       github_token: ${{ secrets.GITHUB_TOKEN }}
       publish_dir: ./performance/reporte-html
       destination_dir: dashboard/performance
```

   Esto dejaría el dashboard de performance accesible en una URL fija
   (`https://younekass.github.io/taller-pruebas-unitarias/dashboard/performance/`),
   actualizada automáticamente en cada merge a `main`, sin necesidad de descargar artefactos
   manualmente.

## Alertas automáticas ante fallos o degradaciones

Para un pipeline en un contexto de equipo real, se configurarían alertas automáticas en dos niveles:

**1. Fallos funcionales (tests unitarios/BDD en rojo):**
- GitHub actions ya notifica por correo al autor del commit/PR cuando un workflow falla
  (comportamiento nativo, sin configuración adicional).
- Para visibilidad de equipo, se agregaría un paso que notifique a un canal de **Slack o
  Discord** vía webhook cuando el job `build-and-test` termine en `failure()`:

```yaml
  - name: Notificar fallo en Slack
    if: failure()
    uses: slackapi/slack-github-action@v1
    with:
      webhook: ${{ secrets.SLACK_WEBHOOK_URL }}
      webhook-type: incoming-webhook
      payload: |
        {
          "text": "❌ Pipeline falló en ${{ github.ref_name }} — commit ${{ github.sha }}. Ver detalle: ${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}"
        }
```

**2. Degradaciones de performance (sin fallo explícito, pero con métricas peores):**
- Se agregaría un paso posterior a la ejecución de JMeter que compare el resultado actual
  contra un umbral fijo (o contra el resultado de la corrida anterior, guardado como artefacto),
  y falle el job si se supera:
  - Latencia promedio > 500 ms, o percentil 95 > 1000 ms.
  - Tasa de error real (excluyendo los 400 esperados) > 1%.
  - Throughput por debajo de un piso mínimo esperado.
- Esta comparación se puede automatizar con un pequeño script (Python o `jq` sobre el
  `statistics.json` de JMeter) que retorne código de salida distinto de 0 si se supera el
  umbral, haciendo que el step —y por lo tanto el job— falle y dispare la misma notificación
  de Slack del punto anterior.

De esta forma, tanto los fallos funcionales como las degradaciones silenciosas de performance
quedan cubiertas por el mismo canal de alerta, sin depender de que alguien revise manualmente
el dashboard.

## .gitignore

Se excluyen del control de versiones los artefactos generados automáticamente por Maven y las
configuraciones locales de editores:

```
target/
*.class
.idea/
*.iml
.vscode/
.DS_Store
```

## Estado del proyecto

- [x] Repositorio Git inicializado con historial de commits y ramas documentado.
- [x] Proyecto Maven configurado con JUnit 5.
- [x] 2 pruebas unitarias atómicas implementadas y pasando localmente.
- [x] Estructura de carpetas estándar de Maven.
- [x] `.gitignore` configurado.
- [x] Pipeline de GitHub Actions configurado (`ci.yml`).
- [x] Confirmación de ejecución exitosa del pipeline en GitHub Actions (run #4, 20s — ver evidencia abajo).
- [x] Reporte navegable de CI enlazado en este README (resumen "tests 2 passed" generado por dorny/test-reporter).
- [x] Sesión Three Amigos simulada y documentada.
- [x] Escenarios BDD/Gherkin implementados con Cucumber (incluyendo caso borde de división por cero).
- [x] Pipeline de CI valida automáticamente las pruebas BDD junto con las unitarias.
- [x] Prueba de performance diseñada y ejecutada con JMeter sobre la funcionalidad `dividir()`.
- [x] Indicadores de performance documentados (throughput, latencia, tasa de error).