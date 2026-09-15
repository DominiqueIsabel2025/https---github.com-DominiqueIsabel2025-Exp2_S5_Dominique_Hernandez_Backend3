package cl.duoc.corebanking.config;

import cl.duoc.corebanking.model.Cliente;
import cl.duoc.corebanking.model.Cuenta;
import cl.duoc.corebanking.model.Transaccion;
import cl.duoc.corebanking.repository.ClienteRepository;
import cl.duoc.corebanking.repository.CuentaRepository;
import cl.duoc.corebanking.repository.TransaccionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Simula la carga inicial de datos que en el sistema legacy real
 * provendria del dataset del Banco XYZ (bank_legacy_data).
 * Se usa H2 en memoria para que el proyecto sea 100% autocontenido
 * y ejecutable sin dependencias externas.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;

    public DataSeeder(ClienteRepository clienteRepository, CuentaRepository cuentaRepository,
                       TransaccionRepository transaccionRepository) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    @Override
    public void run(String... args) {
        Cliente c1 = new Cliente(1L, "12.345.678-9", "Maria Fernanda Soto", "mfsoto@correo.cl", "+56911111111", "PERSONA");
        Cliente c2 = new Cliente(2L, "9.876.543-2", "Jose Luis Pizarro", "jlpizarro@correo.cl", "+56922222222", "PERSONA");
        Cliente c3 = new Cliente(3L, "76.543.210-1", "Comercial Andes SpA", "contacto@andes.cl", "+56233333333", "PYME");
        clienteRepository.saveAll(java.util.List.of(c1, c2, c3));

        Cuenta cu1 = new Cuenta("CTA-0001", 1L, "CORRIENTE", new BigDecimal("850000"), new BigDecimal("850000"), "ACTIVA", new BigDecimal("300000"));
        Cuenta cu2 = new Cuenta("CTA-0002", 2L, "AHORRO", new BigDecimal("1250000"), new BigDecimal("1250000"), "ACTIVA", new BigDecimal("200000"));
        Cuenta cu3 = new Cuenta("CTA-0003", 3L, "CORRIENTE", new BigDecimal("5400000"), new BigDecimal("5400000"), "ACTIVA", new BigDecimal("500000"));
        cuentaRepository.saveAll(java.util.List.of(cu1, cu2, cu3));

        transaccionRepository.saveAll(java.util.List.of(
                new Transaccion("CTA-0001", "DEPOSITO", new BigDecimal("500000"), LocalDateTime.now().minusDays(5), "Deposito sueldo", "WEB"),
                new Transaccion("CTA-0001", "PAGO", new BigDecimal("45000"), LocalDateTime.now().minusDays(4), "Pago cuenta luz", "MOVIL"),
                new Transaccion("CTA-0001", "RETIRO", new BigDecimal("100000"), LocalDateTime.now().minusDays(2), "Retiro cajero", "CAJERO"),
                new Transaccion("CTA-0001", "TRANSFERENCIA", new BigDecimal("30000"), LocalDateTime.now().minusDays(1), "Transferencia a CTA-0002", "WEB"),
                new Transaccion("CTA-0002", "DEPOSITO", new BigDecimal("1000000"), LocalDateTime.now().minusDays(10), "Deposito inicial", "WEB"),
                new Transaccion("CTA-0002", "INTERES", new BigDecimal("15000"), LocalDateTime.now().minusDays(3), "Interes mensual", "SUCURSAL"),
                new Transaccion("CTA-0002", "RETIRO", new BigDecimal("50000"), LocalDateTime.now().minusHours(6), "Retiro cajero", "CAJERO"),
                new Transaccion("CTA-0003", "DEPOSITO", new BigDecimal("2000000"), LocalDateTime.now().minusDays(7), "Deposito ventas", "WEB"),
                new Transaccion("CTA-0003", "PAGO", new BigDecimal("800000"), LocalDateTime.now().minusDays(1), "Pago proveedores", "WEB")
        ));

        System.out.println(">>> [core-banking-service] Datos legacy cargados: 3 clientes, 3 cuentas, 9 transacciones.");
    }
}
