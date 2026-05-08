package es.jls.claude_chatbot_poc.dtos;

/**
 * DTO para guardar los mensajes en el contexto
 *
 * @author JLazar0
 *
 * @param role quien envia el mensaje, [user | assistant]
 * @param content contenido del mensaje
 */
public record Message(
        String role,
        Object content
) {}