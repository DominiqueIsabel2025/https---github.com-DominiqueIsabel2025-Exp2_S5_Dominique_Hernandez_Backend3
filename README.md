# Exp2 - Semana 5: Implementación del patrón Backend for Frontend (BFF)
**Desarrollo Backend III (PBY2203) — Duoc UC**
**Estudiante:** Dominique Hernández

## 1. Objetivo del proyecto

Implementar el patrón arquitectónico **Backend for Frontend (BFF)** para el Banco XYZ,
creando un backend personalizado para cada tipo de cliente:

- **BFF Web**: respuestas completas, ricas en detalle, para navegadores.
- **BFF Móvil**: respuestas livianas, con datos esenciales, para reducir consumo de datos.
- **BFF Cajeros Automáticos**: interfaz segura y mínima para operaciones críticas (saldo y retiro).

Los tres BFF consumen datos desde un servicio central **`core-banking-service`**, que
simula el sistema legacy/monolítico del Banco XYZ (equivalente al dataset
[`bank_legacy_data`](https://github.com/KariVillagran/bank_legacy_data) referenciado en
la actividad), implementado en Spring Boot con base de datos H2 en memoria para que el
proyecto sea 100% autocontenido y ejecutable sin dependencias externas.

## 2. Estrategia de implementación elegida

Las instrucciones específicas de la semana indican explícitamente: *"Configura un
Backend para cada uno de los tres clientes"* y *"Cada cliente deberá tener su propio
Backend"*. Esto corresponde a la **Estrategia 1: Backends independientes por tipo de
cliente**, vista en la guía de aprendizaje.

**Análisis de por qué esta estrategia es la adecuada para este caso:**

| Factor | Justificación |
| --- | --- |
| Personalización por canal | Web, Móvil y Cajero tienen necesidades de datos y seguridad completamente distintas (dashboard completo vs. payload liviano vs. operaciones críticas con autenticación propia). |
| Autonomía y mantenibilidad | Cada BFF puede evolucionar, escalar y desplegarse de forma independiente sin afectar a los otros canales. |
| Seguridad diferenciada | El canal Cajero requiere control de acceso (`X-ATM-KEY`) y reglas de negocio propias (límite diario de retiro) que no aplican a Web ni Móvil. |
| Escalabilidad individual | En un banco real, el tráfico de la app móvil suele ser mucho mayor que el de cajeros; separar los servicios permite escalar cada uno según su demanda real. |

Se descartó la estrategia de *Endpoints personalizados* (usada como ejemplo en la guía
para el caso del monolito de artículos) porque el requerimiento explícito de la pauta es
tener **tres backends independientes**, no rutas distintas dentro de un mismo servicio.
Tampoco se optó por *delegación con microservicios* como arquitectura interna de cada
BFF, ya que no existía previamente una malla de microservicios de negocio (solo el
sistema legacy), aunque sí se aplica el concepto de **integración y agregación de
información desde un servicio backend central**, cumpliendo el criterio 4 de la pauta.

## 3. Arquitectura

```
                         ┌───────────────────────────┐
                         │   core-banking-service     │
                         │   (legacy simulado, :8080) │
                         │   Clientes / Cuentas /     │
                         │   Transacciones (H2)       │
                         └─────────────┬─────────────┘
                    ┌──────────────────┼──────────────────┐
                    │                  │                   │
          ┌─────────▼────────┐ ┌───────▼────────┐ ┌───────▼─────────┐
          │  bff-web-service  │ │ bff-mobile-svc │ │ bff-atm-service  │
          │      :8081        │ │     :8082      │ │      :8083       │
          │  Dashboard         │ │  Resumen        │ │  Saldo + Retiro  │
          │  completo          │ │  liviano        │ │  (con API-Key)   │
          └─────────┬──────────┘ └───────┬────────┘ └────────┬─────────┘
                    │                    │                   │
              Frontend Web         App Móvil            Cajero ATM
```

Cada BFF es un **microservicio Spring Boot independiente**, con su propio `pom.xml`,
su propio puerto y su propia capa de transformación/DTOs. Todos se comunican con
`core-banking-service` vía HTTP (`RestClient`), lo que permite **integrar y agregar
información** desde el backend central y adaptarla a cada canal.

## 4. Estructura del proyecto

```
Exp2_S5_Dominique_Hernandez/
├── core-banking-service/     # Servicio legacy (datos base del Banco XYZ)
├── bff-web-service/          # BFF Web (:8081)
├── bff-mobile-service/       # BFF Móvil (:8082)
├── bff-atm-service/          # BFF Cajero Automático (:8083)
├── docs/
│   └── propuesta-tecnica.md
├── evidencia-ejecucion/
│   ├── comandos-prueba.sh
│   └── README.md
└── README.md
```

## 5. Endpoints implementados

### core-banking-service (:8080) — API legacy interna
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/legacy/clientes/{id}` | Datos crudos del cliente |
| GET | `/api/legacy/cuentas/{numeroCuenta}` | Datos crudos de la cuenta |
| GET | `/api/legacy/cuentas/cliente/{clienteId}` | Cuentas de un cliente |
| GET | `/api/legacy/transacciones/cuenta/{numeroCuenta}` | Historial completo |
| POST | `/api/legacy/cuentas/{numeroCuenta}/retiro` | Descuenta saldo (usado por BFF Cajero) |

### bff-web-service (:8081) — Canal Web
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/web/clientes/{clienteId}/dashboard` | Cliente + todas sus cuentas + historial **completo** de transacciones |

### bff-mobile-service (:8082) — Canal Móvil
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/mobile/cuentas/{numeroCuenta}/resumen` | Saldo disponible + **últimos 5** movimientos (payload mínimo) |

### bff-atm-service (:8083) — Canal Cajero Automático
> Requiere header `X-ATM-KEY: ATM-SECRET-2026` en todas las peticiones a `/atm/**`.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/atm/cuentas/{numeroCuenta}/saldo` | Solo saldo disponible y estado de la cuenta |
| POST | `/atm/cuentas/{numeroCuenta}/retiro` | Retiro con validación de límite diario y fondos |

## 6. Datos de prueba precargados

| Cliente | Cuenta | Tipo | Saldo inicial | Límite retiro cajero |
|---|---|---|---|---|
| Maria Fernanda Soto (id 1) | CTA-0001 | Corriente | $850.000 | $300.000 |
| Jose Luis Pizarro (id 2) | CTA-0002 | Ahorro | $1.250.000 | $200.000 |
| Comercial Andes SpA (id 3) | CTA-0003 | Corriente | $5.400.000 | $500.000 |

## 7. Cómo ejecutar el proyecto

Requisitos: **Java 17+** y **Maven 3.9+** instalados localmente (el core-banking-service
descarga sus dependencias desde Maven Central en la primera compilación).

Abrir **4 terminales** distintas, una por servicio, y ejecutar en cada una:

```bash
# Terminal 1 — Core Banking (debe iniciarse primero)
cd core-banking-service
mvn spring-boot:run

# Terminal 2 — BFF Web
cd bff-web-service
mvn spring-boot:run

# Terminal 3 — BFF Móvil
cd bff-mobile-service
mvn spring-boot:run

# Terminal 4 — BFF Cajero
cd bff-atm-service
mvn spring-boot:run
```

Cuando los 4 servicios estén arriba, ejecutar las pruebas descritas en
`evidencia-ejecucion/comandos-prueba.sh` (o importar los mismos `curl` en Postman) y
capturar pantalla de la consola y de las respuestas JSON para adjuntarlas como
evidencia de ejecución.

## 8. Notas técnicas

- Se usó `RestClient` (Spring 6 / Boot 3.3) en lugar de `RestTemplate` por ser el
  cliente HTTP síncrono recomendado actualmente por Spring.
- H2 en memoria evita depender de una base de datos externa, manteniendo el proyecto
  simple de levantar en cualquier equipo.
- El filtro `ApiKeyAuthFilter` del BFF Cajero es una simulación simplificada de
  autenticación/autorización específica por canal, tal como lo pide la actividad de la
  semana 4 y se mantiene como base en esta semana 5.
