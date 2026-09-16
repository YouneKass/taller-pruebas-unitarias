#!/usr/bin/env bash
set -e

IMAGE=$1
STABLE_PORT=8080     # "blue" - version actualmente en produccion
CANDIDATE_PORT=8081  # "green" - version nueva, en validacion

echo "=== Desplegando candidata (green) en el puerto $CANDIDATE_PORT ==="
docker run -d --name app-green -p $CANDIDATE_PORT:8080 "$IMAGE"

echo "Esperando a que el contenedor este listo..."
sleep 3

if bash deploy/acceptance-tests.sh $CANDIDATE_PORT; then
  echo "=== Acceptance tests OK: promoviendo green a blue (produccion) ==="
  docker stop app-blue 2>/dev/null || true
  docker rm app-blue 2>/dev/null || true
  docker stop app-green
  docker rm app-green
  docker run -d --name app-blue -p $STABLE_PORT:8080 "$IMAGE"
  echo "Version $IMAGE promovida a produccion (puerto $STABLE_PORT)."
else
  echo "=== Acceptance tests FALLARON: ejecutando rollback automatico ==="
  docker stop app-green
  docker rm app-green
  echo "Rollback completo: la version candidata fue descartada. Produccion (blue) sigue intacta y sin interrupciones."
  exit 1
fi