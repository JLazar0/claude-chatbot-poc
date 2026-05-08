package es.jls.claude_chatbot_poc.events;

/**
 * Record para los eventos de error de claude
 *
 * @author JLazar0
 *
 * @param message mensaje de error
 */
public record ErrorEvent(String message) implements ClaudeEvent {}