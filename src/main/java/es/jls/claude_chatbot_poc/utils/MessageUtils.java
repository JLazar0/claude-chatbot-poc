package es.jls.claude_chatbot_poc.utils;

import es.jls.claude_chatbot_poc.events.ToolUseEvent;

import java.util.List;
import java.util.Map;

public class MessageUtils {

    /**
     * Formateamos el mensaje de respuesta a Claude de una tool, indicamos que es role user
     * por que es información nueva que entra en la conversacion
     * @param toolId identificador de la herramienta
     * @param result resultado
     * @return Mapa con el formato aceptado por Claude
     */
    public static Map<String, Object> getToolResult(String toolId, String result){
        return Map.of(
                "role", "user",
                "content", List.of(
                        Map.of(
                                "type", "tool_result",
                                "tool_use_id", toolId,
                                "content", result
                        )
                )
        );
    }

    /**
     * Formateamos el mensaje de que Claude pidio usar una tool
     * @param toolEvent evento que indica la info de la tool
     * @return Mapa con el formato aceptado por Claude
     */
    public static  Map<String, Object> getMessageToolUse(ToolUseEvent toolEvent) {
        return Map.of(
                "type", "tool_use",
                "id", toolEvent.toolId(),
                "name", toolEvent.toolName(),
                "input", toolEvent.input()
        );
    }

    /**
     * Formateamos la información del resultado de la ejecucion de la tool para mandarsela a
     * Claude
     * @param toolEvent evento de uso de la tool
     * @param toolResult resultado de la tool
     * @return Mapa con el formato aceptado por Claude
     */
    public static Map<String, String> getMessageToolResult(ToolUseEvent toolEvent, String toolResult) {
        return Map.of(
                "type", "tool_result",
                "tool_use_id", toolEvent.toolId(),
                "content", toolResult
        );
    }
}
