#!/usr/bin/env bash
set -e

PORT=$1

echo "=== Ejecutando acceptance tests contra http://localhost:$PORT ==="

# Test 1: la pagina principal responde 200
STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/")
if [ "$STATUS" != "200" ]; then
  echo "FALLO: la pagina principal respondio $STATUS (esperado 200)"
  exit 1
fi
echo "OK - Pagina principal responde 200"

# Test 2: division exitosa
RESP=$(curl -s "http://localhost:$PORT/dividir?a=10&b=2")
if [[ "$RESP" != *"5.0"* ]]; then
  echo "FALLO: division 10/2 no devolvio 5.0. Respuesta: $RESP"
  exit 1
fi
echo "OK - Division 10/2 devuelve 5.0"

# Test 3: division por cero controlada (debe responder 400, no un error de servidor)
STATUS_ERR=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/dividir?a=10&b=0")
if [ "$STATUS_ERR" != "400" ]; then
  echo "FALLO: division por cero devolvio $STATUS_ERR (esperado 400)"
  exit 1
fi
echo "OK - Division por cero controlada (400)"

echo "=== Acceptance tests: 3/3 OK ==="