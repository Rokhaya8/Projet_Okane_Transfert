package com.okanetransfer.controller;

import com.okanetransfer.dto.*;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.PayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/agent/payouts")
@PreAuthorize("hasRole('AGENT')")
@RequiredArgsConstructor
@Tag(name = "Agent - Paiement Retraits", description = "Endpoints pour le paiement des retraits et historique")
public class AgentPayoutController {

    private final PayoutService payoutService;

    @Operation(summary = "Rechercher un transfert",
               description = "Recherche un transfert par code de retrait ou par téléphone du bénéficiaire")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès"),
        @ApiResponse(responseCode = "404", description = "Aucun transfert trouvé")
    })
    @GetMapping("/search")
    public ResponseEntity<List<TransferSearchDTO>> searchTransfer(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String telephoneBeneficiaire) {
        
        List<TransferSearchDTO> results = payoutService.searchTransfer(code, telephoneBeneficiaire);
        
        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Payer un retrait",
               description = "Effectue le paiement d'un transfert en attente au bénéficiaire")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paiement effectué avec succès"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable"),
        @ApiResponse(responseCode = "409", description = "Statut invalide ou transfert expiré"),
        @ApiResponse(responseCode = "400", description = "Solde insuffisant")
    })
    @PostMapping("/{transferId}/payout")
    public ResponseEntity<PayoutReceiptDTO> payoutTransfer(
            @PathVariable Long transferId,
            @Valid @RequestBody PayoutRequestDTO request,
            @RequestParam(required = false) Long agentId,
            HttpServletRequest httpRequest) {
        
        Long userId = agentId != null ? agentId : 1L;
        String ipAddress = httpRequest.getRemoteAddr();
        
        PayoutReceiptDTO receipt = payoutService.payoutTransfer(transferId, userId, request, ipAddress);
        return ResponseEntity.ok(receipt);
    }

    @Operation(summary = "Consulter l'historique des transferts",
               description = "Retourne l'historique paginé des transferts de l'agent avec filtres")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès")
    })
    @GetMapping("/history")
    public ResponseEntity<Page<TransferHistoryDTO>> getTransferHistory(
            @RequestParam(required = false) Long agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Transfer.TransferStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String corridor,
            @RequestParam(required = false) String search) {
        
        Long userId = agentId != null ? agentId : 1L;
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        Pageable pageable = PageRequest.of(page, size, Sort.unsorted());
        
        Page<TransferHistoryDTO> history = payoutService.getTransferHistory(
                userId, status, startDateTime, endDateTime, corridor, search, pageable
        );
        
        return ResponseEntity.ok(history);
    }
}
