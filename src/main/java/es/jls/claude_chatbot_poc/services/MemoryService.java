package es.jls.claude_chatbot_poc.services;

import es.jls.claude_chatbot_poc.dtos.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryService {
    private final Map<String, List<Message>> store = new ConcurrentHashMap<>();

    public List<Message> getHistory(String sessionId) {
        return store.getOrDefault(sessionId, new ArrayList<>());
    }

    public void add(String sessionId, Message message) {
        store.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(message);
    }
}
