package es.jls.claude_chatbot_poc.utils;

import es.jls.claude_chatbot_poc.dtos.Message;
import es.jls.claude_chatbot_poc.events.ToolUseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de utilidades para convertir a bodys que Claude
 * sea capaz de leer
 *
 * @author JLazar0
 */
public class MessageUtils {


    /**
     * Método para obtener el cuerpo de mensaje necesario para enviar
     * mensajes a claude
     * @param messageContext contexto de mensajes
     * @param tools herramientas disponibles que se ha decidido usar
     * @return body a enviar a la api de messages
     */
    public static Map<String, Object> getBodySendMessages(List<Map<String, Object>> messageContext, List<Map<String, Object>> tools) {
        Map<String, Object> body = new HashMap<>();

        body.put("model", "claude-haiku-4-5");
        body.put("max_tokens", 1024);
        body.put("cache_control", Map.of("type", "ephemeral"));
        body.put("stream", true);
        body.put("messages", messageContext);

        // Si se ha decidido utilizar alguna tool, añadimos
        if (!tools.isEmpty()) {
            body.put("tools", tools);
        }

        return body;
    }

    /**
     * Formateamos el mensaje de respuesta a Claude de una tool, indicamos que es role user
     * por que es información nueva que entra en la conversacion
     * @param toolId identificador de la herramienta
     * @param result resultado
     * @return Mapa con el formato aceptado por Claude
     */
    public static Map<String, Object> getToolResult(String toolId, String result){
        return Map.of(
                "role", Constants.ROLE_ANTHROPIC_USER,
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

    /**
     * Método para construir el contexto con mensaje actual del usuario
     * y anteriores de usuario y agente
     *
     * @param history listado de mensajes anteriores
     * @return listado de mensajes k-> user|agent v-> mensaje
     */
    public static List<Map<String, Object>> getMessagesWithContext(List<Message> history) {
        // Construimos los mensajes anteriores del historial
        List<Map<String, Object>> messages = new ArrayList<>();
        for (Message m : history) {
            messages.add(Map.of(
                    "role", m.role(),
                    "content", m.content()
            ));
        }

        return messages;
    }
}
