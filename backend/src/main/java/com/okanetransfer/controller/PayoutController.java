package com.okanetransfer.controller;

import com.okanetransfer.dto.*;
import com.okanetransfer.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/payouts")
public class PayoutController {

    private final TransferService transferService;

    public PayoutController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/search/code/{referenceCode}")
    public ResponseEntity<TransferSearchResponse> searchByCode(@PathVariable String referenceCode) {
        return ResponseEntity.ok(transferService.searchPayableByReferenceCode(referenceCode));
    }

    @GetMapping("/search/phone/{phone}")
    public ResponseEntity<List<TransferSearchResponse>> searchByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(transferService.searchPayableByBeneficiaryPhone(phone));
    }

    @PostMapping("/pay")
    public ResponseEntity<TransferPaymentResponse> payTransfer(@Valid @RequestBody PayTransferRequest request) {
        return ResponseEntity.ok(transferService.payTransfer(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransferPaymentResponse>> getPaymentHistory(@RequestParam Long agentId) {
        return ResponseEntity.ok(transferService.getPaymentHistory(agentId));
    }

    @GetMapping("/{paymentId}/receipt")
    public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable Long paymentId) {
        return ResponseEntity.ok(transferService.getReceipt(paymentId));
    }
}
