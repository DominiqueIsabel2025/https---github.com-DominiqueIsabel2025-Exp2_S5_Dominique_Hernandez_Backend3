package cl.duoc.corebanking.controller;

import cl.duoc.corebanking.model.Cuenta;
import cl.duoc.corebanking.repository.CuentaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/legacy/cuentas")
public class CuentaController {

    private final CuentaRepository cuentaRepository;

    public CuentaController(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<Cuenta> obtenerCuenta(@PathVariable String numeroCuenta) {
        return cuentaRepository.findById(numeroCuenta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cuenta>> obtenerCuentasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(cuentaRepository.findByClienteId(clienteId));
    }

    /**
     * Operacion critica usada por el BFF de Cajeros Automaticos.
     * Descuenta el monto del saldo disponible si hay fondos suficientes.
     */
    @PostMapping("/{numeroCuenta}/retiro")
    public ResponseEntity<?> retirar(@PathVariable String numeroCuenta, @RequestBody RetiroRequest request) {
        return cuentaRepository.findById(numeroCuenta).map(cuenta -> {
            if (!"ACTIVA".equals(cuenta.getEstado())) {
                return ResponseEntity.status(409).body("La cuenta no se encuentra activa");
            }
            if (cuenta.getSaldoDisponible().compareTo(request.monto()) < 0) {
                return ResponseEntity.status(422).body("Fondos insuficientes");
            }
            cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().subtract(request.monto()));
            cuenta.setSaldoContable(cuenta.getSaldoContable().subtract(request.monto()));
            cuentaRepository.save(cuenta);
            return ResponseEntity.ok(cuenta);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record RetiroRequest(BigDecimal monto) {}
}
