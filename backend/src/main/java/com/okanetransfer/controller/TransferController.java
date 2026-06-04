package com.okanetransfer.controller;

import com.okanetransfer.dto.PayoutRequest;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/agent/transfers")
@Tag(name = "Agent - Transferts")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @Operation(summary = "Enregistrer un envoi")
    @PostMapping
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Transfer> registerTransfer(@RequestBody Transfer transfer,
                                                      @RequestParam Long agentId) {
        return ResponseEntity.ok(transferService.registerTransfer(transfer, agentId));
    }

    @Operation(summary = "Rechercher un transfert par code ou téléphone bénéficiaire")
    @GetMapping("/search")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<?> searchTransfer(@RequestParam(required = false) String code,
                                             @RequestParam(required = false) String telephoneBeneficiaire) {
        if (code != null && !code.isEmpty()) {
            return transferService.findByReferenceCode(code)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        if (telephoneBeneficiaire != null && !telephoneBeneficiaire.isEmpty()) {
            List<Transfer> list = transferService.findByBeneficiaryPhone(telephoneBeneficiaire);
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.badRequest().body("Fournir code ou téléphone");
    }

    @Operation(summary = "Payer un transfert")
    @PostMapping("/{transfertId}/payout")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Transfer> payout(@PathVariable Long transfertId,
                                            @RequestParam Long agentId,
                                            @Valid @RequestBody PayoutRequest request) {
        return ResponseEntity.ok(transferService.payout(transfertId, agentId, request));
    }

    @Operation(summary = "Historique des transferts de l'agent")
    @GetMapping("/history")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Page<Transfer>> history(
            @RequestParam Long agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(transferService.getHistory(agentId, page, size, status, startDate, endDate));
    }

    @Operation(summary = "Chercher par code de retrait")
    @GetMapping("/{referenceCode}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Transfer> getByReferenceCode(@PathVariable String referenceCode) {
        return transferService.findByReferenceCode(referenceCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Transferts d'un agent")
    @GetMapping("/agent/{agentId}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<Transfer>> getAgentTransfers(@PathVariable Long agentId) {
        return ResponseEntity.ok(transferService.getAgentTransfers(agentId));
    }
}
