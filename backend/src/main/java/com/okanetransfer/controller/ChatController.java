package com.okanetransfer.controller;


import com.okanetransfer.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/client/chat")
public class ChatController {

    private final ChatService chatbotService;

    public ChatController(ChatService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> handleChatMessage(@RequestBody Map<String, String> requestBody) {
        // On extrait directement du JSON recu
        String userMessage = requestBody.get("message");

        String botOutput = chatbotService.generateResponse(userMessage);

        // Gestion de l'escalade humaine
        if ("ESCALADE_HUMAINE".equals(botOutput.trim())) {
            return ResponseEntity.ok(Map.of("response",
                    "Je ne peux pas répondre à cela. Je vous réoriente vers un agent humain en agence."));
        }

        // On renvoie un JSON direct {"response": "..."} à Angular
        return ResponseEntity.ok(Map.of("response", botOutput));
    }
}
