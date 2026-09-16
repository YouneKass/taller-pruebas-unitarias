package cl.iplacex.automatizacion;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servidor HTTP minimo para exponer la funcionalidad de division
 * (usado para pruebas de performance con JMeter y pruebas de
 * integracion/UI con Selenium). No usa frameworks externos.
 */
public class ApiServer {

  private static final Calculadora calculadora = new Calculadora();

  private static final String PAGINA_HTML = """
      <!DOCTYPE html>
      <html lang="es">
      <head>
        <meta charset="UTF-8">
        <title>Calculadora - Division</title>
      </head>
      <body>
        <h1>Calculadora de Division</h1>
        <form id="formDivision">
          <label for="a">Dividendo (a):</label>
          <input type="number" id="a" name="a" step="any" required>
          <br><br>
          <label for="b">Divisor (b):</label>
          <input type="number" id="b" name="b" step="any" required>
          <br><br>
          <button type="submit" id="btnDividir">Dividir</button>
        </form>
        <p id="resultado"></p>

        <script>
          document.getElementById('formDivision').addEventListener('submit', async function (e) {
            e.preventDefault();
            const a = document.getElementById('a').value;
            const b = document.getElementById('b').value;
            const resultadoEl = document.getElementById('resultado');
            try {
              const resp = await fetch(`/dividir?a=${a}&b=${b}`);
              const data = await resp.json();
              if (resp.ok) {
                resultadoEl.textContent = 'Resultado: ' + data.resultado;
              } else {
                resultadoEl.textContent = 'Error: ' + data.error;
              }
            } catch (err) {
              resultadoEl.textContent = 'Error de conexion';
            }
          });
        </script>
      </body>
      </html>
      """;

  public static HttpServer iniciar(int puerto) throws IOException {
    HttpServer servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
    servidor.createContext("/", ApiServer::manejarIndex);
    servidor.createContext("/dividir", ApiServer::manejarDividir);
    servidor.setExecutor(null);
    servidor.start();
    return servidor;
  }

  public static void main(String[] args) throws IOException {
    int puerto = 8080;
    iniciar(puerto);
    System.out.println("Servidor escuchando en http://localhost:" + puerto);
  }

  private static void manejarIndex(HttpExchange exchange) throws IOException {
    if (!"/".equals(exchange.getRequestURI().getPath())) {
      exchange.sendResponseHeaders(404, -1);
      return;
    }
    byte[] cuerpo = PAGINA_HTML.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
    exchange.sendResponseHeaders(200, cuerpo.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(cuerpo);
    }
  }

  private static void manejarDividir(HttpExchange exchange) throws IOException {
    try {
      Map<String, String> parametros = parametrosDeQuery(exchange.getRequestURI());
      double a = Double.parseDouble(parametros.get("a"));
      double b = Double.parseDouble(parametros.get("b"));

      double resultado = calculadora.dividir(a, b);
      String respuesta = "{\"resultado\": " + resultado + "}";
      enviarRespuesta(exchange, 200, respuesta);

    } catch (ArithmeticException e) {
      enviarRespuesta(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
    } catch (Exception e) {
      enviarRespuesta(exchange, 400, "{\"error\": \"Parametros invalidos\"}");
    }
  }

  private static Map<String, String> parametrosDeQuery(URI uri) {
    String query = uri.getQuery();
    if (query == null) return Map.of();
    return java.util.Arrays.stream(query.split("&"))
        .map(p -> p.split("=", 2))
        .collect(Collectors.toMap(p -> p[0], p -> p[1]));
  }

  private static void enviarRespuesta(HttpExchange exchange, int codigo, String cuerpo) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(codigo, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }
}