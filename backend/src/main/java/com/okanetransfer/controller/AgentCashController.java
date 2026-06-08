package com.okanetransfer.controller;

import com.okanetransfer.dto.*;
import com.okanetransfer.entity.User;
import com.okanetransfer.service.CashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur REST pour la gestion de caisse.
 * Endpoints protégés par ROLE_AGENT.
 */
@RestController
@RequestMapping("/api/agent/cash")
@RequiredArgsConstructor
@Tag(name = "Agent - Gestion de Caisse", description = "Endpoints pour la gestion de la caisse de l'agent")
public class AgentCashController {

    private final CashService cashService;

    @Operation(summary = "Consulter le solde de caisse", 
               description = "Retourne le solde actuel de la caisse de l'agence de l'agent")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solde récupéré avec succès"),
        @ApiResponse(responseCode = "404", description = "Caisse introuvable")
    })
    @GetMapping("/balance")
    public ResponseEntity<CashBalanceDTO> getBalance(@RequestParam(required = false) Long agentId) {
        Long userId = agentId != null ? agentId : 1L;
        CashBalanceDTO balance = cashService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "Consulter les opérations de caisse", 
               description = "Retourne la liste des opérations pour une date donnée (par défaut aujourd'hui)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Opérations récupérées avec succès")
    })
    @GetMapping("/operations")
    public ResponseEntity<List<OperationCaisseDTO>> getOperations(
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Long userId = agentId != null ? agentId : 1L;
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<OperationCaisseDTO> operations = cashService.getOperations(userId, targetDate);
        return ResponseEntity.ok(operations);
    }

    @Operation(summary = "Clôturer la caisse", 
               description = "Clôture la caisse journalière en comparant le solde théorique au solde réel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clôture effectuée avec succès"),
        @ApiResponse(responseCode = "409", description = "Caisse déjà clôturée")
    })
    @PostMapping("/close")
    public ResponseEntity<CashCloseResponseDTO> closeCashDrawer(
            @Valid @RequestBody CashCloseRequestDTO request,
            @RequestParam(required = false) Long agentId,
            HttpServletRequest httpRequest) {
        
        Long userId = agentId != null ? agentId : 1L;
        String ipAddress = httpRequest.getRemoteAddr();
        CashCloseResponseDTO response = cashService.closeCashDrawer(userId, request, ipAddress);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Signaler un écart de caisse", 
               description = "Enregistre un signalement d'écart de caisse sans modifier le solde")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Écart signalé avec succès")
    })
    @PostMapping("/discrepancy")
    public ResponseEntity<Void> reportDiscrepancy(
            @Valid @RequestBody DiscrepancyRequestDTO request,
            @RequestParam(required = false) Long agentId,
            HttpServletRequest httpRequest) {
        
        Long userId = agentId != null ? agentId : 1L;
        String ipAddress = httpRequest.getRemoteAddr();
        cashService.reportDiscrepancy(userId, request, ipAddress);
        return ResponseEntity.ok().build();
    }
}
