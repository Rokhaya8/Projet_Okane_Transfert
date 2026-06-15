package com.okanetransfer.controller;

import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.TransferServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "http://localhost:4200") // Permet à ton frontend Angular de requêter ton backend en local
public class ClientTransferController {

    private final TransferServiceClient clientTransferService;

    // Injection de ton service dédié par constructeur
    public ClientTransferController(TransferServiceClient clientTransferService) {
        this.clientTransferService = clientTransferService;
    }

    /**
     * endpoint 1 : Suivre un transfert via son code de référence
     */
    @GetMapping("/{clientId}/track/{referenceCode}")
    public ResponseEntity<?> trackTransfer(@PathVariable("referenceCode") String referenceCode, @PathVariable("clientId") Long clientId) {
        try {
            Transfer transfer = clientTransferService.trackTransfer(referenceCode);
            return ResponseEntity.ok(transfer); // Retourne l'entité complète (200 OK)
        } catch (NoSuchElementException e) {
            // Retourne une erreur 404 propre au format JSON
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * endpoint 2 : Consulter l'historique de tous les transferts d'un client
     */
    @GetMapping("/{clientId}/history")
    public ResponseEntity<List<Transfer>> getHistory(@PathVariable("clientId") Long clientId) {
        List<Transfer> history = clientTransferService.getClientHistory(clientId);
        return ResponseEntity.ok(history);
    }

    /**
     * endpoint 3 : Récupérer les statistiques et transactions récentes pour le Dashboard
     */
    @GetMapping("/{clientId}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@PathVariable("clientId") Long clientId) {
        Map<String, Object> stats = clientTransferService.getClientDashboardInfo(clientId);
        return ResponseEntity.ok(stats);
    }
}