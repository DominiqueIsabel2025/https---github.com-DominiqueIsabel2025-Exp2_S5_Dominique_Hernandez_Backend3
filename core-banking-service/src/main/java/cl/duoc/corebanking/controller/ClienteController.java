package cl.duoc.corebanking.controller;

import cl.duoc.corebanking.model.Cliente;
import cl.duoc.corebanking.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API "legacy" de clientes. Estas rutas representan los datos crudos
 * del sistema monolitico del Banco XYZ, sin ninguna optimizacion por canal.
 * Los BFF son quienes consumen esta API y la adaptan a cada frontend.
 */
@RestController
@RequestMapping("/api/legacy/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }
}
