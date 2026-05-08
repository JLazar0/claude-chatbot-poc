package es.jls.claude_chatbot_poc.events;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Record para los eventos de uso de tools de claude
 *
 * @author JLazar0
 *
 * @param toolId identifidacor de la tool
 * @param toolName nombre de la tool
 * @param input input json
 */
public record ToolUseEvent(String toolId,String toolName,JsonNode input) implements ClaudeEvent {}