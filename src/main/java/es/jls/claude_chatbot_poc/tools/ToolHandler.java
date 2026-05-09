package es.jls.claude_chatbot_poc.tools;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

/**
 * Handler generico que implementaran todas las herramientas
 * definidas y que permitira invocarlas desde el toolservice
 * del mismo modo a todas.
 *
 * @author JLazar0
 */
public interface ToolHandler {

    /**
     * Devuelve el nombre de la herramienta
     * @return nombre herramienta
     */
    String name();

    /**
     * Ejecuta la herramienta
     * @param input {@link JsonNode} con la información para ejecutar la herramienta
     * @return resultado
     */
    Mono<String> execute(JsonNode input);
}
