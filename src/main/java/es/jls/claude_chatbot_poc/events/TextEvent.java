package es.jls.claude_chatbot_poc.events;

/**
 * Record para los eventos de texto de claude
 *
 * @author JLazar0
 *
 * @param text contenido del evento
 */
public record TextEvent(String text) implements ClaudeEvent {}