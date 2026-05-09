package es.jls.claude_chatbot_poc.services;

import es.jls.claude_chatbot_poc.dtos.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que actuara como memoria, sustituible por REDIS
 * o algo asi, para almacenar los mensajes que usaremos
 * para crear el contexto de la conversación
 *
 * @author JLazar0
 */
@Service
public class MemoryService {
    private final Map<String, List<Message>> store = new ConcurrentHashMap<>();

    /**
     * Método para obtener los mensajes en memoria
     * @param sessionId identificador de la sesion
     * @return listado de {@link Message} con la info del mensaje
     */
    public List<Message> getHistory(String sessionId) {
        return store.getOrDefault(sessionId, new ArrayList<>());
    }

    /**
     * Método para añadir un mensaje a la memoria
     * @param sessionId identificador de la sesion
     * @param message mensaje
     */
    public void add(String sessionId, Message message) {
        store.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(message);
    }
}
