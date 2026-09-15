package cl.duoc.corebanking.repository;

import cl.duoc.corebanking.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
