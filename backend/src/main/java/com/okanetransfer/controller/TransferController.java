package com.okanetransfer.controller;

import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Transfer> registerTransfer(
            @RequestBody Transfer transfer,
            @RequestParam Long agentId) {
        return ResponseEntity.ok(transferService.registerTransfer(transfer, agentId));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Transfer>> getAgentTransfers(@PathVariable Long agentId) {
        return ResponseEntity.ok(transferService.getAgentTransfers(agentId));
    }
}
