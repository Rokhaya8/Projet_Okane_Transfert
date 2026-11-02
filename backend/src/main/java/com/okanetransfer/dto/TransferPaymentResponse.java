package com.okanetransfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferPaymentResponse(
        Long id,
        Long transferId,
        String referenceCode,
        Long agentId,
        String agentName,
        Long agencyId,
        String agencyName,
        String beneficiaryName,
        String beneficiaryIdentityNumber,
        BigDecimal paidAmount,
        LocalDateTime paidAt,
        String receiptNumber
) {
}
