package es.jls.claude_chatbot_poc.services;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio que en base a un prompt, identifica palabras clave para
 * devolver si hay que usar alguna tool definida.
 *
 * Esto ayuda a ahorrar tokens
 *
 * @author JLazar0
 */
@Component
public class ToolSelectorService {

    public List<Map<String,Object>> decideToolsUse(String prompt){
        List<Map<String,Object>> toolsToUse = new ArrayList<>();
        if (prompt.matches(".*hora.*")){
                toolsToUse.add(Map.of(
                        "name", "get_current_time",
                        "description", "Devuelve la hora actual del sistema",
                        "input_schema", Map.of(
                                "type", "object",
                                "properties", Map.of(),
                                "required", List.of()
                        )
                    )
                );
        }

        return toolsToUse;
    }
}
