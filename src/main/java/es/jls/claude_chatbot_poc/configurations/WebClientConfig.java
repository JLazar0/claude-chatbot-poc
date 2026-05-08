package es.jls.claude_chatbot_poc.configurations;

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

    /**
     * Inicializamos el cliente web para anthropic con variable de entorno.
     * @param builder Builder para la inicializacion
     * @return cliente incializado
     */
    @Bean
    public WebClient anthropicWebClient(WebClient.Builder builder) {

        return builder
                .baseUrl("https://api.anthropic.com")
                .defaultHeader("x-api-key", System.getenv("ANTHROPIC_API_KEY"))
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
