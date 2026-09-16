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
│       ├── ci.yml                        # Pipeline de integración continua
│       └── cd.yml                        # Pipeline de despliegue (Blue-Green)
├── deploy/
│   ├── acceptance-tests.sh               # Valida la version candidata antes de promoverla
│   └── deploy.sh                         # Orquesta el despliegue Blue-Green y el rollback
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
│       │   ├── DivisionUiTest.java       # Prueba de integración con Selenium (Examen Final)
│       │   ├── RunCucumberTest.java      # Runner que conecta JUnit platform con cucumber
│       │   └── steps/
│       │       └── DivisionStepDefinitions.java  # Step definitions (dado/cuando/entonces)
│       └── resources/features/
│           └── division.feature          # Escenarios Gherkin en español
├── Dockerfile                            # Build multi-etapa de la aplicacion
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
| Selenium | 4.24.0 | Pruebas de integración automatizada de interfaz web (Examen Final) |
| WebDriverManager | 5.9.2 | Gestión automática del driver de Chrome para Selenium |
| Git | 2.55.x | Control de versiones |
| GitHub Actions | — | Integración continua (CI) |
| Apache JMeter | 5.6.3 | Pruebas de performance / carga |
| Docker | 27.x | Containerización de la aplicación |

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

## Pruebas BDD con Cucumber (Gherkin) — Actividad 2 del Taller 1

Como parte de la Actividad 2 del Taller 1, se incorporó una nueva funcionalidad (división)
desarrollada bajo un enfoque BDD (Behavior Driven Development), partiendo de una sesión
simulada **Three Amigos** (Negocio, Desarrollo y QA) documentada en
[`docs/three-amigos-session.md`](docs/three-amigos-session.md).

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

## Pruebas de integración con Selenium — Actividad 2 del Examen Final

Como parte de la Actividad 2 del Examen Final, se extendió `ApiServer.java` (ya existente del
Taller 1) para servir además una página HTML simple en "/", con un formulario que llama al
endpoint `/dividir` mediante `fetch()`. El arranque del servidor se refactorizó a un método
público `ApiServer.iniciar(puerto)` que retorna el `HttpServer`, permitiendo levantarlo y
apagarlo de forma controlada desde los tests.

Se implementó `DivisionUiTest.java`: una prueba de integración con Selenium WebDriver (Chrome
en modo headless) que levanta el `ApiServer` real, abre la página, completa el formulario de
división simulando un usuario real, y verifica el resultado mostrado en pantalla — cubriendo
todo el flujo HTTP + backend + interfaz. Se implementaron 2 escenarios: división exacta y
división por cero.

Durante el desarrollo se detectó un detalle relevante: JavaScript no distingue enteros de
decimales al renderizar el resultado obtenido vía `fetch()`, por lo que `5.0` se muestra en
pantalla como `"5"`. La aserción del test se ajustó para verificar el texto realmente mostrado
(`"Resultado: 5"`), en vez de asumir el formato decimal que sí devuelve el backend.

```bash
mvn test -Dtest=DivisionUiTest
```

### Ejecución en CI

Como Selenium necesita un navegador real disponible, se agregó un paso al pipeline
(`.github/workflows/ci.yml`) que instala Google Chrome en el runner de Ubuntu antes de
ejecutar las pruebas:

```yaml
- name: Instalar Google Chrome (para pruebas Selenium)
  uses: browser-actions/setup-chrome@v1
```

WebDriverManager descarga automáticamente el chromedriver correspondiente, sin configuración
adicional. Con este cambio, `mvn test` corre un total de 8 pruebas (2 JUnit + 4 Cucumber/BDD +
2 Selenium), todas pasando tanto en local como en GitHub Actions.

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
3. **Compilación y ejecución de pruebas** (`mvn -B test`), incluyendo los tests JUnit, los
   escenarios BDD de Cucumber y las pruebas de integración con Selenium (todos corren sobre el
   mismo motor JUnit Platform).
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

### Ejecución en CI (gitHub actions)

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

## Actividad 3 (Examen Final): Deployment pipeline con estrategia Blue-Green

Como extensión de este mismo repositorio para el Examen Final de Automatización de Pruebas,
se implementó un pipeline de despliegue continuo (CD) con acceptance tests y una estrategia
Blue-Green con rollback automático.

### Containerización con Docker

Se creó un `Dockerfile` de build multi-etapa:

- **Etapa 1 (build):** usa `maven:3.9-eclipse-temurin-17` para compilar el proyecto
  (`mvn -B compile -DskipTests`), sin arrastrar las herramientas de build a la imagen final.
- **Etapa 2 (runtime):** usa `eclipse-temurin:17-jre-alpine`, una imagen liviana que solo
  contiene el JRE necesario para ejecutar la aplicación ya compilada, expuesta en el puerto 8080.

Este enfoque de dos etapas mantiene la imagen final pequeña, ya que no incluye Maven ni el
código fuente, solo los `.class` compilados y el runtime mínimo para correrlos.

```bash
docker build -t taller-pruebas-unitarias:local .
docker run -d --name app -p 8080:8080 taller-pruebas-unitarias:local
```

### Estrategia de despliegue: Blue-Green

Se eligió **Blue-Green** por sobre un despliegue Canary por una razón práctica: la aplicación
es un servicio único y sin estado (stateless) sirviendo un solo endpoint de cálculo, sin base
de datos ni sesiones que migrar entre versiones. En un escenario así, Canary (enviar solo un
porcentaje del tráfico a la nueva versión) agrega complejidad — necesitaría un balanceador o
proxy que reparta tráfico entre ambas versiones — sin aportar un beneficio real, porque no hay
manera de que la versión nueva "dañe" datos compartidos con la antigua. Blue-Green resuelve el
mismo problema (validar antes de exponer al 100% del tráfico) de forma más simple: la versión
nueva (green) se valida por completo, aislada, y solo si pasa se promueve de un swap.

- **`blue` (puerto 8080):** versión actualmente en producción, sirviendo tráfico real.
- **`green` (puerto 8081):** versión candidata, desplegada en paralelo para validarse antes de
  reemplazar a blue.

### Acceptance tests (`deploy/acceptance-tests.sh`)

Antes de promover cualquier versión candidata, se ejecutan 3 verificaciones contra ella,
directo sobre el endpoint (sin pasar por el navegador):

1. La página principal responde `200`.
2. `10 / 2` devuelve `5.0`.
3. `10 / 0` responde `400` (división por cero controlada, no un error 500 de servidor).

Si alguno de los tres falla, el script termina con código de salida distinto de 0, señal que
usa `deploy.sh` para decidir si promover o hacer rollback.

### Orquestación del despliegue y rollback (`deploy/deploy.sh`)

1. Levanta la imagen candidata como `app-green` en el puerto 8081.
2. Corre `acceptance-tests.sh` contra ese puerto.
3. **Si pasan:** elimina `app-blue` (la versión anterior) y `app-green`, y levanta la imagen
   nueva como `app-blue` en el puerto 8080 — esto es la promoción a producción.
4. **Si fallan:** elimina solo `app-green` y deja `app-blue` sin tocar — este es el rollback
   automático. La versión estable nunca se interrumpe, incluso si la candidata falla.

```bash
bash deploy/deploy.sh taller-pruebas-unitarias:local
```

### Evidencia de despliegue exitoso

*(Aquí van tus capturas: `deploy.sh` corriendo con acceptance tests 3/3 OK, y `docker ps`
mostrando `app-blue` en el puerto 8080.)*

### Evidencia de rollback

Para demostrar el rollback de forma real (no solo el camino feliz), se forzó una falla a
propósito modificando temporalmente `acceptance-tests.sh` para esperar un resultado incorrecto
en el test de división, y se corrió `deploy.sh` contra esa versión.

*(Aquí van tus capturas: el log mostrando "FALLO: division 10/2 no devolvio 99.0" seguido de
"Acceptance tests FALLARON: ejecutando rollback automatico", y "Rollback completo: la version
candidata fue descartada. Produccion (blue) sigue intacta y sin interrupciones.")*

Tras capturar la evidencia, el cambio se revirtió con `git checkout -- deploy/acceptance-tests.sh`,
confirmando con `git diff` que el archivo quedó idéntico al commit original.

### Pipeline de CD (`.github/workflows/cd.yml`)

Se agregó un segundo workflow de GitHub Actions, independiente del de CI, con trigger manual
(`workflow_dispatch`) en lugar de automático en cada push. Esta decisión fue deliberada: al
necesitar forzar fallas a propósito para generar evidencia de rollback, un trigger automático
habría desplegado en cada push accidental durante esa etapa de pruebas. El workflow:

1. Hace checkout del código.
2. Construye la imagen Docker (`docker build -t taller-pruebas-unitarias:${{ github.sha }} .`).
3. Da permisos de ejecución a los scripts de `deploy/`.
4. Corre `deploy.sh` con la imagen recién construida.

### Evidencia de ejecución en CI

*(Aquí va tu captura del run "CD - Despliegue Blue-Green" en GitHub Actions, en verde, con el
log expandido del paso "Ejecutar despliegue Blue-Green" mostrando los 3/3 acceptance tests OK
y la promoción a producción.)*

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
- [x] Pruebas de integración con Selenium implementadas (`DivisionUiTest.java`) — Actividad 2 del Examen Final.
- [x] Dockerfile multi-etapa creado y probado en local.
- [x] Scripts de despliegue Blue-Green (`acceptance-tests.sh` y `deploy.sh`) implementados.
- [x] Pipeline de CD (`cd.yml`) configurado con trigger manual y ejecutado exitosamente en GitHub Actions.
- [x] Evidencia real de rollback generada (falla forzada, versión estable intacta).