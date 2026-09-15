package cl.duoc.bffatm.client;

import cl.duoc.bffatm.dto.CuentaLegacyDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

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

    public CuentaLegacyDTO retirar(String numeroCuenta, BigDecimal monto) {
        return restClient.post()
                .uri("/api/legacy/cuentas/{numeroCuenta}/retiro", numeroCuenta)
                .body(Map.of("monto", monto))
                .retrieve()
                .body(CuentaLegacyDTO.class);
    }
}
