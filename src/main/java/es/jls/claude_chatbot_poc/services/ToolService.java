package es.jls.claude_chatbot_poc.services;

import es.jls.claude_chatbot_poc.events.ToolUseEvent;
import es.jls.claude_chatbot_poc.tools.ToolHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ToolService {
    private final Map<String, ToolHandler> handlers;

    /**
     * Constructor en el que construimos el mapa de tools
     * @param toolHandlers implementaciones de {@link ToolHandler} que spring inyecta
     *                     y convertimos en un mapa con clave nombre de tool
     */
    public ToolService(List<ToolHandler> toolHandlers) {
        this.handlers = toolHandlers.stream()
                .collect(Collectors.toMap(ToolHandler::name,Function.identity()));
    }

    public Mono<String> execute(ToolUseEvent tool) {
        ToolHandler handler = handlers.get(tool.toolName());
        if (handler == null) {
            return Mono.just("Tool no encontrada");
        }
        return handler.execute(tool.input());
    }
}
