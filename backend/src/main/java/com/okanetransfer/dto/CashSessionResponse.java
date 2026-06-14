package com.okanetransfer.dto;

import com.okanetransfer.entity.CashSessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashSessionResponse(
        Long id,
        Long agentId,
        String agentName,
        Long agencyId,
        String agencyName,
        BigDecimal openingBalance,
        BigDecimal currentBalance,
        BigDecimal closingBalance,
        BigDecimal countedAmount,
        BigDecimal discrepancyAmount,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        CashSessionStatus status
) {
}
