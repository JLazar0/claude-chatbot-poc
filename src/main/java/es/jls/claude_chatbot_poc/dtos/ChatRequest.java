package es.jls.claude_chatbot_poc.dtos;

/**
 * DTO de entrada para la capa de control del chat
 *
 * @author JLazar0
 *
 * @param message mensaje del usuario
 * @param sessionId guid con la sesion que servira para guardar el contexto
 */
public record ChatRequest(
        String message,
        String sessionId
) {
}