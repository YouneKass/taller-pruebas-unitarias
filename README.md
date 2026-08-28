# Taller 1 - Actividad 1: Pruebas Unitarias, Maven y CI con GitHub Actions

Proyecto desarrollado para el Taller 1 (Evaluación Unidad N°II) de Automatización de Pruebas — IPLACEX.

## Objetivo

Implementar un flujo básico de integración continua para un proyecto Java, incluyendo gestión
de versiones con Git, configuración de dependencias con Maven, pruebas unitarias atómicas con
JUnit 5, y un pipeline de integración continua con GitHub Actions.

## Estructura del proyecto

```
taller-pruebas-unitarias/
├── .github/
│   └── workflows/
│       └── ci.yml              # Pipeline de integración continua (GitHub Actions)
├── src/
│   ├── main/java/cl/iplacex/automatizacion/
│   │   ├── App.java
│   │   └── Calculadora.java    # Clase con las operaciones a probar
│   └── test/java/cl/iplacex/automatizacion/
│       └── AppTest.java        # Pruebas unitarias (JUnit 5)
├── .gitignore
└── pom.xml                     # Configuración de Maven y dependencias
```

## Tecnologías utilizadas

| Herramienta | Versión | Uso |
|---|---|---|
| Java (Temurin) | 17 | Lenguaje / runtime |
| Maven | 3.9.x | Gestión de dependencias y build |
| JUnit | 5.10.2 (Jupiter) | Framework de pruebas unitarias |
| Git | 2.55.x | Control de versiones |
| GitHub Actions | — | Integración continua (CI) |

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

## Flujo de trabajo con Git

El proyecto sigue un flujo de ramas + Pull Request + merge:

1. Cada funcionalidad se desarrolla en una rama descriptiva (`feature/...`, `ci/...`, `fix/...`).
2. Los commits usan prefijos claros (`feat:`, `chore:`, `ci:`, `fix:`) describiendo el tipo de cambio.
3. Al terminar, se abre un Pull Request hacia `main`.
4. Tras revisar que no haya conflictos, se fusiona (merge) el PR.
5. La rama de feature se elimina una vez fusionada, para mantener el repositorio ordenado.

## Pipeline de Integración Continua (`.github/workflows/ci.yml`)

El pipeline se dispara automáticamente en cada `push` o `pull request` dirigido a `main`, y ejecuta:

1. **Checkout** del código del repositorio.
2. **Configuración de JDK 17** (distribución Temurin), con caché de dependencias Maven para
   acelerar builds sucesivos.
3. **Compilación y ejecución de pruebas** (`mvn -B test`).
4. **Publicación de un reporte navegable** de resultados de pruebas (a partir del XML que genera
   el plugin Surefire), visible directamente en la interfaz de GitHub Actions.

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

