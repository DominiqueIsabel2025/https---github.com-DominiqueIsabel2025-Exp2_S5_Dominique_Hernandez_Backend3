# Propuesta técnica — BFF Banco XYZ

## 1. Contexto
El Banco XYZ requiere optimizar la comunicación entre su backend y tres frontends con
necesidades muy distintas: aplicación web, aplicación móvil y cajeros automáticos.
Actualmente (según la actividad de la semana 4) se identificó un backend monolítico
sin ninguna estrategia BFF aplicada.

## 2. Estrategia seleccionada: Backends independientes por canal
Justificada en detalle en el `README.md` principal, sección 2. En resumen: la pauta y
las instrucciones específicas de la semana exigen explícitamente un backend propio por
canal, lo cual coincide con la Estrategia 1 de la guía de aprendizaje.

## 3. Diseño de cada BFF

### BFF Web
- **Objetivo:** soportar interfaces complejas (dashboards, gráficos).
- **Transformación:** agrega cliente + todas sus cuentas + historial completo de
  transacciones en una sola respuesta (`DashboardWebResponse`), evitando múltiples
  llamadas desde el frontend.
- **Sin restricción de tamaño de payload.**

### BFF Móvil
- **Objetivo:** minimizar consumo de datos y batería.
- **Transformación:** reduce cada transacción a solo tipo, monto, fecha y detalle;
  limita a las últimas 5; omite todos los datos personales del cliente que no son
  necesarios para la pantalla de resumen.

### BFF Cajero Automático
- **Objetivo:** máxima seguridad y mínima superficie de datos expuesta.
- **Transformación:** solo expone saldo y estado de cuenta; para retiros, valida un
  límite diario propio del canal antes de delegar la operación al backend legacy.
- **Seguridad:** filtro `ApiKeyAuthFilter` que exige el header `X-ATM-KEY` en todas las
  rutas `/atm/**`, simulando la autenticación de tarjeta + PIN.

## 4. Integración y agregación con el backend

Cada BFF mantiene un cliente HTTP propio (`CoreBankingClient`) hacia
`core-banking-service`, que expone los datos "crudos" bajo `/api/legacy/**`. Esto
permite que la lógica de transformación y agregación viva exclusivamente en la capa
BFF, dejando el backend central agnóstico de los canales que lo consumen — un
requisito clave del patrón BFF.

## 5. Escalabilidad y modularidad
- Cada servicio es un proyecto Maven independiente, con su propio ciclo de vida,
  despliegue y escalado horizontal.
- Los DTOs de cada BFF son específicos de su canal, evitando acoplar los modelos de
  presentación entre canales.
- Agregar un nuevo canal (ej. Smart TV, Wearables) implicaría crear un nuevo módulo
  `bff-<canal>-service` sin tocar los BFF existentes ni el backend central.

## 6. Limitaciones y trabajo futuro
- La autenticación de Web y Móvil no se implementó en profundidad (fuera del alcance
  explícito de la pauta de esta semana), pero el mismo patrón de filtro usado en el
  BFF Cajero es extensible a los otros canales (ej. JWT).
- En un entorno productivo, `core-banking-service` sería reemplazado por el sistema
  legacy real del banco, y la comunicación podría añadir circuit breakers (Resilience4j)
  para tolerancia a fallos entre BFF y backend.
