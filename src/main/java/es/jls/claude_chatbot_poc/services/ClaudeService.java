package es.jls.claude_chatbot_poc.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jls.claude_chatbot_poc.dtos.Message;
import es.jls.claude_chatbot_poc.events.ClaudeEvent;
import es.jls.claude_chatbot_poc.events.ErrorEvent;
import es.jls.claude_chatbot_poc.events.TextEvent;
import es.jls.claude_chatbot_poc.events.ToolUseEvent;
import es.jls.claude_chatbot_poc.utils.MessageUtils;
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

        final List<Map<String, Object>> messageContext = MessageUtils.getMessagesWithContext(history);
        final List<Map<String, Object>> tools = MessageUtils.getTools();
        final Map<String, Object> body = MessageUtils.getBodySendMessages(messageContext, tools);

        return anthropicWebClient.post()
                .uri("/v1/messages")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
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

            // delta -> Streaming de texto
            if ("content_block_delta".equals(type)) {
                JsonNode delta = node.path("delta");
                if ("text_delta".equals(delta.path("type").asText())) {

                    String text = delta.path("text").asText("");

                    if (!text.isBlank()) {
                        return Mono.just(new TextEvent(text));
                    }
                }
            }

            // content_block_start -> Uso de herramienta (más adelante puede variar)
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
}
