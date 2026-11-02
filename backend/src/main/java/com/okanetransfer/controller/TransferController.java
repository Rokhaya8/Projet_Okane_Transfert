package com.okanetransfer.controller;

import com.okanetransfer.dto.response.TransferResponse;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    // ===== ESPACE AGENT =====
    @PostMapping("/api/agent/transfers")
    public ResponseEntity<Transfer> registerTransfer(
            @RequestBody Transfer transfer,
            @RequestParam("agentId") Long agentId) {
        return ResponseEntity.ok(transferService.registerTransfer(transfer, agentId));
    }

    @GetMapping("/api/agent/transfers/agent/{agentId}")
    public ResponseEntity<List<Transfer>> getAgentTransfers(
            @PathVariable("agentId") Long agentId) {
        return ResponseEntity.ok(transferService.getAgentTransfers(agentId));
    }

    // ===== ESPACE ADMIN =====
    @GetMapping("/api/transfers")
    public ResponseEntity<List<TransferResponse>> getAll() {
        return ResponseEntity.ok(transferService.getAll());
    }
}