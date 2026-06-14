package com.okanetransfer.controller;

import com.okanetransfer.dto.*;
import com.okanetransfer.service.AgentCashSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/cash-sessions")
public class AgentCashSessionController {

    private final AgentCashSessionService cashSessionService;

    public AgentCashSessionController(AgentCashSessionService cashSessionService) {
        this.cashSessionService = cashSessionService;
    }

    @PostMapping("/open")
    public ResponseEntity<CashSessionResponse> openSession(@Valid @RequestBody OpenCashSessionRequest request) {
        return ResponseEntity.ok(cashSessionService.openSession(request));
    }

    @GetMapping("/current")
    public ResponseEntity<CashSessionResponse> getCurrentSession(@RequestParam Long agentId) {
        return ResponseEntity.ok(cashSessionService.getCurrentSession(agentId));
    }

    @GetMapping
    public ResponseEntity<List<CashSessionResponse>> getAgentSessions(@RequestParam Long agentId) {
        return ResponseEntity.ok(cashSessionService.getAgentSessions(agentId));
    }

    @GetMapping("/{sessionId}/operations")
    public ResponseEntity<List<CashOperationResponse>> getSessionOperations(@PathVariable Long sessionId) {
        return ResponseEntity.ok(cashSessionService.getSessionOperations(sessionId));
    }

    @GetMapping("/operations")
    public ResponseEntity<List<CashOperationResponse>> getAgentOperations(@RequestParam Long agentId) {
        return ResponseEntity.ok(cashSessionService.getAgentOperations(agentId));
    }

    @PostMapping("/current/adjustments")
    public ResponseEntity<CashSessionResponse> adjustCurrentSession(
            @RequestParam Long agentId,
            @Valid @RequestBody CashAdjustmentRequest request
    ) {
        return ResponseEntity.ok(cashSessionService.adjustCurrentSession(agentId, request));
    }

    @PostMapping("/current/close")
    public ResponseEntity<CashSessionResponse> closeCurrentSession(
            @RequestParam Long agentId,
            @Valid @RequestBody CloseCashSessionRequest request
    ) {
        return ResponseEntity.ok(cashSessionService.closeCurrentSession(agentId, request));
    }
}
