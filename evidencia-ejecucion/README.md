# Evidencia de ejecución

Este proyecto no puede compilarse ni ejecutarse dentro del entorno de generación de
esta entrega (sandbox sin acceso a Maven Central), por lo que **no se incluyen
capturas de pantalla reales**. Para generar la evidencia solicitada por la pauta:

1. Levanta los 4 servicios siguiendo el `README.md` de la raíz del proyecto (sección 7).
2. Ejecuta `bash comandos-prueba.sh` (requiere `curl` y `jq`), o importa las mismas
   peticiones en Postman/Insomnia.
3. Toma capturas de pantalla de:
   - Las 4 consolas mostrando `Started ...Application in X seconds`.
   - Cada respuesta JSON obtenida (dashboard web completo, resumen móvil liviano,
     rechazo 401 sin API key, saldo y retiro exitoso/rechazado en el cajero).
4. Guarda las capturas en esta misma carpeta (`evidencia-ejecucion/`) antes de
   comprimir la entrega final.

## Resultado esperado de cada prueba

1. **Cliente crudo (legacy):** JSON con `id, rut, nombreCompleto, email, telefono, segmento`.
2. **Dashboard Web:** JSON anidado con cliente + lista de cuentas, cada una con su
   `historialCompleto` de transacciones (payload grande, sin límite).
3. **Resumen Móvil:** JSON pequeño con `saldoDisponible` y solo 5 `ultimosMovimientos`.
4. **Cajero sin API key:** `HTTP 401` con body `{"error":"Acceso no autorizado..."}`.
5. **Cajero con API key (saldo):** JSON con `numeroCuenta, saldoDisponible, estado`.
6. **Retiro dentro del límite:** `HTTP 200`, `mensaje: "Retiro exitoso"`, saldo actualizado.
7. **Retiro sobre el límite:** `HTTP 403`, mensaje indicando el límite diario excedido.
