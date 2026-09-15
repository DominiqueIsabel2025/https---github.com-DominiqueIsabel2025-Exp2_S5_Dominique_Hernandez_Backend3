#!/bin/bash
# Script de pruebas manuales para generar evidencia de ejecucion.
# Ejecutar SOLO despues de levantar los 4 servicios (ver README principal).

echo "==== 1) CORE BANKING - cliente crudo ===="
curl -s http://localhost:8080/api/legacy/clientes/1 | jq .

echo -e "\n==== 2) BFF WEB - dashboard completo del cliente 1 ===="
curl -s http://localhost:8081/web/clientes/1/dashboard | jq .

echo -e "\n==== 3) BFF MOVIL - resumen liviano de CTA-0001 ===="
curl -s http://localhost:8082/mobile/cuentas/CTA-0001/resumen | jq .

echo -e "\n==== 4) BFF CAJERO - consulta de saldo (sin API key -> debe fallar 401) ===="
curl -s -o /dev/null -w "HTTP %{http_code}\n" http://localhost:8083/atm/cuentas/CTA-0001/saldo

echo -e "\n==== 5) BFF CAJERO - consulta de saldo (con API key) ===="
curl -s -H "X-ATM-KEY: ATM-SECRET-2026" http://localhost:8083/atm/cuentas/CTA-0001/saldo | jq .

echo -e "\n==== 6) BFF CAJERO - retiro dentro del limite diario ($50.000) ===="
curl -s -X POST -H "X-ATM-KEY: ATM-SECRET-2026" -H "Content-Type: application/json" \
  -d '{"monto": 50000}' \
  http://localhost:8083/atm/cuentas/CTA-0001/retiro | jq .

echo -e "\n==== 7) BFF CAJERO - retiro que EXCEDE el limite diario ($500.000 > $300.000) ===="
curl -s -X POST -H "X-ATM-KEY: ATM-SECRET-2026" -H "Content-Type: application/json" \
  -d '{"monto": 500000}' \
  http://localhost:8083/atm/cuentas/CTA-0001/retiro | jq .
