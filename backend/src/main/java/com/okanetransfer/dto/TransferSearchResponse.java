package com.okanetransfer.dto;

import com.okanetransfer.entity.Transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferSearchResponse(
        Long id,
        String referenceCode,
        BigDecimal amountSent,
        BigDecimal amountReceived,
        Transfer.TransferStatus status,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        LocalDateTime expiryDate,
        String beneficiaryName,
        String beneficiaryPhone,
        String beneficiaryCountry,
        boolean payable
) {
}
