package com.okanetransfer.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class ChatService {

    private final RestTemplate restTemplate = new RestTemplate();
    // Consigne 6.2 : Récupération sécurisée via l'environnement, pas de clé en clair !
    private final String apiKey = System.getenv("groq.api.key");
    private final String apiUrl = "https://api.groq.com/openai/v1/chat/completions";

    public String generateResponse(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Clé API manquante";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // Consignes du CDC (Statut, frais, délais + Multilingue)
            String systemPrompt = "Tu es l'assistant d'Okane Transfert. Réponds sur les frais, statuts et délais. "
                    + "Réponds dans la langue de l'utilisateur (Français, Anglais, Arabe). "
                    + "Si la demande sort de ce cadre ou demande un humain, réponds UNIQUEMENT : 'ESCALADE_HUMAINE'.";

            Map<String, Object> body = new HashMap<>();
            body.put("model", "llama3-8b-8192");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userMessage));
            body.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                Map<String, Object> messageResult = (Map<String, Object>) choices.get(0).get("message");
                return (String) messageResult.get("content");
            }
        } catch (Exception e) {
            System.err.println("Erreur chatbot Groq: " + e.getMessage());
        }
        return "Erreur lors de la communication avec l'assistant.";
    }
}