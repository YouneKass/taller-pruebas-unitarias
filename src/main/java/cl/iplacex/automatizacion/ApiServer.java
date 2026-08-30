package cl.iplacex.automatizacion;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servidor HTTP minimo para exponer la funcionalidad de division
 * y poder ejecutar pruebas de performance con JMeter.
 * No usa frameworks externos (solo la libreria estandar de Java).
 */
public class ApiServer {

  private static final Calculadora calculadora = new Calculadora();

  public static void main(String[] args) throws IOException {
    int puerto = 8080;
    HttpServer servidor = HttpServer.create(new InetSocketAddress(puerto), 0);

    servidor.createContext("/dividir", ApiServer::manejarDividir);
    servidor.setExecutor(null); // usa un executor por defecto (single-threaded)
    servidor.start();

    System.out.println("Servidor escuchando en http://localhost:" + puerto);
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
    exchange.sendResponseHeaders(codigo, cuerpo.getBytes().length);
    OutputStream os = exchange.getResponseBody();
    os.write(cuerpo.getBytes());
    os.close();
  }
}