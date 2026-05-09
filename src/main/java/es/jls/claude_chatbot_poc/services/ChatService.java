package es.jls.claude_chatbot_poc.services;

import es.jls.claude_chatbot_poc.dtos.ChatRequest;
import es.jls.claude_chatbot_poc.dtos.Message;
import es.jls.claude_chatbot_poc.events.ClaudeEvent;
import es.jls.claude_chatbot_poc.events.TextEvent;
import es.jls.claude_chatbot_poc.events.ToolUseEvent;
import es.jls.claude_chatbot_poc.utils.Constants;
import es.jls.claude_chatbot_poc.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Servicio que gestionara toda la responsabilidad de la conversación.
 *
 * @author JLazar0
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ClaudeService claudeService;
    private final MemoryService memoryService;
    private final ToolService toolService;

    public Flux<ClaudeEvent> chat(ChatRequest request) {
        // Guardamos el nuevo mensaje
        memoryService.add(request.sessionId(), new Message(Constants.ROLE_ANTHROPIC_USER, request.message()));
        // Leemos los mensajes en memoria para crear contexto
        List<Message> history = memoryService.getHistory(request.sessionId());

        return streamTurn(request.sessionId(), history);
    }

    private Flux<ClaudeEvent> streamTurn( String sessionId,List<Message> history) {

        return claudeService.stream(history)
                .concatMap(event -> {
                    //Texto
                    if (event instanceof TextEvent textEvent) {
                        return Flux.just(textEvent);
                    }
                    //Tools
                    if (event instanceof ToolUseEvent toolEvent) {
                        return toolService.execute(toolEvent)
                                .flatMapMany(toolResult -> {
                                    // guardar contexto
                                    memoryService.add(
                                            sessionId,
                                            new Message(Constants.ROLE_ANTHROPIC_ASSISTANT, List.of(MessageUtils.getMessageToolUse(toolEvent)))
                                    );

                                    memoryService.add(
                                            sessionId,
                                            new Message(Constants.ROLE_ANTHROPIC_USER,List.of(MessageUtils.getMessageToolResult(toolEvent, toolResult)))
                                    );

                                    List<Message> updated = memoryService.getHistory(sessionId);

                                    return streamTurn(sessionId, updated);
                                });
                    }

                    return Flux.empty();
                });
    }
}
