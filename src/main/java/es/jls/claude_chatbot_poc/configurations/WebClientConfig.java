package es.jls.claude_chatbot_poc.configurations;

import es.jls.claude_chatbot_poc.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuracion para atacar a la api de webflux
 *
 * @author JLazar0
 */
@Configuration
public class WebClientConfig {

    @Value("${anthropic.base-url}")
    private String baseUrl;

    @Value("${anthropic.api-key}")
    private String apiKey;

    @Value("${anthropic.version}")
    private String anthropicVersion;

    /**
     * Inicializamos el cliente web para anthropic con variable de entorno.
     * @param builder Builder para la inicializacion
     * @return cliente incializado
     */
    @Bean
    public WebClient anthropicWebClient(WebClient.Builder builder) {

        return builder
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", anthropicVersion)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
