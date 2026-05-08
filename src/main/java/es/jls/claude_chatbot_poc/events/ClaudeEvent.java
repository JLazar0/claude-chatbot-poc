package es.jls.claude_chatbot_poc.events;

/**
 * Interfaz para los eventos de Claude
 *
 * @author JLazar0
 */
public sealed interface ClaudeEvent
        permits ErrorEvent, TextEvent, ToolUseEvent {
}
