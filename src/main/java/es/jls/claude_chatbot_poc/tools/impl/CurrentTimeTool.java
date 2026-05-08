package es.jls.claude_chatbot_poc.tools.impl;

import com.fasterxml.jackson.databind.JsonNode;
import es.jls.claude_chatbot_poc.tools.ToolHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class CurrentTimeTool implements ToolHandler {

    @Override
    public String name() {
        return "get_current_time";
    }

    @Override
    public Mono<String> execute(JsonNode input) {
        return Mono.just(LocalDateTime.now().toString());
    }
}
