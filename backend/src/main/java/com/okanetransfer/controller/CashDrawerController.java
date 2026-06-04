package com.okanetransfer.controller;

import com.okanetransfer.dto.DiscrepancyRequest;
import com.okanetransfer.entity.CashDrawer;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.CashDrawerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent/cash")
@Tag(name = "Agent - Caisse")
public class CashDrawerController {

    @Autowired
    private CashDrawerService cashDrawerService;

    @Operation(summary = "Ouvrir la caisse")
    @PostMapping("/open")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<CashDrawer> open(@RequestParam Long agentId,
                                            @RequestParam Long agencyId,
                                            @RequestParam BigDecimal initialBalance) {
        return ResponseEntity.ok(cashDrawerService.openCashDrawer(agentId, agencyId, initialBalance));
    }

    @Operation(summary = "Solde caisse en temps réel")
    @GetMapping("/balance")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<BigDecimal> balance(@RequestParam Long agentId) {
        return ResponseEntity.ok(cashDrawerService.getCurrentBalance(agentId));
    }

    @Operation(summary = "Opérations du jour")
    @GetMapping("/operations")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<Transfer>> operations(
            @RequestParam Long agentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(cashDrawerService.getDayOperations(agentId, date));
    }

    @Operation(summary = "Clôture de caisse avec réconciliation")
    @PostMapping("/close")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Map<String, Object>> close(@RequestParam Long agentId,
                                                      @RequestParam BigDecimal countedAmount) {
        return ResponseEntity.ok(cashDrawerService.closeCashDrawer(agentId, countedAmount));
    }

    @Operation(summary = "Signaler un écart de caisse")
    @PostMapping("/discrepancy")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Map<String, Object>> discrepancy(@RequestParam Long agentId,
                                                             @Valid @RequestBody DiscrepancyRequest request) {
        return ResponseEntity.ok(cashDrawerService.reportDiscrepancy(agentId, request));
    }

    @Operation(summary = "Caisses d'une agence")
    @GetMapping("/agency/{agencyId}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<CashDrawer>> agencyCashDrawers(@PathVariable Long agencyId) {
        return ResponseEntity.ok(cashDrawerService.getAgencyCashDrawers(agencyId));
    }
}
