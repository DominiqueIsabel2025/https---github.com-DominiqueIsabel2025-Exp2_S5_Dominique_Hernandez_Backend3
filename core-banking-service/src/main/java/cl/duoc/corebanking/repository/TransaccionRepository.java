package cl.duoc.corebanking.repository;

import cl.duoc.corebanking.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByNumeroCuentaOrderByFechaDesc(String numeroCuenta);
}
