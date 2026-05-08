package es.jls.claude_chatbot_poc.tools;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface ToolHandler {

    String name();
    Mono<String> execute(JsonNode input);
}
