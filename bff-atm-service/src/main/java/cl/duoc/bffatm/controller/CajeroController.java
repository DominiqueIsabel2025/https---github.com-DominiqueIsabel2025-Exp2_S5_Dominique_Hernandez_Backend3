package cl.duoc.bffatm.controller;

import cl.duoc.bffatm.client.CoreBankingClient;
import cl.duoc.bffatm.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

/**
 * BFF Cajero Automatico: expone solo las 2 operaciones criticas que un
 * cajero necesita (consulta de saldo y retiro), con el minimo de datos
 * expuestos posible y validaciones adicionales de seguridad propias del
 * canal (limite diario de retiro), independientes de Web y Movil.
 * El acceso a "/atm/**" esta protegido por ApiKeyAuthFilter.
 */
@RestController
@RequestMapping("/atm/cuentas")
public class CajeroController {

    private final CoreBankingClient coreBankingClient;

    public CajeroController(CoreBankingClient coreBankingClient) {
        this.coreBankingClient = coreBankingClient;
    }

    @GetMapping("/{numeroCuenta}/saldo")
    public ResponseEntity<?> consultarSaldo(@PathVariable String numeroCuenta) {
        CuentaLegacyDTO cuenta = coreBankingClient.obtenerCuenta(numeroCuenta);
        if (cuenta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SaldoCajeroResponse(cuenta.numeroCuenta(), cuenta.saldoDisponible(), cuenta.estado()));
    }

    @PostMapping("/{numeroCuenta}/retiro")
    public ResponseEntity<?> retirar(@PathVariable String numeroCuenta, @RequestBody RetiroCajeroRequest request) {
        CuentaLegacyDTO cuenta = coreBankingClient.obtenerCuenta(numeroCuenta);
        if (cuenta == null) {
            return ResponseEntity.notFound().build();
        }

        // Regla especifica del canal Cajero: no superar el limite diario configurado.
        if (request.monto().compareTo(cuenta.limiteRetiroDiarioCajero()) > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new RetiroCajeroResponse(numeroCuenta, request.monto(), cuenta.saldoDisponible(),
                            "Monto excede el limite diario de retiro por cajero ($" + cuenta.limiteRetiroDiarioCajero() + ")"));
        }

        try {
            CuentaLegacyDTO cuentaActualizada = coreBankingClient.retirar(numeroCuenta, request.monto());
            return ResponseEntity.ok(new RetiroCajeroResponse(
                    numeroCuenta, request.monto(), cuentaActualizada.saldoDisponible(), "Retiro exitoso"));
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(new RetiroCajeroResponse(numeroCuenta, request.monto(), cuenta.saldoDisponible(), ex.getResponseBodyAsString()));
        }
    }
}
