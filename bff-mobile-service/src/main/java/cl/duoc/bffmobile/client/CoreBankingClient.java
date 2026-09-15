package cl.duoc.bffmobile.client;

import cl.duoc.bffmobile.dto.CuentaLegacyDTO;
import cl.duoc.bffmobile.dto.TransaccionLegacyDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CoreBankingClient {

    private final RestClient restClient;

    public CoreBankingClient(RestClient coreBankingRestClient) {
        this.restClient = coreBankingRestClient;
    }

    public CuentaLegacyDTO obtenerCuenta(String numeroCuenta) {
        return restClient.get()
                .uri("/api/legacy/cuentas/{numeroCuenta}", numeroCuenta)
                .retrieve()
                .body(CuentaLegacyDTO.class);
    }

    public List<TransaccionLegacyDTO> obtenerTransacciones(String numeroCuenta) {
        return restClient.get()
                .uri("/api/legacy/transacciones/cuenta/{numeroCuenta}", numeroCuenta)
                .retrieve()
                .body(new ParameterizedTypeReference<List<TransaccionLegacyDTO>>() {});
    }
}
