package cl.duoc.corebanking.controller;

import cl.duoc.corebanking.model.Transaccion;
import cl.duoc.corebanking.repository.TransaccionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legacy/transacciones")
public class TransaccionController {

    private final TransaccionRepository transaccionRepository;

    public TransaccionController(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    public ResponseEntity<List<Transaccion>> obtenerPorCuenta(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(transaccionRepository.findByNumeroCuentaOrderByFechaDesc(numeroCuenta));
    }
}
