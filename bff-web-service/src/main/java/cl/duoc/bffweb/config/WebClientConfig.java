package cl.duoc.bffweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {

    @Value("${core.banking.base-url}")
    private String coreBankingBaseUrl;

    @Bean
    public RestClient coreBankingRestClient() {
        return RestClient.builder()
                .baseUrl(coreBankingBaseUrl)
                .build();
    }
}
