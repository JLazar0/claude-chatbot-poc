package es.jls.claude_chatbot_poc.mappers;

import es.jls.claude_chatbot_poc.events.ClaudeEvent;
import es.jls.claude_chatbot_poc.events.ErrorEvent;
import es.jls.claude_chatbot_poc.events.TextEvent;
import es.jls.claude_chatbot_poc.events.ToolUseEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Mapper para transformar los eventos claude
 *
 * @author JLazar0
 */
@Component
public class ClaudeEventMapper {

    /**
     * Convierte cualquier evento Claude a texto
     * @param event cualquiera de los eventos que hereda de {@link ClaudeEvent}
     * @return flujo reactivo de texto
     */
    public Flux<String> toSseText(ClaudeEvent event) {

        if (event instanceof TextEvent textEvent) {
            return Flux.just(textEvent.text());
        }

        if (event instanceof ErrorEvent errorEvent) {
            return Flux.just("ERROR: " + errorEvent.message());
        }

        if (event instanceof ToolUseEvent) {
            return Flux.just("[tool executed]");
        }

        return Flux.empty();
    }
}
