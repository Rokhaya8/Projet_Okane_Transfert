package com.okanetransfer.controller;

import com.okanetransfer.entity.CashDrawer;
import com.okanetransfer.service.CashDrawerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/agent/cash-drawer")
public class CashDrawerController {

    @Autowired
    private CashDrawerService cashDrawerService;

    // Ouvrir la caisse
    @PostMapping("/open")
    public ResponseEntity<CashDrawer> openCashDrawer(
            @RequestParam Long agentId,
            @RequestParam Long agencyId,
            @RequestParam BigDecimal initialBalance) {
        CashDrawer cashDrawer = cashDrawerService.openCashDrawer(agentId, agencyId, initialBalance);
        return ResponseEntity.ok(cashDrawer);
    }

    // Clôturer la caisse
    @PutMapping("/close")
    public ResponseEntity<CashDrawer> closeCashDrawer(
            @RequestParam Long agentId,
            @RequestParam BigDecimal countedAmount) {
        CashDrawer cashDrawer = cashDrawerService.closeCashDrawer(agentId, countedAmount);
        return ResponseEntity.ok(cashDrawer);
    }

    // Voir le solde actuel
    @GetMapping("/balance/{agentId}")
    public ResponseEntity<BigDecimal> getCurrentBalance(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(cashDrawerService.getCurrentBalance(agentId));
    }

    // Voir les caisses d'une agence
    @GetMapping("/agency/{agencyId}")
    public ResponseEntity<List<CashDrawer>> getAgencyCashDrawers(
            @PathVariable Long agencyId) {
        return ResponseEntity.ok(cashDrawerService.getAgencyCashDrawers(agencyId));
    }
}