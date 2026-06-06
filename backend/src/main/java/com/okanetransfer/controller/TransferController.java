package com.okanetransfer.controller;

import com.okanetransfer.dto.TransferDTO;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import com.okanetransfer.dto.SimulationDTO;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/agent/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    // Enregistrer un envoi → renvoie un DTO
    @PostMapping
    public ResponseEntity<TransferDTO> registerTransfer(
            @RequestBody Transfer transfer,
            @RequestParam Long agentId) {
        Transfer saved = transferService.registerTransfer(transfer, agentId);
        return ResponseEntity.ok(TransferDTO.fromEntity(saved));
    }

    // Payer un retrait → renvoie un DTO
    @PutMapping("/pay/{referenceCode}")
    public ResponseEntity<TransferDTO> payTransfer(
            @PathVariable String referenceCode,
            @RequestParam Long agentId) {
        Transfer paid = transferService.payTransfer(referenceCode, agentId);
        return ResponseEntity.ok(TransferDTO.fromEntity(paid));
    }

    // Chercher par code → renvoie un DTO
    @GetMapping("/{referenceCode}")
    public ResponseEntity<TransferDTO> getByReferenceCode(
            @PathVariable String referenceCode) {
        return transferService.findByReferenceCode(referenceCode)
                .map(t -> ResponseEntity.ok(TransferDTO.fromEntity(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Transferts d'un agent → renvoie une liste de DTO
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<TransferDTO>> getAgentTransfers(
            @PathVariable Long agentId) {
        List<TransferDTO> dtos = transferService.getAgentTransfers(agentId)
                .stream()
                .map(TransferDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Simulation des frais (pour l'étape Récapitulatif, sans créer le transfert)
    @GetMapping("/simulate")
    public ResponseEntity<SimulationDTO> simulate(
            @RequestParam String sourceCountry,
            @RequestParam String destinationCountry,
            @RequestParam BigDecimal amountSent) {
        SimulationDTO result = transferService.simulateTransfer(sourceCountry, destinationCountry, amountSent);
        return ResponseEntity.ok(result);
    }
}