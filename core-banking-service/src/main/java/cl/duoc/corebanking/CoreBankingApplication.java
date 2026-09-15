package cl.duoc.corebanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Servicio Core Banking (legacy simulado).
 * Representa el sistema monolitico del Banco XYZ que expone los datos
 * base (clientes, cuentas, transacciones) que luego seran consumidos,
 * transformados y optimizados por cada BFF (Web, Movil, Cajero).
 */
@SpringBootApplication
public class CoreBankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreBankingApplication.class, args);
    }
}
