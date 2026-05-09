package es.jls.claude_chatbot_poc.controllers;

import es.jls.claude_chatbot_poc.dtos.ChatRequest;
import es.jls.claude_chatbot_poc.mappers.ClaudeEventMapper;
import es.jls.claude_chatbot_poc.services.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

/**
 * Controller para invocar el chat
 *
 * @author JLazar0
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ClaudeEventMapper claudeEventMapper;

    /**
     * Post para chatear con el agente
     * @param request de tipo {@link ChatRequest}
     * @return flujo reactivo de texto
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestBody ChatRequest request) {
        return chatService.chat(request)
                .flatMap(claudeEventMapper::toSseText)
                .bufferTimeout(100, Duration.ofMillis(1000))
                .map(list -> String.join("", list))
                .filter(s -> !s.isBlank());
    }
}