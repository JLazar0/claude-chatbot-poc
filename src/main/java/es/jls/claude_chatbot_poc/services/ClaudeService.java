package es.jls.claude_chatbot_poc.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jls.claude_chatbot_poc.dtos.Message;
import es.jls.claude_chatbot_poc.events.ClaudeEvent;
import es.jls.claude_chatbot_poc.events.ErrorEvent;
import es.jls.claude_chatbot_poc.events.TextEvent;
import es.jls.claude_chatbot_poc.events.ToolUseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClaudeService {

    private final WebClient anthropicWebClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Flux<ClaudeEvent> stream(List<Message> history) {

        Map<String, Object> body = Map.of(
                "model", "claude-haiku-4-5",
                "max_tokens", 1024,
                "stream", true,
                "messages", getMessagesWithContext(history),
                "tools", getTools()
        );

        return anthropicWebClient.post()
                .uri("/v1/messages")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(System.out::println)
                .map(line -> line.trim())
                .filter(line -> !line.equals("[DONE]"))
                .filter(s -> !s.isBlank())
                .flatMap(this::parseEvent)
                .timeout(Duration.ofSeconds(60))
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1)))
                .onErrorResume(e -> Flux.just(new ErrorEvent(
                        e.getMessage()
                )));
    }

    private Mono<ClaudeEvent> parseEvent(String json) {

        try {
            JsonNode node = objectMapper.readTree(json);
            String type = node.path("type").asText();

            // TEXTO STREAMING
            if ("content_block_delta".equals(type)) {
                JsonNode delta = node.path("delta");
                if ("text_delta".equals(delta.path("type").asText())) {

                    String text = delta.path("text").asText("");

                    if (!text.isBlank()) {
                        return Mono.just(new TextEvent(text));
                    }
                }
            }

            // TOOL USE
            if ("content_block_start".equals(type)) {
                JsonNode block = node.path("content_block");
                if ("tool_use".equals(block.path("type").asText())) {
                    return Mono.just(
                            new ToolUseEvent(
                                    block.path("id").asText(),
                                    block.path("name").asText(),
                                    block.path("input")
                            )
                    );
                }
            }
            return Mono.empty();
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    public Flux<ClaudeEvent> continueConversation(
            List<Message> history
    ) {

        List<Map<String, Object>> messages =
                history.stream()

                        .map(m -> Map.of(
                                "role", m.role(),
                                "content", m.content()
                        ))

                        .toList();

        Map<String, Object> body = Map.of(
                "model", "claude-haiku-4-5",
                "stream", true,
                "max_tokens", 1024,
                "messages", messages,
                "tools", getTools()
        );

        return anthropicWebClient.post()
                .uri("/v1/messages")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(this::parseEvent);
    }

    /**
     * Método para construir el contexto con mensaje actual del usuario
     * y anteriores de usuario y agente
     *
     * @param history listado de mensajes anteriores
     * @return listado de mensajes k-> user|agent v-> mensaje
     */
    private List<Map<String, Object>> getMessagesWithContext(List<Message> history) {
        // Construimos los mensajes anteriores del historial
        List<Map<String, Object>> messages = new ArrayList<>();
        for (Message m : history) {
            messages.add(Map.of(
                    "role", m.role(),
                    "content", m.content()
            ));
        }

        return messages;
    }

    /**
     * Método para definir herramientas que el agente puede usar
     *
     * @return Listado de herramientas
     */
    private List<Map<String, Object>> getTools() {
        return List.of(Map.of(
                        "name", "get_current_time",
                        "description", "Devuelve la hora actual del sistema",
                        "input_schema", Map.of(
                                "type", "object",
                                "properties", Map.of(),
                                "required", List.of()
                        )
                )
        );
    }


}
