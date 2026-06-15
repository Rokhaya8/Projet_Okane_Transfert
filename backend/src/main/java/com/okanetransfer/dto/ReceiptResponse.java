package com.okanetransfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceiptResponse(
        String receiptNumber,
        String transferReferenceCode,
        String agentName,
        String agencyName,
        String beneficiaryName,
        String beneficiaryPhone,
        String beneficiaryIdentityNumber,
        BigDecimal paidAmount,
        LocalDateTime paidAt
) {
}
