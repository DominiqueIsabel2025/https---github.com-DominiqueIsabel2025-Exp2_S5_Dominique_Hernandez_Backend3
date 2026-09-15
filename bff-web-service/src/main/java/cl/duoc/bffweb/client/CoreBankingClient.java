package cl.duoc.bffweb.client;

import cl.duoc.bffweb.dto.ClienteLegacyDTO;
import cl.duoc.bffweb.dto.CuentaLegacyDTO;
import cl.duoc.bffweb.dto.TransaccionLegacyDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Cliente HTTP hacia el servicio Core Banking (legacy).
 * Aisla al BFF Web de los detalles de comunicacion con el backend real,
 * permitiendo integrar y agregar informacion desde el servicio central.
 */
@Component
public class CoreBankingClient {

    private final RestClient restClient;

    public CoreBankingClient(RestClient coreBankingRestClient) {
        this.restClient = coreBankingRestClient;
    }

    public ClienteLegacyDTO obtenerCliente(Long clienteId) {
        return restClient.get()
                .uri("/api/legacy/clientes/{id}", clienteId)
                .retrieve()
                .body(ClienteLegacyDTO.class);
    }

    public List<CuentaLegacyDTO> obtenerCuentasPorCliente(Long clienteId) {
        return restClient.get()
                .uri("/api/legacy/cuentas/cliente/{clienteId}", clienteId)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<CuentaLegacyDTO>>() {});
    }

    public List<TransaccionLegacyDTO> obtenerTransacciones(String numeroCuenta) {
        return restClient.get()
                .uri("/api/legacy/transacciones/cuenta/{numeroCuenta}", numeroCuenta)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<TransaccionLegacyDTO>>() {});
    }
}
