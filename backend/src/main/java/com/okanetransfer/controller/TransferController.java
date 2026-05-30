package com.okanetransfer.controller;

import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agent/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    // Enregistrer un envoi
    @PostMapping
    public ResponseEntity<Transfer> registerTransfer(
            @RequestBody Transfer transfer,
            @RequestParam Long agentId) {
        Transfer saved = transferService.registerTransfer(transfer, agentId);
        return ResponseEntity.ok(saved);
    }

    // Payer un retrait
    @PutMapping("/pay/{referenceCode}")
    public ResponseEntity<Transfer> payTransfer(
            @PathVariable String referenceCode,
            @RequestParam Long agentId) {
        Transfer paid = transferService.payTransfer(referenceCode, agentId);
        return ResponseEntity.ok(paid);
    }

    // Chercher par code de retrait
    @GetMapping("/{referenceCode}")
    public ResponseEntity<Transfer> getByReferenceCode(
            @PathVariable String referenceCode) {
        return transferService.findByReferenceCode(referenceCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Transferts d'un agent
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Transfer>> getAgentTransfers(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(transferService.getAgentTransfers(agentId));
    }
}